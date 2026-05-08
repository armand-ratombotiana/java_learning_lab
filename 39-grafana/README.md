# 39 - Grafana Learning Module

## Overview
Grafana is an open-source analytics and interactive visualization platform. This module covers Grafana dashboard creation and data source configuration.

## Module Structure
- `grafana-dashboards/` - Dashboard configurations and provisioning

## Technology Stack
- Grafana 10.x
- Prometheus (primary data source)
- JSON provisioning
- Maven (for dashboard packaging)

## Prerequisites
- Grafana running on `localhost:3000`
- Prometheus running on `localhost:9090`

## Key Features
- Interactive dashboards with panels
- Multiple data source support (Prometheus, Elasticsearch, etc.)
- Alerting and notifications
- Variable and templating
- Sharing and embedding
- Team and organization management

## Build & Run
```bash
cd grafana-dashboards
# Provision dashboards to Grafana
# Or import JSON files manually
```

## Default Configuration
- Grafana URL: `http://localhost:3000`
- Default login: `admin`/`admin`

## Panel Types
- Graph (time series)
- Stat
- Gauge
- Table
- Heatmap
- Text/Markdown

## Related Modules
- 38-prometheus (metrics source)
- 40-jaeger (traces source)