package com.example.devicemanagement.messaging.producer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.TelemetryEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryEventProducer {

    private final RabbitTemplate rabbitTemplate;

    public void sendTelemetry(TelemetryEvent event) {
        log.info("Publishing telemetry for device: {}", event.getDeviceId());
        rabbitTemplate.convertAndSend(
                RabbitMQConfig.TELEMETRY_EXCHANGE,
                RabbitMQConfig.TELEMETRY_ROUTING_KEY,
                event
        );
    }
}
