package com.arcadia.ecodrive.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

public record VehicleDrivingAnalysisResponse(
        String vin,
        OffsetDateTime from,
        OffsetDateTime to,
        long totalTrips,
        BigDecimal distanceTravelledKm,
        long totalDrivingMinutes,
        BigDecimal averageSpeedKmph,
        BigDecimal maxSpeedKmph,
        BigDecimal fuelConsumedLitres,
        BigDecimal fuelEconomyKmPerLitre,
        Long harshBrakingEvents,
        Long harshAccelerationEvents,
        Long speedingEvents,
        Long idlingMinutes,
        BigDecimal ecoScore,
        String scoreCategory,
        Map<LocalDate, BigDecimal> dailyDistance,
        List<String> unavailableMetrics,
        ScoreAnalysisResponse scoreAnalysis
) {
}
