# Visual Guide to Monitoring & Logging

## Observability Stack Architecture
```
┌────────────────────────────────────────────────────────────┐
│                   Observability Stack                       │
│                                                             │
│  ┌──────────┐    ┌──────────┐     ┌──────────┐            │
│  │ Metrics  │    │  Logs    │     │  Traces  │            │
│  │ (Prom)   │    │ (ELK)    │     │ (Jaeger) │            │
│  └────┬─────┘    └────┬─────┘     └────┬─────┘            │
│       │               │                │                  │
│       └───────────────┼────────────────┘                  │
│                       │                                   │
│                 ┌─────▼──────┐                             │
│                 │  Grafana   │                             │
│                 │ (Unified   │                             │
│                 │  Dashboard)│                             │
│                 └────────────┘                             │
└────────────────────────────────────────────────────────────┘
```

## Prometheus Scrape Flow
```
Target 1:443/metrics ──┐
Target 2:443/metrics ──┼──▶ Prometheus Server ──▶ Grafana
Target 3:443/metrics ──┘         │
                           ┌─────▼─────┐
                           │ Alert     │
                           │ manager   │──▶ Slack / PagerDuty
                           └───────────┘
```

## ELK Pipeline
```
Application ──▶ Filebeat ──▶ Logstash ──▶ Elasticsearch ──▶ Kibana
  stdout        (tail        (parse,        (store /        (visualize
  /stderr       log files)   transform)     index)          / search)
```

## Grafana Dashboard Panel
```
┌──────────────────────────────────────────────────────────┐
│  [CPU Usage]                        [Memory Usage]       │
│  ╭──────────────╮                  ╭──────────────╮     │
│  │   ▁▃▅▇▆▄▃▁   │ 87%             │   ▅▇▆▅▄▃▁▂▃   │ 72% │
│  ╰──────────────╯                  ╰──────────────╯     │
│  [Request Rate]                    [Error Rate]          │
│  ╭──────────────╮  1.2k/s         ╭──────────────╮  0.01%│
│  │   ▃▅▇▆▄▃▁▂   │                  │   ▁▁▁▁▁▂▁▁   │     │
│  ╰──────────────╯                  ╰──────────────╯     │
└──────────────────────────────────────────────────────────┘
```
