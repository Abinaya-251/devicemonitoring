package com.example.devicemanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "notifications")
@Data
public class Notification {

    @Id
    private String id;

    private String alertId;
    private String deviceId;
    private String deviceName;
    private String severity;
    private String message;
    private String channel;     // CONSOLE, EMAIL, WEBHOOK
    private boolean delivered;
    private Instant createdAt;
}
