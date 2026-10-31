package com.busbooking.trip;

import com.busbooking.bus.Bus;
import com.busbooking.bus.BusRepository;
import com.busbooking.route.Route;
import com.busbooking.route.RouteRepository;
import com.busbooking.trip.dto.TripRequest;
import com.busbooking.trip.dto.TripResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class TripServiceImpl implements TripService {
    private final TripRepository tripRepository;
    private final BusRepository busRepository;
    private final RouteRepository routeRepository;
    private final TripMapper tripMapper;

    public TripServiceImpl(
            TripRepository tripRepository,
            BusRepository busRepository,
            RouteRepository routeRepository,
            TripMapper tripMapper) {
        this.tripRepository = tripRepository;
        this.busRepository = busRepository;
        this.routeRepository = routeRepository;
        this.tripMapper = tripMapper;
    }

    @Override
    public TripResponse createTrip(TripRequest tripRequest) {
        Bus bus = busRepository.findById(tripRequest.getBusId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Bus not found with ID " + tripRequest.getBusId()));
        Route route = routeRepository.findById(tripRequest.getRouteId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Route not found with ID " + tripRequest.getRouteId()));

        Trip trip = new Trip();
        trip.setBusId(bus.getBusId());
        trip.setRouteId(route.getRouteId());
        trip.setSource(route.getSource());
        trip.setDestination(route.getDestination());
        trip.setDepartureTime(tripRequest.getDepartureTime());
        trip.setArrivalTime(tripRequest.getArrivalTime());
        trip.setFare(tripRequest.getFare());
        trip.setTravelDate(tripRequest.getTravelDate());

        return tripMapper.toTripResponse(tripRepository.save(trip));
    }

    @Override
    public List<TripResponse> searchTrips(String source, String destination, LocalDate travelDate) {
        return tripRepository.findBySourceIgnoreCaseAndDestinationIgnoreCaseAndTravelDate(
                        source.trim(), destination.trim(), travelDate)
                .stream()
                .map(tripMapper::toTripResponse)
                .toList();
    }
}
