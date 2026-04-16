package com.example.devicemanagement.dto;

import com.example.devicemanagement.model.enums.DeviceStatus;
import com.example.devicemanagement.model.enums.DeviceType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DeviceResponse {

    private String id;
    private String deviceName;
    private DeviceType deviceType;
    private String macAddress;
    private String ipAddress;
    private String firmwareVersion;
    private String location;
    private DeviceStatus status;
    private Instant lastHeartbeat;
    private Instant registeredAt;
    private List<String> tags;
    private Map<String, Object> configuration;
}
