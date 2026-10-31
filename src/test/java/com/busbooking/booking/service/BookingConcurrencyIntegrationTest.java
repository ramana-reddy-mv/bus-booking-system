package com.busbooking.booking.service;

import com.busbooking.booking.dto.BookingRequest;
import com.busbooking.booking.entity.Booking;
import com.busbooking.booking.repository.BookingRepository;
import com.busbooking.booking.repository.SeatAllocationRepository;
import com.busbooking.bus.Bus;
import com.busbooking.bus.BusRepository;
import com.busbooking.bus.BusType;
import com.busbooking.seat.DeckType;
import com.busbooking.seat.Seat;
import com.busbooking.seat.SeatRepository;
import com.busbooking.seat.SeatType;
import com.busbooking.trip.Trip;
import com.busbooking.trip.TripRepository;
import com.busbooking.user.Role;
import com.busbooking.user.User;
import com.busbooking.user.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BookingConcurrencyIntegrationTest {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BusRepository busRepository;
    @Autowired
    private TripRepository tripRepository;
    @Autowired
    private SeatRepository seatRepository;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private SeatAllocationRepository allocationRepository;

    private User firstUser;
    private User secondUser;
    private Bus bus;
    private Trip trip;
    private Seat seat;

    @BeforeEach
    void createFixtures() {
        String unique = UUID.randomUUID().toString();
        firstUser = userRepository.save(user("first-" + unique));
        secondUser = userRepository.save(user("second-" + unique));

        bus = new Bus();
        bus.setBusNumber("B-" + unique);
        bus.setBusType(BusType.AC);
        bus.setTotalSeats(1);
        bus.setOperator(firstUser);
        bus = busRepository.save(bus);

        trip = new Trip();
        trip.setBusId(bus.getBusId());
        trip.setRouteId(1L);
        trip.setSource("A");
        trip.setDestination("B");
        trip.setTravelDate(LocalDate.now().plusDays(2));
        trip.setDepartureTime(LocalTime.of(9, 0));
        trip.setArrivalTime(LocalTime.of(11, 0));
        trip.setFare(new BigDecimal("25.00"));
        trip = tripRepository.save(trip);

        seat = new Seat();
        seat.setSeatNumber(Math.abs(unique.hashCode()) + 1);
        seat.setSeatType(SeatType.SEATER);
        seat.setDeckType(DeckType.LOWER);
        seat.setWindowSeat(false);
        seat.setBus(bus);
        seat = seatRepository.save(seat);
    }

    @AfterEach
    void removeFixtures() {
        if (trip != null && trip.getTripId() != null) {
            allocationRepository.deleteAllByTrip_TripId(trip.getTripId());
        }
        if (firstUser != null && firstUser.getUserId() != null) {
            bookingRepository.deleteAllByUser_UserId(firstUser.getUserId());
        }
        if (secondUser != null && secondUser.getUserId() != null) {
            bookingRepository.deleteAllByUser_UserId(secondUser.getUserId());
        }
        if (seat != null && seat.getSeatId() != null) {
            seatRepository.delete(seat);
        }
        if (trip != null && trip.getTripId() != null) {
            tripRepository.delete(trip);
        }
        if (bus != null && bus.getBusId() != null) {
            busRepository.delete(bus);
        }
        if (firstUser != null && firstUser.getUserId() != null) {
            userRepository.delete(firstUser);
        }
        if (secondUser != null && secondUser.getUserId() != null) {
            userRepository.delete(secondUser);
        }
    }

    @Test
    void concurrentCustomersCannotHoldTheSameTripSeat() throws Exception {
        CountDownLatch ready = new CountDownLatch(2);
        CountDownLatch start = new CountDownLatch(1);
        var executor = Executors.newFixedThreadPool(2);
        try {
            Future<Boolean> first = executor.submit(() -> tryToBook(firstUser, ready, start));
            Future<Boolean> second = executor.submit(() -> tryToBook(secondUser, ready, start));
            ready.await();
            start.countDown();

            List<Boolean> results = List.of(first.get(), second.get());
            assertEquals(1, results.stream().filter(Boolean::booleanValue).count());
            assertEquals(1, results.stream().filter(result -> !result).count());
        } finally {
            executor.shutdownNow();
        }
    }

    private boolean tryToBook(User user, CountDownLatch ready, CountDownLatch start) throws Exception {
        ready.countDown();
        start.await();
        try {
            bookingService.createBooking(new BookingRequest(user.getUserId(), trip.getTripId(),
                    List.of(seat.getSeatId())));
            return true;
        } catch (ResponseStatusException exception) {
            if (exception.getStatusCode() == HttpStatus.CONFLICT) {
                return false;
            }
            throw exception;
        }
    }

    private User user(String username) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(username + "@example.test");
        user.setPassword("test-only");
        user.setFirstName("Test");
        user.setLastName("Customer");
        user.setPhoneNumber("0000000000");
        user.setRole(Role.USER);
        return user;
    }
}
