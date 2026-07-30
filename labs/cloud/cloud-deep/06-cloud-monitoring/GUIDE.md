# GUIDE — Cloud Monitoring

## Step 1: Metrics Engine
```java
public enum MetricType { COUNTER, GAUGE, HISTOGRAM }
public record MetricPoint(String name, double value, long timestamp, Map<String,String> labels) {}
```

## Step 2: Aggregation Pipeline
- Window-based average, P50/P95/P99 computation
- Rate and delta calculation for counters
- Label-based grouping and filtering

## Step 3: Structured Logging
```java
LogEvent event = LogEvent.info("processing.request")
    .with("requestId", reqId)
    .with("duration", elapsedMs);
```

## Step 4: Distributed Tracing
- Create root span for incoming request
- Create child spans for downstream calls
- Propagate trace context via headers

## Step 5: Anomaly Detector
```java
MovingAverageDetector detector = new MovingAverageDetector(windowSize, threshold);
detector.record(value);
if (detector.isAnomalous()) { alertManager.fire(alert); }
```

## Step 6: Exercises
1. Build a trace exporter that writes spans to stdout in OTLP format
2. Implement a correlation rule that links log lines to traces
3. Create a dashboard rendering engine with time-series charts
