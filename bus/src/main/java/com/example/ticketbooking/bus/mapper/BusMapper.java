package com.example.ticketbooking.bus.mapper;

import com.example.ticketbooking.bus.entity.Bus;
import com.example.ticketbooking.shared.bus.BusDTO;
import com.example.ticketbooking.shared.bus.CreateBusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(source = "busId", target = "busId")
    BusDTO toBusDTO(Bus bus);

    Bus toBusEntity(CreateBusDTO createBusDTO);
}
