package com.busbooking.bus;

import com.busbooking.bus.dto.BusDTO;
import com.busbooking.bus.dto.CreateBusDTO;
import com.busbooking.bus.dto.UpdateBusDTO;
import com.busbooking.route.RouteNotFoundException;
import com.busbooking.route.RouteService;
import com.busbooking.route.dto.RouteDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;
    private final RouteService routeService;

    public BusServiceImpl(BusRepository busRepository, BusMapper busMapper, RouteService routeService) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
        this.routeService = routeService;
    }

    @Override
    public BusDTO createBus(CreateBusDTO createBusDTO) {
        RouteDTO routeDTO = routeService.getRoute(createBusDTO.getRouteId())
                .orElseThrow(() -> new RouteNotFoundException(
                        "route not found with id " + createBusDTO.getRouteId()));
        Bus bus = busMapper.toBusEntity(createBusDTO);
        Bus savedBus = busRepository.save(bus);
        BusDTO busDTO = busMapper.toBusDTO(savedBus);
        busDTO.setRouteDetails(routeDTO);
        return busDTO;
    }

    @Override
    public Optional<BusDTO> getBus(Long busId) {
        return busRepository.findById(busId).map(busMapper::toBusDTO);
    }

    @Override
    public List<BusDTO> getAllBuses() {
        return busRepository.findAll().stream().map(busMapper::toBusDTO).toList();
    }

    @Override
    public BusDTO updateBus(Long busId, UpdateBusDTO updateBusDTO) {
        Bus existingUser = busRepository.findById(busId)
                        .orElseThrow(() -> new BusNotFoundException("bus not found with id "+busId));
        Optional.ofNullable(updateBusDTO.getBusType()).ifPresent(existingUser::setBusType);
        Optional.ofNullable(updateBusDTO.getBusNumber()).ifPresent(existingUser::setBusNumber);
        Optional.ofNullable(updateBusDTO.getTotalSeats()).ifPresent(existingUser::setTotalSeats);
        Optional.ofNullable(updateBusDTO.getRouteId()).ifPresent(existingUser::setRouteId);

        return busMapper.toBusDTO(existingUser);
    }

    @Override
    public void deleteBus(Long busId) {
        busRepository.deleteById(busId);
    }
}
