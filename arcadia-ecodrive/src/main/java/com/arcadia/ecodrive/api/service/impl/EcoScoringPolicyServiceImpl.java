package com.arcadia.ecodrive.api.service.impl;

import com.arcadia.ecodrive.api.domain.EcoScoringPolicy;
import com.arcadia.ecodrive.api.dto.CreateEcoScoringPolicyRequest;
import com.arcadia.ecodrive.api.dto.EcoScoringPolicyResponse;
import com.arcadia.ecodrive.api.repository.EcoScoringPolicyRepository;
import com.arcadia.ecodrive.api.service.EcoScoringPolicyService;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class EcoScoringPolicyServiceImpl implements EcoScoringPolicyService {

    private final EcoScoringPolicyRepository repository;

    public EcoScoringPolicyServiceImpl(EcoScoringPolicyRepository repository) {
        this.repository = repository;
    }

    @Override
    @Transactional
    public EcoScoringPolicyResponse create(CreateEcoScoringPolicyRequest request) {
        validateThresholds(request);

        EcoScoringPolicy policy = EcoScoringPolicy.create(
                request.policyName().trim(),
                request.baseScore(),
                request.harshBrakingPenalty(),
                request.harshAccelerationPenalty(),
                request.speedingPenalty(),
                request.idlingPenaltyPerMinute(),
                request.fairMinimum(),
                request.goodMinimum(),
                request.excellentMinimum()
        );

        return toResponse(repository.save(policy));
    }

    @Override
    public List<EcoScoringPolicyResponse> getAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Override
    public EcoScoringPolicyResponse getById(UUID policyId) {
        EcoScoringPolicy policy = repository.findById(policyId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Scoring policy not found"
                ));
        return toResponse(policy);
    }

    private void validateThresholds(CreateEcoScoringPolicyRequest request) {
        BigDecimal base = request.baseScore();

        if (request.fairMinimum().compareTo(request.goodMinimum()) >= 0
                || request.goodMinimum().compareTo(request.excellentMinimum()) >= 0
                || request.excellentMinimum().compareTo(base) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Thresholds must satisfy fair < good < excellent <= base score"
            );
        }

        if (request.harshBrakingPenalty().compareTo(base) > 0
                || request.harshAccelerationPenalty().compareTo(base) > 0
                || request.speedingPenalty().compareTo(base) > 0
                || request.idlingPenaltyPerMinute().compareTo(base) > 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Each penalty must not exceed the base score"
            );
        }
    }

    private EcoScoringPolicyResponse toResponse(EcoScoringPolicy policy) {
        return new EcoScoringPolicyResponse(
                policy.getId(),
                policy.getPolicyName(),
                policy.getBaseScore(),
                policy.getHarshBrakingPenalty(),
                policy.getHarshAccelerationPenalty(),
                policy.getSpeedingPenalty(),
                policy.getIdlingPenaltyPerMinute(),
                policy.getFairMinimum(),
                policy.getGoodMinimum(),
                policy.getExcellentMinimum(),
                policy.getCreatedAt()
        );
    }
}
