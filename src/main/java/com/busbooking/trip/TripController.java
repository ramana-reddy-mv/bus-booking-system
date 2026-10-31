package com.busbooking.trip;

import com.busbooking.booking.dto.SeatAvailabilityResponse;
import com.busbooking.booking.service.SeatAllocationService;
import com.busbooking.trip.dto.TripRequest;
import com.busbooking.trip.dto.TripResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/trips")
public class TripController {
    private final TripService tripService;
    private final SeatAllocationService seatAllocationService;

    public TripController(TripService tripService, SeatAllocationService seatAllocationService) {
        this.tripService = tripService;
        this.seatAllocationService = seatAllocationService;
    }

    @PostMapping
    public ResponseEntity<TripResponse> createTrip(@Valid @RequestBody TripRequest tripRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tripService.createTrip(tripRequest));
    }

    @GetMapping("/search")
    public ResponseEntity<List<TripResponse>> searchTrips(
            @RequestParam String source,
            @RequestParam String destination,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        return ResponseEntity.ok(tripService.searchTrips(source, destination, date));
    }

    @Operation(summary = "Get trip seat availability",
            description = "Returns all seats for the trip's bus. Missing allocations are initialized safely, and expired holds are released.")
    @ApiResponse(responseCode = "200", description = "Seat availability",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = SeatAvailabilityResponse.class)),
                    examples = @ExampleObject(value = "[{\"seatId\":11,\"seatNumber\":11,\"status\":\"AVAILABLE\"}]")))
    @ApiResponse(responseCode = "404", description = "Trip not found")
    @GetMapping("/{tripId}/seats")
    public ResponseEntity<List<SeatAvailabilityResponse>> getTripSeats(@PathVariable Long tripId) {
        return ResponseEntity.ok(seatAllocationService.getAvailableSeats(tripId));
    }
}
