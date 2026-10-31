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

    @Mapping(source = "busId", target = "bus.busId")
    Seat toSeatEntity(CreateSeatDTO createSeatDTO);

    @Mapping(source = "busId", target = "bus.busId")
    Seat toSeatEntity(UpdateSeatDTO updateSeatDTO);
}
