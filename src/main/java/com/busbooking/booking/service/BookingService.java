package com.busbooking.booking.service;

import com.busbooking.booking.dto.BookingRequest;
import com.busbooking.booking.dto.BookingResponse;
import com.busbooking.booking.dto.PaymentRequest;

import java.util.List;

public interface BookingService {
    BookingResponse createBooking(BookingRequest request);

    BookingResponse processPayment(Long bookingId, Long userId, PaymentRequest request);

    BookingResponse cancelBooking(Long bookingId, Long userId);

    BookingResponse getBooking(Long bookingId, Long userId);

    List<BookingResponse> getMyBookings(Long userId);
}
