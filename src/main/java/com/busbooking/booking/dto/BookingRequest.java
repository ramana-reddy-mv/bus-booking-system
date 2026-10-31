package com.busbooking.booking.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingRequest {
    @NotNull
    @Positive
    private Long userId;

    @NotNull
    @Positive
    private Long tripId;

    @NotEmpty
    private List<@NotNull @Positive Long> seatIds;
}
