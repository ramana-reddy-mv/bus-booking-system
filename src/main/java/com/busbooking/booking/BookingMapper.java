package com.busbooking.booking;

import com.busbooking.booking.Booking;
import com.busbooking.booking.dto.BookingDTO;
import com.busbooking.booking.dto.CreateBookingDTO;
import com.busbooking.booking.dto.UpdateBookingDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface BookingMapper {

    @Mapping(source = "bookingId", target = "bookingId")
    BookingDTO toBookingDTO(Booking booking);

    @Mapping(source = "bookingId", target = "bookingId")
    Booking toBookingEntity(BookingDTO bookingDTO);

    Booking toBookingEntity(CreateBookingDTO createBookingDTO);

    @Mapping(source = "bookingId", target = "bookingId")
    Booking toBookingEntity(UpdateBookingDTO updateBookingDTO);
}
