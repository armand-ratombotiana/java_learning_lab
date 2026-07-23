# Interview Cheatsheet: Kafka Consumer Lag

## Key Diagnostic Commands
- `kafka-consumer-groups --bootstrap-server --group --describe` — group lag
- `kafka-consumer-groups --bootstrap-server --group --reset-offsets` — reset
- JMX metrics: `kafka.consumer:type=consumer-fetch-manager-metrics`
- Burrow (LinkedIn) — Kafka lag monitoring
- Prometheus + kafka_exporter — consumer lag metrics

## Common Metrics to Check
- Consumer lag (messages behind)
- Consumer lag growth rate
- Messages consumed per second
- Consumer group rebalance count
- Processing time per message
- Consumer thread pool queue size

## Typical Root Causes
- Slow message processing (CPU/IO bound)
- Backend dependency bottleneck (DB write, API call)
- Consumer poll interval too small → rebalance
- Partition imbalance (hot partitions)
- Serialization/deserialization performance issue
- Batch processing not optimized
- Consumer thread count < partition count
- GC pauses causing session timeout

## Interview Question Patterns
- "How do you monitor and alert on Kafka consumer lag?"
- "Design a system to handle sudden lag spikes"
- "How does Kafka partition rebalancing work?"
- "What causes consumer group rebalance storms?"

## STAR Story Template
**S**: Order processing pipeline lagged from 10ms to 8 hours
**T**: Clear the backlog and prevent future lag
**A**: Found slow DB write (missing index) + single consumer thread, added index, increased consumer threads to match partitions
**R**: Lag cleared in 20 min, processing latency back to 10ms
