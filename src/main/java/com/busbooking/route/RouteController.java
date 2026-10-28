package com.busbooking.route;

import com.busbooking.route.dto.CreateRouteDTO;
import com.busbooking.route.dto.RouteDTO;
import com.busbooking.route.dto.UpdateRouteDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/routes")
public class RouteController {

    private final RouteService routeService;

    public RouteController(RouteService routeService) {
        this.routeService = routeService;
    }

    @PostMapping
    public ResponseEntity<RouteDTO> createRoute(@RequestBody CreateRouteDTO createRouteDTO) {
        RouteDTO createdRoute = routeService.createRoute(createRouteDTO);
        return new ResponseEntity<>(createdRoute, HttpStatus.CREATED);
    }

    @GetMapping("/{routeId}")
    public ResponseEntity<RouteDTO> getRoute(@PathVariable Long routeId) {
        RouteDTO routeDTO = routeService.getRoute(routeId).orElseThrow(() ->
                new RouteNotFoundException("Route not found with ID " + routeId));
        return new ResponseEntity<>(routeDTO, HttpStatus.OK);
    }

    @PutMapping("/{routeId}")
    public ResponseEntity<RouteDTO> updateRoute(@PathVariable Long routeId, @RequestBody UpdateRouteDTO updateRouteDTO) {
        RouteDTO routeDTO = routeService.updateRoute(routeId, updateRouteDTO);
        return new ResponseEntity<>(routeDTO, HttpStatus.OK);
    }

    @DeleteMapping("/{routeId}")
    public ResponseEntity<Void> deleteRoute(@PathVariable Long routeId) {
        routeService.deleteRoute(routeId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<RouteDTO>> getAllRoutes() {
        List<RouteDTO> routeDTOS = routeService.getAllRoutees();
        return new ResponseEntity<>(routeDTOS, HttpStatus.OK);
    }
}
