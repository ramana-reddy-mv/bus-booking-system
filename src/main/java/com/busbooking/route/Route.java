package com.busbooking.route;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Table(name = "route_table")
@AllArgsConstructor
@NoArgsConstructor
public class Route {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long routeId;

    @Column(nullable = false, unique = true)
    private String source;

    @Column(nullable = false)
    private String destination;

    @Column(nullable = false)
    private Double distance;

    @Column(nullable = false)
    private Long busId;
}
