package com.example.devicemanagement.simulator;

import com.example.devicemanagement.dto.TelemetryRequest;
import com.example.devicemanagement.model.Device;
import com.example.devicemanagement.repository.DeviceRepository;
import com.example.devicemanagement.service.TelemetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
public class TelemetrySimulator {

    private final DeviceRepository deviceRepository;
    private final TelemetryService telemetryService;

    /**
     * Simulates telemetry data from all registered devices every 30 seconds.
     * In production, real SNMP/MQTT agents would send this data.
     */
    @Scheduled(fixedRate = 30000, initialDelay = 10000)
    public void simulateTelemetry() {
        List<Device> devices = deviceRepository.findAll();
        if (devices.isEmpty()) {
            return;
        }

        log.info("Simulating telemetry for {} devices", devices.size());

        for (Device device : devices) {
            TelemetryRequest request = new TelemetryRequest();
            request.setDeviceId(device.getId());
            request.setCpuUsage(randomDouble(10, 95));
            request.setMemoryUsage(randomDouble(20, 85));
            request.setBandwidthIn(randomDouble(50, 800));
            request.setBandwidthOut(randomDouble(30, 500));
            request.setActiveClients(ThreadLocalRandom.current().nextInt(0, 100));
            request.setTemperature(randomDouble(35, 75));
            request.setUptime(ThreadLocalRandom.current().nextLong(3600, 864000));
            request.setPacketLoss(randomDouble(0, 5));
            request.setLatency(randomDouble(1, 50));

            try {
                telemetryService.ingestTelemetry(request);
            } catch (Exception e) {
                log.warn("Failed to simulate telemetry for device {}: {}", device.getId(), e.getMessage());
            }
        }
    }

    private double randomDouble(double min, double max) {
        return Math.round(ThreadLocalRandom.current().nextDouble(min, max) * 10.0) / 10.0;
    }
}
