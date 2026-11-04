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
        name = "vehicle_trips",
        schema = "operations",
        indexes = {
                @Index(
                        name = "idx_vehicle_trips_vin",
                        columnList = "vin"
                )
        }
)
public class VehicleTrip {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Setter
    @Column(nullable = false, length = 32)
    private String vin;
    @Setter
    @Column(name = "start_address", nullable = false, length = 500)
    private String startAddress;
    @Setter
    @Column(name = "end_address", nullable = false, length = 500)
    private String endAddress;
    @Setter
    @Column(name = "engine_start_time", nullable = false)
    private OffsetDateTime engineStartTime;
    @Setter
    @Column(name = "engine_end_time", nullable = false)
    private OffsetDateTime engineEndTime;
    @Setter
    @Column(
            name = "km_driven",
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal kmDriven;
    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    protected VehicleTrip() {
    }

    public static VehicleTrip create() {
        return new VehicleTrip();
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
}