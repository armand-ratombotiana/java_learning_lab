# Common Monitoring & Logging Mistakes

1. **Alert fatigue**: Too many alerts; on-call ignores them. Use proper thresholds and grouping.
2. **Not setting up dashboards first** — can't observe what you don't measure.
3. **Logging everything at INFO level** — too much noise; use structured logging with levels.
4. **No log rotation/retention** — disks fill up, lose historical data.
5. **Scraping too frequently** — unnecessary load; 15-30s is usually sufficient.
6. **Storing high-cardinality labels** — Prometheus TSDB performance degrades.
7. **Not testing alerts** — discover broken alert rules during incidents.
8. **No correlation between metrics and logs** — lose context during debugging.
9. **Forgetting to monitor monitoring** — alertmanager failure goes unnoticed.
10. **No SLOs** — don't know when to alert vs when it's normal.
11. **Using Grafana as alerting source** — use Prometheus Alertmanager for reliability.
12. **Exposing sensitive data in logs** — PII, secrets, credentials.
