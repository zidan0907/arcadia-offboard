package com.arcadia.maintenance.api.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "service_schedules", schema = "maintenance")
@Getter
public class ServiceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "schedule_id", nullable = false, updatable = false)
    private UUID scheduleId;

    @Setter
    @Column(name = "user_id", nullable = false, unique = true, length = 64)
    private String userId;

    @Setter
    @Column(name = "total_distance_covered", nullable = false, precision = 10, scale = 2)
    private BigDecimal totalDistanceCovered = BigDecimal.ZERO;

    @Setter
    @Column(name = "service_schedule_date", nullable = false)
    private LocalDate serviceScheduleDate;

    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        OffsetDateTime now = OffsetDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = OffsetDateTime.now();
    }
}
