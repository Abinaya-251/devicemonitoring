package com.example.devicemanagement.service.impl;

import com.example.devicemanagement.dto.TelemetryRequest;
import com.example.devicemanagement.messaging.event.TelemetryEvent;
import com.example.devicemanagement.messaging.producer.TelemetryEventProducer;
import com.example.devicemanagement.model.TelemetryData;
import com.example.devicemanagement.repository.TelemetryElasticRepository;
import com.example.devicemanagement.service.TelemetryService;
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
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelemetryServiceImpl implements TelemetryService {

    private final TelemetryEventProducer telemetryEventProducer;
    private final TelemetryElasticRepository telemetryRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void ingestTelemetry(TelemetryRequest request) {
        TelemetryEvent event = TelemetryEvent.builder()
                .deviceId(request.getDeviceId())
                .timestamp(Instant.now())
                .cpuUsage(request.getCpuUsage())
                .memoryUsage(request.getMemoryUsage())
                .bandwidthIn(request.getBandwidthIn())
                .bandwidthOut(request.getBandwidthOut())
                .activeClients(request.getActiveClients())
                .temperature(request.getTemperature())
                .uptime(request.getUptime())
                .packetLoss(request.getPacketLoss())
                .latency(request.getLatency())
                .build();

        telemetryEventProducer.sendTelemetry(event);
        log.info("Telemetry ingested for device: {}", request.getDeviceId());
    }

    @Override
    public void ingestBatch(List<TelemetryRequest> requests) {
        requests.forEach(this::ingestTelemetry);
    }

    @Override
    public TelemetryData getLatestTelemetry(String deviceId) {
        List<TelemetryData> results = telemetryRepository.findByDeviceIdOrderByTimestampDesc(deviceId);
        return results.isEmpty() ? null : results.get(0);
    }

    @Override
    public List<TelemetryData> getTelemetryHistory(String deviceId, String from, String to) {
        Criteria criteria = new Criteria("deviceId").is(deviceId);

        if (from != null && to != null) {
            criteria = criteria.and("timestamp").between(Instant.parse(from), Instant.parse(to));
        } else if (from != null) {
            criteria = criteria.and("timestamp").greaterThanEqual(Instant.parse(from));
        } else if (to != null) {
            criteria = criteria.and("timestamp").lessThanEqual(Instant.parse(to));
        }

        CriteriaQuery query = new CriteriaQuery(criteria)
                .setPageable(PageRequest.of(0, 100, Sort.by(Sort.Direction.DESC, "timestamp")));

        SearchHits<TelemetryData> hits = elasticsearchOperations.search(query, TelemetryData.class);
        return hits.getSearchHits().stream()
                .map(SearchHit::getContent)
                .collect(Collectors.toList());
    }
}
