package com.arcadia.ecodrive.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AddTripDrivingMetricsRequest(
        @NotNull @DecimalMin("0.0")
        BigDecimal maxSpeedKmph,
        @NotNull @DecimalMin("0.001")
        BigDecimal fuelConsumedLitres,
        @NotNull @Min(0)
        Integer harshBrakingEvents,
        @NotNull @Min(0)
        Integer harshAccelerationEvents,
        @NotNull @Min(0)
        Integer speedingEvents,
        @NotNull @Min(0)
        Integer idlingMinutes
) {
}
