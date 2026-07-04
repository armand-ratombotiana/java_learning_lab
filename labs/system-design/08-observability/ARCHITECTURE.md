# Observability - ARCHITECTURE

## Full Observability Stack

```
┌─────────────────────────────────────────────────────────────────┐
│                         Service Layer                            │
│  ┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐          │
│  │ Order   │  │ Payment │  │Product  │  │Inventory│          │
│  │ Service │  │ Service │  │Service  │  │Service  │          │
│  └────┬────┘  └────┬────┘  └────┬────┘  └────┬────┘          │
│       │            │            │            │                │
│  Actuator + Micrometer + OpenTelemetry + Structured Logs      │
└───────┼────────────┼────────────┼────────────┼────────────────┘
        │            │            │            │
  ┌─────┼────────────┼────────────┼────────────┼─────┐
  │     ▼            ▼            ▼            ▼     │
  │                                                    │
  │  ┌───────────────────────────────────────────┐    │
  │  │           Metrics Pipeline                 │    │
  │  │                                           │    │
  │  │  Prometheus (scrape) ──► Thanos (long-term)│   │
  │  │                     ──► Alertmanager      │    │
  │  └───────────────────────────────────────────┘    │
  │                                                    │
  │  ┌───────────────────────────────────────────┐    │
  │  │           Log Pipeline                     │    │
  │  │                                           │    │
  │  │  Fluentd/Logstash ──► Elasticsearch       │    │
  │  │  (log shipping)   ──► (storage/search)    │    │
  │  └───────────────────────────────────────────┘    │
  │                                                    │
  │  ┌───────────────────────────────────────────┐    │
  │  │           Trace Pipeline                   │    │
  │  │                                           │    │
  │  │  OpenTelemetry Collector ──► Jaeger       │    │
  │  │  (batch, sample, filter)  ──► (store+UI)  │    │
  │  └───────────────────────────────────────────┘    │
  │                                                    │
  │  ┌───────────────────────────────────────────┐    │
  │  │           Visualization Layer              │    │
  │  │                                           │    │
  │  │  Grafana (dashboards + alerts)            │    │
  │  │  Kibana  (log exploration)                │    │
  │  │  Jaeger UI (trace analysis)               │    │
  │  └───────────────────────────────────────────┘    │
  └────────────────────────────────────────────────────┘
```

## Observability Data Flow

```
SERVICE                        OBSERVABILITY STACK
  │                                   │
  │── Structured JSON logs ──────────► Fluentd → Elasticsearch → Kibana
  │── Metrics endpoint ──────────────► Prometheus → Grafana
  │── Traces (OTLP) ─────────────────► Collector → Jaeger
  │── Health endpoint ───────────────► Load Balancer → Monitoring
```

## Monitoring Topology

```
┌─────────────────────────────────────────────────────┐
│                     Production                       │
│                                                       │
│  ┌──────────────┐  ┌──────────────┐                  │
│  │ Service Mesh  │  │  Kubernetes  │                  │
│  │ (Istio)       │  │  Auto-scaling │                  │
│  │ Metrics       │  │  Based on     │                  │
│  │ + Traces      │  │  Metrics      │                  │
│  └──────────────┘  └──────────────┘                  │
│                                                       │
│  ┌──────────────────────────────────────────────┐    │
│  │            Observability Stack                 │    │
│  │  Separate Kubernetes namespace/instances      │    │
│  │  High availability (replicas + persistence)   │    │
│  └──────────────────────────────────────────────┘    │
└─────────────────────────────────────────────────────┘
```

## Key Decisions

| Component | Choice | Rationale |
|-----------|--------|-----------|
| Metrics | Prometheus + Thanos | Long-term storage, HA |
| Logs | Elasticsearch + Kibana | Full-text search, RBAC |
| Traces | OpenTelemetry + Jaeger | Open standard, rich UI |
| Dashboards | Grafana | Multi-source, alerting |
| Alerting | Alertmanager | Routing, silencing, dedup |
