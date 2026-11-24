package com.arcadia.ecodrive.api.controller;

import com.arcadia.ecodrive.api.dto.VehicleDrivingAnalysisResponse;
import com.arcadia.ecodrive.api.service.VehicleEcoAnalyticsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/ecodrive/vehicles")
public class VehicleDrivingAnalysisController {

    private final VehicleEcoAnalyticsService service;

    public VehicleDrivingAnalysisController(VehicleEcoAnalyticsService service) {
        this.service = service;
    }

    @GetMapping("/{vin}/driving-analysis")
    public VehicleDrivingAnalysisResponse getDrivingAnalysis(
            @PathVariable String vin,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime from,
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            OffsetDateTime to,
            @RequestParam(required = false)
            UUID policyId
    ) {
        return service.getDrivingAnalysis(vin, from, to, policyId);
    }
}