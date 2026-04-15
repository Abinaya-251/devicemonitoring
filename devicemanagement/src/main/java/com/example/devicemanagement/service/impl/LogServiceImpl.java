package com.example.devicemanagement.service.impl;

import com.example.devicemanagement.dto.LogSearchRequest;
import com.example.devicemanagement.model.DeviceLog;
import com.example.devicemanagement.repository.DeviceLogElasticRepository;
import com.example.devicemanagement.service.LogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Criteria;
import org.springframework.data.elasticsearch.core.query.CriteriaQuery;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
@Slf4j
public class LogServiceImpl implements LogService {

    private final DeviceLogElasticRepository deviceLogRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void ingestLog(DeviceLog deviceLog) {
        if (deviceLog.getTimestamp() == null) {
            deviceLog.setTimestamp(Instant.now());
        }
        deviceLogRepository.save(deviceLog);
        log.info("Log ingested for device: {}", deviceLog.getDeviceId());
    }

    @Override
    public void ingestBatch(List<DeviceLog> logs) {
        logs.forEach(l -> {
            if (l.getTimestamp() == null) l.setTimestamp(Instant.now());
        });
        deviceLogRepository.saveAll(logs);
        log.info("Batch of {} logs ingested", logs.size());
    }

    @Override
    public List<DeviceLog> searchLogs(LogSearchRequest request) {
        Criteria criteria = new Criteria();

        if (request.getQuery() != null && !request.getQuery().isBlank()) {
            criteria = criteria.and("message").contains(request.getQuery());
        }
        if (request.getDeviceId() != null && !request.getDeviceId().isBlank()) {
            criteria = criteria.and("deviceId").is(request.getDeviceId());
        }
        if (request.getLevel() != null && !request.getLevel().isBlank()) {
            criteria = criteria.and("level").is(request.getLevel());
        }
        if (request.getSource() != null && !request.getSource().isBlank()) {
            criteria = criteria.and("source").is(request.getSource());
        }
        if (request.getFrom() != null && request.getTo() != null) {
            criteria = criteria.and("timestamp").between(
                    Instant.parse(request.getFrom()), Instant.parse(request.getTo()));
        }

        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(request.getPage(), request.getSize(),
                        Sort.by(Sort.Direction.DESC, "timestamp")));

        SearchHits<DeviceLog> hits = elasticsearchOperations.search(query, DeviceLog.class);
        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }

    @Override
    public List<DeviceLog> getLogsByDeviceId(String deviceId) {
        return deviceLogRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
    }

    @Override
    public Map<String, Long> getLogStats() {
        Map<String, Long> stats = new HashMap<>();
        long total = StreamSupport.stream(deviceLogRepository.findAll().spliterator(), false).count();
        stats.put("total", total);

        for (String level : List.of("DEBUG", "INFO", "WARN", "ERROR", "FATAL")) {
            long count = deviceLogRepository.findByLevel(level).size();
            stats.put(level.toLowerCase(), count);
        }
        return stats;
    }
}
