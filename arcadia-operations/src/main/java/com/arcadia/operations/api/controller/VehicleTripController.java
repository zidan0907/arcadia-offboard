package com.arcadia.operations.api.controller;

import com.arcadia.operations.api.dto.CreateTripRequest;
import com.arcadia.operations.api.dto.TripResponse;
import com.arcadia.operations.api.service.VehicleTripService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vehicles/{vin}/trips")
public class VehicleTripController {
    private final VehicleTripService service;

    public VehicleTripController(VehicleTripService service) {
        this.service = service;
    }

    // Save a new trip for a vehicle.
    @PostMapping
    public ResponseEntity<TripResponse> createTrip(
            @PathVariable String vin,
            @Valid @RequestBody CreateTripRequest request) {
        TripResponse response = service.createTrip(vin, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all trips belonging to a VIN.
    @GetMapping
    public ResponseEntity<List<TripResponse>> getTrips(
            @PathVariable String vin) {
        return ResponseEntity.ok(service.getTrips(vin));
    }

    // Get a particular trip belonging to a VIN.
    @GetMapping("/{tripId}")
    public ResponseEntity<TripResponse> getTrip(
            @PathVariable String vin,
            @PathVariable UUID tripId) {
        return ResponseEntity.ok(service.getTrip(vin, tripId));
    }
}