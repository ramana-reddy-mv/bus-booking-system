package com.busbooking.bus;

import com.busbooking.bus.dto.BusDTO;
import com.busbooking.bus.dto.CreateBusDTO;
import com.busbooking.bus.dto.UpdateBusDTO;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public interface BusService {

    BusDTO createBus(CreateBusDTO createBusDTO);

    Optional<BusDTO> getBus(Long busId);

    List<BusDTO> getAllBuses();

    BusDTO updateBus(Long busId, UpdateBusDTO updateBusDTO);

    void deleteBus(Long busId);
}
