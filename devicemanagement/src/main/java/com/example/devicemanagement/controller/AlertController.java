package com.example.devicemanagement.controller;

import com.example.devicemanagement.model.Alert;
import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;
import com.example.devicemanagement.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/alerts")
@RequiredArgsConstructor
public class AlertController {

    private final AlertService alertService;

    @GetMapping
    public ResponseEntity<List<Alert>> getAllAlerts(
            @RequestParam(required = false) AlertStatus status,
            @RequestParam(required = false) AlertSeverity severity) {

        if (status != null) {
            return ResponseEntity.ok(alertService.getAlertsByStatus(status));
        }
        if (severity != null) {
            return ResponseEntity.ok(alertService.getAlertsBySeverity(severity));
        }
        return ResponseEntity.ok(alertService.getAllAlerts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Alert> getAlertById(@PathVariable String id) {
        return ResponseEntity.ok(alertService.getAlertById(id));
    }

    @PatchMapping("/{id}/acknowledge")
    public ResponseEntity<Alert> acknowledgeAlert(@PathVariable String id,
                                                  @RequestParam String acknowledgedBy) {
        return ResponseEntity.ok(alertService.acknowledgeAlert(id, acknowledgedBy));
    }

    @PatchMapping("/{id}/resolve")
    public ResponseEntity<Alert> resolveAlert(@PathVariable String id) {
        return ResponseEntity.ok(alertService.resolveAlert(id));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(alertService.getAlertStats());
    }
}
