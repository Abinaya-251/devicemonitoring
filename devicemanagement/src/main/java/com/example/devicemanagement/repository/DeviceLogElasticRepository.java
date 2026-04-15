package com.example.devicemanagement.repository;

import com.example.devicemanagement.model.DeviceLog;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface DeviceLogElasticRepository extends ElasticsearchRepository<DeviceLog, String> {

    List<DeviceLog> findByDeviceIdOrderByTimestampDesc(String deviceId);

    List<DeviceLog> findByLevel(String level);
}
