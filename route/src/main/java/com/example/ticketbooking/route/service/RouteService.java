package com.example.ticketbooking.route.service;

import com.example.ticketbooking.shared.route.CreateRouteDTO;
import com.example.ticketbooking.shared.route.RouteDTO;
import com.example.ticketbooking.shared.route.UpdateRouteDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface RouteService {

    RouteDTO createRoute(CreateRouteDTO createRouteDTO);

    Optional<RouteDTO> getRoute(Long routeId);

    List<RouteDTO> getAllRoutees();

    RouteDTO updateRoute(Long routeId, UpdateRouteDTO updateRouteDTO);

    void deleteRoute(Long routeId);
}
