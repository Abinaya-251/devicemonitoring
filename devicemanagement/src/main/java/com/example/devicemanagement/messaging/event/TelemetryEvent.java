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
public class TelemetryEvent implements Serializable {

    private String deviceId;
    private Instant timestamp;
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
