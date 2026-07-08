package com.dataeng.twenty;

public class StreamingAnalyticsJob {
    private final String jobName;
    private final String sourceTopic;
    private final String sinkTable;
    private final String query;

    public StreamingAnalyticsJob(String jobName, String sourceTopic, String sinkTable, String query) {
        this.jobName = jobName;
        this.sourceTopic = sourceTopic;
        this.sinkTable = sinkTable;
        this.query = query;
    }

    public String getFlinkSqlStatement() {
        return String.format(
            "CREATE TABLE source (event_id BIGINT, event_type STRING, " +
            "value DOUBLE, event_ts TIMESTAMP(3), " +
            "WATERMARK FOR event_ts AS event_ts - INTERVAL '5' SECOND) WITH (" +
            "'connector' = 'kafka', 'topic' = '%s', " +
            "'properties.bootstrap.servers' = 'localhost:9092', 'format' = 'json'); " +
            "CREATE TABLE sink (window_start TIMESTAMP(3), metric STRING, " +
            "count_value BIGINT, avg_value DOUBLE, " +
            "PRIMARY KEY (window_start, metric) NOT ENFORCED) WITH (" +
            "'connector' = 'jdbc', 'url' = 'jdbc:postgresql://localhost:5432/analytics', " +
            "'table-name' = '%s'); INSERT INTO sink %s;",
            sourceTopic, sinkTable, query);
    }

    public String getTumblingWindowQuery(String windowSize) {
        return String.format(
            "SELECT TUMBLE_START(event_ts, INTERVAL '%s') AS window_start, " +
            "event_type AS metric, COUNT(*) AS count_value, AVG(value) AS avg_value " +
            "FROM source GROUP BY TUMBLE(event_ts, INTERVAL '%s'), event_type",
            windowSize, windowSize);
    }

    public String getSlidingWindowQuery(String windowSize, String slideSize) {
        return String.format(
            "SELECT HOP_START(event_ts, INTERVAL '%s', INTERVAL '%s') AS window_start, " +
            "event_type AS metric, COUNT(*) AS count_value, AVG(value) AS avg_value " +
            "FROM source GROUP BY HOP(event_ts, INTERVAL '%s', INTERVAL '%s'), event_type",
            slideSize, windowSize, slideSize, windowSize);
    }

    public String getSessionWindowQuery(String gapSize) {
        return String.format(
            "SELECT SESSION_START(event_ts, INTERVAL '%s') AS window_start, " +
            "event_type AS metric, COUNT(*) AS count_value, AVG(value) AS avg_value " +
            "FROM source GROUP BY SESSION(event_ts, INTERVAL '%s'), event_type",
            gapSize, gapSize);
    }

    public String getJobName() { return jobName; }
}
