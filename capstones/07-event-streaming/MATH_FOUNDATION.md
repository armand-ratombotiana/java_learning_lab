# Math Foundation: Event Streaming

## Throughput
- Write throughput per partition: sequential disk write speed ~ 200-500 MB/s
- Max throughput = num_partitions * write_speed_per_partition
- Read throughput: similar (sequential read)

## Replication Latency
- min(ISR) condition: producer acks=all waits for all ISR to acknowledge
- P99 latency vs replication factor: RF=3 adds ~1 RTT versus RF=1

## Consumer Lag
- lag = producer_offset - consumer_offset
- Catching up time = lag / (fetch_bytes_per_request / network_bandwidth)

## Compaction
- Active segments not compacted
- Clean ratio threshold: dirty_ratio > 50% triggers compaction
- Compaction copies clean segments, removes obsolete keys

## Partition Count
- Max recommended partitions per broker: 4000 (with 4GB heap, 4 CPU cores)
- Partitions per topic: aim for max consumer parallelism
