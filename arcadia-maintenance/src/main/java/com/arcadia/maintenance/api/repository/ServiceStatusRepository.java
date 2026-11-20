package com.arcadia.maintenance.api.repository;

import com.arcadia.maintenance.api.domain.ServiceStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ServiceStatusRepository extends JpaRepository<ServiceStatus, UUID> {
    Optional<ServiceStatus> findFirstByUserId(String userId);
}
