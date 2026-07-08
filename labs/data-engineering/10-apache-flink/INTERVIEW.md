# Interview Questions: Apache Flink

### Core Concepts
**Q**: How does Flink handle out-of-order events?
**A**: Through watermarks and allowed lateness. Watermarks define event-time progress; late events within allowedLateness trigger another window evaluation.

### Fault Tolerance
**Q**: Explain Flink's checkpointing algorithm.
**A**: Chandy-Lamport distributed snapshots with barrier alignment; state snapshotted asynchronously to durable storage; on failure, all tasks restart from latest checkpoint.

### State
**Q**: When would you use RocksDB vs Heap state backend?
**A**: RocksDB for >1GB state or memory-constrained environments; Heap for low-latency, sub-second state access with smaller state sizes.

### Performance
**Q**: How do you handle backpressure in Flink?
**A**: Monitor backpressure in web UI; increase parallelism; optimize operator chains; use unaligned checkpoints; increase network buffers.
