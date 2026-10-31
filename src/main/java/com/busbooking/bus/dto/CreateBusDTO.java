package com.busbooking.bus.dto;

import com.busbooking.bus.BusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateBusDTO {

    private String busNumber;

    private BusType busType;

    private Integer totalSeats;

    private Long operatorId;
}
