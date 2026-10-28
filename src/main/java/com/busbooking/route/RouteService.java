package com.busbooking.route;

import com.busbooking.route.dto.CreateRouteDTO;
import com.busbooking.route.dto.RouteDTO;
import com.busbooking.route.dto.UpdateRouteDTO;
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
