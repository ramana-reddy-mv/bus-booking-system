package com.busbooking.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class BookingHoldExpiredException extends RuntimeException {
    public BookingHoldExpiredException(String message) {
        super(message);
    }
}
