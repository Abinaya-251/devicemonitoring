package com.example.devicemanagement.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import com.example.devicemanagement.dto.DeviceRequest;
import com.example.devicemanagement.model.Device;
import com.example.devicemanagement.service.DeviceService;

@RestController
// @RequestMapping("/devices")
@RequiredArgsConstructor
public class DeviceController {

    private final DeviceService service;

    // ✅ CREATE DEVICE
    @PostMapping("/devices")
    public Device createDevice(@RequestBody DeviceRequest request) {
        return service.createDevice(request);
    }

    // ✅ GET ALL DEVICES
    @GetMapping("/alldevices")
    public List<Device> getAllDevices() {
        return service.getAllDevices();
    }
}