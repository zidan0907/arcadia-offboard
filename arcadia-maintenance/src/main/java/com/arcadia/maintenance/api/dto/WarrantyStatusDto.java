package com.arcadia.maintenance.api.dto;

import java.time.LocalDate;

public record WarrantyStatusDto(
        int daysRemaining,
        LocalDate warrantyExpiryDate) {
}
