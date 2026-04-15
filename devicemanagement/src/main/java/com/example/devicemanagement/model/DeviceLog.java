package com.example.devicemanagement.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.Instant;
import java.util.Map;

@Document(indexName = "device-logs", createIndex = false)
@Data
public class DeviceLog {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String deviceId;

    @Field(type = FieldType.Keyword)
    private String deviceName;

    @Field(type = FieldType.Date)
    private Instant timestamp;

    @Field(type = FieldType.Keyword)
    private String level;       // DEBUG, INFO, WARN, ERROR, FATAL

    @Field(type = FieldType.Keyword)
    private String source;      // SYSTEM, NETWORK, SECURITY, CONFIG

    @Field(type = FieldType.Text)
    private String message;

    @Field(type = FieldType.Object)
    private Map<String, Object> metadata;
}
