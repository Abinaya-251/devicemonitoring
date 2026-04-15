package com.example.devicemanagement.exception;

public class DeviceNotFoundException extends RuntimeException {
    public DeviceNotFoundException(String id) {
        super("Device not found with id: " + id);
    }
}
