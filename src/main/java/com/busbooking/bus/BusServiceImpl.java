package com.busbooking.bus;

import com.busbooking.bus.dto.BusDTO;
import com.busbooking.bus.dto.CreateBusDTO;
import com.busbooking.bus.dto.UpdateBusDTO;
import com.busbooking.route.RouteNotFoundException;
import com.busbooking.route.RouteService;
import com.busbooking.route.dto.RouteDTO;
import com.busbooking.seat.DeckType;
import com.busbooking.seat.Seat;
import com.busbooking.seat.SeatRepository;
import com.busbooking.seat.SeatType;
import com.busbooking.user.User;
import com.busbooking.user.UserNotFoundException;
import com.busbooking.user.UserRepository;
import com.busbooking.user.UserService;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class BusServiceImpl implements BusService {

    private final BusRepository busRepository;
    private final BusMapper busMapper;
    private final RouteService routeService;
    private final UserService userService;
    private final UserRepository userRepository;
    private final SeatRepository seatRepository;

    public BusServiceImpl(BusRepository busRepository, BusMapper busMapper, RouteService routeService, UserService userService, UserRepository userRepository, SeatRepository seatRepository) {
        this.busRepository = busRepository;
        this.busMapper = busMapper;
        this.routeService = routeService;
        this.userService = userService;
        this.userRepository = userRepository;
        this.seatRepository = seatRepository;
    }

    @Transactional
    public BusDTO createBus(CreateBusDTO request) {

        User operator = userRepository.findById(request.getOperatorId())
                .orElseThrow(() -> new RuntimeException("Operator not found"));

        Bus bus = busMapper.toBusEntity(request);
        bus.setOperator(operator);

        Bus savedBus = busRepository.save(bus);

        List<Seat> seats = new ArrayList<>();

        for (int i = 1; i <= request.getTotalSeats(); i++) {
            Seat seat = new Seat();
            seat.setBus(savedBus);
            seat.setSeatNumber( i);
            seat.setSeatType(SeatType.SEATER);
            seat.setWindowSeat(false);
            seat.setDeckType(DeckType.LOWER);

            seats.add(seat);
        }

        seatRepository.saveAll(seats);

        return busMapper.toBusDTO(savedBus);
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
        User operator = userRepository.findById(updateBusDTO.getOperatorId())
                .orElseThrow(() -> new UserNotFoundException("Operator not found with ID " + updateBusDTO.getOperatorId()));

        Optional.ofNullable(updateBusDTO.getOperatorId()).ifPresent(id -> existingUser.setOperator(operator));

        return busMapper.toBusDTO(existingUser);
    }

    @Override
    public void deleteBus(Long busId) {
        busRepository.deleteById(busId);
    }
}
