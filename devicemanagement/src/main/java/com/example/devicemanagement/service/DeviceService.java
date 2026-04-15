package com.example.devicemanagement.service;

import com.example.devicemanagement.dto.DeviceRequest;
import com.example.devicemanagement.dto.DeviceResponse;
import com.example.devicemanagement.model.Device;
import com.example.devicemanagement.model.enums.DeviceStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface DeviceService {

    DeviceResponse createDevice(DeviceRequest request);

    List<DeviceResponse> getAllDevices();

    DeviceResponse getDeviceById(String id);

    DeviceResponse updateDevice(String id, DeviceRequest request);

    void deleteDevice(String id);

    DeviceResponse updateDeviceStatus(String id, DeviceStatus status);

    Page<DeviceResponse> searchDevices(String name, Pageable pageable);

    Map<String, Long> getDeviceStats();
}
