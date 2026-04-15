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
public class DeviceEvent implements Serializable {

    private String eventType;       // DEVICE_CREATED, DEVICE_UPDATED, DEVICE_STATUS_CHANGED, DEVICE_DELETED
    private String deviceId;
    private String deviceName;
    private String deviceType;
    private String ipAddress;
    private String status;
    private Instant timestamp;
}
