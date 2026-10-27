package com.example.ticketbooking.route.mapper;

import com.example.ticketbooking.route.entity.Route;
import com.example.ticketbooking.shared.route.CreateRouteDTO;
import com.example.ticketbooking.shared.route.RouteDTO;
import com.example.ticketbooking.shared.route.UpdateRouteDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RouteMapper {

    @Mapping(source = "routeId", target = "routeId")
    RouteDTO toRouteDTO(Route route);

    Route toRouteEntity(CreateRouteDTO createRouteDTO);

    Route toRouteEntity(UpdateRouteDTO updateRouteDTO);
}
