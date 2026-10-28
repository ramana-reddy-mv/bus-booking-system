package com.busbooking.bus;

public class BusNotFoundException extends RuntimeException {
    public BusNotFoundException(String message){
        super(message);
    }
}
