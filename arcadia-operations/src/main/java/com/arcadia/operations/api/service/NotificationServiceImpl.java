package com.arcadia.operations.api.service;

import com.arcadia.operations.api.dto.CommandRequest;
import com.arcadia.operations.api.dto.SendNotificationResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;

@Service
public class NotificationServiceImpl implements NotificationService {

    @Value("${arcadia.fcm-token}")
    private String fcmToken;

    @Override
    public SendNotificationResponse sendNotification(CommandRequest request) {

        System.out.println("Received notification request in service: " + request);

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle("Test Message")
                        .setBody("Test message body")
                        .build())
                .build();

        SendNotificationResponse response = null;

        try {
            String messageId = FirebaseMessaging.getInstance(firebaseApp()).send(message);
            response = new SendNotificationResponse(messageId);
        } catch (FirebaseMessagingException e) {
            System.err.println("Error sending notification: " + e);
            throw new ResponseStatusException(HttpStatus.BAD_GATEWAY,
                    "Firebase could not send the notification.", e);
        } catch (IOException e) {
            System.err.println("Error loading Firebase credentials: " + e);
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Firebase credentials could not be loaded. Check the service-account configuration.", e);
        }

        return response;
    }

    private static synchronized FirebaseApp firebaseApp() throws IOException {
        FirebaseApp existingApp = defaultFirebaseApp();
        if (existingApp != null) {
            return existingApp;
        }

        GoogleCredentials credentials;
        try (var stream = new ClassPathResource("vehiclenotifications.json").getInputStream()) {
            credentials = GoogleCredentials.fromStream(stream);
        }

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(credentials)
                // Force standard Java transport layer to avoid Apache HttpClient conflicts
                .setHttpTransport(new NetHttpTransport())
                .build();

        try {
            return FirebaseApp.initializeApp(options);
        } catch (IllegalStateException e) {
            existingApp = defaultFirebaseApp();
            if (existingApp != null) {
                return existingApp;
            }
            throw e;
        }
    }

    private static FirebaseApp defaultFirebaseApp() {
        return FirebaseApp.getApps().stream()
                .filter(app -> FirebaseApp.DEFAULT_APP_NAME.equals(app.getName()))
                .findFirst()
                .orElse(null);
    }

}
