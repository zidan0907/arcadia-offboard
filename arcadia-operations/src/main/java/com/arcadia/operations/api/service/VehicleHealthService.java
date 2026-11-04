package com.arcadia.operations.api.service;

import com.arcadia.operations.api.dto.UpsertVehicleHealthRequest;
import com.arcadia.operations.api.dto.VehicleHealthResponse;

public interface VehicleHealthService {

    VehicleHealthResponse upsertHealth(String vin, UpsertVehicleHealthRequest request);

    VehicleHealthResponse getHealth(String vin);
}