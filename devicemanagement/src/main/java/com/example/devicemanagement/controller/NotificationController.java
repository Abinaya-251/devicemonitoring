package com.example.devicemanagement.controller;

import com.example.devicemanagement.model.Notification;
import com.example.devicemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationService.getAllNotifications());
    }

    @GetMapping("/device/{deviceId}")
    public ResponseEntity<List<Notification>> getByDevice(@PathVariable String deviceId) {
        return ResponseEntity.ok(notificationService.getNotificationsByDeviceId(deviceId));
    }
}
