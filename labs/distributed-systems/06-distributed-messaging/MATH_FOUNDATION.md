# Distributed Messaging: Mathematical Foundation

## Throughput Modeling

For a Kafka broker with N partitions:

Total throughput T = N × T_partition

Where T_partition depends on:
- Disk sequential write speed S
- Replication factor R
- Ack configuration (1, all, -1)

T_partition = S / R (for acks=all)

## Latency

End-to-end latency L:
L = L_produce + L_broker + L_consume

- L_produce: Network + batch wait + acks
- L_broker: Queue time (near zero underprovisioned)
- L_consume: Poll interval + processing

## Consumer Lag

Lag(t) = produce_rate × Δt - consume_rate × Δt

Stable when produce_rate ≤ consume_rate × consumer_count / partition_count

## Exactly-Once Semantics

Requires:
1. Idempotent producer (unique sequence ID per partition)
2. Transactional coordinator (atomic commit across partitions)
3. Idempotent consumer (deduplication on read)
