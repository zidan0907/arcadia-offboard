package com.arcadia.operations.api.controller;

import com.arcadia.operations.api.dto.UpsertVehicleHealthRequest;
import com.arcadia.operations.api.dto.VehicleHealthResponse;
import com.arcadia.operations.api.service.VehicleHealthService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/vehicles/{vin}/health")
public class VehicleHealthController {
    private final VehicleHealthService service;

    public VehicleHealthController(VehicleHealthService service) {
        this.service = service;
    }

    // Create or update the latest health information for a VIN.
    @PutMapping
    public ResponseEntity<VehicleHealthResponse> upsertHealth(
            @PathVariable String vin,
            @Valid @RequestBody UpsertVehicleHealthRequest request) {
        return ResponseEntity.ok(service.upsertHealth(vin, request));
    }

    // Retrieve the latest health information for a VIN.
    @GetMapping
    public ResponseEntity<VehicleHealthResponse> getHealth(
            @PathVariable String vin) {
        return ResponseEntity.ok(service.getHealth(vin));
    }
}