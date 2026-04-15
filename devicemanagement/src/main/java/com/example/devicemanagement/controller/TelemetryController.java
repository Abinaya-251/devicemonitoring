package com.example.devicemanagement.controller;

import com.example.devicemanagement.dto.TelemetryRequest;
import com.example.devicemanagement.model.TelemetryData;
import com.example.devicemanagement.service.TelemetryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/telemetry")
@RequiredArgsConstructor
public class TelemetryController {

    private final TelemetryService telemetryService;

    @PostMapping
    public ResponseEntity<String> ingestTelemetry(@Valid @RequestBody TelemetryRequest request) {
        telemetryService.ingestTelemetry(request);
        return ResponseEntity.ok("Telemetry ingested successfully");
    }

    @PostMapping("/batch")
    public ResponseEntity<String> ingestBatch(@RequestBody List<TelemetryRequest> requests) {
        telemetryService.ingestBatch(requests);
        return ResponseEntity.ok("Batch telemetry ingested: " + requests.size() + " records");
    }

    @GetMapping("/{deviceId}")
    public ResponseEntity<TelemetryData> getLatest(@PathVariable String deviceId) {
        TelemetryData data = telemetryService.getLatestTelemetry(deviceId);
        return data != null ? ResponseEntity.ok(data) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{deviceId}/history")
    public ResponseEntity<List<TelemetryData>> getHistory(
            @PathVariable String deviceId,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to) {
        return ResponseEntity.ok(telemetryService.getTelemetryHistory(deviceId, from, to));
    }
}
