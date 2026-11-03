package com.arcadia.operations.api.dto;

import com.arcadia.operations.api.domain.HealthStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record VehicleHealthResponse(
        UUID id,
        String vin,
        HealthStatus airbagSystem,
        HealthStatus brakeSystem,
        HealthStatus powerTrainSystem,
        HealthStatus steeringSystem,
        HealthStatus lvBatteryStatus,
        BigDecimal lvBatteryHealthPercent,
        OffsetDateTime updatedAt) {
}