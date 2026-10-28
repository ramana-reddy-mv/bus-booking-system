package com.busbooking.seat;

import com.busbooking.seat.dto.CreateSeatDTO;
import com.busbooking.seat.dto.SeatDTO;
import com.busbooking.seat.dto.UpdateSeatDTO;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public interface SeatService {

    SeatDTO createSeat(CreateSeatDTO createSeatDTO);

    Optional<SeatDTO> getSeat(Long seatId);

    List<SeatDTO> getAllSeates();

    SeatDTO updateSeat(Long seatId, UpdateSeatDTO updateSeatDTO);

    void deleteSeat(Long seatId);
}
