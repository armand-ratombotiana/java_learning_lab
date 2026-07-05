# Performance: Fraud Detection

## Throughput Targets
- Rule engine: < 50ms P99 per transaction
- ML inference: < 500ms P99 (async)
- End-to-end scoring: < 2s P99
- Feature retrieval: < 5ms P99 from Redis

## Bottlenecks
- **Geo-distance calculation**: Haversine formula is CPU-intensive. Cache results for common locations.
- **Serialization overhead**: JSON parsing for every transaction. Use Avro with Kafka.
- **Redis round trips**: Multiple Redis calls per transaction (features, velocity, blacklist). Pipeline commands.
- **ML model size**: Large models (100+ trees) increase inference time. Prune trees or use smaller ensemble.

## Optimization Strategies
- Fan-out feature extraction across multiple threads
- Use Redis pipelining for batch feature retrieval
- Pre-compute z-score statistics offline and cache in feature store
- Quantize ML model to float16 for faster inference
- Implement a local cache (Caffeine) for hot features
