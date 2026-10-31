package com.busbooking.seat.dto;

import com.busbooking.seat.DeckType;
import com.busbooking.seat.SeatType;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    private Long busId;

    private DeckType deckType;

    private boolean windowSeat;
}
