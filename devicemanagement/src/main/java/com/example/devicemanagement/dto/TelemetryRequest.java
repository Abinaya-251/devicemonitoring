package com.example.devicemanagement.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class TelemetryRequest {

    @NotBlank(message = "Device ID is required")
    private String deviceId;

    private double cpuUsage;
    private double memoryUsage;
    private double bandwidthIn;
    private double bandwidthOut;
    private int activeClients;
    private double temperature;
    private long uptime;
    private double packetLoss;
    private double latency;
}
