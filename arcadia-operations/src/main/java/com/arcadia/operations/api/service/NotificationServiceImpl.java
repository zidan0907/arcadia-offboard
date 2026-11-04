package com.arcadia.operations.api.service;

import com.arcadia.operations.api.dto.CommandRequest;
import com.arcadia.operations.api.dto.PushNotificationTemplate;
import com.arcadia.operations.api.dto.SendNotificationResponse;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class NotificationServiceImpl implements NotificationService {

    private static final Pattern TEMPLATE_PLACEHOLDER = Pattern.compile("\\{\\{([A-Za-z][A-Za-z0-9]*)\\}\\}");

    private final ObjectMapper objectMapper;

    @Value("${arcadia.fcm-token}")
    private String fcmToken;

    public NotificationServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public SendNotificationResponse sendNotification(CommandRequest request) {

        PushNotificationTemplate template;
        try {
            template = renderTemplate(request);
        } catch (IOException e) {
            System.err.println("Error loading notification template: " + e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Notification template could not be loaded.", e);
        }

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(Notification.builder()
                        .setTitle(template.title())
                        .setBody(template.body())
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

    private PushNotificationTemplate renderTemplate(CommandRequest request) throws IOException {
        String templatePath = switch (request.command()) {
            case DOOR_LOCK -> "templates/door-locked-notification.json";
            case DOOR_UNLOCK -> "templates/door-unlocked-notification.json";
            case CLIMATE_START, CLIMATE_STOP -> throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "No notification template is configured for this command.");
        };

        PushNotificationTemplate template;
        try (var input = new ClassPathResource(templatePath).getInputStream()) {
            template = objectMapper.readValue(input, PushNotificationTemplate.class);
        }

        Map<String, String> values = new HashMap<>();
        request.params().forEach((key, value) -> values.put(key, value == null ? null : value.toString()));
        values.put("requestedBy", request.requestedBy());
        values.put("timestamp", request.timestamp().toString());

        return new PushNotificationTemplate(
                template.title(),
                replaceTemplateValues(template.body(), values));
    }

    private String replaceTemplateValues(String template, Map<String, String> values) {
        Matcher matcher = TEMPLATE_PLACEHOLDER.matcher(template);
        StringBuilder rendered = new StringBuilder();
        while (matcher.find()) {
            String name = matcher.group(1);
            String value = values.get(name);
            if (value == null || value.isBlank()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "Missing notification template value: " + name);
            }
            matcher.appendReplacement(rendered, Matcher.quoteReplacement(value));
        }
        matcher.appendTail(rendered);
        return rendered.toString();
    }


}
