package com.example.devicemanagement.model;

import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.DeviceType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "alert_rules")
@Data
public class AlertRule {

    @Id
    private String id;

    private String ruleName;
    private String metric;
    private String condition;       // GREATER_THAN, LESS_THAN, EQUALS
    private double threshold;
    private AlertSeverity severity;
    private DeviceType deviceType;  // null means ALL device types
    private boolean enabled;
    private int cooldownMinutes;
}
