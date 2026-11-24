package com.arcadia.ecodrive.api.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record CreateEcoScoringPolicyRequest(
        @NotBlank
        @Size(max = 100)
        String policyName,
        @NotNull
        @DecimalMin("0.01")
        BigDecimal baseScore,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal harshBrakingPenalty,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal harshAccelerationPenalty,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal speedingPenalty,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal idlingPenaltyPerMinute,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal fairMinimum,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal goodMinimum,
        @NotNull
        @DecimalMin("0.0")
        BigDecimal excellentMinimum
) {
}
