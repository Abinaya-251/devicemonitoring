# NetPulse — Real-Time Device Monitoring & Analytics Platform

> A production-grade, cloud-style network device monitoring platform inspired by **NETGEAR Insight**. Built to manage thousands of network devices (routers, switches, access points, firewalls) with real-time telemetry ingestion, intelligent alerting, full-text log search, and a live dashboard.

---

## Tech Stack

| Layer              | Technology                        | Purpose                                              |
|--------------------|-----------------------------------|------------------------------------------------------|
| **Backend**        | Java 17+ / Spring Boot 3.x       | REST APIs, business logic, event processing          |
| **Database**       | MongoDB                          | Primary data store for devices, configs, alerts      |
| **Search & Logs**  | Elasticsearch                    | Full-text search on device logs, telemetry indexing  |
| **Message Broker** | RabbitMQ                         | Async event-driven communication between services    |
| **Frontend**       | React + Material-UI              | Real-time dashboard, device management UI            |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                           REACT FRONTEND                                    │
│  ┌──────────┐ ┌──────────────┐ ┌────────────┐ ┌──────────────┐            │
│  │ Dashboard │ │ Device Mgmt  │ │ Alert View │ │  Log Search  │            │
│  └─────┬────┘ └──────┬───────┘ └─────┬──────┘ └──────┬───────┘            │
└────────┼──────────────┼───────────────┼───────────────┼────────────────────┘
         │              │               │               │
         ▼              ▼               ▼               ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│                     SPRING BOOT BACKEND (REST API)                          │
│                                                                             │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐            │
│  │ Device Service   │  │ Telemetry       │  │ Alert Service   │            │
│  │                  │  │ Service         │  │                 │            │
│  │ • CRUD devices   │  │ • Ingest metrics│  │ • Rule engine   │            │
│  │ • Registration   │  │ • Health checks │  │ • Notifications │            │
│  │ • Status tracking│  │ • Aggregation   │  │ • Alert history │            │
│  └────────┬─────────┘  └────────┬───────┘  └────────┬────────┘            │
│           │                     │                    │                      │
│  ┌────────┴─────────┐  ┌───────┴────────┐  ┌───────┴────────┐             │
│  │ Log Service      │  │ Search Service │  │ Notification   │             │
│  │                  │  │                │  │ Service        │             │
│  │ • Device logs    │  │ • ES queries   │  │ • Email/Webhook│             │
│  │ • Event logs     │  │ • Filters      │  │ • Event publish│             │
│  │ • Audit trail    │  │ • Aggregations │  │                │             │
│  └──────────────────┘  └────────────────┘  └────────────────┘             │
└──────────┬──────────────────┬───────────────────┬─────────────────────────┘
           │                  │                   │
     ┌─────▼─────┐    ┌──────▼──────┐    ┌───────▼───────┐
     │  MongoDB   │    │Elasticsearch│    │   RabbitMQ    │
     │            │    │             │    │               │
     │ • devices  │    │ • device-   │    │ Exchanges:    │
     │ • alerts   │    │   logs      │    │ • device.     │
     │ • configs  │    │ • telemetry │    │   events      │
     │ • users    │    │ • alerts    │    │ • telemetry.  │
     │ • rules    │    │             │    │   ingestion   │
     └────────────┘    └─────────────┘    │ • alert.      │
                                          │   notifications│
                                          └───────────────┘
```

---

## Microservices / Modules Breakdown

### 1. Device Service (Core)
**What it does:** Central registry for all network devices in the system.

| Endpoint                     | Method   | Description                                      |
|------------------------------|----------|--------------------------------------------------|
| `POST /api/devices`         | POST     | Register a new device (router, switch, AP, etc.) |
| `GET /api/devices`          | GET      | List all devices with filtering & pagination     |
| `GET /api/devices/{id}`     | GET      | Get device details by ID                         |
| `PUT /api/devices/{id}`     | PUT      | Update device configuration                      |
| `DELETE /api/devices/{id}`  | DELETE   | Decommission/remove a device                     |
| `PATCH /api/devices/{id}/status` | PATCH | Update device status (ONLINE/OFFLINE/WARNING)   |
| `GET /api/devices/search`   | GET      | Search devices by name, IP, type, location       |
| `GET /api/devices/stats`    | GET      | Get device count by status, type, location       |

**MongoDB Collection:** `devices`
```json
{
  "_id": "ObjectId",
  "deviceName": "AP-Floor3-East",
  "deviceType": "ACCESS_POINT",       // ROUTER, SWITCH, ACCESS_POINT, FIREWALL, GATEWAY
  "macAddress": "AA:BB:CC:DD:EE:FF",
  "ipAddress": "192.168.1.100",
  "firmwareVersion": "2.4.1",
  "location": "Building A - Floor 3",
  "status": "ONLINE",                 // ONLINE, OFFLINE, WARNING, MAINTENANCE
  "lastHeartbeat": "2026-04-15T10:30:00Z",
  "registeredAt": "2026-01-10T08:00:00Z",
  "tags": ["production", "floor-3"],
  "configuration": {
    "ssid": "Corp-WiFi",
    "channel": 6,
    "txPower": "HIGH"
  }
}
```

**RabbitMQ Events Published:**
- `device.registered` — when a new device is added
- `device.updated` — when device config changes
- `device.status.changed` — when device goes ONLINE/OFFLINE/WARNING
- `device.deleted` — when a device is removed

---

### 2. Telemetry Service
**What it does:** Ingests real-time metrics from devices (CPU, memory, bandwidth, temperature, client count). Simulates what SNMP/MQTT agents would send from real hardware.

| Endpoint                                | Method | Description                                       |
|-----------------------------------------|--------|---------------------------------------------------|
| `POST /api/telemetry`                  | POST   | Ingest a telemetry data point                     |
| `POST /api/telemetry/batch`            | POST   | Bulk ingest telemetry (multiple devices)          |
| `GET /api/telemetry/{deviceId}`        | GET    | Get latest telemetry for a device                 |
| `GET /api/telemetry/{deviceId}/history`| GET    | Get historical telemetry (time range, aggregation)|
| `GET /api/telemetry/dashboard`         | GET    | Aggregated metrics for dashboard widgets          |

**How it works:**
1. Devices (simulated) POST metrics every 30 seconds
2. Raw telemetry is published to RabbitMQ exchange `telemetry.ingestion`
3. A **consumer** picks up the message, stores raw data in **Elasticsearch** (for time-series search)
4. A summary/latest snapshot is stored in **MongoDB** for quick dashboard reads
5. If any metric breaches a threshold → publishes `alert.triggered` event to RabbitMQ

**Elasticsearch Index:** `telemetry-YYYY.MM` (monthly rolling index)
```json
{
  "deviceId": "abc123",
  "timestamp": "2026-04-15T10:30:00Z",
  "metrics": {
    "cpuUsage": 72.5,
    "memoryUsage": 65.3,
    "bandwidthIn": 450.2,
    "bandwidthOut": 320.1,
    "activeClients": 47,
    "temperature": 62.0,
    "uptime": 864000,
    "packetLoss": 0.02,
    "latency": 12.5
  }
}
```

**RabbitMQ Events:**
- **Consumes:** `telemetry.ingestion.queue` (raw metrics from devices)
- **Publishes:** `alert.triggered` (when thresholds breached)

---

### 3. Alert Service
**What it does:** Rule-based alerting engine. Monitors device health and telemetry, generates alerts when conditions are met, and tracks alert lifecycle (OPEN → ACKNOWLEDGED → RESOLVED).

| Endpoint                              | Method | Description                                   |
|---------------------------------------|--------|-----------------------------------------------|
| `POST /api/alerts/rules`             | POST   | Create an alert rule (e.g., CPU > 90%)        |
| `GET /api/alerts/rules`              | GET    | List all alert rules                          |
| `PUT /api/alerts/rules/{id}`         | PUT    | Update an alert rule                          |
| `DELETE /api/alerts/rules/{id}`      | DELETE | Delete an alert rule                          |
| `GET /api/alerts`                    | GET    | List all alerts (filterable by severity, status)|
| `GET /api/alerts/{id}`               | GET    | Get alert details                             |
| `PATCH /api/alerts/{id}/acknowledge` | PATCH  | Acknowledge an alert                          |
| `PATCH /api/alerts/{id}/resolve`     | PATCH  | Resolve an alert                              |
| `GET /api/alerts/stats`              | GET    | Alert counts by severity and status           |

**MongoDB Collection:** `alert_rules`
```json
{
  "_id": "ObjectId",
  "ruleName": "High CPU Alert",
  "metric": "cpuUsage",
  "condition": "GREATER_THAN",        // GREATER_THAN, LESS_THAN, EQUALS
  "threshold": 90.0,
  "severity": "CRITICAL",             // INFO, WARNING, CRITICAL
  "deviceType": "ALL",                // or specific type
  "enabled": true,
  "cooldownMinutes": 5
}
```

**MongoDB Collection:** `alerts`
```json
{
  "_id": "ObjectId",
  "ruleId": "rule123",
  "deviceId": "device456",
  "deviceName": "Router-Main-Lobby",
  "alertType": "HIGH_CPU",
  "severity": "CRITICAL",
  "message": "CPU usage at 95.2% exceeds threshold of 90%",
  "status": "OPEN",                   // OPEN, ACKNOWLEDGED, RESOLVED
  "metricValue": 95.2,
  "threshold": 90.0,
  "triggeredAt": "2026-04-15T10:30:00Z",
  "acknowledgedAt": null,
  "resolvedAt": null,
  "acknowledgedBy": null
}
```

**RabbitMQ Events:**
- **Consumes:** `alert.triggered` (from Telemetry Service)
- **Publishes:** `notification.send` (to Notification Service)

---

### 4. Log & Event Service
**What it does:** Collects, stores, and provides searchable access to device logs and system events. This is the audit trail and debugging backbone of the platform.

| Endpoint                           | Method | Description                                      |
|------------------------------------|--------|--------------------------------------------------|
| `POST /api/logs`                  | POST   | Ingest a device log entry                        |
| `POST /api/logs/batch`            | POST   | Bulk ingest logs                                 |
| `GET /api/logs/search`            | GET    | Full-text search across all logs (Elasticsearch) |
| `GET /api/logs/device/{deviceId}` | GET    | Get logs for a specific device                   |
| `GET /api/logs/stats`             | GET    | Log volume stats (counts by level, device, time) |

**Elasticsearch Index:** `device-logs-YYYY.MM`
```json
{
  "deviceId": "abc123",
  "deviceName": "Switch-Floor2",
  "timestamp": "2026-04-15T10:30:00Z",
  "level": "ERROR",                   // DEBUG, INFO, WARN, ERROR, FATAL
  "source": "SYSTEM",                 // SYSTEM, NETWORK, SECURITY, CONFIG
  "message": "Port GE-0/0/1 link down - cable disconnected",
  "metadata": {
    "port": "GE-0/0/1",
    "previousState": "UP",
    "currentState": "DOWN"
  }
}
```

**Why Elasticsearch?**
- Full-text search across millions of log entries ("find all logs mentioning 'link down' in last 24 hours")
- Aggregations: log volume over time, error rate per device, top error messages
- Time-range filtering with millisecond precision
- Kibana-style analytics on the React frontend

**RabbitMQ Events:**
- **Consumes:** `device.events.queue` (system events from all services)

---

### 5. Notification Service
**What it does:** Handles delivery of alert notifications. Listens for alert events from RabbitMQ and dispatches notifications (logs to console in dev, extensible to email/webhook/Slack in production).

| Endpoint                            | Method | Description                              |
|-------------------------------------|--------|------------------------------------------|
| `GET /api/notifications`           | GET    | List notification history                |
| `GET /api/notifications/settings`  | GET    | Get notification preferences             |
| `PUT /api/notifications/settings`  | PUT    | Update notification preferences          |

**RabbitMQ Events:**
- **Consumes:** `notification.send` (from Alert Service)

---

### 6. Dashboard / Analytics Service
**What it does:** Aggregates data from all services to power the React dashboard with real-time statistics and historical trends.

| Endpoint                           | Method | Description                                        |
|------------------------------------|--------|----------------------------------------------------|
| `GET /api/dashboard/summary`      | GET    | Overall platform health (device counts, alert counts)|
| `GET /api/dashboard/topology`     | GET    | Network topology map data                          |
| `GET /api/dashboard/trends`       | GET    | Historical trends (devices online over time)       |

---

## RabbitMQ — Event-Driven Architecture (Detailed)

```
┌─────────────────────────────────────────────────────────────────────┐
│                        RabbitMQ Broker                               │
│                                                                      │
│  Exchange: device.events (topic)                                     │
│  ├── Routing Key: device.registered → Queue: device.events.queue     │
│  ├── Routing Key: device.status.changed → Queue: device.events.queue │
│  ├── Routing Key: device.updated → Queue: device.events.queue        │
│  └── Routing Key: device.deleted → Queue: device.events.queue        │
│                                                                      │
│  Exchange: telemetry.exchange (direct)                               │
│  └── Routing Key: telemetry.ingest → Queue: telemetry.ingestion.queue│
│                                                                      │
│  Exchange: alert.exchange (topic)                                    │
│  ├── Routing Key: alert.triggered → Queue: alert.processing.queue    │
│  └── Routing Key: alert.resolved → Queue: alert.processing.queue     │
│                                                                      │
│  Exchange: notification.exchange (fanout)                            │
│  └── Queue: notification.send.queue                                  │
└─────────────────────────────────────────────────────────────────────┘
```

**Why RabbitMQ?**
- **Decoupling:** Device Service doesn't need to know about Alert or Log Service
- **Reliability:** Messages are persisted; no data loss even if a consumer is down
- **Scalability:** Add more consumers to process telemetry spikes
- **Interview talking point:** "We chose RabbitMQ over Kafka because our message patterns are request-response and work-queue based, not high-throughput stream processing. RabbitMQ's routing flexibility with topic exchanges fits our event-driven needs perfectly."

---

## Elasticsearch — Search & Analytics (Detailed)

### Indices

| Index Pattern          | Purpose                          | Retention   |
|------------------------|----------------------------------|-------------|
| `device-logs-YYYY.MM` | Device logs (syslog-style)       | 90 days     |
| `telemetry-YYYY.MM`   | Time-series metrics              | 30 days     |
| `alerts-YYYY.MM`      | Alert history for analytics      | 365 days    |

### Key Queries Implemented
1. **Full-text log search:** Search across all device logs with highlighting
2. **Time-range filter:** "Show me all ERROR logs from the last 6 hours"
3. **Aggregation:** "Top 10 devices by error count this week"
4. **Terms filter:** Filter by log level, device type, source
5. **Histogram:** Log volume over time (for dashboard charts)

**Interview talking point:** "MongoDB stores the live operational data — current device states, active alerts, configurations. Elasticsearch handles the analytical and search workloads — searching through millions of log entries, time-series telemetry aggregations, and powering the dashboard charts. This separation lets each database do what it's best at."

---

## MongoDB — Collections Overview

| Collection      | Purpose                                  | Key Indexes                    |
|-----------------|------------------------------------------|--------------------------------|
| `devices`       | Device registry (master data)            | ipAddress (unique), macAddress |
| `alerts`        | Active and historical alerts             | deviceId, status, severity     |
| `alert_rules`   | Alert rule definitions                   | metric, enabled                |
| `notifications` | Notification delivery history            | alertId, createdAt             |
| `device_configs`| Device configuration snapshots           | deviceId, version              |

---

## React Frontend — Pages & Components

### Pages
| Page                  | Route                | Description                                        |
|-----------------------|----------------------|----------------------------------------------------|
| **Dashboard**         | `/`                  | Real-time overview: device counts, alert summary, health gauges, telemetry charts |
| **Device List**       | `/devices`           | Searchable/filterable table of all devices          |
| **Device Detail**     | `/devices/:id`       | Single device view: config, telemetry graphs, logs, alerts |
| **Alerts**            | `/alerts`            | Alert list with filters (severity, status, device)  |
| **Alert Rules**       | `/alerts/rules`      | CRUD interface for alert rules                      |
| **Log Explorer**      | `/logs`              | Elasticsearch-powered log search with filters       |
| **Network Topology**  | `/topology`          | Visual network map showing device connections       |

### Key React Components
```
src/
├── components/
│   ├── Dashboard/
│   │   ├── DeviceStatusWidget.jsx      // Donut chart: online/offline/warning counts
│   │   ├── AlertSummaryWidget.jsx      // Active alerts by severity
│   │   ├── TelemetryChart.jsx          // Real-time line chart (CPU, bandwidth)
│   │   ├── TopDevicesTable.jsx         // Devices with highest resource usage
│   │   └── NetworkHealthGauge.jsx      // Overall network health score
│   ├── Devices/
│   │   ├── DeviceTable.jsx             // Paginated device list with search
│   │   ├── DeviceForm.jsx              // Add/Edit device form
│   │   ├── DeviceDetailPanel.jsx       // Device detail with tabs
│   │   └── DeviceStatusBadge.jsx       // Color-coded status indicator
│   ├── Alerts/
│   │   ├── AlertList.jsx               // Filterable alert feed
│   │   ├── AlertRuleForm.jsx           // Create/edit alert rules
│   │   └── AlertSeverityIcon.jsx       // Severity color coding
│   ├── Logs/
│   │   ├── LogSearchBar.jsx            // Full-text search input
│   │   ├── LogTable.jsx                // Log entries with syntax highlighting
│   │   ├── LogFilters.jsx              // Level, device, time-range filters
│   │   └── LogVolumeChart.jsx          // Histogram of log volume over time
│   └── common/
│       ├── Navbar.jsx
│       ├── Sidebar.jsx
│       └── LoadingSpinner.jsx
├── services/
│   ├── deviceService.js                // Axios calls to /api/devices
│   ├── telemetryService.js             // Axios calls to /api/telemetry
│   ├── alertService.js                 // Axios calls to /api/alerts
│   └── logService.js                   // Axios calls to /api/logs
├── App.jsx
└── index.jsx
```

---

## Data Flow — End to End Example

### Scenario: "A router's CPU spikes to 95%"

```
1. [Telemetry Simulator] POST /api/telemetry
   → Body: { deviceId: "router-01", metrics: { cpuUsage: 95.2 } }

2. [Telemetry Service] Receives the API call
   → Stores raw data in Elasticsearch (telemetry-2026.04 index)
   → Updates latest snapshot in MongoDB (devices collection → lastTelemetry)
   → Publishes message to RabbitMQ: exchange=telemetry.exchange, key=telemetry.ingest

3. [Alert Service] Consumes from telemetry.ingestion.queue
   → Evaluates against alert_rules: "CPU > 90% → CRITICAL"
   → Match found! Creates alert in MongoDB (alerts collection)
   → Publishes to RabbitMQ: exchange=alert.exchange, key=alert.triggered

4. [Notification Service] Consumes from notification.send.queue
   → Logs alert notification (or sends email/webhook)
   → Stores notification record in MongoDB

5. [Log Service] Consumes from device.events.queue
   → Indexes the event in Elasticsearch (device-logs-2026.04)

6. [React Dashboard] Polling /api/dashboard/summary every 10s
   → Shows updated alert count, device status changes
   → User clicks alert → sees device detail → views telemetry chart showing the CPU spike
```

---

## Project Structure (Backend — Final)

```
src/main/java/com/example/devicemanagement/
├── DevicemanagementApplication.java
├── config/
│   ├── MongoConfig.java
│   ├── ElasticsearchConfig.java
│   ├── RabbitMQConfig.java               // Exchanges, queues, bindings
│   └── CorsConfig.java
├── controller/
│   ├── DeviceController.java
│   ├── TelemetryController.java
│   ├── AlertController.java
│   ├── AlertRuleController.java
│   ├── LogController.java
│   ├── NotificationController.java
│   └── DashboardController.java
├── dto/
│   ├── DeviceRequest.java
│   ├── DeviceResponse.java
│   ├── TelemetryRequest.java
│   ├── AlertRuleRequest.java
│   ├── LogSearchRequest.java
│   └── DashboardSummaryResponse.java
├── model/
│   ├── Device.java                        // MongoDB @Document
│   ├── Alert.java                         // MongoDB @Document
│   ├── AlertRule.java                     // MongoDB @Document
│   ├── Notification.java                  // MongoDB @Document
│   ├── TelemetryData.java                 // Elasticsearch @Document
│   ├── DeviceLog.java                     // Elasticsearch @Document
│   └── enums/
│       ├── DeviceType.java                // ROUTER, SWITCH, ACCESS_POINT, FIREWALL
│       ├── DeviceStatus.java              // ONLINE, OFFLINE, WARNING, MAINTENANCE
│       ├── AlertSeverity.java             // INFO, WARNING, CRITICAL
│       ├── AlertStatus.java               // OPEN, ACKNOWLEDGED, RESOLVED
│       └── LogLevel.java                  // DEBUG, INFO, WARN, ERROR, FATAL
├── repository/
│   ├── DeviceRepository.java              // MongoRepository
│   ├── AlertRepository.java               // MongoRepository
│   ├── AlertRuleRepository.java           // MongoRepository
│   ├── NotificationRepository.java        // MongoRepository
│   ├── TelemetryElasticRepository.java    // ElasticsearchRepository
│   └── DeviceLogElasticRepository.java    // ElasticsearchRepository
├── service/
│   ├── DeviceService.java
│   ├── TelemetryService.java
│   ├── AlertService.java
│   ├── LogService.java
│   ├── NotificationService.java
│   ├── DashboardService.java
│   └── impl/
│       ├── DeviceServiceImpl.java
│       ├── TelemetryServiceImpl.java
│       ├── AlertServiceImpl.java
│       ├── LogServiceImpl.java
│       ├── NotificationServiceImpl.java
│       └── DashboardServiceImpl.java
├── messaging/
│   ├── producer/
│   │   ├── DeviceEventProducer.java       // Publishes device lifecycle events
│   │   ├── TelemetryEventProducer.java    // Publishes telemetry data
│   │   └── AlertEventProducer.java        // Publishes alert events
│   └── consumer/
│       ├── TelemetryConsumer.java          // Processes incoming telemetry
│       ├── AlertConsumer.java             // Evaluates alert rules
│       ├── NotificationConsumer.java      // Sends notifications
│       └── LogEventConsumer.java          // Indexes events into ES
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── DeviceNotFoundException.java
│   └── AlertRuleNotFoundException.java
└── simulator/
    └── TelemetrySimulator.java            // @Scheduled task generating fake metrics
```

---

## Implementation Phases

### Phase 1 — Device CRUD + MongoDB ✅ (Current)
- [x] Device model, repository, service, controller
- [x] MongoDB integration
- [ ] Add enums (DeviceType, DeviceStatus)
- [ ] Add proper validation and error handling
- [ ] Pagination and filtering on GET /api/devices

### Phase 2 — RabbitMQ Event System
- [ ] RabbitMQ configuration (exchanges, queues, bindings)
- [ ] Device event producer (publish on create/update/delete)
- [ ] Log event consumer (listen and store events)
- [ ] Test message flow end-to-end

### Phase 3 — Telemetry Ingestion + Elasticsearch
- [ ] Elasticsearch configuration and index templates
- [ ] Telemetry model and Elasticsearch repository
- [ ] Telemetry controller and service
- [ ] Telemetry consumer (RabbitMQ → Elasticsearch)
- [ ] Telemetry simulator (scheduled fake data generator)
- [ ] Historical telemetry query API

### Phase 4 — Alert Engine
- [ ] Alert rule CRUD (MongoDB)
- [ ] Alert evaluation logic (consumes telemetry events)
- [ ] Alert lifecycle management (open → ack → resolve)
- [ ] Alert event publishing to notification queue

### Phase 5 — Log Search & Analytics
- [ ] Device log model and ES repository
- [ ] Full-text search API with Elasticsearch queries
- [ ] Log aggregation endpoints (volume, top errors)
- [ ] Log ingestion via RabbitMQ consumer

### Phase 6 — Notification Service
- [ ] Notification consumer (listens to alert events)
- [ ] Notification history storage
- [ ] Console/log-based notification (extensible to email)

### Phase 7 — Dashboard & Analytics API
- [ ] Summary endpoint (device/alert counts)
- [ ] Trend endpoints (historical data)
- [ ] Network health score calculation

### Phase 8 — React Frontend
- [ ] Project setup (Create React App + Material-UI + Axios + React Router)
- [ ] Dashboard page with widgets
- [ ] Device management pages (list, detail, form)
- [ ] Alert pages (list, rules)
- [ ] Log explorer with search
- [ ] Polling for real-time updates

---

## How to Run (Target Setup)

### Prerequisites
- Java 17+
- Maven 3.8+
- MongoDB 6.0+ (running on `localhost:27017`)
- Elasticsearch 8.x (running on `localhost:9200`)
- RabbitMQ 3.12+ (running on `localhost:5672`, management UI on `15672`)
- Node.js 18+ (for React frontend)

### Backend
```bash
cd devicemanagement
./mvnw spring-boot:run
```
Backend runs on `http://localhost:8080`

### Frontend
```bash
cd frontend
npm install
npm start
```
Frontend runs on `http://localhost:3000`

### Infrastructure (Docker Compose)
```bash
docker-compose up -d    # Starts MongoDB, Elasticsearch, RabbitMQ
```

---

## Key Interview Discussion Points

1. **Why MongoDB + Elasticsearch (dual database)?**
   MongoDB is optimized for document-level CRUD with flexible schemas — perfect for device configs and alert management. Elasticsearch excels at full-text search, time-series aggregation, and analytics across millions of log entries. Using both plays to each database's strength.

2. **Why RabbitMQ for messaging?**
   RabbitMQ provides reliable message delivery with acknowledgments, dead-letter queues for failed processing, and flexible routing (topic, direct, fanout exchanges). It decouples our services so the Device Service doesn't need to know about alerting or logging.

3. **How does the alert system work?**
   It's event-driven: telemetry data flows through RabbitMQ → Alert Service evaluates against configurable rules → generates alerts with severity levels → publishes notification events. Cooldown periods prevent alert storms.

4. **How would you scale this?**
   - Horizontal scaling: Run multiple instances of each service behind a load balancer
   - Elasticsearch: Sharding and replicas for search performance
   - RabbitMQ: Multiple consumers for parallel processing of telemetry
   - MongoDB: Replica sets for high availability

5. **How does this relate to NETGEAR Insight?**
   This mirrors Insight's core architecture: device registration, real-time monitoring, intelligent alerting, and centralized log management — the fundamental building blocks of any cloud-managed network platform.

---

## API Quick Reference

```
DEVICES:     POST/GET /api/devices, GET/PUT/DELETE /api/devices/{id}
TELEMETRY:   POST /api/telemetry, GET /api/telemetry/{deviceId}/history
ALERTS:      GET /api/alerts, PATCH /api/alerts/{id}/acknowledge
ALERT RULES: POST/GET/PUT/DELETE /api/alerts/rules
LOGS:        GET /api/logs/search?q=link+down&level=ERROR&from=2026-04-14
DASHBOARD:   GET /api/dashboard/summary
```
