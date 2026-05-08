# 38 - Prometheus Learning Module

## Overview
Prometheus is an open-source monitoring system with a dimensional data model, flexible query language, and alerting capabilities.

## Module Structure
- `prometheus-metrics/` - Micrometer + Prometheus implementation

## Technology Stack
- Spring Boot 3.x
- Micrometer Prometheus Registry
- Prometheus client library
- Maven

## Prerequisites
- Prometheus server running on `localhost:9090`
- Node exporter (optional): `localhost:9100`

## Key Features
- Multi-dimensional data model (metric name + labels)
- PromQL flexible query language
- Pull-based metrics collection
- Alerting with Alertmanager
- Service discovery integration
- Time series storage

## Build & Run
```bash
cd prometheus-metrics
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Metrics endpoint: `/actuator/prometheus`
- Metrics port: `8080`

## Metric Types
- Counter: monotonically increasing values
- Gauge: current value (can go up/down)
- Histogram: distribution of values
- Summary: quantiles over sliding window

## Related Modules
- 39-grafana (visualization)
- 41-actuator (health endpoints)