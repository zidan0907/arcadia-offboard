package com.arcadia.operations.api.service;

import com.arcadia.operations.api.domain.VehicleHealth;
import com.arcadia.operations.api.dto.UpsertVehicleHealthRequest;
import com.arcadia.operations.api.dto.VehicleHealthResponse;
import com.arcadia.operations.api.repository.VehicleHealthRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.Locale;

@Service
public class VehicleHealthServiceImpl implements VehicleHealthService {

    private final VehicleHealthRepository repository;

    public VehicleHealthServiceImpl(VehicleHealthRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public VehicleHealthResponse upsertHealth(
            String vinValue,
            UpsertVehicleHealthRequest request) {

        String vin = normalizeVin(vinValue);

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Vehicle health details are required"
            );
        }

        // Retrieve existing health record for this VIN.
        // If no record exists, create a new one.
        VehicleHealth health = repository.findByVin(vin)
                .orElseGet(VehicleHealth::create);

        health.setVin(vin);

        health.setAirbagSystem(request.airbagSystem());
        health.setBrakeSystem(request.brakeSystem());
        health.setPowerTrainSystem(request.powerTrainSystem());
        health.setSteeringSystem(request.steeringSystem());
        health.setLvBatteryStatus(request.lvBatteryStatus());
        health.setLvBatteryHealthPercent(
                request.lvBatteryHealthPercent()
        );

        VehicleHealth savedHealth = repository.save(health);

        return toResponse(savedHealth);
    }

    @Override
    @Transactional(readOnly = true)
    public VehicleHealthResponse getHealth(String vinValue) {

        String vin = normalizeVin(vinValue);

        VehicleHealth health = repository.findByVin(vin)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Vehicle health not found for VIN: " + vin
                ));

        return toResponse(health);
    }

    private VehicleHealthResponse toResponse(VehicleHealth health) {

        return new VehicleHealthResponse(
                health.getId(),
                health.getVin(),
                health.getAirbagSystem(),
                health.getBrakeSystem(),
                health.getPowerTrainSystem(),
                health.getSteeringSystem(),
                health.getLvBatteryStatus(),
                health.getLvBatteryHealthPercent(),
                health.getUpdatedAt()
        );
    }

    private String normalizeVin(String vin) {

        if (vin == null || vin.isBlank() || vin.trim().length() > 17) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid VIN"
            );
        }

        return vin.trim().toUpperCase(Locale.ROOT);
    }
}