package com.example.devicemanagement.controller;

import com.example.devicemanagement.dto.AlertRuleRequest;
import com.example.devicemanagement.model.AlertRule;
import com.example.devicemanagement.service.AlertService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts/rules")
@RequiredArgsConstructor
public class AlertRuleController {

    private final AlertService alertService;

    @PostMapping
    public ResponseEntity<AlertRule> createRule(@Valid @RequestBody AlertRuleRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(alertService.createRule(request));
    }

    @GetMapping
    public ResponseEntity<List<AlertRule>> getAllRules() {
        return ResponseEntity.ok(alertService.getAllRules());
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlertRule> updateRule(@PathVariable String id,
                                               @Valid @RequestBody AlertRuleRequest request) {
        return ResponseEntity.ok(alertService.updateRule(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRule(@PathVariable String id) {
        alertService.deleteRule(id);
        return ResponseEntity.noContent().build();
    }
}
