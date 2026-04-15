package com.example.devicemanagement.service;

import com.example.devicemanagement.dto.AlertRuleRequest;
import com.example.devicemanagement.model.Alert;
import com.example.devicemanagement.model.AlertRule;
import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;

import java.util.List;
import java.util.Map;

public interface AlertService {

    // Alert Rules
    AlertRule createRule(AlertRuleRequest request);

    List<AlertRule> getAllRules();

    AlertRule updateRule(String id, AlertRuleRequest request);

    void deleteRule(String id);

    // Alerts
    List<Alert> getAllAlerts();

    List<Alert> getAlertsByStatus(AlertStatus status);

    List<Alert> getAlertsBySeverity(AlertSeverity severity);

    Alert getAlertById(String id);

    Alert acknowledgeAlert(String id, String acknowledgedBy);

    Alert resolveAlert(String id);

    Map<String, Long> getAlertStats();
}
