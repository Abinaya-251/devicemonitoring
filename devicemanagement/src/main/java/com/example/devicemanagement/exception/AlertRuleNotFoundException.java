package com.example.devicemanagement.exception;

public class AlertRuleNotFoundException extends RuntimeException {
    public AlertRuleNotFoundException(String id) {
        super("Alert rule not found with id: " + id);
    }
}
