package com.example.ticketbooking.route.service;

import com.example.ticketbooking.route.entity.Route;
import com.example.ticketbooking.route.exception.RouteNotFoundException;
import com.example.ticketbooking.route.mapper.RouteMapper;
import com.example.ticketbooking.route.repository.RouteRepository;
import com.example.ticketbooking.shared.route.CreateRouteDTO;
import com.example.ticketbooking.shared.route.RouteDTO;
import com.example.ticketbooking.shared.route.UpdateRouteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RouteServiceImpl implements RouteService {

    private final RouteRepository routeRepository;
    private final RouteMapper routeMapper;

    @Autowired
    public RouteServiceImpl(RouteRepository routeRepository, RouteMapper routeMapper) {
        this.routeRepository = routeRepository;
        this.routeMapper = routeMapper;
    }


    @Override
    public RouteDTO createRoute(CreateRouteDTO createRouteDTO) {
        Route route = routeMapper.toRouteEntity(createRouteDTO);

        Route savedRoute = routeRepository.save(route);

        return routeMapper.toRouteDTO(savedRoute);
    }

    @Override
    public Optional<RouteDTO> getRoute(Long routeId) {
        return routeRepository.findById(routeId).map(routeMapper::toRouteDTO);
    }

    @Override
    public List<RouteDTO> getAllRoutees() {
        return routeRepository.findAll().stream().map(routeMapper::toRouteDTO).toList();
    }

    @Override
    public RouteDTO updateRoute(Long routeId, UpdateRouteDTO updateRouteDTO) {
        Route existingUser = routeRepository.findById(routeId)
                        .orElseThrow(() -> new RouteNotFoundException("route not found with id "+routeId));
        Optional.ofNullable(updateRouteDTO.getDistance()).ifPresent(existingUser::setDistance);
        Optional.ofNullable(updateRouteDTO.getSource()).ifPresent(existingUser::setSource);
        Optional.ofNullable(updateRouteDTO.getBusId()).ifPresent(existingUser::setBusId);
        Optional.ofNullable(updateRouteDTO.getDestination()).ifPresent(existingUser::setDestination);

        return routeMapper.toRouteDTO(existingUser);
    }

    @Override
    public void deleteRoute(Long routeId) {
        routeRepository.deleteById(routeId);
    }
}
