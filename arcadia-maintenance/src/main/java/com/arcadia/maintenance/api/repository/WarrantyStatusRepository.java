package com.arcadia.maintenance.api.repository;

import com.arcadia.maintenance.api.domain.WarrantyStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface WarrantyStatusRepository extends JpaRepository<WarrantyStatus, UUID> {
    Optional<WarrantyStatus> findFirstByUserId(String userId);
}
