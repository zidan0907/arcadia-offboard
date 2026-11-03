package com.arcadia.operations.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record TripResponse(
        UUID id,
        String vin,
        String startAddress,
        String endAddress,
        OffsetDateTime engineStartTime,
        OffsetDateTime engineEndTime,
        BigDecimal kmDriven,
        OffsetDateTime createdAt) {
}