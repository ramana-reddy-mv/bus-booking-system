package com.busbooking.seat.dto;

import com.busbooking.seat.SeatType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateSeatDTO {

    private Integer seatNumber;

    private SeatType seatType;

    private Integer availableSeats;

    private BigDecimal farePerSeat;

    private boolean isAvailable;

    private Long bookingId;

    private Long busId;
}
