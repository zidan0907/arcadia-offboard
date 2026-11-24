package com.arcadia.ecodrive.api.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record EcoScoringPolicyResponse(
        UUID id,
        String policyName,
        BigDecimal baseScore,
        BigDecimal harshBrakingPenalty,
        BigDecimal harshAccelerationPenalty,
        BigDecimal speedingPenalty,
        BigDecimal idlingPenaltyPerMinute,
        BigDecimal fairMinimum,
        BigDecimal goodMinimum,
        BigDecimal excellentMinimum,
        OffsetDateTime createdAt
) {
}
