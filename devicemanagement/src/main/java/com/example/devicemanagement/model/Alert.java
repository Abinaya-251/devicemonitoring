package com.example.devicemanagement.model;

import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document(collection = "alerts")
@Data
public class Alert {

    @Id
    private String id;

    private String ruleId;
    private String deviceId;
    private String deviceName;
    private String alertType;
    private AlertSeverity severity;
    private String message;
    private AlertStatus status;
    private double metricValue;
    private double threshold;
    private Instant triggeredAt;
    private Instant acknowledgedAt;
    private Instant resolvedAt;
    private String acknowledgedBy;
}
