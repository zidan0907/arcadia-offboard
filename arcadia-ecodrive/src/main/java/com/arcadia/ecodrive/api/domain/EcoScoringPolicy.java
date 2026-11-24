package com.arcadia.ecodrive.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(name = "eco_scoring_policies", schema = "ecodrive")
public class EcoScoringPolicy {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "policy_name", nullable = false, unique = true, length = 100)
    private String policyName;
    @Column(name = "base_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal baseScore;
    @Column(name = "harsh_braking_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal harshBrakingPenalty;
    @Column(name = "harsh_acceleration_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal harshAccelerationPenalty;
    @Column(name = "speeding_penalty", nullable = false, precision = 5, scale = 2)
    private BigDecimal speedingPenalty;
    @Column(name = "idling_penalty_per_minute", nullable = false, precision = 5, scale = 2)
    private BigDecimal idlingPenaltyPerMinute;
    @Column(name = "fair_minimum", nullable = false, precision = 5, scale = 2)
    private BigDecimal fairMinimum;
    @Column(name = "good_minimum", nullable = false, precision = 5, scale = 2)
    private BigDecimal goodMinimum;
    @Column(name = "excellent_minimum", nullable = false, precision = 5, scale = 2)
    private BigDecimal excellentMinimum;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected EcoScoringPolicy() {
    }

    public static EcoScoringPolicy create(
            String policyName, BigDecimal baseScore,
            BigDecimal harshBrakingPenalty, BigDecimal harshAccelerationPenalty,
            BigDecimal speedingPenalty, BigDecimal idlingPenaltyPerMinute,
            BigDecimal fairMinimum, BigDecimal goodMinimum,
            BigDecimal excellentMinimum
    ) {
        EcoScoringPolicy policy = new EcoScoringPolicy();
        policy.policyName = policyName;
        policy.baseScore = baseScore;
        policy.harshBrakingPenalty = harshBrakingPenalty;
        policy.harshAccelerationPenalty = harshAccelerationPenalty;
        policy.speedingPenalty = speedingPenalty;
        policy.idlingPenaltyPerMinute = idlingPenaltyPerMinute;
        policy.fairMinimum = fairMinimum;
        policy.goodMinimum = goodMinimum;
        policy.excellentMinimum = excellentMinimum;
        policy.createdAt = OffsetDateTime.now();
        return policy;
    }
}
