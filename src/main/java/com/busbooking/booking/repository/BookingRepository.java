package com.busbooking.booking.repository;

import com.busbooking.booking.entity.Booking;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select booking from Booking booking where booking.bookingId = :bookingId")
    Optional<Booking> findByIdForUpdate(@Param("bookingId") Long bookingId);

    @EntityGraph(attributePaths = {"user", "seats", "seats.trip", "seats.seat"})
    @Query("select distinct booking from Booking booking where booking.bookingId = :bookingId")
    Optional<Booking> findWithDetailsById(@Param("bookingId") Long bookingId);

    @EntityGraph(attributePaths = {"seats", "seats.trip", "seats.seat"})
    List<Booking> findByUser_UserIdOrderByCreatedAtDesc(Long userId);

    @Transactional
    void deleteAllByUser_UserId(Long userId);
}
