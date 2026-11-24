package com.arcadia.ecodrive.api.service.impl;

import com.arcadia.common.exceptions.ResourceNotFoundException;
import com.arcadia.ecodrive.api.domain.TripDrivingMetrics;
import com.arcadia.ecodrive.api.dto.AddTripDrivingMetricsRequest;
import com.arcadia.ecodrive.api.dto.TripDrivingMetricsResponse;
import com.arcadia.ecodrive.api.repository.TripDrivingMetricsRepository;
import com.arcadia.ecodrive.api.service.TripDrivingMetricsService;
import com.arcadia.operations.api.domain.VehicleTrip;
import com.arcadia.operations.api.repository.VehicleTripRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;
import java.util.UUID;

@Service
public class TripDrivingMetricsServiceImpl implements TripDrivingMetricsService {

    private final TripDrivingMetricsRepository metricsRepository;
    private final VehicleTripRepository tripRepository;

    public TripDrivingMetricsServiceImpl(
            TripDrivingMetricsRepository metricsRepository,
            VehicleTripRepository tripRepository
    ) {
        this.metricsRepository = metricsRepository;
        this.tripRepository = tripRepository;
    }

    @Override
    @Transactional
    public TripDrivingMetricsResponse addMetrics(
            String vinValue,
            UUID tripId,
            AddTripDrivingMetricsRequest request
    ) {
        String vin = normalizeVin(vinValue);

        VehicleTrip trip = tripRepository.findByIdAndVin(tripId, vin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found for VIN: " + vin
                ));

        TripDrivingMetrics metrics = metricsRepository
                .findByTripIdAndVin(trip.getId(), vin)
                .orElseGet(() -> TripDrivingMetrics.create(
                        trip.getId(),
                        vin,
                        request.maxSpeedKmph(),
                        request.fuelConsumedLitres(),
                        request.harshBrakingEvents(),
                        request.harshAccelerationEvents(),
                        request.speedingEvents(),
                        request.idlingMinutes()
                ));

        metrics.update(
                vin,
                request.maxSpeedKmph(),
                request.fuelConsumedLitres(),
                request.harshBrakingEvents(),
                request.harshAccelerationEvents(),
                request.speedingEvents(),
                request.idlingMinutes()
        );

        TripDrivingMetrics saved = metricsRepository.save(metrics);

        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public TripDrivingMetricsResponse getMetrics(
            String vinValue,
            UUID tripId
    ) {
        String vin = normalizeVin(vinValue);

        // Verify that the trip exists for this VIN
        VehicleTrip trip = tripRepository.findByIdAndVin(tripId, vin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Trip not found for VIN: " + vin
                ));

        // Retrieve driving metrics for the trip
        TripDrivingMetrics metrics = metricsRepository
                .findByTripIdAndVin(trip.getId(), vin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Driving metrics not found for trip: " + tripId
                ));

        return toResponse(metrics);
    }

    private TripDrivingMetricsResponse toResponse(TripDrivingMetrics metrics) {
        return new TripDrivingMetricsResponse(
                metrics.getTripId(),
                metrics.getVin(),
                metrics.getMaxSpeedKmph(),
                metrics.getFuelConsumedLitres(),
                metrics.getHarshBrakingEvents(),
                metrics.getHarshAccelerationEvents(),
                metrics.getSpeedingEvents(),
                metrics.getIdlingMinutes(),
                metrics.getRecordedAt()
        );
    }

    private String normalizeVin(String vin) {
        if (vin == null || vin.isBlank() || vin.trim().length() > 17) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid VIN");
        }
        return vin.trim().toUpperCase(Locale.ROOT);
    }
}
