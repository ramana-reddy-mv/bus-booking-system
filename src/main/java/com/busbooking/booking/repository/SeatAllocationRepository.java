package com.busbooking.booking.repository;

import com.busbooking.booking.entity.SeatAllocation;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SeatAllocationRepository extends JpaRepository<SeatAllocation, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select allocation from SeatAllocation allocation "
            + "where allocation.trip.tripId = :tripId order by allocation.seat.seatId")
    List<SeatAllocation> findByTripIdForUpdate(@Param("tripId") Long tripId);

    @Transactional
    void deleteAllByTrip_TripId(Long tripId);
}
