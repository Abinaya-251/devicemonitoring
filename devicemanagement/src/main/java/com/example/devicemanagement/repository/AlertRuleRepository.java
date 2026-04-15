package com.example.devicemanagement.repository;

import com.example.devicemanagement.model.AlertRule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface AlertRuleRepository extends MongoRepository<AlertRule, String> {

    List<AlertRule> findByEnabled(boolean enabled);

    List<AlertRule> findByMetric(String metric);
}
