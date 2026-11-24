package com.arcadia.ecodrive.api.service;

import com.arcadia.ecodrive.api.dto.VehicleDrivingAnalysisResponse;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface VehicleEcoAnalyticsService {

    VehicleDrivingAnalysisResponse getDrivingAnalysis(
            String vin,
            OffsetDateTime from,
            OffsetDateTime to,
            UUID policyId
    );
}
