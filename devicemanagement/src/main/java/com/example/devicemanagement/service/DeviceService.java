package com.example.devicemanagement.service;

import java.util.List;
import com.example.devicemanagement.dto.DeviceRequest;
import com.example.devicemanagement.model.Device;

public interface DeviceService {

    Device createDevice(DeviceRequest request);

    List<Device> getAllDevices();
}
