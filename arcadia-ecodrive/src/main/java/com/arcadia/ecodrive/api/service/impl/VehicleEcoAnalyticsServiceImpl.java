package com.arcadia.ecodrive.api.service.impl;

import com.arcadia.ecodrive.api.domain.EcoScoringPolicy;
import com.arcadia.ecodrive.api.domain.TripDrivingMetrics;
import com.arcadia.ecodrive.api.dto.ScoreAnalysisResponse;
import com.arcadia.ecodrive.api.dto.VehicleDrivingAnalysisResponse;
import com.arcadia.ecodrive.api.repository.EcoScoringPolicyRepository;
import com.arcadia.ecodrive.api.repository.TripDrivingMetricsRepository;
import com.arcadia.ecodrive.api.service.VehicleEcoAnalyticsService;
import com.arcadia.operations.api.domain.VehicleTrip;
import com.arcadia.operations.api.repository.VehicleTripRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class VehicleEcoAnalyticsServiceImpl implements VehicleEcoAnalyticsService {

    private final VehicleTripRepository tripRepository;
    private final TripDrivingMetricsRepository metricsRepository;
    private final EcoScoringPolicyRepository policyRepository;

    public VehicleEcoAnalyticsServiceImpl(
            VehicleTripRepository tripRepository,
            TripDrivingMetricsRepository metricsRepository,
            EcoScoringPolicyRepository policyRepository
    ) {
        this.tripRepository = tripRepository;
        this.metricsRepository = metricsRepository;
        this.policyRepository = policyRepository;
    }

    @Override
    public VehicleDrivingAnalysisResponse getDrivingAnalysis(
            String vin,
            OffsetDateTime from,
            OffsetDateTime to,
            UUID policyId
    ) {
        validateRequest(vin, from, to);

        List<VehicleTrip> trips = tripRepository
                .findAllByVinAndEngineStartTimeGreaterThanEqualAndEngineStartTimeLessThanOrderByEngineStartTimeAsc(
                        vin.trim().toUpperCase(), from, to
                );

        if (trips.isEmpty()) {
            if (policyId != null) {
                policyRepository.findById(policyId).orElseThrow(() ->
                        new ResponseStatusException(HttpStatus.NOT_FOUND, "Scoring policy not found"));
            }
            return emptyResponse(vin.trim().toUpperCase(), from, to);
        }

        List<UUID> tripIds = trips.stream().map(VehicleTrip::getId).toList();

        Map<UUID, TripDrivingMetrics> metricsByTripId =
                metricsRepository.findAllByTripIdIn(tripIds).stream()
                        .collect(Collectors.toMap(
                                TripDrivingMetrics::getTripId,
                                Function.identity()
                        ));

        List<TripDrivingMetrics> metrics = trips.stream()
                .map(trip -> metricsByTripId.get(trip.getId()))
                .filter(java.util.Objects::nonNull)
                .toList();

        BigDecimal totalDistance = trips.stream()
                .map(VehicleTrip::getKmDriven)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        long totalDrivingMinutes = trips.stream()
                .mapToLong(trip -> Duration.between(
                        trip.getEngineStartTime(), trip.getEngineEndTime()
                ).toMinutes())
                .sum();

        BigDecimal averageSpeed = totalDrivingMinutes > 0
                ? totalDistance.multiply(BigDecimal.valueOf(60))
                    .divide(BigDecimal.valueOf(totalDrivingMinutes), 2, RoundingMode.HALF_UP)
                : null;

        Map<LocalDate, BigDecimal> dailyDistance = new TreeMap<>();
        for (VehicleTrip trip : trips) {
            dailyDistance.merge(
                    trip.getEngineStartTime().toLocalDate(),
                    trip.getKmDriven(),
                    BigDecimal::add
            );
        }

        boolean complete = metrics.size() == trips.size();

        boolean hasMaxSpeed = complete && metrics.stream()
                .allMatch(m -> m.getMaxSpeedKmph() != null);
        boolean hasFuel = complete && metrics.stream()
                .allMatch(m -> m.getFuelConsumedLitres() != null
                        && m.getFuelConsumedLitres().signum() > 0);
        boolean hasBraking = complete && metrics.stream()
                .allMatch(m -> m.getHarshBrakingEvents() != null);
        boolean hasAcceleration = complete && metrics.stream()
                .allMatch(m -> m.getHarshAccelerationEvents() != null);
        boolean hasSpeeding = complete && metrics.stream()
                .allMatch(m -> m.getSpeedingEvents() != null);
        boolean hasIdling = complete && metrics.stream()
                .allMatch(m -> m.getIdlingMinutes() != null);

        BigDecimal maxSpeed = hasMaxSpeed ? metrics.stream()
                .map(TripDrivingMetrics::getMaxSpeedKmph)
                .max(BigDecimal::compareTo).orElse(null) : null;

        BigDecimal totalFuel = hasFuel ? metrics.stream()
                .map(TripDrivingMetrics::getFuelConsumedLitres)
                .reduce(BigDecimal.ZERO, BigDecimal::add) : null;

        BigDecimal fuelEconomy = totalFuel != null
                ? totalDistance.divide(totalFuel, 2, RoundingMode.HALF_UP)
                : null;

        Long harshBraking = hasBraking
                ? sum(metrics, TripDrivingMetrics::getHarshBrakingEvents) : null;
        Long harshAcceleration = hasAcceleration
                ? sum(metrics, TripDrivingMetrics::getHarshAccelerationEvents) : null;
        Long speeding = hasSpeeding
                ? sum(metrics, TripDrivingMetrics::getSpeedingEvents) : null;
        Long idling = hasIdling
                ? sum(metrics, TripDrivingMetrics::getIdlingMinutes) : null;

        List<String> unavailable = new ArrayList<>();
        if (!hasMaxSpeed) unavailable.add("maxSpeedKmph");
        if (!hasFuel) unavailable.add("fuelConsumedLitres/fuelEconomyKmPerLitre");
        if (!hasBraking) unavailable.add("harshBrakingEvents");
        if (!hasAcceleration) unavailable.add("harshAccelerationEvents");
        if (!hasSpeeding) unavailable.add("speedingEvents");
        if (!hasIdling) unavailable.add("idlingMinutes");

        BigDecimal ecoScore = null;
        String category = null;
        ScoreAnalysisResponse scoreAnalysis = null;

        boolean scoreInputsComplete = hasBraking && hasAcceleration && hasSpeeding && hasIdling;

        if (policyId != null) {
            EcoScoringPolicy policy = policyRepository.findById(policyId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND, "Scoring policy not found: " + policyId
                    ));

            if (scoreInputsComplete) {
                scoreAnalysis = calculateScore(
                        harshBraking, harshAcceleration, speeding, idling, policy
                );
                ecoScore = scoreAnalysis.score();
                category = scoreAnalysis.category();
            } else {
                unavailable.add("ecoScore (driving event metrics are incomplete)");
            }
        } else {
            unavailable.add("ecoScore (policyId not provided)");
        }

        return new VehicleDrivingAnalysisResponse(
                vin.trim().toUpperCase(),
                from,
                to,
                trips.size(),
                totalDistance.setScale(2, RoundingMode.HALF_UP),
                totalDrivingMinutes,
                averageSpeed,
                maxSpeed,
                totalFuel,
                fuelEconomy,
                harshBraking,
                harshAcceleration,
                speeding,
                idling,
                ecoScore,
                category,
                dailyDistance,
                unavailable,
                scoreAnalysis
        );
    }

    private ScoreAnalysisResponse calculateScore(
            long braking, long acceleration, long speeding, long idling,
            EcoScoringPolicy policy
    ) {
        BigDecimal score = policy.getBaseScore()
                .subtract(policy.getHarshBrakingPenalty().multiply(BigDecimal.valueOf(braking)))
                .subtract(policy.getHarshAccelerationPenalty().multiply(BigDecimal.valueOf(acceleration)))
                .subtract(policy.getSpeedingPenalty().multiply(BigDecimal.valueOf(speeding)))
                .subtract(policy.getIdlingPenaltyPerMinute().multiply(BigDecimal.valueOf(idling)))
                .max(BigDecimal.ZERO)
                .min(policy.getBaseScore())
                .setScale(2, RoundingMode.HALF_UP);

        String category;
        if (score.compareTo(policy.getExcellentMinimum()) >= 0) {
            category = "EXCELLENT";
        } else if (score.compareTo(policy.getGoodMinimum()) >= 0) {
            category = "GOOD";
        } else if (score.compareTo(policy.getFairMinimum()) >= 0) {
            category = "FAIR";
        } else {
            category = "NEEDS_IMPROVEMENT";
        }

        List<String> recommendations = new ArrayList<>();
        if (braking > 0) recommendations.add("Avoid sudden braking and maintain a safe following distance.");
        if (acceleration > 0) recommendations.add("Accelerate smoothly and avoid sudden acceleration.");
        if (speeding > 0) recommendations.add("Follow speed limits and maintain a safe driving speed.");
        if (idling > 0) recommendations.add("Reduce unnecessary engine idling.");
        if (recommendations.isEmpty()) recommendations.add("No recorded driving events require improvement.");

        return new ScoreAnalysisResponse(score, category, recommendations);
    }

    private long sum(
            List<TripDrivingMetrics> metrics,
            Function<TripDrivingMetrics, Integer> getter
    ) {
        return metrics.stream().map(getter).mapToLong(Integer::longValue).sum();
    }

    private void validateRequest(String vin, OffsetDateTime from, OffsetDateTime to) {
        if (vin == null || vin.isBlank() || vin.trim().length() > 17) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid VIN");
        }
        if (from == null || to == null || !to.isAfter(from)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "'to' must be later than 'from'"
            );
        }
    }

    private VehicleDrivingAnalysisResponse emptyResponse(
            String vin, OffsetDateTime from, OffsetDateTime to
    ) {
        return new VehicleDrivingAnalysisResponse(
                vin, from, to, 0, BigDecimal.ZERO, 0,
                null, null, null, null,
                null, null, null, null,
                null, null, Collections.emptyMap(),
                List.of(
                        "maxSpeedKmph", "fuelConsumedLitres/fuelEconomyKmPerLitre",
                        "harshBrakingEvents", "harshAccelerationEvents",
                        "speedingEvents", "idlingMinutes", "ecoScore"
                ),
                null
        );
    }
}
