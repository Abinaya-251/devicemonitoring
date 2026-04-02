package com.example.devicemanagement.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.example.devicemanagement.model.Device;

public interface DeviceRepository extends MongoRepository<Device, String> {
}