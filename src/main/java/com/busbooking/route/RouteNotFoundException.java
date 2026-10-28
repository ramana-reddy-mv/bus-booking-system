package com.busbooking.route;

public class RouteNotFoundException extends RuntimeException {
    public RouteNotFoundException(String message){
        super(message);
    }
}
