package com.example.ticketbooking.seat.service;

import com.example.ticketbooking.shared.seat.CreateSeatDTO;
import com.example.ticketbooking.shared.seat.SeatDTO;
import com.example.ticketbooking.shared.seat.UpdateSeatDTO;
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
