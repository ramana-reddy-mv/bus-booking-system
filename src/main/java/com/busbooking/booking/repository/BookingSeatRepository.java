package com.busbooking.booking.repository;

import com.busbooking.booking.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    List<BookingSeat> findByBooking_BookingId(Long bookingId);
}
