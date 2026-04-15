package com.example.devicemanagement.messaging.consumer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.DeviceEvent;
import com.example.devicemanagement.model.DeviceLog;
import com.example.devicemanagement.repository.DeviceLogElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class LogEventConsumer {

    private final DeviceLogElasticRepository deviceLogRepository;

    @RabbitListener(queues = RabbitMQConfig.DEVICE_EVENTS_QUEUE)
    public void processDeviceEvent(DeviceEvent event) {
        log.info("Logging device event: {} for device: {}", event.getEventType(), event.getDeviceId());

        DeviceLog deviceLog = new DeviceLog();
        deviceLog.setDeviceId(event.getDeviceId());
        deviceLog.setDeviceName(event.getDeviceName());
        deviceLog.setTimestamp(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());
        deviceLog.setLevel("INFO");
        deviceLog.setSource("SYSTEM");
        deviceLog.setMessage("Device event: " + event.getEventType() + " - " + event.getDeviceName());
        deviceLog.setMetadata(Map.of(
                "eventType", event.getEventType(),
                "ipAddress", event.getIpAddress() != null ? event.getIpAddress() : "",
                "status", event.getStatus() != null ? event.getStatus() : ""
        ));

        deviceLogRepository.save(deviceLog);
        log.info("Device event logged to Elasticsearch");
    }
}
