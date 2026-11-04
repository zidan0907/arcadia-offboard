package com.arcadia.operations.api.dto;

import com.arcadia.operations.api.domain.HealthStatus;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record UpsertVehicleHealthRequest(
        @NotNull(message = "Airbag system status is required")
        HealthStatus airbagSystem,
        @NotNull(message = "Brake system status is required")
        HealthStatus brakeSystem,
        @NotNull(message = "Power train system status is required")
        HealthStatus powerTrainSystem,
        @NotNull(message = "Steering system status is required")
        HealthStatus steeringSystem,
        @NotNull(message = "LV battery status is required")
        HealthStatus lvBatteryStatus,
        @NotNull(message = "LV battery health percentage is required")
        @DecimalMin("0.00")
        @DecimalMax("100.00")
        @Digits(integer = 3, fraction = 2)
        BigDecimal lvBatteryHealthPercent) {
}