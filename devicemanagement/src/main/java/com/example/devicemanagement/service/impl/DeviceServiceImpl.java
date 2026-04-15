package com.example.devicemanagement.service.impl;

import com.example.devicemanagement.dto.DeviceRequest;
import com.example.devicemanagement.dto.DeviceResponse;
import com.example.devicemanagement.exception.DeviceNotFoundException;
import com.example.devicemanagement.messaging.producer.DeviceEventProducer;
import com.example.devicemanagement.model.Device;
import com.example.devicemanagement.model.enums.DeviceStatus;
import com.example.devicemanagement.model.enums.DeviceType;
import com.example.devicemanagement.repository.DeviceRepository;
import com.example.devicemanagement.service.DeviceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeviceServiceImpl implements DeviceService {

    private final DeviceRepository repository;
    private final DeviceEventProducer deviceEventProducer;

    @Override
    public DeviceResponse createDevice(DeviceRequest request) {
        Device device = new Device();
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setMacAddress(request.getMacAddress());
        device.setIpAddress(request.getIpAddress());
        device.setFirmwareVersion(request.getFirmwareVersion());
        device.setLocation(request.getLocation());
        device.setStatus(request.getStatus() != null ? request.getStatus() : DeviceStatus.ONLINE);
        device.setTags(request.getTags());
        device.setConfiguration(request.getConfiguration());
        device.setRegisteredAt(Instant.now());
        device.setLastHeartbeat(Instant.now());

        Device saved = repository.save(device);
        log.info("Device created: {} ({})", saved.getDeviceName(), saved.getId());

        deviceEventProducer.sendDeviceCreated(saved);

        return toResponse(saved);
    }

    @Override
    public List<DeviceResponse> getAllDevices() {
        return repository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public DeviceResponse getDeviceById(String id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
        return toResponse(device);
    }

    @Override
    public DeviceResponse updateDevice(String id, DeviceRequest request) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setMacAddress(request.getMacAddress());
        device.setIpAddress(request.getIpAddress());
        device.setFirmwareVersion(request.getFirmwareVersion());
        device.setLocation(request.getLocation());
        if (request.getStatus() != null) {
            device.setStatus(request.getStatus());
        }
        device.setTags(request.getTags());
        device.setConfiguration(request.getConfiguration());

        Device saved = repository.save(device);
        log.info("Device updated: {} ({})", saved.getDeviceName(), saved.getId());

        deviceEventProducer.sendDeviceUpdated(saved);

        return toResponse(saved);
    }

    @Override
    public void deleteDevice(String id) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        repository.deleteById(id);
        log.info("Device deleted: {} ({})", device.getDeviceName(), id);

        deviceEventProducer.sendDeviceDeleted(device);
    }

    @Override
    public DeviceResponse updateDeviceStatus(String id, DeviceStatus status) {
        Device device = repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));

        device.setStatus(status);
        device.setLastHeartbeat(Instant.now());
        Device saved = repository.save(device);
        log.info("Device status updated: {} -> {}", saved.getDeviceName(), status);

        deviceEventProducer.sendDeviceStatusChanged(saved);

        return toResponse(saved);
    }

    @Override
    public Page<DeviceResponse> searchDevices(String name, Pageable pageable) {
        return repository.findByDeviceNameContainingIgnoreCase(name, pageable)
                .map(this::toResponse);
    }

    @Override
    public Map<String, Long> getDeviceStats() {
        Map<String, Long> stats = new HashMap<>();

        stats.put("total", repository.count());
        for (DeviceStatus status : DeviceStatus.values()) {
            stats.put(status.name().toLowerCase(), repository.countByStatus(status));
        }
        for (DeviceType type : DeviceType.values()) {
            stats.put("type_" + type.name().toLowerCase(), repository.countByDeviceType(type));
        }

        return stats;
    }

    private DeviceResponse toResponse(Device device) {
        return DeviceResponse.builder()
                .id(device.getId())
                .deviceName(device.getDeviceName())
                .deviceType(device.getDeviceType())
                .macAddress(device.getMacAddress())
                .ipAddress(device.getIpAddress())
                .firmwareVersion(device.getFirmwareVersion())
                .location(device.getLocation())
                .status(device.getStatus())
                .lastHeartbeat(device.getLastHeartbeat())
                .registeredAt(device.getRegisteredAt())
                .tags(device.getTags())
                .configuration(device.getConfiguration())
                .build();
    }
}