package com.example.devicemanagement.repository;

import com.example.devicemanagement.model.Device;
import com.example.devicemanagement.model.enums.DeviceStatus;
import com.example.devicemanagement.model.enums.DeviceType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device, String> {

    Optional<Device> findByIpAddress(String ipAddress);

    List<Device> findByStatus(DeviceStatus status);

    List<Device> findByDeviceType(DeviceType deviceType);

    List<Device> findByLocation(String location);

    Page<Device> findByDeviceNameContainingIgnoreCase(String name, Pageable pageable);

    long countByStatus(DeviceStatus status);

    long countByDeviceType(DeviceType deviceType);
}