package com.example.devicemanagement.controller;

import com.example.devicemanagement.dto.DeviceRequest;
import com.example.devicemanagement.dto.DeviceResponse;
import com.example.devicemanagement.model.enums.DeviceStatus;
import com.example.devicemanagement.service.DeviceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService service;

    @PostMapping
    public ResponseEntity<DeviceResponse> createDevice(@Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDevice(request));
    }

    @GetMapping
    public ResponseEntity<List<DeviceResponse>> getAllDevices() {
        return ResponseEntity.ok(service.getAllDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceResponse> getDeviceById(@PathVariable String id) {
        return ResponseEntity.ok(service.getDeviceById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceResponse> updateDevice(@PathVariable String id,
                                                       @Valid @RequestBody DeviceRequest request) {
        return ResponseEntity.ok(service.updateDevice(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        service.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<DeviceResponse> updateStatus(@PathVariable String id,
                                                       @RequestParam DeviceStatus status) {
        return ResponseEntity.ok(service.updateDeviceStatus(id, status));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<DeviceResponse>> searchDevices(
            @RequestParam String name,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(service.searchDevices(name, PageRequest.of(page, size)));
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(service.getDeviceStats());
    }
}