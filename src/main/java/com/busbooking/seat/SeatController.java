package com.busbooking.seat;

import com.busbooking.seat.dto.CreateSeatDTO;
import com.busbooking.seat.dto.SeatDTO;
import com.busbooking.seat.dto.UpdateSeatDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/seats")
public class SeatController {

    private final SeatService seatService;

    public SeatController(SeatService seatService) {
        this.seatService = seatService;
    }

    @PostMapping
    public ResponseEntity<SeatDTO> createSeat(@RequestBody CreateSeatDTO createSeatDTO) {
        SeatDTO createdSeat = seatService.createSeat(createSeatDTO);
        return new ResponseEntity<>(createdSeat, HttpStatus.CREATED);
    }

    @GetMapping("/{seatId}")
    public ResponseEntity<SeatDTO> getSeat(@PathVariable Long seatId) {
        SeatDTO seatDTO = seatService.getSeat(seatId).orElseThrow(() ->
                new SeatNotFoundException("Seat not found with ID " + seatId));
        return new ResponseEntity<>(seatDTO, HttpStatus.OK);
    }

    @PutMapping("/{seatId}")
    public ResponseEntity<SeatDTO> updateSeat(@PathVariable Long seatId, @RequestBody UpdateSeatDTO updateSeatDTO) {
        SeatDTO seatDTO = seatService.updateSeat(seatId, updateSeatDTO);
        return new ResponseEntity<>(seatDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{seatId}")
    public ResponseEntity<Void> deleteSeat(@PathVariable Long seatId) {
        seatService.deleteSeat(seatId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<SeatDTO>> getAllSeats() {
        List<SeatDTO> seatDTOS = seatService.getAllSeates();
        return new ResponseEntity<>(seatDTOS, HttpStatus.OK);
    }
}
