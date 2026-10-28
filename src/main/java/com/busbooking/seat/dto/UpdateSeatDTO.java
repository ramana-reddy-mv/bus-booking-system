package com.busbooking.seat.dto;

import com.busbooking.seat.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateSeatDTO {

    private Long seatId;

    private Integer seatNumber;

    private SeatType seatType;

    private Integer availableSeats;

    private BigDecimal farePerSeat;

    private boolean isAvailable;

    private Long busId;

    private Long bookingId;
}
