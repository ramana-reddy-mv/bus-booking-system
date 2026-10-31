package com.busbooking.bus;

import com.busbooking.bus.dto.BusDTO;
import com.busbooking.bus.dto.CreateBusDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface BusMapper {

    @Mapping(source = "busId", target = "busId")
    @Mapping(source = "operator.userId", target = "operatorId")
    BusDTO toBusDTO(Bus bus);

    Bus toBusEntity(CreateBusDTO createBusDTO);
}
