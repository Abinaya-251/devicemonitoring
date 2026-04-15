package com.example.devicemanagement.dto;

import lombok.Data;

@Data
public class LogSearchRequest {
    private String query;           // full-text search
    private String deviceId;
    private String level;           // DEBUG, INFO, WARN, ERROR, FATAL
    private String source;          // SYSTEM, NETWORK, SECURITY, CONFIG
    private String from;            // ISO date string
    private String to;              // ISO date string
    private int page = 0;
    private int size = 20;
}
