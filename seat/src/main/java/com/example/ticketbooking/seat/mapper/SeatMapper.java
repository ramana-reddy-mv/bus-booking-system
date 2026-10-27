package com.example.ticketbooking.seat.mapper;

import com.example.ticketbooking.seat.entity.Seat;
import com.example.ticketbooking.shared.seat.CreateSeatDTO;
import com.example.ticketbooking.shared.seat.SeatDTO;
import com.example.ticketbooking.shared.seat.UpdateSeatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(source = "seatId", target = "seatId")
    SeatDTO toSeatDTO(Seat seat);

    Seat toSeatEntity(CreateSeatDTO createSeatDTO);

    Seat toSeatEntity(UpdateSeatDTO updateSeatDTO);
}
