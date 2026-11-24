package com.arcadia.ecodrive.api.repository;

import com.arcadia.ecodrive.api.domain.EcoScoringPolicy;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EcoScoringPolicyRepository
        extends JpaRepository<EcoScoringPolicy, UUID> {
}
