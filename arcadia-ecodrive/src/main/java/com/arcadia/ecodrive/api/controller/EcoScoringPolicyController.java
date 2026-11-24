package com.arcadia.ecodrive.api.controller;

import com.arcadia.ecodrive.api.dto.CreateEcoScoringPolicyRequest;
import com.arcadia.ecodrive.api.dto.EcoScoringPolicyResponse;
import com.arcadia.ecodrive.api.service.EcoScoringPolicyService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ecodrive/scoring-policies")
public class EcoScoringPolicyController {

    private final EcoScoringPolicyService service;

    public EcoScoringPolicyController(EcoScoringPolicyService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<EcoScoringPolicyResponse> create(
            @Valid @RequestBody CreateEcoScoringPolicyRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request));
    }

    @GetMapping
    public ResponseEntity<List<EcoScoringPolicyResponse>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    @GetMapping("/{policyId}")
    public ResponseEntity<EcoScoringPolicyResponse> getById(
            @PathVariable UUID policyId
    ) {
        return ResponseEntity.ok(service.getById(policyId));
    }
}
