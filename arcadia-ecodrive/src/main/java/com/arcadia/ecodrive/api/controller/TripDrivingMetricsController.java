package com.arcadia.ecodrive.api.controller;

import com.arcadia.ecodrive.api.dto.AddTripDrivingMetricsRequest;
import com.arcadia.ecodrive.api.dto.TripDrivingMetricsResponse;
import com.arcadia.ecodrive.api.service.TripDrivingMetricsService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ecodrive/vehicles/{vin}/trips")
public class TripDrivingMetricsController {

    private final TripDrivingMetricsService service;

    public TripDrivingMetricsController(TripDrivingMetricsService service) {
        this.service = service;
    }

    @PostMapping("/{tripId}/driving-metrics")
    public ResponseEntity<TripDrivingMetricsResponse> addMetrics(
            @PathVariable String vin,
            @PathVariable UUID tripId,
            @Valid @RequestBody AddTripDrivingMetricsRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.addMetrics(vin, tripId, request));
    }

    // Get driving metrics for a particular trip
    @GetMapping("/{tripId}/driving-metrics")
    public ResponseEntity<TripDrivingMetricsResponse> getMetrics(
            @PathVariable String vin,
            @PathVariable UUID tripId
    ) {
        return ResponseEntity.ok(service.getMetrics(vin, tripId));
    }
}
