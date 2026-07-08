# How Data Observability Works

1. Observability agents collect metadata: table sizes, update timestamps, schemas
2. Data profilers sample data and compute statistics (null rates, distributions)
3. Lineage parsers analyze SQL logs to build dependency graph
4. Anomaly detection compares current metrics against historical baselines
5. Violations trigger alerts based on severity and thresholds
6. Incident management workflows notify owners and track resolution
7. Dashboards provide real-time health scores and trend charts
8. Metrics stored for historical analysis and compliance
9. Root cause analysis uses lineage graph to trace upstream issues
