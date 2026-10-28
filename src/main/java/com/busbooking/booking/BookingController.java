package com.busbooking.booking;

import com.busbooking.booking.BookingNotFoundException;
import com.busbooking.booking.BookingService;
import com.busbooking.booking.dto.BookingDTO;
import com.busbooking.booking.dto.CreateBookingDTO;
import com.busbooking.booking.dto.UpdateBookingDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping("/createBooking")
    public ResponseEntity<BookingDTO> createBooking(@RequestBody CreateBookingDTO createBookingDTO) {
        BookingDTO createdBooking = bookingService.createBooking(createBookingDTO);
        return new ResponseEntity<>(createdBooking, HttpStatus.CREATED);
    }

    @GetMapping("/getBooking/{bookingId}")
    public ResponseEntity<BookingDTO> getBooking(@PathVariable Long bookingId) {
        BookingDTO bookingDTO = bookingService.getBooking(bookingId).orElseThrow(() ->
                new BookingNotFoundException("Booking not found with ID " + bookingId));
        return new ResponseEntity<>(bookingDTO, HttpStatus.OK);
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO> updateBooking(@PathVariable Long bookingId, @RequestBody UpdateBookingDTO updateBookingDTO) {
        BookingDTO bookingDTO = bookingService.updateBooking(bookingId, updateBookingDTO);
        return new ResponseEntity<>(bookingDTO, HttpStatus.OK);
    }

    @DeleteMapping("/deleteBooking/{bookingId}")
    public ResponseEntity<BookingDTO> deleteBooking(@PathVariable Long bookingId) {
        bookingService.deleteBooking(bookingId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping("/getAllBookinges")
    public ResponseEntity<List<BookingDTO>> getAllBooking() {
        List<BookingDTO> bookingDTOS = bookingService.getAllBookinges();
        return new ResponseEntity<>(bookingDTOS, HttpStatus.OK);
    }
}
