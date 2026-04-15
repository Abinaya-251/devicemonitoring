package com.example.devicemanagement.dto;

import lombok.Builder;
import lombok.Data;

import java.util.Map;

@Data
@Builder
public class DashboardSummaryResponse {
    private long totalDevices;
    private long onlineDevices;
    private long offlineDevices;
    private long warningDevices;
    private long totalAlerts;
    private long openAlerts;
    private long criticalAlerts;
    private Map<String, Long> devicesByType;
    private Map<String, Long> alertsBySeverity;
}
