package com.busbooking.booking;

import com.busbooking.booking.dto.BookingDTO;
import com.busbooking.booking.dto.CreateBookingDTO;
import com.busbooking.booking.dto.UpdateBookingDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface BookingService {

    BookingDTO createBooking(CreateBookingDTO createBookingDTO);

    Optional<BookingDTO> getBooking(Long bookingId);

    List<BookingDTO> getAllBookinges();

    BookingDTO updateBooking(Long bookingId, UpdateBookingDTO updateBookingDTO);

    void deleteBooking(Long bookingId);
}
