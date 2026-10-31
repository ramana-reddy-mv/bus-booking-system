package com.busbooking.booking.service;

import com.busbooking.booking.dto.SeatAvailabilityResponse;
import com.busbooking.booking.BookingHoldExpiredException;
import com.busbooking.booking.entity.Booking;
import com.busbooking.booking.entity.SeatAllocation;
import com.busbooking.booking.enums.BookingStatus;
import com.busbooking.booking.enums.SeatAllocationStatus;
import com.busbooking.booking.repository.SeatAllocationRepository;
import com.busbooking.seat.Seat;
import com.busbooking.seat.SeatRepository;
import com.busbooking.trip.Trip;
import com.busbooking.trip.TripRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class SeatAllocationService {
    private final SeatAllocationRepository allocationRepository;
    private final SeatRepository seatRepository;
    private final TripRepository tripRepository;

    public SeatAllocationService(
            SeatAllocationRepository allocationRepository,
            SeatRepository seatRepository,
            TripRepository tripRepository) {
        this.allocationRepository = allocationRepository;
        this.seatRepository = seatRepository;
        this.tripRepository = tripRepository;
    }

    @Transactional
    public List<SeatAvailabilityResponse> getAvailableSeats(Long tripId) {
        Trip trip = lockTrip(tripId);
        List<Seat> seats = initializeAllocations(trip);
        Map<Long, SeatAllocation> allocations = allocationsBySeat(trip.getTripId());
        return seats.stream()
                .map(seat -> new SeatAvailabilityResponse(
                        seat.getSeatId(),
                        seat.getSeatNumber(),
                        allocations.get(seat.getSeatId()).getStatus()))
                .toList();
    }

    @Transactional
    public void holdSeats(Trip trip, List<Seat> seats, Booking booking, LocalDateTime now) {
        initializeAllocations(trip);
        Map<Long, SeatAllocation> allocations = allocationsBySeat(trip.getTripId());
        for (Seat seat : seats) {
            SeatAllocation allocation = allocations.get(seat.getSeatId());
            if (allocation == null || allocation.getStatus() != SeatAllocationStatus.AVAILABLE) {
                throw new ResponseStatusException(HttpStatus.CONFLICT,
                        "Seat " + seat.getSeatNumber() + " is unavailable");
            }
        }

        LocalDateTime expiration = now.plusMinutes(5);
        for (Seat seat : seats) {
            SeatAllocation allocation = allocations.get(seat.getSeatId());
            allocation.setBooking(booking);
            allocation.setStatus(SeatAllocationStatus.HELD);
            allocation.setHoldExpiresAt(expiration);
        }
        allocationRepository.saveAll(seats.stream()
                .map(seat -> allocations.get(seat.getSeatId()))
                .toList());
    }

    @Transactional(noRollbackFor = BookingHoldExpiredException.class)
    public void confirmBooking(Booking booking, LocalDateTime now) {
        Map<Long, SeatAllocation> allocations = allocationsBySeat(booking.getSeats().getFirst().getTrip().getTripId());
        List<SeatAllocation> bookingAllocations = allocations.values().stream()
                .filter(allocation -> allocation.getBooking() != null
                        && Objects.equals(allocation.getBooking().getBookingId(), booking.getBookingId()))
                .toList();
        if (bookingAllocations.size() != booking.getSeats().size()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Booking seat holds are no longer available");
        }
        if (bookingAllocations.stream().anyMatch(allocation ->
                allocation.getStatus() != SeatAllocationStatus.HELD
                        || allocation.getHoldExpiresAt() == null
                        || !allocation.getHoldExpiresAt().isAfter(now))) {
            expireAllocations(bookingAllocations, now);
            booking.setStatus(BookingStatus.EXPIRED);
            throw new com.busbooking.booking.BookingHoldExpiredException("The booking seat hold has expired");
        }
        for (SeatAllocation allocation : bookingAllocations) {
            allocation.setStatus(SeatAllocationStatus.BOOKED);
            allocation.setHoldExpiresAt(null);
        }
        allocationRepository.saveAll(bookingAllocations);
    }

    @Transactional
    public void releaseBookingSeats(Booking booking) {
        Long tripId = booking.getSeats().getFirst().getTrip().getTripId();
        List<SeatAllocation> allocations = allocationRepository.findByTripIdForUpdate(tripId);
        List<SeatAllocation> released = new ArrayList<>();
        for (SeatAllocation allocation : allocations) {
            if (allocation.getBooking() != null
                    && Objects.equals(allocation.getBooking().getBookingId(), booking.getBookingId())) {
                allocation.setBooking(null);
                allocation.setHoldExpiresAt(null);
                allocation.setStatus(SeatAllocationStatus.AVAILABLE);
                released.add(allocation);
            }
        }
        allocationRepository.saveAll(released);
    }

    private Trip lockTrip(Long tripId) {
        return tripRepository.findByIdForUpdate(tripId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Trip not found with ID " + tripId));
    }

    private List<Seat> initializeAllocations(Trip trip) {
        List<Seat> seats = seatRepository.findByBus_BusIdOrderBySeatNumber(trip.getBusId());
        List<SeatAllocation> allocations = allocationRepository.findByTripIdForUpdate(trip.getTripId());
        Map<Long, SeatAllocation> bySeat = new HashMap<>();
        allocations.forEach(allocation -> bySeat.put(allocation.getSeat().getSeatId(), allocation));

        List<SeatAllocation> created = new ArrayList<>();
        for (Seat seat : seats) {
            if (!bySeat.containsKey(seat.getSeatId())) {
                SeatAllocation allocation = new SeatAllocation();
                allocation.setTrip(trip);
                allocation.setSeat(seat);
                allocation.setStatus(SeatAllocationStatus.AVAILABLE);
                bySeat.put(seat.getSeatId(), allocation);
                created.add(allocation);
            }
        }
        if (!created.isEmpty()) {
            allocationRepository.saveAll(created);
        }

        expireAllocations(allocations, LocalDateTime.now());
        return seats;
    }

    private Map<Long, SeatAllocation> allocationsBySeat(Long tripId) {
        List<SeatAllocation> allocations = allocationRepository.findByTripIdForUpdate(tripId);
        Map<Long, SeatAllocation> bySeat = new HashMap<>();
        allocations.forEach(allocation -> bySeat.put(allocation.getSeat().getSeatId(), allocation));
        return bySeat;
    }

    private void expireAllocations(List<SeatAllocation> allocations, LocalDateTime now) {
        List<SeatAllocation> expired = new ArrayList<>();
        for (SeatAllocation allocation : allocations) {
            if (allocation.getStatus() == SeatAllocationStatus.HELD
                    && allocation.getHoldExpiresAt() != null
                    && !allocation.getHoldExpiresAt().isAfter(now)) {
                Booking booking = allocation.getBooking();
                allocation.setBooking(null);
                allocation.setHoldExpiresAt(null);
                allocation.setStatus(SeatAllocationStatus.AVAILABLE);
                if (booking != null && booking.getStatus() == BookingStatus.PENDING_PAYMENT) {
                    booking.setStatus(BookingStatus.EXPIRED);
                }
                expired.add(allocation);
            }
        }
        allocationRepository.saveAll(expired);
    }
}
