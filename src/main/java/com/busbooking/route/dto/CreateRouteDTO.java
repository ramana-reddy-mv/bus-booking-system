package com.busbooking.route.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class CreateRouteDTO {

    private String source;

    private String destination;

    private Double distance;

    private Long busId;
}
