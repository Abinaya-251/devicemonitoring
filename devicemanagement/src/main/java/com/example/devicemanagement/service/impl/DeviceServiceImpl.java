package com.example.devicemanagement.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import com.example.devicemanagement.dto.DeviceRequest;
import com.example.devicemanagement.model.Device;
import com.example.devicemanagement.repository.DeviceRepository;
import com.example.devicemanagement.service.DeviceService;

@Service
@RequiredArgsConstructor
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository repository;

    @Override
    public Device createDevice(DeviceRequest request) {

        Device device = new Device();
        device.setName(request.getName());
        device.setType(request.getType());
        device.setIpAddress(request.getIpAddress());
        device.setLocation(request.getLocation());
        device.setStatus(request.getStatus());

        return repository.save(device);
    }

    @Override
    public List<Device> getAllDevices() {
        return repository.findAll();
    }
}