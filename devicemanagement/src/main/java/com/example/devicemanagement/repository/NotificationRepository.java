package com.example.devicemanagement.repository;

import com.example.devicemanagement.model.Notification;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface NotificationRepository extends MongoRepository<Notification, String> {

    List<Notification> findByAlertId(String alertId);

    List<Notification> findByDeviceId(String deviceId);
}
