# How Streaming Analytics Works

1. Events produced to Kafka topic from various sources (apps, IoT, DBs)
2. Flink SQL reads stream with watermark strategy for event-time handling
3. SQL queries compute windowed aggregations (per minute, hour, day)
4. Results upserted into materialized view (PostgreSQL, ClickHouse)
5. Grafana connects to materialized view database for fast queries
6. Dashboard panels auto-refresh to show latest aggregated data
7. Alert rules evaluate streaming metrics (threshold, trend, anomaly)
8. Alert notifications sent via Slack, email, PagerDuty
9. Users interact with dashboards for real-time business insights
