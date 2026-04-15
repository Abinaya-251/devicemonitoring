package com.example.devicemanagement.repository;

import com.example.devicemanagement.model.Alert;
import com.example.devicemanagement.model.enums.AlertSeverity;
import com.example.devicemanagement.model.enums.AlertStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlertRepository extends MongoRepository<Alert, String> {

    List<Alert> findByDeviceId(String deviceId);

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findBySeverity(AlertSeverity severity);

    List<Alert> findByStatusAndSeverity(AlertStatus status, AlertSeverity severity);

    long countByStatus(AlertStatus status);

    long countBySeverity(AlertSeverity severity);

    long countByStatusAndSeverity(AlertStatus status, AlertSeverity severity);
}
