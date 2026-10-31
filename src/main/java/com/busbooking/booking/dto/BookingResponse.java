package com.busbooking.booking.dto;

import com.busbooking.booking.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BookingResponse {
    private Long bookingId;
    private String bookingReference;
    private BookingStatus status;
    private BookingTripResponse trip;
    private List<BookingSeatResponse> seats;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
}
