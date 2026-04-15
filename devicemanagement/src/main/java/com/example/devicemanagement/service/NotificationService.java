package com.example.devicemanagement.service;

import com.example.devicemanagement.model.Notification;

import java.util.List;

public interface NotificationService {

    List<Notification> getAllNotifications();

    List<Notification> getNotificationsByDeviceId(String deviceId);
}
