package com.arcadia.ecodrive.api.repository;

import com.arcadia.ecodrive.api.domain.TripDrivingMetrics;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface TripDrivingMetricsRepository
        extends JpaRepository<TripDrivingMetrics, UUID> {

    Optional<TripDrivingMetrics> findByTripIdAndVin(UUID tripId, String vin);

    List<TripDrivingMetrics> findAllByTripIdIn(Collection<UUID> tripIds);
}
