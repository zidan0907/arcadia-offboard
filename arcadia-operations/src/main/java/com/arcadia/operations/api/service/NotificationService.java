package com.arcadia.operations.api.service;

import com.arcadia.operations.api.dto.CommandRequest;
import com.arcadia.operations.api.dto.SendNotificationResponse;

public interface NotificationService {

    SendNotificationResponse sendNotification(CommandRequest request);
}
