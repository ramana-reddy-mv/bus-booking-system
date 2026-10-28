package com.busbooking.seat;

import com.busbooking.seat.dto.CreateSeatDTO;
import com.busbooking.seat.dto.SeatDTO;
import com.busbooking.seat.dto.UpdateSeatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SeatServiceImpl implements SeatService {

    private final SeatRepository seatRepository;
    private final SeatMapper seatMapper;

    @Autowired
    public SeatServiceImpl(SeatRepository seatRepository, SeatMapper seatMapper) {
        this.seatRepository = seatRepository;
        this.seatMapper = seatMapper;
    }


    @Override
    public SeatDTO createSeat(CreateSeatDTO createSeatDTO) {
        Seat seat = seatMapper.toSeatEntity(createSeatDTO);

        Seat savedSeat = seatRepository.save(seat);

        return seatMapper.toSeatDTO(savedSeat);
    }

    @Override
    public Optional<SeatDTO> getSeat(Long seatId) {
        return seatRepository.findById(seatId).map(seatMapper::toSeatDTO);
    }

    @Override
    public List<SeatDTO> getAllSeates() {
        return seatRepository.findAll().stream().map(seatMapper::toSeatDTO).toList();
    }

    @Override
    public SeatDTO updateSeat(Long seatId, UpdateSeatDTO updateSeatDTO) {
        Seat existingUser = seatRepository.findById(seatId)
                        .orElseThrow(() -> new SeatNotFoundException("seat not found with id "+seatId));
        Optional.ofNullable(updateSeatDTO.getSeatType()).ifPresent(existingUser::setSeatType);
        Optional.ofNullable(updateSeatDTO.getSeatNumber()).ifPresent(existingUser::setSeatNumber);
        Optional.ofNullable(updateSeatDTO.getAvailableSeats()).ifPresent(existingUser::setAvailableSeats);
        Optional.ofNullable(updateSeatDTO.getBookingId()).ifPresent(existingUser::setBookingId);
        Optional.ofNullable(updateSeatDTO.getFarePerSeat()).ifPresent(existingUser::setFarePerSeat);
        return seatMapper.toSeatDTO(existingUser);
    }

    @Override
    public void deleteSeat(Long seatId) {
        seatRepository.deleteById(seatId);
    }
}
