# Data Observability Architecture

```
[Data Sources: DB, Lake, Warehouse, Stream]
                    |
[Observability Agents: Collectors, Profilers, Parsers]
    |                 |                  |
[Freshness]    [Volume/Profiling]    [Lineage Capture]
    |                 |                  |
[Alerting] <--- [Anomaly Detection] ---> [Metrics DB]
    |                                    |
[PagerDuty/Slack]              [Dashboard/Grafana]
```
