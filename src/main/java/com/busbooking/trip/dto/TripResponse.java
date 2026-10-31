package com.busbooking.trip.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@AllArgsConstructor
@Data
public class TripResponse {
    private final Long tripId;
    private final Long busId;
    private final Long routeId;
    private final String source;
    private final String destination;
    private final LocalTime departureTime;
    private final LocalTime arrivalTime;
    private final BigDecimal fare;
    private final LocalDate travelDate;

}
