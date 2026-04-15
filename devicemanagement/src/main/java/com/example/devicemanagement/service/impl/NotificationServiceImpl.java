package com.example.devicemanagement.service.impl;

import com.example.devicemanagement.model.Notification;
import com.example.devicemanagement.repository.NotificationRepository;
import com.example.devicemanagement.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> getNotificationsByDeviceId(String deviceId) {
        return notificationRepository.findByDeviceId(deviceId);
    }
}
