package com.example.ticketbooking.bus.service;

import com.example.ticketbooking.bus.entity.Bus;
import com.example.ticketbooking.bus.exception.BusNotFoundException;
import com.example.ticketbooking.bus.mapper.BusMapper;
import com.example.ticketbooking.bus.repository.BusRepository;
import com.example.ticketbooking.shared.bus.BusDTO;
import com.example.ticketbooking.shared.bus.CreateBusDTO;
import com.example.ticketbooking.shared.bus.UpdateBusDTO;
import com.example.ticketbooking.shared.route.RouteDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;
    private final WebClient webClient;

    @Autowired
    public BusServiceImpl(BusRepository busRepository, BusMapper busMapper, WebClient webClient) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
        this.webClient = webClient;
    }


    @Override
    public Mono<BusDTO> createBus(CreateBusDTO createBusDTO) {
        Bus bus = busMapper.toBusEntity(createBusDTO);

        return Mono.fromCallable(() -> busRepository.save(bus))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(savedBus ->
                        webClient.get()
                                .uri("/routes/"+savedBus.getRouteId())
                                .retrieve()
                                .bodyToMono(RouteDTO.class)
                                .map(routeDTO -> {
                                    BusDTO busDTO = busMapper.toBusDTO(savedBus);
                                    busDTO.setRouteDetails(routeDTO);
                                    return busDTO;
                                })
                );
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
