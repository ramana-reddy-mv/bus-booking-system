package com.busbooking.trip;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface TripRepository extends JpaRepository<Trip, Long> {
    List<Trip> findBySourceIgnoreCaseAndDestinationIgnoreCaseAndTravelDate(
            String source, String destination, LocalDate travelDate);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select trip from Trip trip where trip.tripId = :tripId")
    Optional<Trip> findByIdForUpdate(@Param("tripId") Long tripId);
}
