# Code Deep Dive: Streaming Analytics

See Java source files in src/main/java/com/dataeng/twenty/ for:
- StreamingAnalyticsJob.java: Flink SQL streaming analytics pipeline
- DashboardDataService.java: REST API for materialized view serving

Key patterns:
```java
// Flink SQL streaming analytics
StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
StreamTableEnvironment tEnv = StreamTableEnvironment.create(env);

tEnv.executeSql("CREATE TABLE orders (" +
    "order_id BIGINT, amount DECIMAL(10,2), customer_id STRING, " +
    "order_ts TIMESTAMP(3), WATERMARK FOR order_ts AS order_ts - INTERVAL '5' SECOND" +
    ") WITH ('connector' = 'kafka', 'topic' = 'orders', 'format' = 'json', " +
    "'properties.bootstrap.servers' = 'localhost:9092')");

tEnv.executeSql("CREATE TABLE metrics (" +
    "window_start TIMESTAMP(3), revenue DECIMAL(10,2), order_count BIGINT, " +
    "PRIMARY KEY (window_start) NOT ENFORCED" +
    ") WITH ('connector' = 'jdbc', 'url' = 'jdbc:postgresql://localhost/metrics', ...)");

tEnv.executeSql("INSERT INTO metrics SELECT " +
    "TUMBLE_START(order_ts, INTERVAL '1' MINUTE), " +
    "SUM(amount), COUNT(*) " +
    "FROM orders GROUP BY TUMBLE(order_ts, INTERVAL '1' MINUTE)");

env.execute("Revenue Analytics Pipeline");
```
