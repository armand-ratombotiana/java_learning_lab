# Mock Interview: Apache Flink (05-apache-flink)

## Scenario: Design a real-time anomaly detection pipeline
You need to detect anomalies in IoT sensor readings (100K events/sec) from 50K sensors. Each reading includes: sensor_id, temperature, pressure, vibration, timestamp. Anomalies flagged within 5 seconds.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Pipeline Architecture (15 min)

**Flink streaming topology:**

```
Kafka Source (sensor-readings topic)
    │
    ├── Map (parse JSON, validate schema)
    │
    ├── KeyBy (sensor_id)
    │
    ├── FlatMap (sliding window stats computation)
    │     │
    │     ├── State: ValueState (last 100 readings per sensor)
    │     ├── State: MapState (time-window aggregates per minute)
    │     └── State: ValueState (running mean + stddev)
    │
    ├── Process (anomaly detection logic)
    │     │
    │     ├── If |value - mean| > 3σ → emit anomaly alert
    │     └── If value < min_operational or > max_operational → emit critical alert
    │
    ├── Side Output 1: Anomaly events → Kafka (alerts topic)
    ├── Side Output 2: Raw readings → Kafka (archive topic)
    └── Main Output: Aggregated stats → Kafka (sensor-stats topic)
```

**Windowing strategy:**
- **Sliding window:** 5-minute window, slide every 1 minute
- Per-sensor statistics: mean, stddev, min, max, count
- Update running statistics incrementally (avoid recomputing full window each time)

**State schema per sensor:**
```java
// Running statistics state
public class SensorStats {
    double runningMean;
    double m2;  // sum of squares of differences (for stddev)
    long count;
    double min;
    double max;
    long lastUpdateTimestamp;
}

// Recent readings buffer (for exact anomaly detection)
public class ReadingBuffer {
    List<SensorReading> lastReadings = new ArrayList<>(100);
    int maxSize = 100;
}
```

---

## Part 2: State Management (10 min)

**Keyed state types used:**

| State Type | Purpose | Sizing |
|-----------|---------|--------|
| ValueState<Double> | Running mean per sensor | 8 bytes × 50K = 400KB |
| ValueState<Double> | Running stddev per sensor | 8 bytes × 50K = 400KB |
| ValueState<Long> | Reading count per sensor | 8 bytes × 50K = 400KB |
| ValueState<SensorStats> | All stats in one object | ~40 bytes × 50K = 2MB |
| MapState<Long, Reading> | Time-windowed readings | Dependent on parallelism |

**State backend choice:**
- **RocksDB:** Default for large state (disk-based, spill to disk)
- **Heap (FsStateBackend):** Fast but limited by heap memory
- **For 50K sensors with 100 readings each = 5M records:** RocksDB recommended

**Backpressure handling:**
- Monitor: `backPressureTimeMsPerSecond` metric > 100ms
- Causes: slow sink, insufficient parallelism, data skew
- Fix:
  1. Increase parallelism: `pipeline.default-parallelism`: 32 → 64
  2. Optimize operators: use `RichMapFunction` instead of `ProcessFunction` when possible
  3. Add buffer debloating: `taskmanager.memory.segment-size`, buffer timeout
  4. Checkpoint alignment: `task.numberOfPendingCheckpoints` alerts

---

## Part 3: Windowing & Late Data (10 min)

**Sliding window implementation:**
```java
DataStream<SensorReading> readings = env.addSource(kafkaSource);
DataStream<SensorStats> windowedStats = readings
    .keyBy(r -> r.sensorId)
    .window(SlidingEventTimeWindows.of(
        Time.minutes(5),   // window size
        Time.minutes(1)))  // slide interval
    .aggregate(new SensorStatsAggregator());
```

**Late data handling:**
```java
// Allow late data up to 1 minute
.window(SlidingEventTimeWindows.of(
    Time.minutes(5), Time.minutes(1)))
.allowedLateness(Time.minutes(1))
.sideOutputLateData(lateOutputTag);
```

**Watermark strategy:**
- BoundedOutOfOrderness: 30 seconds (assume 30s max network delay)
- `WatermarkStrategy.forBoundedOutOfOrderness(Duration.ofSeconds(30))`
- With allowedLateness(1 min): total late arrival window = 90 seconds

**Anomaly detection logic:**
```java
public class AnomalyDetector extends ProcessFunction<SensorReading, Alert> {
    ValueState<SensorStats> statsState;

    @Override
    public void processElement(SensorReading reading, Context ctx, Collector<Alert> out) {
        SensorStats stats = statsState.value();
        // Update incremental statistics
        stats.count++;
        double delta = reading.value - stats.runningMean;
        stats.runningMean += delta / stats.count;
        stats.m2 += delta * (reading.value - stats.runningMean);

        // Detect anomaly (3 standard deviations)
        double variance = stats.m2 / stats.count;
        double stddev = Math.sqrt(variance);
        if (Math.abs(reading.value - stats.runningMean) > 3 * stddev) {
            out.collect(new Alert(reading.sensorId, reading.timestamp,
                reading.value, stats.runningMean, stddev, "ANOMALY"));
        }
        statsState.update(stats);
    }
}
```

---

## Part 4: Fault Tolerance (10 min)

**Checkpointing configuration:**
```java
// Enable checkpointing every 30 seconds
env.enableCheckpointing(30000, CheckpointingMode.EXACTLY_ONCE);

// Advanced settings
CheckpointConfig config = env.getCheckpointConfig();
config.setCheckpointTimeout(60000);        // 1 min timeout
config.setMinPauseBetweenCheckpoints(500); // Minimum pause
config.setMaxConcurrentCheckpoints(1);     // One at a time
config.enableExternalizedCheckpoints(
    CheckpointConfig.ExternalizedCheckpointCleanup.RETAIN_ON_CANCELLATION);

// State backend: RocksDB
env.setStateBackend(new RocksDBStateBackend("hdfs:///flink/checkpoints", true));
```

**Savepoint strategy:**
- Manual savepoints before deployment changes
- `flink savepoint <jobId> [targetDirectory]`
- Upgrade job: `flink run -s <savepointPath> -d job.jar`
- Savepoints capture full state, checkpoints are for recovery

**Recovery scenario:**
1. Flink job crashes (e.g., Kafka cluster unavailable)
2. JobManager restarts with last successful checkpoint
3. State restored to checkpoint boundary
4. Source re-reads from last committed Kafka offset
5. Exactly-once: no data loss, no duplicates (with idempotent sink)

---

## Follow-up Questions

**Flink vs Spark Structured Streaming:**
| Feature | Flink | Spark Streaming |
|---------|-------|----------------|
| Engine | True streaming (event-by-event) | Micro-batch |
| Latency | Sub-second | Seconds (5s+ for microbatch) |
| State management | Rich (RocksDB, timers) | Limited (state stores) |
| Event time | Excellent (watermarks, allowed lateness) | Good |
| Snapshot | Distributed checkpointing | Checkpoints possible but heavier |
| When to choose | Low latency, complex state | Batch + stream, existing Spark |

**State backpressure detection:**
- `numRecordsInPerSecond` / `numRecordsOutPerSecond` ratio
- `mailboxMailsPerSecond` (task mailbox pressure)
- Operator latency: `latencyTrackingInterval` metric
- Add metrics reporter (Prometheus, Graphite) for dashboarding

**Idempotent sink pattern:**
```java
// Use Kafka producer with idempotent enabled
properties.setProperty("enable.idempotence", "true");
properties.setProperty("acks", "all");

// Or use transactional sink
kafkaSink = KafkaSink.<Alert>builder()
    .setDeliveryGuarantee(DeliveryGuarantee.EXACTLY_ONCE)
    .setTransactionalIdPrefix("anomaly-alert")
    .build();
```

