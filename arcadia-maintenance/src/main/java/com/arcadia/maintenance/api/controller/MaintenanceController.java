package com.arcadia.maintenance.api.controller;

import com.arcadia.maintenance.api.dto.ServiceStatusDto;
import com.arcadia.maintenance.api.dto.WarrantyStatusDto;
import com.arcadia.maintenance.api.service.MaintenanceService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class MaintenanceController {

    private final MaintenanceService service;

    public MaintenanceController(MaintenanceService service) {
        this.service = service;
    }

    @GetMapping("/warranty/status")
    public WarrantyStatusDto getWarrantyStatus() {
        return service.getWarrantyStatus();
    }

    @GetMapping("/service/status")
    public ServiceStatusDto getServiceStatus() {
        return service.getServiceStatus();
    }
}
