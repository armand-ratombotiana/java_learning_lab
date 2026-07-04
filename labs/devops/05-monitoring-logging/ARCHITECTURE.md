# Monitoring & Logging Architecture

## Complete Observability Stack
```
┌──────────────────────────────────────────────────────────────────┐
│                        Application Layer                          │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐        │
│  │ Service A│  │ Service B│  │ Service C│  │ Service D│        │
│  │ /metrics │  │ /metrics │  │ /metrics │  │ /metrics │        │
│  │ stdout   │  │ stdout   │  │ stdout   │  │ stdout   │        │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘        │
│       │              │              │              │             │
├───────┼──────────────┼──────────────┼──────────────┼────────────┤
│       │              │              │              │             │
│  ┌────▼────┐   ┌────▼────┐   ┌────▼────┐   ┌────▼────┐        │
│  │Prometheus│   │  Grafana│   │Elastic- │   │  Jaeger │        │
│  │ + Alert  │   │  + Alert│   │search   │   │(Traces) │        │
│  └──────────┘   └──────────┘   └────┬────┘   └─────────┘       │
│                                     │                            │
│                              ┌──────▼──────┐                    │
│                              │   Kibana    │                    │
│                              └─────────────┘                    │
└──────────────────────────────────────────────────────────────────┘
```

## Data Flow
```
Metrics: App → /metrics → Prometheus scrape → TSDB → Grafana query
Logs:    App → stdout → Filebeat → Logstash → ES → Kibana search
Traces:  App → OTel SDK → Collector → Jaeger → Query UI
Alerts:  Prometheus rules → Alertmanager → Slack/PagerDuty
```
