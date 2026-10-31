package com.busbooking.booking.service;

import com.busbooking.booking.dto.SeatAvailabilityResponse;
import com.busbooking.booking.entity.Booking;
import com.busbooking.booking.entity.SeatAllocation;
import com.busbooking.booking.enums.BookingStatus;
import com.busbooking.booking.enums.SeatAllocationStatus;
import com.busbooking.booking.repository.SeatAllocationRepository;
import com.busbooking.bus.Bus;
import com.busbooking.seat.Seat;
import com.busbooking.seat.SeatRepository;
import com.busbooking.trip.Trip;
import com.busbooking.trip.TripRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class SeatAllocationServiceTest {
    private SeatAllocationRepository allocationRepository;
    private SeatRepository seatRepository;
    private TripRepository tripRepository;
    private SeatAllocationService service;

    @BeforeEach
    void setUp() {
        allocationRepository = mock(SeatAllocationRepository.class);
        seatRepository = mock(SeatRepository.class);
        tripRepository = mock(TripRepository.class);
        service = new SeatAllocationService(allocationRepository, seatRepository, tripRepository);
    }

    @Test
    void getSeatsInitializesAllocationsAndExpiresOldHolds() {
        Trip trip = trip();
        Seat seat = seat(11L, 7L);
        Booking booking = new Booking();
        booking.setBookingId(20L);
        booking.setStatus(BookingStatus.PENDING_PAYMENT);
        SeatAllocation allocation = allocation(trip, seat, booking, SeatAllocationStatus.HELD);
        allocation.setHoldExpiresAt(LocalDateTime.now().minusMinutes(1));
        when(tripRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(trip));
        when(seatRepository.findByBus_BusIdOrderBySeatNumber(7L)).thenReturn(List.of(seat));
        when(allocationRepository.findByTripIdForUpdate(50L)).thenReturn(List.of(allocation));

        List<SeatAvailabilityResponse> result = service.getAvailableSeats(50L);

        assertEquals(1, result.size());
        assertEquals(SeatAllocationStatus.AVAILABLE, result.getFirst().getStatus());
        assertEquals(BookingStatus.EXPIRED, booking.getStatus());
        assertNull(allocation.getBooking());
        verify(allocationRepository).saveAll(List.of(allocation));
    }

    @Test
    void rejectsASeatHeldByAnotherBooking() {
        Trip trip = trip();
        Seat seat = seat(11L, 7L);
        Booking owner = new Booking();
        owner.setBookingId(20L);
        SeatAllocation allocation = allocation(trip, seat, owner, SeatAllocationStatus.HELD);
        allocation.setHoldExpiresAt(LocalDateTime.now().plusMinutes(3));
        when(seatRepository.findByBus_BusIdOrderBySeatNumber(7L)).thenReturn(List.of(seat));
        when(allocationRepository.findByTripIdForUpdate(50L)).thenReturn(List.of(allocation));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> service.holdSeats(trip, List.of(seat), new Booking(), LocalDateTime.now()));

        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        assertEquals(20L, allocation.getBooking().getBookingId());
    }

    @Test
    void holdsAllRequestedSeatsForFiveMinutes() {
        Trip trip = trip();
        Seat firstSeat = seat(11L, 7L);
        Seat secondSeat = seat(12L, 7L);
        List<SeatAllocation> allocations = new ArrayList<>();
        when(seatRepository.findByBus_BusIdOrderBySeatNumber(7L))
                .thenReturn(List.of(firstSeat, secondSeat));
        when(allocationRepository.findByTripIdForUpdate(50L)).thenReturn(allocations);
        when(allocationRepository.saveAll(any())).thenAnswer(invocation -> {
            for (SeatAllocation allocation : invocation.<List<SeatAllocation>>getArgument(0)) {
                if (!allocations.contains(allocation)) {
                    allocations.add(allocation);
                }
            }
            return allocations;
        });
        Booking booking = new Booking();
        booking.setBookingId(30L);
        LocalDateTime createdAt = LocalDateTime.now();

        service.holdSeats(trip, List.of(firstSeat, secondSeat), booking, createdAt);

        assertEquals(2, allocations.size());
        assertEquals(SeatAllocationStatus.HELD, allocations.get(0).getStatus());
        assertEquals(SeatAllocationStatus.HELD, allocations.get(1).getStatus());
        assertEquals(booking, allocations.get(0).getBooking());
        assertEquals(createdAt.plusMinutes(5), allocations.get(0).getHoldExpiresAt());
        assertEquals(allocations.get(0).getHoldExpiresAt(), allocations.get(1).getHoldExpiresAt());
    }

    @Test
    void createsAvailableAllocationForEachBusSeat() {
        Trip trip = trip();
        Seat seat = seat(11L, 7L);
        List<SeatAllocation> allocations = new ArrayList<>();
        when(tripRepository.findByIdForUpdate(50L)).thenReturn(Optional.of(trip));
        when(seatRepository.findByBus_BusIdOrderBySeatNumber(7L)).thenReturn(List.of(seat));
        when(allocationRepository.findByTripIdForUpdate(50L)).thenReturn(allocations);
        when(allocationRepository.saveAll(any())).thenAnswer(invocation -> {
            allocations.addAll(invocation.getArgument(0));
            return allocations;
        });

        List<SeatAvailabilityResponse> result = service.getAvailableSeats(50L);

        assertEquals(SeatAllocationStatus.AVAILABLE, result.getFirst().getStatus());
        verify(allocationRepository, atLeastOnce()).saveAll(any());
    }

    private Trip trip() {
        Trip trip = new Trip();
        trip.setTripId(50L);
        trip.setBusId(7L);
        return trip;
    }

    private Seat seat(Long id, Long busId) {
        Bus bus = new Bus();
        bus.setBusId(busId);
        Seat seat = new Seat();
        seat.setSeatId(id);
        seat.setSeatNumber(id.intValue());
        seat.setBus(bus);
        return seat;
    }

    private SeatAllocation allocation(
            Trip trip, Seat seat, Booking booking, SeatAllocationStatus status) {
        SeatAllocation allocation = new SeatAllocation();
        allocation.setTrip(trip);
        allocation.setSeat(seat);
        allocation.setBooking(booking);
        allocation.setStatus(status);
        return allocation;
    }
}
