# Step-by-Step: Working with Streaming Analytics

1. Set up Kafka cluster and create input topic
2. Build event generator producing JSON events to Kafka
3. Configure Flink cluster: standalone, YARN, or K8s
4. Create Kafka source table in Flink SQL with watermark
5. Write windowed aggregation query (TUMBLE, HOP, SESSION)
6. Create sink table (PostgreSQL, ClickHouse) for materialized view
7. Insert streaming results into sink table
8. Configure Grafana datasource to PostgreSQL/ClickHouse
9. Build dashboard panels with auto-refresh
10. Set up alerting rules (threshold, anomaly)
