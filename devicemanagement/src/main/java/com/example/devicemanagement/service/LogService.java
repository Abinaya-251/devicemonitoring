package com.example.devicemanagement.service;

import com.example.devicemanagement.dto.LogSearchRequest;
import com.example.devicemanagement.model.DeviceLog;

import java.util.List;
import java.util.Map;

public interface LogService {

    void ingestLog(DeviceLog log);

    void ingestBatch(List<DeviceLog> logs);

    List<DeviceLog> searchLogs(LogSearchRequest request);

    List<DeviceLog> getLogsByDeviceId(String deviceId);

    Map<String, Long> getLogStats();
}
