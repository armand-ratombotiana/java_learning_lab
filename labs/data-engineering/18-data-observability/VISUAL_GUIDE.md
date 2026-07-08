# Visual Guide to Data Observability

```
Observability Dashboard:
Data Health Score: 92/100

Freshness: \\[========= ] 92% (avg lag: 12 min)
Volume:    \\[==========] 99% (within expected range)
Schema:    \\[==========] 100% (no changes detected)

Lineage View:
[Source API] -> [Kafka] -> [Flink Job] -> [Snowflake] -> [Dashboard]
                                   |                     |
                              [Alert: lag spike]    [Impacted]

Anomaly Timeline:
Volume:  ----####----++++----####----
        Normal   Spike  Drop   Spike
```
