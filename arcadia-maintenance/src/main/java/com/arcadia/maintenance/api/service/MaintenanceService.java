package com.arcadia.maintenance.api.service;

import com.arcadia.maintenance.api.dto.ServiceStatusDto;
import com.arcadia.maintenance.api.dto.WarrantyStatusDto;

public interface MaintenanceService {

    WarrantyStatusDto getWarrantyStatus();

    ServiceStatusDto getServiceStatus();
}

