# Messaging - PERFORMANCE

## Throughput Benchmarks

| Broker | Max Throughput | Latency P99 | Max Messages |
|--------|---------------|-------------|-------------|
| Kafka (single node) | 500K msg/s | 10ms | 1MB per message |
| Kafka (3 node cluster) | 1.5M msg/s | 20ms | 1MB per message |
| RabbitMQ (single) | 50K msg/s | 2ms | 2GB max queue |
| Pulsar (cluster) | 1M+ msg/s | 10ms | Unlimited (segment storage) |

## Performance Factors

### Batch Size
| Batch Size | Throughput | Latency |
|-----------|-----------|---------|
| 1 message | 10K msg/s | 2ms |
| 100 messages | 100K msg/s | 10ms |
| 1000 messages | 1M msg/s | 100ms |

### Compression
| Type | Ratio | CPU Overhead |
|------|-------|-------------|
| None | 1.0x | 0% |
| Snappy | 1.7x | 5% |
| Gzip | 2.5x | 20% |
| Zstd | 2.2x | 8% |

## Optimization Tips

### Producer
```yaml
# Increase throughput at cost of latency
spring.kafka.producer.properties:
  linger.ms: 20        # wait for more messages before sending
  batch.size: 65536    # 64KB batch
  compression.type: snappy
  buffer.memory: 64MB
```

### Consumer
```yaml
# Increase fetch size for batch processing
spring.kafka.consumer.properties:
  fetch.min.bytes: 65536      # wait for 64KB before fetching
  fetch.max.wait.ms: 500       # max wait if less than min bytes
  max.poll.records: 500        # process 500 records per poll
```

### Partitions
```
More partitions = higher throughput but:
- More file handles
- More memory for replicas
- Longer rebalancing

Rule of thumb:
- partitions >= desired_throughput / per_partition_throughput
- partitions = max(consumers, throughput / per_consumer)
```

## Memory Usage

| Configuration | Memory Impact |
|--------------|--------------|
| 10 partitions × RF=3 × 8GB retention | 240GB storage |
| 1M inflight requests (producer) | ~500MB buffer memory |
| Consumer poll (500 records × 10KB) | ~5MB per poll |
| ZooKeeper (3 nodes) | ~2GB per node |

## Network Usage

```
Network_throughput = produce_rate × message_size × replication_factor
                   + consume_rate × message_size
```

For 100K msg/s × 1KB × RF=3:
```
Produce: 100K × 1KB × 3 = 300MB/s (leader receives, replicates 2x)
Consume: 100K × 1KB = 100MB/s
Total: ~400MB/s (3.2 Gbps)
```
