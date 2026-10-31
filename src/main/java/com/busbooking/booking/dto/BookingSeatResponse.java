package com.busbooking.booking.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingSeatResponse {
    private Long seatId;
    private Integer seatNumber;
    private BigDecimal fare;
}
