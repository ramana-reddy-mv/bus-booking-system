package com.busbooking.booking.service;

import com.busbooking.booking.BookingHoldExpiredException;
import com.busbooking.booking.BookingNotFoundException;
import com.busbooking.booking.dto.BookingRequest;
import com.busbooking.booking.dto.BookingResponse;
import com.busbooking.booking.dto.BookingSeatResponse;
import com.busbooking.booking.dto.BookingTripResponse;
import com.busbooking.booking.dto.PaymentRequest;
import com.busbooking.booking.entity.Booking;
import com.busbooking.booking.entity.BookingSeat;
import com.busbooking.booking.enums.BookingStatus;
import com.busbooking.booking.repository.BookingRepository;
import com.busbooking.seat.Seat;
import com.busbooking.seat.SeatRepository;
import com.busbooking.trip.Trip;
import com.busbooking.trip.TripRepository;
import com.busbooking.user.User;
import com.busbooking.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final TripRepository tripRepository;
    private final SeatRepository seatRepository;
    private final UserRepository userRepository;
    private final SeatAllocationService seatAllocationService;

    public BookingServiceImpl(
            BookingRepository bookingRepository,
            TripRepository tripRepository,
            SeatRepository seatRepository,
            UserRepository userRepository,
            SeatAllocationService seatAllocationService) {
        this.bookingRepository = bookingRepository;
        this.tripRepository = tripRepository;
        this.seatRepository = seatRepository;
        this.userRepository = userRepository;
        this.seatAllocationService = seatAllocationService;
    }

    @Override
    @Transactional
    public BookingResponse createBooking(BookingRequest request) {
        User user = requireExistingUser(request.getUserId());
        if (request.getSeatIds() == null || request.getSeatIds().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "At least one seat is required");
        }
        if (new HashSet<>(request.getSeatIds()).size() != request.getSeatIds().size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Seat IDs must be unique");
        }

        Trip trip = lockTrip(request.getTripId());
        LocalDateTime departure = LocalDateTime.of(trip.getTravelDate(), trip.getDepartureTime());
        if (departure.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "This trip is no longer bookable");
        }

        List<Seat> requestedSeats = seatRepository.findAllById(request.getSeatIds());
        if (requestedSeats.size() != request.getSeatIds().size()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "One or more seats were not found");
        }
        Map<Long, Seat> seatsById = requestedSeats.stream()
                .collect(Collectors.toMap(Seat::getSeatId, Function.identity()));
        List<Seat> orderedSeats = request.getSeatIds().stream().map(seatsById::get).toList();
        if (orderedSeats.stream().anyMatch(seat ->
                !Objects.equals(seat.getBus().getBusId(), trip.getBusId()))) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Every seat must belong to the bus assigned to this trip");
        }

        BigDecimal fare = trip.getFare();
        BigDecimal total = fare.multiply(BigDecimal.valueOf(orderedSeats.size()));
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        booking.setTotalAmount(total);
        for (Seat seat : orderedSeats) {
            BookingSeat bookingSeat = new BookingSeat();
            bookingSeat.setTrip(trip);
            bookingSeat.setSeat(seat);
            bookingSeat.setFare(fare);
            booking.addSeat(bookingSeat);
        }

        Booking savedBooking = bookingRepository.save(booking);
        seatAllocationService.holdSeats(trip, orderedSeats, savedBooking, LocalDateTime.now());
        return toResponse(savedBooking);
    }

    @Override
    @Transactional(noRollbackFor = BookingHoldExpiredException.class)
    public BookingResponse processPayment(Long bookingId, Long userId, PaymentRequest request) {
        if (request == null || request.getSuccess() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Payment success result is required");
        }
        Booking booking = lockBooking(bookingId);
        requireOwner(booking, userId);
        if (booking.getStatus() != BookingStatus.PENDING_PAYMENT) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Payment is only allowed for a pending booking");
        }
        lockTrip(booking.getSeats().getFirst().getTrip().getTripId());

        if (Boolean.TRUE.equals(request.getSuccess())) {
            seatAllocationService.confirmBooking(booking, LocalDateTime.now());
            booking.setStatus(BookingStatus.CONFIRMED);
        } else {
            seatAllocationService.releaseBookingSeats(booking);
            booking.setStatus(BookingStatus.PAYMENT_FAILED);
        }
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingResponse cancelBooking(Long bookingId, Long userId) {
        Booking booking = lockBooking(bookingId);
        requireOwner(booking, userId);
        if (booking.getStatus() != BookingStatus.CONFIRMED) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Only confirmed bookings can be cancelled");
        }
        lockTrip(booking.getSeats().getFirst().getTrip().getTripId());
        seatAllocationService.releaseBookingSeats(booking);
        booking.setStatus(BookingStatus.CANCELLED);
        return toResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional(readOnly = true)
    public BookingResponse getBooking(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findWithDetailsById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID " + bookingId));
        requireOwner(booking, userId);
        return toResponse(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingResponse> getMyBookings(Long userId) {
        User user = requireExistingUser(userId);
        return bookingRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private void requireOwner(Booking booking, Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid userId is required");
        }
        if (!Objects.equals(booking.getUser().getUserId(), userId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Booking does not belong to this user");
        }
    }

    private User requireExistingUser(Long userId) {
        if (userId == null || userId <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "A valid userId is required");
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "User not found with ID " + userId));
    }

    private Booking lockBooking(Long bookingId) {
        bookingRepository.findByIdForUpdate(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID " + bookingId));
        return bookingRepository.findWithDetailsById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("Booking not found with ID " + bookingId));
    }

    private Trip lockTrip(Long tripId) {
        return tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Trip not found with ID " + tripId));
    }

    private BookingResponse toResponse(Booking booking) {
        BookingSeat firstSeat = booking.getSeats().getFirst();
        Trip trip = firstSeat.getTrip();
        List<BookingSeatResponse> seats = booking.getSeats().stream()
                .map(seat -> new BookingSeatResponse(
                        seat.getSeat().getSeatId(),
                        seat.getSeat().getSeatNumber(),
                        seat.getFare()))
                .toList();
        return new BookingResponse(
                booking.getBookingId(),
                booking.getBookingReference(),
                booking.getStatus(),
                new BookingTripResponse(
                        trip.getTripId(),
                        trip.getSource(),
                        trip.getDestination(),
                        trip.getTravelDate(),
                        trip.getDepartureTime(),
                        trip.getArrivalTime()),
                seats,
                booking.getTotalAmount(),
                booking.getCreatedAt());
    }
}
