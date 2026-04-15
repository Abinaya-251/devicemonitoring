package com.example.devicemanagement.controller;

import com.example.devicemanagement.dto.LogSearchRequest;
import com.example.devicemanagement.model.DeviceLog;
import com.example.devicemanagement.service.LogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/logs")
@RequiredArgsConstructor
public class LogController {

    private final LogService logService;

    @PostMapping
    public ResponseEntity<String> ingestLog(@RequestBody DeviceLog log) {
        logService.ingestLog(log);
        return ResponseEntity.ok("Log ingested");
    }

    @PostMapping("/batch")
    public ResponseEntity<String> ingestBatch(@RequestBody List<DeviceLog> logs) {
        logService.ingestBatch(logs);
        return ResponseEntity.ok("Batch ingested: " + logs.size() + " logs");
    }

    @GetMapping("/search")
    public ResponseEntity<List<DeviceLog>> searchLogs(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String deviceId,
            @RequestParam(required = false) String level,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {

        LogSearchRequest request = new LogSearchRequest();
        request.setQuery(q);
        request.setDeviceId(deviceId);
        request.setLevel(level);
        request.setSource(source);
        request.setFrom(from);
        request.setTo(to);
        request.setPage(page);
        request.setSize(size);

        return ResponseEntity.ok(logService.searchLogs(request));
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<DeviceLog>> getLogsByDevice(@PathVariable String deviceId) {
        return ResponseEntity.ok(logService.getLogsByDeviceId(deviceId));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(logService.getLogStats());
    }
}
