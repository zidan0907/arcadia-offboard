package com.arcadia.operations.api.dto;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public record CreateTripRequest(
        @NotBlank(message = "Start address is required")
        @Size(max = 500)
        String startAddress,
        @NotBlank(message = "End address is required")
        @Size(max = 500)
        String endAddress,
        @NotNull(message = "Engine start time is required")
        OffsetDateTime engineStartTime,
        @NotNull(message = "Engine end time is required")
        OffsetDateTime engineEndTime,
        @NotNull(message = "KM driven is required")
        @DecimalMin(value = "0.00", message = "KM driven cannot be negative")
        @Digits(integer = 10, fraction = 2)
        BigDecimal kmDriven) {
}