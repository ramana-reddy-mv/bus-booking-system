package com.busbooking.booking.service;

import com.busbooking.booking.BookingHoldExpiredException;
import com.busbooking.booking.dto.BookingRequest;
import com.busbooking.booking.dto.BookingResponse;
import com.busbooking.booking.dto.PaymentRequest;
import com.busbooking.booking.entity.Booking;
import com.busbooking.booking.entity.BookingSeat;
import com.busbooking.booking.enums.BookingStatus;
import com.busbooking.booking.repository.BookingRepository;
import com.busbooking.bus.Bus;
import com.busbooking.seat.Seat;
import com.busbooking.seat.SeatRepository;
import com.busbooking.trip.Trip;
import com.busbooking.trip.TripRepository;
import com.busbooking.user.User;
import com.busbooking.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class BookingServiceImplTest {
    private BookingRepository bookingRepository;
    private TripRepository tripRepository;
    private SeatRepository seatRepository;
    private UserRepository userRepository;
    private SeatAllocationService allocationService;
    private BookingServiceImpl service;

    @BeforeEach
    void setUp() {
        bookingRepository = mock(BookingRepository.class);
        tripRepository = mock(TripRepository.class);
        seatRepository = mock(SeatRepository.class);
        userRepository = mock(UserRepository.class);
        allocationService = mock(SeatAllocationService.class);
        service = new BookingServiceImpl(
                bookingRepository, tripRepository, seatRepository, userRepository, allocationService);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user(1L)));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user(2L)));
        when(tripRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(trip()));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> {
            Booking booking = invocation.getArgument(0);
            booking.setBookingId(90L);
            booking.setBookingReference("booking-reference");
            booking.setCreatedAt(LocalDateTime.now());
            booking.setUpdatedAt(LocalDateTime.now());
            booking.getSeats().forEach(seat -> seat.setBookingSeatId(1L));
            return booking;
        });
    }

    @Test
    void createsOneBookingWithMultipleFareSnapshots() {
        when(seatRepository.findAllById(List.of(11L, 12L))).thenReturn(List.of(seat(11L, 7L), seat(12L, 7L)));

        BookingResponse response = service.createBooking(new BookingRequest(1L, 50L, List.of(11L, 12L)));

        assertEquals(BookingStatus.PENDING_PAYMENT, response.getStatus());
        assertEquals(new BigDecimal("50.00"), response.getTotalAmount());
        assertEquals(2, response.getSeats().size());
        assertEquals(new BigDecimal("25.00"), response.getSeats().getFirst().getFare());
        assertEquals("booking-reference", response.getBookingReference());
        verify(allocationService).holdSeats(any(Trip.class), any(), any(Booking.class), any(LocalDateTime.class));
    }

    @Test
    void rejectsSeatsAssignedToAnotherBus() {
        when(seatRepository.findAllById(List.of(11L))).thenReturn(List.of(seat(11L, 8L)));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createBooking(new BookingRequest(1L, 50L, List.of(11L))));

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(bookingRepository, never()).save(any(Booking.class));
    }

    @Test
    void serializesCompetingBookingAttemptsThroughTripLock() {
        when(seatRepository.findAllById(List.of(11L))).thenReturn(List.of(seat(11L, 7L)));
        doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Seat unavailable"))
                .when(allocationService).holdSeats(any(), any(), any(), any());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.createBooking(new BookingRequest(2L, 50L, List.of(11L))));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(tripRepository).findByIdForUpdate(50L);
        verify(allocationService).holdSeats(any(), any(), any(), any());
    }

    @Test
    void successfulPaymentConfirmsBooking() {
        Booking booking = booking(1L, BookingStatus.PENDING_PAYMENT);
        prepareLockedBooking(booking);

        BookingResponse response = service.processPayment(90L, 1L, new PaymentRequest(true));

        assertEquals(BookingStatus.CONFIRMED, response.getStatus());
        verify(allocationService).confirmBooking(any(Booking.class), any(LocalDateTime.class));
    }

    @Test
    void failedPaymentReleasesSeats() {
        Booking booking = booking(1L, BookingStatus.PENDING_PAYMENT);
        prepareLockedBooking(booking);

        BookingResponse response = service.processPayment(90L, 1L, new PaymentRequest(false));

        assertEquals(BookingStatus.PAYMENT_FAILED, response.getStatus());
        verify(allocationService).releaseBookingSeats(booking);
    }

    @Test
    void expiredPaymentHoldMarksBookingExpired() {
        Booking booking = booking(1L, BookingStatus.PENDING_PAYMENT);
        prepareLockedBooking(booking);
        doAnswer(invocation -> {
            booking.setStatus(BookingStatus.EXPIRED);
            throw new BookingHoldExpiredException("Hold expired");
        }).when(allocationService).confirmBooking(any(), any());

        assertThrows(BookingHoldExpiredException.class,
                () -> service.processPayment(90L, 1L, new PaymentRequest(true)));
        assertEquals(BookingStatus.EXPIRED, booking.getStatus());
    }

    @Test
    void confirmedBookingCanBeCancelledAndSeatsReleased() {
        Booking booking = booking(1L, BookingStatus.CONFIRMED);
        prepareLockedBooking(booking);

        BookingResponse response = service.cancelBooking(90L, 1L);

        assertEquals(BookingStatus.CANCELLED, response.getStatus());
        verify(allocationService).releaseBookingSeats(booking);
    }

    @Test
    void customerCannotReadAnotherUsersBooking() {
        Booking booking = booking(1L, BookingStatus.CONFIRMED);
        when(bookingRepository.findWithDetailsById(90L)).thenReturn(Optional.of(booking));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.getBooking(90L, 2L));

        assertEquals(HttpStatus.FORBIDDEN, exception.getStatusCode());
    }

    private void prepareLockedBooking(Booking booking) {
        when(bookingRepository.findByIdForUpdate(90L)).thenReturn(Optional.of(booking));
        when(bookingRepository.findWithDetailsById(90L)).thenReturn(Optional.of(booking));
        when(tripRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(trip()));
    }

    private Booking booking(Long userId, BookingStatus status) {
        Booking booking = new Booking();
        booking.setBookingId(90L);
        booking.setBookingReference("booking-reference");
        booking.setUser(user(userId));
        booking.setStatus(status);
        booking.setTotalAmount(new BigDecimal("25.00"));
        booking.setCreatedAt(LocalDateTime.now());
        Trip trip = trip();
        BookingSeat bookingSeat = new BookingSeat();
        bookingSeat.setBookingSeatId(1L);
        bookingSeat.setTrip(trip);
        bookingSeat.setSeat(seat(11L, 7L));
        bookingSeat.setFare(new BigDecimal("25.00"));
        booking.addSeat(bookingSeat);
        return booking;
    }

    private User user(Long userId) {
        User user = new User();
        user.setUserId(userId);
        user.setUsername("user-" + userId);
        return user;
    }

    private Trip trip() {
        Trip trip = new Trip();
        trip.setTripId(50L);
        trip.setBusId(7L);
        trip.setRouteId(8L);
        trip.setSource("A");
        trip.setDestination("B");
        trip.setTravelDate(LocalDate.now().plusDays(2));
        trip.setDepartureTime(LocalTime.of(9, 0));
        trip.setArrivalTime(LocalTime.of(11, 0));
        trip.setFare(new BigDecimal("25.00"));
        return trip;
    }

    private Seat seat(Long seatId, Long busId) {
        Bus bus = new Bus();
        bus.setBusId(busId);
        Seat seat = new Seat();
        seat.setSeatId(seatId);
        seat.setSeatNumber(seatId.intValue());
        seat.setBus(bus);
        return seat;
    }
}
