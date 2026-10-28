package com.busbooking.route.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateRouteDTO {

    private Long routeId;

    private String source;

    private String destination;

    private Double distance;

    private Long busId;
}
