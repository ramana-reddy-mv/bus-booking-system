package com.busbooking.bus;

import com.busbooking.bus.dto.BusDTO;
import com.busbooking.bus.dto.CreateBusDTO;
import com.busbooking.bus.dto.UpdateBusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @PostMapping
    public ResponseEntity<BusDTO> createBus(@RequestBody CreateBusDTO createBusDTO) {
        BusDTO createdBus = busService.createBus(createBusDTO);
        return new ResponseEntity<>(createdBus, HttpStatus.CREATED);
    }

    @GetMapping("/{busId}")
    public ResponseEntity<BusDTO> getBus(@PathVariable Long busId) {
        BusDTO busDTO = busService.getBus(busId).orElseThrow(() ->
                new BusNotFoundException("Bus not found with ID " + busId));
        return new ResponseEntity<>(busDTO, HttpStatus.OK);
    }

    @PutMapping("/{busId}")
    public ResponseEntity<BusDTO> updateBus(@PathVariable Long busId, @RequestBody UpdateBusDTO updateBusDTO) {
        BusDTO busDTO = busService.updateBus(busId, updateBusDTO);
        return new ResponseEntity<>(busDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {
        busService.deleteBus(busId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<BusDTO>> getAllBuses() {
        List<BusDTO> busDTOS = busService.getAllBuses();
        return new ResponseEntity<>(busDTOS, HttpStatus.OK);
    }
}
