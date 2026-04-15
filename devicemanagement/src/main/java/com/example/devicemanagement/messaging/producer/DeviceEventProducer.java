package com.example.devicemanagement.messaging.producer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.DeviceEvent;
import com.example.devicemanagement.model.Device;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeviceEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendDeviceCreated(Device device) {
        DeviceEvent event = buildEvent("DEVICE_CREATED", device);
        send("device.registered", event);
    }

    public void sendDeviceUpdated(Device device) {
        DeviceEvent event = buildEvent("DEVICE_UPDATED", device);
        send("device.updated", event);
    }

    public void sendDeviceStatusChanged(Device device) {
        DeviceEvent event = buildEvent("DEVICE_STATUS_CHANGED", device);
        send("device.status.changed", event);
    }

    public void sendDeviceDeleted(Device device) {
        DeviceEvent event = buildEvent("DEVICE_DELETED", device);
        send("device.deleted", event);
    }

    private void send(String routingKey, DeviceEvent event) {
        log.info("Publishing device event: {} for device: {}", event.getEventType(), event.getDeviceId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.DEVICE_EXCHANGE, routingKey, event);
    }

    private DeviceEvent buildEvent(String eventType, Device device) {
        return DeviceEvent.builder()
                .eventType(eventType)
                .deviceId(device.getId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType() != null ? device.getDeviceType().name() : null)
                .ipAddress(device.getIpAddress())
                .status(device.getStatus() != null ? device.getStatus().name() : null)
                .timestamp(Instant.now())
                .build();
    }
}
