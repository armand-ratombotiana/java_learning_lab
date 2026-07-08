# Visual Guide to Streaming Analytics

```
Streaming Analytics Architecture:
[Event Sources: Apps, IoT, DBs]
    |
[Kafka / Kinesis: Stream Buffer]
    |
[Flink SQL / Kinesis Analytics: Stream Processing]
    |
[Materialized Views (PostgreSQL/ClickHouse)]
    |
[Grafana Dashboard / BI Tools]


Flink SQL Example:
CREATE TABLE orders (order_id BIGINT, amount DECIMAL, ts TIMESTAMP(3))
WITH ('connector' = 'kafka', 'topic' = 'orders', 'format' = 'json');

SELECT TUMBLE_START(ts, INTERVAL '1' MINUTE) AS window_start,
       SUM(amount) AS total_revenue
FROM orders
GROUP BY TUMBLE(ts, INTERVAL '1' MINUTE);

Dashboard: Real-Time Revenue
Current Minute: $12,450  [========          ] 62%
Today:         $847,230  [===============   ] 85%
Active Users:  1,423     [====              ]
```
