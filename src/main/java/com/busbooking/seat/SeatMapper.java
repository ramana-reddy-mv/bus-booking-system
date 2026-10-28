package com.busbooking.seat;

import com.busbooking.seat.dto.CreateSeatDTO;
import com.busbooking.seat.dto.SeatDTO;
import com.busbooking.seat.dto.UpdateSeatDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SeatMapper {

    @Mapping(source = "seatId", target = "seatId")
    SeatDTO toSeatDTO(Seat seat);

    Seat toSeatEntity(CreateSeatDTO createSeatDTO);

    Seat toSeatEntity(UpdateSeatDTO updateSeatDTO);
}
