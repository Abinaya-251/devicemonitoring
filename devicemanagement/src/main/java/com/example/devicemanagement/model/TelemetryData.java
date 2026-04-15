package com.example.devicemanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.Map;

@Document(indexName = "telemetry", createIndex = false)
@Data
public class TelemetryData {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String deviceId;

    @Field(type = FieldType.Date)
    private Instant timestamp;

    @Field(type = FieldType.Double)
    private double cpuUsage;

    @Field(type = FieldType.Double)
    private double memoryUsage;

    @Field(type = FieldType.Double)
    private double bandwidthIn;

    @Field(type = FieldType.Double)
    private double bandwidthOut;

    @Field(type = FieldType.Integer)
    private int activeClients;

    @Field(type = FieldType.Double)
    private double temperature;

    @Field(type = FieldType.Long)
    private long uptime;

    @Field(type = FieldType.Double)
    private double packetLoss;

    @Field(type = FieldType.Double)
    private double latency;
}
