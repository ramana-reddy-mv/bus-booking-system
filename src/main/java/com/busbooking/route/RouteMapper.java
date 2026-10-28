package com.busbooking.route;

import com.busbooking.route.dto.CreateRouteDTO;
import com.busbooking.route.dto.RouteDTO;
import com.busbooking.route.dto.UpdateRouteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(source = "routeId", target = "routeId")
    RouteDTO toRouteDTO(Route route);

    Route toRouteEntity(CreateRouteDTO createRouteDTO);

    Route toRouteEntity(UpdateRouteDTO updateRouteDTO);
}
