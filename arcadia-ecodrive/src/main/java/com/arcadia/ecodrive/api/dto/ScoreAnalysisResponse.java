package com.arcadia.ecodrive.api.dto;

import java.math.BigDecimal;
import java.util.List;

public record ScoreAnalysisResponse(
        BigDecimal score,
        String category,
        List<String> recommendations
) {
}
