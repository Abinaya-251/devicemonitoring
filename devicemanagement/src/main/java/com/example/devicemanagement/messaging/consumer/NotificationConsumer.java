package com.example.devicemanagement.messaging.consumer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.AlertEvent;
import com.example.devicemanagement.model.Notification;
import com.example.devicemanagement.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void processNotification(AlertEvent event) {
        log.info("=== NOTIFICATION ===");
        log.info("Device: {} | Severity: {} | Message: {}",
                event.getDeviceId(), event.getSeverity(), event.getMessage());
        log.info("Metric Value: {} | Threshold: {}", event.getMetricValue(), event.getThreshold());
        log.info("====================");

        // Store notification record
        Notification notification = new Notification();
        notification.setAlertId(event.getAlertId());
        notification.setDeviceId(event.getDeviceId());
        notification.setDeviceName(event.getDeviceName());
        notification.setSeverity(event.getSeverity());
        notification.setMessage(event.getMessage());
        notification.setChannel("CONSOLE");
        notification.setDelivered(true);
        notification.setCreatedAt(Instant.now());

        notificationRepository.save(notification);
        log.info("Notification record saved");
    }
}
