package com.arcadia.ecodrive.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TripDrivingMetricsResponse(
        UUID tripId,
        String vin,
        BigDecimal maxSpeedKmph,
        BigDecimal fuelConsumedLitres,
        Integer harshBrakingEvents,
        Integer harshAccelerationEvents,
        Integer speedingEvents,
        Integer idlingMinutes,
        OffsetDateTime recordedAt
) {
}
