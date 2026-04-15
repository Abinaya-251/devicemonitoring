package com.example.devicemanagement.messaging.consumer;

import com.example.devicemanagement.config.RabbitMQConfig;
import com.example.devicemanagement.messaging.event.TelemetryEvent;
import com.example.devicemanagement.model.AlertRule;
import com.example.devicemanagement.model.TelemetryData;
import com.example.devicemanagement.messaging.event.AlertEvent;
import com.example.devicemanagement.messaging.producer.AlertEventProducer;
import com.example.devicemanagement.repository.AlertRuleRepository;
import com.example.devicemanagement.repository.TelemetryElasticRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetryConsumer {

    private final TelemetryElasticRepository telemetryRepository;
    private final AlertRuleRepository alertRuleRepository;
    private final AlertEventProducer alertEventProducer;

    @RabbitListener(queues = RabbitMQConfig.TELEMETRY_INGESTION_QUEUE)
    public void processTelemetry(TelemetryEvent event) {
        log.info("Received telemetry for device: {}", event.getDeviceId());

        // 1. Store in Elasticsearch
        TelemetryData data = new TelemetryData();
        data.setDeviceId(event.getDeviceId());
        data.setTimestamp(event.getTimestamp() != null ? event.getTimestamp() : Instant.now());
        data.setCpuUsage(event.getCpuUsage());
        data.setMemoryUsage(event.getMemoryUsage());
        data.setBandwidthIn(event.getBandwidthIn());
        data.setBandwidthOut(event.getBandwidthOut());
        data.setActiveClients(event.getActiveClients());
        data.setTemperature(event.getTemperature());
        data.setUptime(event.getUptime());
        data.setPacketLoss(event.getPacketLoss());
        data.setLatency(event.getLatency());

        telemetryRepository.save(data);
        log.info("Telemetry saved to Elasticsearch for device: {}", event.getDeviceId());

        // 2. Evaluate alert rules
        evaluateAlertRules(event);
    }

    private void evaluateAlertRules(TelemetryEvent event) {
        List<AlertRule> rules = alertRuleRepository.findByEnabled(true);

        Map<String, Double> metrics = Map.of(
                "cpuUsage", event.getCpuUsage(),
                "memoryUsage", event.getMemoryUsage(),
                "bandwidthIn", event.getBandwidthIn(),
                "bandwidthOut", event.getBandwidthOut(),
                "temperature", event.getTemperature(),
                "packetLoss", event.getPacketLoss(),
                "latency", event.getLatency()
        );

        for (AlertRule rule : rules) {
            Double metricValue = metrics.get(rule.getMetric());
            if (metricValue == null) continue;

            boolean breached = switch (rule.getCondition()) {
                case "GREATER_THAN" -> metricValue > rule.getThreshold();
                case "LESS_THAN" -> metricValue < rule.getThreshold();
                case "EQUALS" -> Math.abs(metricValue - rule.getThreshold()) < 0.001;
                default -> false;
            };

            if (breached) {
                log.warn("Alert rule '{}' breached! {} = {} (threshold: {})",
                        rule.getRuleName(), rule.getMetric(), metricValue, rule.getThreshold());

                AlertEvent alertEvent = AlertEvent.builder()
                        .ruleId(rule.getId())
                        .deviceId(event.getDeviceId())
                        .severity(rule.getSeverity().name())
                        .message(String.format("%s at %.1f exceeds threshold of %.1f",
                                rule.getMetric(), metricValue, rule.getThreshold()))
                        .metricValue(metricValue)
                        .threshold(rule.getThreshold())
                        .timestamp(Instant.now())
                        .build();

                alertEventProducer.sendAlertTriggered(alertEvent);
            }
        }
    }
}
