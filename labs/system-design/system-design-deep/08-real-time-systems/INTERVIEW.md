# Interview Deep-Dive: Real-Time Systems

## Common Questions

### Q1: What's the difference between event time and processing time?
**Answer**: Event time is when the event occurred at the source. Processing time is when the system processes it. Event time gives correct results regardless of processing delays but requires watermarking for out-of-order data. Processing time is simpler but results vary with system load.

### Q2: How do watermarks work in Apache Flink?
**Answer**: Flink tracks the minimum event time across all active sources. Watermarks = (min observed event time) - (allowed lateness). When a watermark passes a window's end time, Flink triggers the window computation. Watermarks can be generated periodically or per-event, and can use a heuristic based on observed event time skew.

### Q3: How do you achieve exactly-once semantics in a streaming system?
**Answer**: Three components: (1) **Idempotent sinks** — writes are idempotent so duplicates produce the same result, (2) **Transactional sources** — consume and process in a transaction (Kafka transactions, Flink checkpoints), (3) **Offset tracking** — commit offsets atomically with processing output. All three together prevent any data loss or duplication.

## System Design Whiteboard

**Design a real-time fraud detection system.**
- **Source**: 50K transactions/second from Kafka (12 partitions)
- **Processing**: Flink with 100ms checkpoint interval
- **Windowing**: 5-second sliding window, 1-second slide
- **Watermarks**: Heuristic, 2-second allowed lateness
- **State**: RocksDB-backed keyed state per user (last 100 transactions)
- **Exactly-once**: Kafka transactions (source + sink)
- **Alerting**: Side output for suspicious transactions → alert service
- **Latency**: p99 < 500ms end-to-end

## Key Trade-offs to Discuss
- Event time vs processing time (accuracy vs simplicity)
- Checkpoint interval vs recovery time
- State size vs performance (RocksDB vs in-memory)
- Exactly-once vs throughput overhead
