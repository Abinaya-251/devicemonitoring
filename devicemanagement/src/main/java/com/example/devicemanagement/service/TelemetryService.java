package com.example.devicemanagement.service;

import com.example.devicemanagement.dto.TelemetryRequest;
import com.example.devicemanagement.model.TelemetryData;

import java.util.List;

public interface TelemetryService {

    void ingestTelemetry(TelemetryRequest request);

    void ingestBatch(List<TelemetryRequest> requests);

    TelemetryData getLatestTelemetry(String deviceId);

    List<TelemetryData> getTelemetryHistory(String deviceId, String from, String to);
}
