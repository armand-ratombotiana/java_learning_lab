# How Monitoring & Logging Works

## Prometheus Architecture
```
Application (exposes /metrics) ──scrapes──▶ Prometheus Server
                                               │
                                         ┌─────┴─────┐
                                         │ TSDB      │
                                         │ (storage) │
                                         └─────┬─────┘
                                               │
                                    ┌──────────┴──────────┐
                                    │ PromQL queries      │
                                    │ Alertmanager        │
                                    │ Grafana (visualize) │
                                    └─────────────────────┘
```

## ELK Stack Flow
```
Application ──logs──▶ Filebeat ──▶ Logstash ──▶ Elasticsearch ──▶ Kibana
                        │            │              │
                    (ship logs)  (parse/enrich)  (store/index)  (visualize)
```

## Alerting Pipeline
```
Prometheus ──alert──▶ Alertmanager ──group/route──▶ Slack/Email/PagerDuty
    │                                                   │
    └──────────── resolve ◄─────────────────────────────┘
```

## How Metrics Work
1. Application exposes `/metrics` endpoint (counter, gauge, histogram).
2. Prometheus scrapes endpoint every N seconds (configurable).
3. Time-series data stored in TSDB (block-based, compressed).
4. Grafana queries Prometheus via PromQL for visualization.
5. Alerting rules in Prometheus trigger Alertmanager when conditions met.
