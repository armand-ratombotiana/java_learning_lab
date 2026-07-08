# Common Mistakes with Data Observability

1. Too Many Alerts: Every failed check triggers alert; aggregate by severity and use escalation policies
2. No Baselines: Anomaly detection requires historical baselines; start collecting immediately
3. Ignoring Schema Drift: Only monitoring volume/freshness; schema changes silently break downstream
4. Manual Lineage: Relying on manual documentation; automated capture is more reliable and current
5. No Incident Response: Detecting issues without defined process; incidents should have clear ownership and SLA
