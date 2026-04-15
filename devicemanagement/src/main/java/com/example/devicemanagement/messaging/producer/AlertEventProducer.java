package com.example.devicemanagement.messaging.producer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.AlertEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AlertEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendAlertTriggered(AlertEvent event) {
        log.info("Publishing alert triggered for device: {} - {}", event.getDeviceId(), event.getMessage());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERT_EXCHANGE,
                "alert.triggered",
                event
        );
    }

    public void sendAlertResolved(AlertEvent event) {
        log.info("Publishing alert resolved for device: {}", event.getDeviceId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.ALERT_EXCHANGE,
                "alert.resolved",
                event
        );
    }
}
