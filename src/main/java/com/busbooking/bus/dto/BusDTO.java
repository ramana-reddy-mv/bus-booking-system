package com.busbooking.bus.dto;

import com.busbooking.bus.BusType;
import com.busbooking.route.dto.RouteDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BusDTO {

    private Long busId;

    private String busNumber;

    private BusType busType;

    private Integer totalSeats;

    private Long operatorId;
}
