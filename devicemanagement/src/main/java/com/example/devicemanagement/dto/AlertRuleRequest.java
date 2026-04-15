package com.example.devicemanagement.dto;

import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.DeviceType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AlertRuleRequest {

    @NotBlank(message = "Rule name is required")
    private String ruleName;

    @NotBlank(message = "Metric is required")
    private String metric;

    @NotBlank(message = "Condition is required")
    private String condition;       // GREATER_THAN, LESS_THAN, EQUALS

    @NotNull(message = "Threshold is required")
    private Double threshold;

    @NotNull(message = "Severity is required")
    private AlertSeverity severity;

    private DeviceType deviceType;  // null means ALL
    private boolean enabled = true;
    private int cooldownMinutes = 5;
}
