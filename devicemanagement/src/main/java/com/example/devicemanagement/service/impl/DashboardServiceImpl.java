package com.example.devicemanagement.service.impl;

import com.example.devicemanagement.dto.DashboardSummaryResponse;
import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;
import com.example.devicemanagement.model.enums.DeviceStatus;
import com.example.devicemanagement.model.enums.DeviceType;
import com.example.devicemanagement.repository.AlertRepository;
import com.example.devicemanagement.repository.DeviceRepository;
import com.example.devicemanagement.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final DeviceRepository deviceRepository;
    private final AlertRepository alertRepository;

    @Override
    public DashboardSummaryResponse getSummary() {
        Map<String, Long> devicesByType = new HashMap<>();
        for (DeviceType type : DeviceType.values()) {
            devicesByType.put(type.name(), deviceRepository.countByDeviceType(type));
        }

        Map<String, Long> alertsBySeverity = new HashMap<>();
        for (AlertSeverity severity : AlertSeverity.values()) {
            alertsBySeverity.put(severity.name(), alertRepository.countBySeverity(severity));
        }

        return DashboardSummaryResponse.builder()
                .totalDevices(deviceRepository.count())
                .onlineDevices(deviceRepository.countByStatus(DeviceStatus.ONLINE))
                .offlineDevices(deviceRepository.countByStatus(DeviceStatus.OFFLINE))
                .warningDevices(deviceRepository.countByStatus(DeviceStatus.WARNING))
                .totalAlerts(alertRepository.count())
                .openAlerts(alertRepository.countByStatus(AlertStatus.OPEN))
                .criticalAlerts(alertRepository.countByStatusAndSeverity(AlertStatus.OPEN, AlertSeverity.CRITICAL))
                .devicesByType(devicesByType)
                .alertsBySeverity(alertsBySeverity)
                .build();
    }
}
