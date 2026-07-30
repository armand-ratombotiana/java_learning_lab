# Implementation Guide: Real-Time Systems

## 1. Stream Processing Models

### Record-at-a-Time
Process each event independently. Lowest latency, no state needed. Good for simple transformations.

### Micro-Batch
Buffer events into small batches (seconds). Higher throughput, moderate latency. Spark Streaming uses this.

### Continuous Processing
Process each event as it arrives with persistent state. Flink, Kafka Streams, ksqlDB.

## 2. Windowing Strategies

| Window Type | Behavior | Use Case |
|-------------|----------|----------|
| **Tumbling** | Fixed non-overlapping intervals | Hourly aggregates |
| **Sliding** | Fixed overlapping intervals | Moving averages |
| **Session** | Activity-based intervals | User session analytics |
| **Global** | All events, one window | Full dataset aggregates |

### Implementation
```java
// Tumbling window: 1-minute intervals
long windowStart = (timestamp / 60_000) * 60_000;
long windowEnd = windowStart + 60_000;

// Sliding window: 10-second slide, 30-second length
long windowStart = ((timestamp / 10_000) - 2) * 10_000;
long windowEnd = windowStart + 30_000;
```

## 3. Watermarks

### Concept
A watermark is a timestamp that indicates "no events with timestamp < watermark will arrive." Used for event-time processing with out-of-order data.

### Types
- **Perfect watermark**: guarantees no late events (requires ordered source)
- **Heuristic watermark**: estimated, some late events may arrive
- **Idle watermark**: advances when no new data (prevents stuck pipelines)

### Late Data Handling
1. Drop late events (simplest)
2. Recompute window and emit update
3. Side output for late events (reprocess later)

## 4. Exactly-Once Semantics

### Idempotent Writes
Write operation produces the same result regardless of how many times it's applied:
```sql
INSERT INTO sink (key, value, timestamp)
VALUES (?, ?, ?)
ON CONFLICT (key, timestamp) DO UPDATE SET value = excluded.value;
```

### Transactional Sinks
Use two-phase commit between stream processor and sink:
- Kafka transactions: produce + consume atomically
- Flink's exactly-once checkpoints

### Source-side
Track consumed offsets in the sink:
```java
// After processing and writing, commit offset
source.commitOffset(partition, offset);
// Or use transactional: write + offset commit in same transaction
```

## 5. Low-Latency Design Patterns

### State Management
- **Embedded state**: RocksDB for large state (disk-backed)
- **Managed state**: Heap-based for small state (fast)
- **KV store**: Redis for shared state across instances

### Serialization
- Use binary formats (Avro, Protobuf)
- Avoid Java serialization
- Pre-allocate buffers

### Backpressure
- Dynamic scaling based on lag
- Rate-limiting the source
- Async I/O for external calls
