package com.example.devicemanagement.repository;

import com.example.devicemanagement.model.TelemetryData;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface TelemetryElasticRepository extends ElasticsearchRepository<TelemetryData, String> {

    List<TelemetryData> findByDeviceIdOrderByTimestampDesc(String deviceId);
}
