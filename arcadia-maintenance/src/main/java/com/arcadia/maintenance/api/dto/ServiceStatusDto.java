package com.arcadia.maintenance.api.dto;

import java.time.LocalDate;

public record ServiceStatusDto(
        LocalDate nextServiceDate,
        int daysRemaining,
        int currentOdometerReading) {
}
