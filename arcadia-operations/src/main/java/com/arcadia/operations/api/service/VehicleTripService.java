package com.arcadia.operations.api.service;

import com.arcadia.operations.api.dto.CreateTripRequest;
import com.arcadia.operations.api.dto.TripResponse;

import java.util.List;
import java.util.UUID;

public interface VehicleTripService {

    TripResponse createTrip(String vin, CreateTripRequest request);

    List<TripResponse> getTrips(String vin);

    TripResponse getTrip(String vin, UUID tripId);
}