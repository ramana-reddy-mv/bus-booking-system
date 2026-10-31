package com.busbooking.trip;

import com.busbooking.trip.dto.TripResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TripMapper {
    TripResponse toTripResponse(Trip trip);
}
