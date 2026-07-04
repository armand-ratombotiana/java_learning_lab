# Messaging - MATH FOUNDATION

## Throughput Calculation

### Kafka Throughput
```
Throughput = batch_size × batch_count × compression_ratio
           ─────────────────────────────────────────────
                       replication_factor
```

For 10KB messages, 1000 batch count, snappy compression (2x), 3x replication:
```
Throughput ≈ 10KB × 1000 × 2 / 3 ≈ 6.7MB/s per partition
```

### Scaling Partitions
```
Total_throughput = min(producer_throughput, broker_throughput × partitions, consumer_throughput)
```

With 3 brokers, 12 partitions, 100KB/s per partition consumer:
```
Max = min(∞, 3 × 100MB/s, 12 × 100KB/s) = 1.2MB/s
```
Consumer processing speed is often the bottleneck.

## Queueing Theory (Little's Law)

### Consumer Queue
```
L = λ × W
```

Where:
- `L` = number of messages in queue
- `λ` = message arrival rate
- `W` = processing time per message

### Backlog Prediction
With 1000 msg/s arrival, 200ms processing time, 1 consumer:
```
L = 1000 × 0.2 = 200 messages
Backlog per minute of consumer outage: 1000 × 60 = 60,000 messages
Recovery time with 1 consumer: 60s
Recovery time with 4 consumers: 15s
```

## Partition Count Optimization

### Kafka Partition Count
```
min_partitions = max(throughput_required / throughput_per_partition,
                     consumers_required)
```

### Maximum Partitions Per Broker
```
max_partitions ≈ min(memory_per_broker / (partition_overhead + log_size),
                     file_descriptor_limit / 3)
```

Partition overhead: ~1KB per partition × replicas
Typical max: 4000 partitions per broker

## Replication Factor

### Durability vs Storage
```
Wasteful_storage = data_size × (replication_factor - 1)
Write_latency = max(ack_from_replicas_count × network_latency)
```

RF=3, W=2 (minISR=2): Tolerates 1 broker failure
RF=3, W=3 (all ISR): Tolerates 0 broker failure during write

## Consumer Group Rebalancing

### Rebalance Duration
```
T_rebalance = T_detect + T_assign + T_seek + T_catch_up
```

For 100 consumers, 1000 partitions:
```
T_rebalance ≈ 100ms + 500ms + 50ms + (lag × processing_time)
```
With 1M lag at 100 msg/s: 10,000 seconds — catastrophic.

## Delivery Semantics Overhead

| Semantics | Producer Overhead | Consumer Overhead |
|-----------|------------------|-------------------|
| At-most-once | None | None |
| At-least-once | Retries | Idempotency check |
| Exactly-once | Transactional API + idempotency | Transactional read + dedup |
