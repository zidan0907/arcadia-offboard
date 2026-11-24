package com.arcadia.operations.api.repository;

import com.arcadia.operations.api.domain.VehicleTrip;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehicleTripRepository extends JpaRepository<VehicleTrip, UUID> {
    List<VehicleTrip> findAllByVinOrderByEngineStartTimeDesc(String vin);

    Optional<VehicleTrip> findByIdAndVin(UUID id, String vin);

    List<VehicleTrip> findAllByVinAndEngineStartTimeGreaterThanEqualAndEngineStartTimeLessThanOrderByEngineStartTimeAsc(
            String vin, OffsetDateTime from, OffsetDateTime to);
}