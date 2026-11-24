package com.arcadia.ecodrive.api.service;

import com.arcadia.ecodrive.api.dto.AddTripDrivingMetricsRequest;
import com.arcadia.ecodrive.api.dto.TripDrivingMetricsResponse;

import java.util.UUID;

public interface TripDrivingMetricsService {

    TripDrivingMetricsResponse addMetrics(
            String vin,
            UUID tripId,
            AddTripDrivingMetricsRequest request
    );

    TripDrivingMetricsResponse getMetrics(String vin, UUID tripId);
}
