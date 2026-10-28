package com.busbooking.seat;

public class SeatNotFoundException extends RuntimeException {
    public SeatNotFoundException(String message){
        super(message);
    }
}
