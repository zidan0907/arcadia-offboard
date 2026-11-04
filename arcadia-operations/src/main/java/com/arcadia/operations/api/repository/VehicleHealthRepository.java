package com.arcadia.operations.api.repository;

import com.arcadia.operations.api.domain.VehicleHealth;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface VehicleHealthRepository extends JpaRepository<VehicleHealth, UUID> {
    Optional<VehicleHealth> findByVin(String vin);
}