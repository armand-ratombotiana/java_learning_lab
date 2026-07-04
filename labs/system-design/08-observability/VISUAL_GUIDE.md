# Observability - VISUAL GUIDE

## Observability Stack

```
┌────────────────────────────────────────────────────────────┐
│                        Services                             │
│  ┌──────────┐  ┌──────────┐  ┌──────────┐                 │
│  │ Order    │  │ Payment  │  │Inventory │                 │
│  │ Service  │  │ Service  │  │ Service  │                 │
│  └────┬─────┘  └────┬─────┘  └────┬─────┘                 │
│       │             │             │                       │
│  Structured Logs  Metrics +     Traces                    │
│  (stdout/JSON)    Prometheus    OpenTelemetry              │
└───────┼─────────────┼─────────────┼───────────────────────┘
        │             │             │
        ▼             ▼             ▼
┌──────────────┐ ┌──────────┐ ┌────────────┐
│  Log         │ │Prometheus│ │  Jaeger    │
│  Aggregator  │ │(Metrics) │ │ (Tracing)  │
│  (Loki/ELK)  │ └────┬─────┘ └─────┬──────┘
└──────┬───────┘      │             │
       │              │             │
       ▼              ▼             ▼
┌────────────────────────────────────────────┐
│              Grafana                        │
│  Dashboards + Alerting                     │
└────────────────────────────────────────────┘
```

## Trace Visualization (Jaeger)

```
Service: order-service
  POST /api/v1/orders                      Duration: 250ms
  ├── validateOrder                        50ms
  ├── processPayment (HTTP → payment-svc)  120ms
  │   └── chargeCard                       80ms
  ├── reserveInventory (Kafka → inventory)  30ms
  └── sendConfirmation (Kafka → notify)     20ms
```

## Grafana Dashboard Example

```
┌────────────────────────────────────────────────────────────┐
│  API Overview (last 1 hour)                                │
├───────────────────┬───────────────────┬────────────────────┤
│ Requests/sec      │ Error Rate        │ Avg Latency        │
│ ┌───┐             │ ┌───┐             │ ┌───┐              │
│ │500│             │ │0.5%│            │ │150│              │
│ └───┘             │ └───┘             │ └───┘              │
├───────────────────┴───────────────────┴────────────────────┤
│ Request Rate (line chart)                                  │
│    ▁▃▇██▇▆▅▄▃▁▁▃▅▇███▇▆▄▃▂▁▂▃▄▅▇█                       │
│    └────────────────────────────────────────────           │
│      12:00           12:30           13:00                 │
├───────────────────┬───────────────────────────────────────┤
│ P99 Latency       │ Top 5 Endpoints by Latency             │
│ ┌────────┐       │ ┌────────────────────────────────────┐ │
│ │ 1.2s   │       │ │ /api/v1/orders       250ms         │ │
│ └────────┘       │ │ /api/v1/products     120ms         │ │
│                   │ │ /api/v1/payments      95ms         │ │
│                   │ └────────────────────────────────────┘ │
└───────────────────┴───────────────────────────────────────┘
```

## Alert Flow

```
                    ┌─────────────┐
                    │  Service    │
                    │  Metrics    │
                    └──────┬──────┘
                           │ scrape
                    ┌──────▼──────┐
                    │  Prometheus  │
                    │  Rules       │
                    │  Evaluation  │
                    └──────┬──────┘
                           │ fire
                    ┌──────▼──────┐
                    │ Alertmanager │
                    │              │
                    │ Routing,     │
                    │ Silencing,   │
                    │ Grouping     │
                    └──────┬──────┘
                           │ notify
        ┌──────────────────┼──────────────────┐
        ▼                  ▼                  ▼
┌──────────────┐  ┌──────────────┐  ┌──────────────┐
│   PagerDuty  │  │    Slack     │  │    Email     │
│  (On-call)   │  │ (Team Alert) │  │ (Digest)     │
└──────────────┘  └──────────────┘  └──────────────┘
```
