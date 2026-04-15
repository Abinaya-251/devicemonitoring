package com.example.devicemanagement.messaging.consumer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.AlertEvent;
import com.example.devicemanagement.model.Alert;
import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;
import com.example.devicemanagement.repository.AlertRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertConsumer {

    private final AlertRepository alertRepository;
    private final RabbitTemplate rabbitTemplate;

    @RabbitListener(queues = RabbitMQConfig.ALERT_PROCESSING_QUEUE)
    public void processAlert(AlertEvent event) {
        log.info("Processing alert for device: {} - {}", event.getDeviceId(), event.getMessage());

        // Create alert record in MongoDB
        Alert alert = new Alert();
        alert.setRuleId(event.getRuleId());
        alert.setDeviceId(event.getDeviceId());
        alert.setDeviceName(event.getDeviceName());
        alert.setAlertType(event.getMessage());
        alert.setSeverity(AlertSeverity.valueOf(event.getSeverity()));
        alert.setMessage(event.getMessage());
        alert.setStatus(AlertStatus.OPEN);
        alert.setMetricValue(event.getMetricValue());
        alert.setThreshold(event.getThreshold());
        alert.setTriggeredAt(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());

        Alert saved = alertRepository.save(alert);
        log.info("Alert saved with ID: {}", saved.getId());

        // Forward to notification queue
        event.setAlertId(saved.getId());
        rabbitTemplate.convertAndSend(RabbitMQConfig.NOTIFICATION_EXCHANGE, "", event);
        log.info("Alert forwarded to notification queue");
    }
}
