package com.busbooking.booking.controller;

import com.busbooking.booking.dto.BookingRequest;
import com.busbooking.booking.dto.BookingResponse;
import com.busbooking.booking.dto.PaymentRequest;
import com.busbooking.booking.service.BookingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @Operation(summary = "Create a booking",
            description = "Creates a five-minute hold for the selected seats for the supplied customer. "
                    + "User IDs are not authenticated in this V1.")
    @ApiResponse(responseCode = "201", description = "Booking created with seats held",
            content = @Content(schema = @Schema(implementation = BookingResponse.class),
                    examples = @ExampleObject(value = "{\"bookingId\":123,\"bookingReference\":\"a2be1d31-763b-4ee2-8e06-d04e050b41bf\",\"status\":\"PENDING_PAYMENT\",\"trip\":{\"tripId\":50,\"source\":\"A\",\"destination\":\"B\",\"travelDate\":\"2026-11-02\",\"departureTime\":\"09:00:00\",\"arrivalTime\":\"11:00:00\"},\"seats\":[{\"seatId\":11,\"seatNumber\":11,\"fare\":25.00}],\"totalAmount\":25.00,\"createdAt\":\"2026-10-31T11:10:00\"}")))
    @ApiResponse(responseCode = "409", description = "One or more seats are unavailable")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Customer, trip and seat IDs to book",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{\"userId\":1,\"tripId\":50,\"seatIds\":[11,12]}")))
    @PostMapping
    public ResponseEntity<BookingResponse> createBooking(@Valid @RequestBody BookingRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(bookingService.createBooking(request));
    }

    @Operation(summary = "Process mock payment",
            description = "Confirms a pending booking or releases its seats on payment failure.")
    @ApiResponse(responseCode = "200", description = "Payment result processed",
            content = @Content(schema = @Schema(implementation = BookingResponse.class)))
    @ApiResponse(responseCode = "409", description = "Booking is not pending or its seat hold expired")
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Mock payment outcome",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    examples = @ExampleObject(value = "{\"success\":true}")))
    @PostMapping("/{bookingId}/payment")
    public ResponseEntity<BookingResponse> processPayment(
            @PathVariable Long bookingId,
            @RequestParam @Positive Long userId,
            @Valid @RequestBody PaymentRequest request) {
        return ResponseEntity.ok(bookingService.processPayment(bookingId, userId, request));
    }

    @Operation(summary = "Cancel a confirmed booking",
            description = "The supplied userId must own the booking; its seats are released.")
    @ApiResponse(responseCode = "200", description = "Booking cancelled",
            content = @Content(schema = @Schema(implementation = BookingResponse.class)))
    @ApiResponse(responseCode = "403", description = "Caller does not own this booking")
    @ApiResponse(responseCode = "409", description = "Only confirmed bookings can be cancelled")
    @PostMapping("/{bookingId}/cancel")
    public ResponseEntity<BookingResponse> cancelBooking(
            @PathVariable Long bookingId,
            @RequestParam @Positive Long userId) {
        return ResponseEntity.ok(bookingService.cancelBooking(bookingId, userId));
    }

    @Operation(summary = "Get a booking",
            description = "Returns trip, seat fare snapshots, total, status and reference to the supplied booking owner.")
    @ApiResponse(responseCode = "200", description = "Booking details",
            content = @Content(schema = @Schema(implementation = BookingResponse.class)))
    @ApiResponse(responseCode = "403", description = "Caller does not own this booking")
    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingResponse> getBooking(
            @PathVariable Long bookingId,
            @RequestParam @Positive Long userId) {
        return ResponseEntity.ok(bookingService.getBooking(bookingId, userId));
    }

    @Operation(summary = "Get my bookings",
            description = "Returns all bookings belonging to the supplied customer. User IDs are not authenticated in this V1.")
    @ApiResponse(responseCode = "200", description = "Customer bookings",
            content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                    array = @ArraySchema(schema = @Schema(implementation = BookingResponse.class)),
                    examples = @ExampleObject(value = "[{\"bookingId\":1,\"bookingReference\":\"a2be1d31-763b-4ee2-8e06-d04e050b41bf\",\"status\":\"PENDING_PAYMENT\",\"totalAmount\":2500.00}]")))
    @GetMapping("/my-bookings")
    public ResponseEntity<List<BookingResponse>> getMyBookings(@RequestParam @Positive Long userId) {
        return ResponseEntity.ok(bookingService.getMyBookings(userId));
    }
}
