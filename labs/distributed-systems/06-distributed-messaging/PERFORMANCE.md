# Distributed Messaging: Performance

## Broker Performance

| Broker     | Max Throughput | P99 Latency | Durability | Scalability |
|------------|---------------|-------------|------------|-------------|
| Kafka      | 10M+ msg/s    | 5-10ms      | High       | Excellent   |
| Pulsar     | 10M+ msg/s    | 2-5ms       | High       | Excellent   |
| RabbitMQ   | 100K msg/s    | <1ms        | Medium     | Good        |

## Optimization

### Kafka
- Increase batch size (batch.size, linger.ms)
- Compress messages (snappy, zstd)
- Tune page cache (vm.dirty_ratio)
- Use SSDs for partition data

### Consumer
- Process messages in parallel within each consumer
- Use multiple consumer partitions
- Tune fetch.min.bytes and fetch.max.wait.ms

## Bottlenecks
- Disk I/O (for durable writes)
- Network bandwidth
- GC pauses (JVM brokers)
