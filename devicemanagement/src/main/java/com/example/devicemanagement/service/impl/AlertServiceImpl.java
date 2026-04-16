package com.example.devicemanagement.service.impl;

import com.example.devicemanagement.dto.AlertRuleRequest;
import com.example.devicemanagement.exception.AlertRuleNotFoundException;
import com.example.devicemanagement.model.Alert;
import com.example.devicemanagement.model.AlertRule;
import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;
import com.example.devicemanagement.repository.AlertRepository;
import com.example.devicemanagement.repository.AlertRuleRepository;
import com.example.devicemanagement.service.AlertService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRepository alertRepository;
    private final AlertRuleRepository alertRuleRepository;

    // ========== ALERT RULES ==========

    @Override
    @CacheEvict(value = "alertRules", allEntries = true)
    public AlertRule createRule(AlertRuleRequest request) {
        AlertRule rule = new AlertRule();
        rule.setRuleName(request.getRuleName());
        rule.setMetric(request.getMetric());
        rule.setCondition(request.getCondition());
        rule.setThreshold(request.getThreshold());
        rule.setSeverity(request.getSeverity());
        rule.setDeviceType(request.getDeviceType());
        rule.setEnabled(request.isEnabled());
        rule.setCooldownMinutes(request.getCooldownMinutes());

        AlertRule saved = alertRuleRepository.save(rule);
        log.info("Alert rule created: {} ({})", saved.getRuleName(), saved.getId());
        return saved;
    }

    @Override
    @Cacheable(value = "alertRules")
    public List<AlertRule> getAllRules() {
        return alertRuleRepository.findAll();
    }

    @Override
    @CacheEvict(value = "alertRules", allEntries = true)
    public AlertRule updateRule(String id, AlertRuleRequest request) {
        AlertRule rule = alertRuleRepository.findById(id)
                .orElseThrow(() -> new AlertRuleNotFoundException(id));

        rule.setRuleName(request.getRuleName());
        rule.setMetric(request.getMetric());
        rule.setCondition(request.getCondition());
        rule.setThreshold(request.getThreshold());
        rule.setSeverity(request.getSeverity());
        rule.setDeviceType(request.getDeviceType());
        rule.setEnabled(request.isEnabled());
        rule.setCooldownMinutes(request.getCooldownMinutes());

        return alertRuleRepository.save(rule);
    }

    @Override
    @CacheEvict(value = "alertRules", allEntries = true)
    public void deleteRule(String id) {
        if (!alertRuleRepository.existsById(id)) {
            throw new AlertRuleNotFoundException(id);
        }
        alertRuleRepository.deleteById(id);
        log.info("Alert rule deleted: {}", id);
    }

    // ========== ALERTS ==========

    @Override
    public List<Alert> getAllAlerts() {
        return alertRepository.findAll();
    }

    @Override
    public List<Alert> getAlertsByStatus(AlertStatus status) {
        return alertRepository.findByStatus(status);
    }

    @Override
    public List<Alert> getAlertsBySeverity(AlertSeverity severity) {
        return alertRepository.findBySeverity(severity);
    }

    @Override
    public Alert getAlertById(String id) {
        return alertRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Alert not found: " + id));
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "alertStats", allEntries = true),
            @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Alert acknowledgeAlert(String id, String acknowledgedBy) {
        Alert alert = getAlertById(id);
        alert.setStatus(AlertStatus.ACKNOWLEDGED);
        alert.setAcknowledgedAt(Instant.now());
        alert.setAcknowledgedBy(acknowledgedBy);
        log.info("Alert acknowledged: {} by {}", id, acknowledgedBy);
        return alertRepository.save(alert);
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "alertStats", allEntries = true),
            @CacheEvict(value = "dashboard", allEntries = true)
    })
    public Alert resolveAlert(String id) {
        Alert alert = getAlertById(id);
        alert.setStatus(AlertStatus.RESOLVED);
        alert.setResolvedAt(Instant.now());
        log.info("Alert resolved: {}", id);
        return alertRepository.save(alert);
    }

    @Override
    @Cacheable(value = "alertStats")
    public Map<String, Long> getAlertStats() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("total", alertRepository.count());
        for (AlertStatus status : AlertStatus.values()) {
            stats.put(status.name().toLowerCase(), alertRepository.countByStatus(status));
        }
        for (AlertSeverity severity : AlertSeverity.values()) {
            stats.put("severity_" + severity.name().toLowerCase(), alertRepository.countBySeverity(severity));
        }
        return stats;
    }
}
