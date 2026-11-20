package com.arcadia.maintenance.api.service;

import com.arcadia.common.exceptions.ResourceNotFoundException;
import com.arcadia.maintenance.api.domain.ServiceStatus;
import com.arcadia.maintenance.api.domain.WarrantyStatus;
import com.arcadia.maintenance.api.dto.ServiceStatusDto;
import com.arcadia.maintenance.api.dto.WarrantyStatusDto;
import com.arcadia.maintenance.api.repository.ServiceStatusRepository;
import com.arcadia.maintenance.api.repository.WarrantyStatusRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Service
public class MaintenanceServiceImpl implements MaintenanceService {

    private final WarrantyStatusRepository warrantyStatusRepository;
    private final ServiceStatusRepository serviceStatusRepository;

    public MaintenanceServiceImpl(
            WarrantyStatusRepository warrantyStatusRepository,
            ServiceStatusRepository serviceStatusRepository
    ) {
        this.warrantyStatusRepository = warrantyStatusRepository;
        this.serviceStatusRepository = serviceStatusRepository;
    }

    @Override
    public WarrantyStatusDto getWarrantyStatus() {
        String userId = "user-3fccc663"; // Replace with the actual user ID
        WarrantyStatus warranty = warrantyStatusRepository
                .findFirstByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Warranty status was not found for user '" + userId + "'."
                ));

        LocalDate expiryDate = warranty.getWarrantyDate();
        int daysRemaining = (int) Math.max(0, ChronoUnit.DAYS.between(LocalDate.now(), expiryDate));
        return new WarrantyStatusDto(daysRemaining, expiryDate);
    }

    @Override
    public ServiceStatusDto getServiceStatus() {
        String userId = "user-3fccc663"; // Replace with the actual user ID
        ServiceStatus schedule = serviceStatusRepository.findFirstByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service status was not found for user '" + userId + "'."
                ));

        LocalDate nextServiceDate = schedule.getServiceScheduleDate();
        int daysRemaining = (int) Math.max(
                0,
                ChronoUnit.DAYS.between(LocalDate.now(), nextServiceDate)
        );
        return new ServiceStatusDto(
                nextServiceDate,
                daysRemaining,
                schedule.getTotalDistanceCovered().intValue()
        );
    }
}
