package com.arcadia.maintenance.api.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "vehicle_warranties", schema = "maintenance")
@Getter
public class WarrantyStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "warranty_id", nullable = false, updatable = false)
    private UUID warrantyId;

    @Setter
    @Column(name = "user_id", nullable = false, unique = true, length = 64)
    private String userId;

    @Setter
    @Column(name = "warranty_date", nullable = false)
    private LocalDate warrantyDate;

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
