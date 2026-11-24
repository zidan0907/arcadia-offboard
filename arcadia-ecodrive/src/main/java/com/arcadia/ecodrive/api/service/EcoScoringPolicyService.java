package com.arcadia.ecodrive.api.service;

import com.arcadia.ecodrive.api.dto.CreateEcoScoringPolicyRequest;
import com.arcadia.ecodrive.api.dto.EcoScoringPolicyResponse;

import java.util.List;
import java.util.UUID;

public interface EcoScoringPolicyService {

    EcoScoringPolicyResponse create(CreateEcoScoringPolicyRequest request);

    List<EcoScoringPolicyResponse> getAll();

    EcoScoringPolicyResponse getById(UUID policyId);
}
