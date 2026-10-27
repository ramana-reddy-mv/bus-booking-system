package com.example.ticketbooking.booking.service;

import com.example.ticketbooking.booking.entity.Booking;
import com.example.ticketbooking.booking.exception.BookingNotFoundException;
import com.example.ticketbooking.booking.mapper.BookingMapper;
import com.example.ticketbooking.booking.repository.BookingRepository;
import com.example.ticketbooking.shared.booking.BookingDTO;
import com.example.ticketbooking.shared.booking.CreateBookingDTO;
import com.example.ticketbooking.shared.booking.UpdateBookingDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final BookingMapper bookingMapper;

    @Autowired
    public BookingServiceImpl(BookingRepository bookingRepository, BookingMapper bookingMapper) {
        this.bookingRepository = bookingRepository;
        this.bookingMapper = bookingMapper;
    }

    @Override
    public BookingDTO createBooking(CreateBookingDTO createBookingDTO) {
        Booking booking = bookingMapper.toBookingEntity(createBookingDTO);
        Booking savedBooking = bookingRepository.save(booking);
        return bookingMapper.toBookingDTO(savedBooking);
    }

    @Override
    public Optional<BookingDTO> getBooking(Long bookingId) {
        return bookingRepository.findById(bookingId).map(bookingMapper::toBookingDTO);
    }

    @Override
    public List<BookingDTO> getAllBookinges() {
        return bookingRepository.findAll().stream().map(bookingMapper::toBookingDTO).toList();
    }

    @Override
    public BookingDTO updateBooking(Long bookingId, UpdateBookingDTO updateBookingDTO) {
        Booking existingBooking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException("booking not found with id " + bookingId));
        Optional.ofNullable(updateBookingDTO.getBookingStatus()).ifPresent(existingBooking::setBookingStatus);
        Optional.ofNullable(updateBookingDTO.getBookingDate()).ifPresent(existingBooking::setBookingDate);
        Optional.ofNullable(updateBookingDTO.getBusId()).ifPresent(existingBooking::setBusId);
        Optional.ofNullable(updateBookingDTO.getSeatId()).ifPresent(existingBooking::setSeatId);
        Optional.ofNullable(updateBookingDTO.getUserId()).ifPresent(existingBooking::setUserId);

        Booking savedBooking = bookingRepository.save(existingBooking);
        return bookingMapper.toBookingDTO(savedBooking);
    }

    @Override
    public void deleteBooking(Long bookingId) {
        bookingRepository.deleteById(bookingId);
    }
}
