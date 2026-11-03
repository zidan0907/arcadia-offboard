package com.arcadia.operations.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Entity
@Table(
        name = "vehicle_health",
        schema = "operations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_vehicle_health_vin",
                        columnNames = "vin"
                )
        }
)
public class VehicleHealth {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Setter
    @Column(nullable = false, length = 32)
    private String vin;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "airbag_system", nullable = false, length = 16)
    private HealthStatus airbagSystem;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "brake_system", nullable = false, length = 16)
    private HealthStatus brakeSystem;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "power_train_system", nullable = false, length = 16)
    private HealthStatus powerTrainSystem;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "steering_system", nullable = false, length = 16)
    private HealthStatus steeringSystem;
    @Setter
    @Enumerated(EnumType.STRING)
    @Column(name = "lv_battery_status", nullable = false, length = 16)
    private HealthStatus lvBatteryStatus;
    @Setter
    @Column(
            name = "lv_battery_health_percent",
            precision = 5,
            scale = 2
    )
    private BigDecimal lvBatteryHealthPercent;
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    protected VehicleHealth() {
    }

    public static VehicleHealth create() {
        return new VehicleHealth();
    }

    @PrePersist
    @PreUpdate
    protected void updateTimestamp() {
        this.updatedAt = OffsetDateTime.now();
    }
}