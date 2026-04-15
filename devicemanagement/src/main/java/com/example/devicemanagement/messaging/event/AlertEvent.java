package com.example.devicemanagement.messaging.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AlertEvent implements Serializable {

    private String alertId;
    private String ruleId;
    private String deviceId;
    private String deviceName;
    private String severity;
    private String message;
    private double metricValue;
    private double threshold;
    private Instant timestamp;
}
