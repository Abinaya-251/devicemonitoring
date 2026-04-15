package com.example.devicemanagement.model;

import com.example.devicemanagement.model.enums.DeviceStatus;
import com.example.devicemanagement.model.enums.DeviceType;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Document(collection = "devices")
@Data
public class Device {

    @Id
    private String id;

    private String deviceName;
    private DeviceType deviceType;

    private String macAddress;

    @Indexed(unique = true)
    private String ipAddress;

    private String firmwareVersion;
    private String location;
    private DeviceStatus status;
    private Instant lastHeartbeat;
    private Instant registeredAt;
    private List<String> tags;
    private Map<String, Object> configuration;
}