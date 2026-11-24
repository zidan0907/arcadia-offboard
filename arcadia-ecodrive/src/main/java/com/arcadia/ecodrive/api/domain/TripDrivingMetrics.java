package com.arcadia.ecodrive.api.domain;

import jakarta.persistence.*;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "trip_driving_metrics",
        schema = "ecodrive",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_trip_driving_metrics_trip_id",
                columnNames = "trip_id"
        ),
        indexes = @Index(
                name = "idx_trip_driving_metrics_vin",
                columnList = "vin"
        )
)
public class TripDrivingMetrics {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(name = "trip_id", nullable = false)
    private UUID tripId;
    @Column(nullable = false, length = 32)
    private String vin;
    @Column(name = "max_speed_kmph", precision = 8, scale = 2)
    private BigDecimal maxSpeedKmph;
    @Column(name = "fuel_consumed_litres", precision = 10, scale = 3)
    private BigDecimal fuelConsumedLitres;
    @Column(name = "harsh_braking_events")
    private Integer harshBrakingEvents;
    @Column(name = "harsh_acceleration_events")
    private Integer harshAccelerationEvents;
    @Column(name = "speeding_events")
    private Integer speedingEvents;
    @Column(name = "idling_minutes")
    private Integer idlingMinutes;
    @Column(name = "recorded_at", nullable = false)
    private OffsetDateTime recordedAt;

    protected TripDrivingMetrics() {
    }

    public static TripDrivingMetrics create(
            UUID tripId,
            String vin,
            BigDecimal maxSpeedKmph,
            BigDecimal fuelConsumedLitres,
            Integer harshBrakingEvents,
            Integer harshAccelerationEvents,
            Integer speedingEvents,
            Integer idlingMinutes
    ) {
        TripDrivingMetrics metrics = new TripDrivingMetrics();
        metrics.update(
                vin, maxSpeedKmph, fuelConsumedLitres,
                harshBrakingEvents, harshAccelerationEvents,
                speedingEvents, idlingMinutes
        );
        metrics.tripId = tripId;
        return metrics;
    }

    public void update(
            String vin,
            BigDecimal maxSpeedKmph,
            BigDecimal fuelConsumedLitres,
            Integer harshBrakingEvents,
            Integer harshAccelerationEvents,
            Integer speedingEvents,
            Integer idlingMinutes
    ) {
        this.vin = vin;
        this.maxSpeedKmph = maxSpeedKmph;
        this.fuelConsumedLitres = fuelConsumedLitres;
        this.harshBrakingEvents = harshBrakingEvents;
        this.harshAccelerationEvents = harshAccelerationEvents;
        this.speedingEvents = speedingEvents;
        this.idlingMinutes = idlingMinutes;
        this.recordedAt = OffsetDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        if (recordedAt == null) {
            recordedAt = OffsetDateTime.now();
        }
    }
}
