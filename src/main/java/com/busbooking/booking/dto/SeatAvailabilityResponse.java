package com.busbooking.booking.dto;

import com.busbooking.booking.enums.SeatAllocationStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SeatAvailabilityResponse {
    private Long seatId;
    private Integer seatNumber;
    private SeatAllocationStatus status;
}
