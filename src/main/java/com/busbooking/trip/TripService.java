package com.busbooking.trip;

import com.busbooking.trip.dto.TripRequest;
import com.busbooking.trip.dto.TripResponse;

import java.time.LocalDate;
import java.util.List;

public interface TripService {
    TripResponse createTrip(TripRequest tripRequest);

    List<TripResponse> searchTrips(String source, String destination, LocalDate travelDate);
}
