# Performance Optimization for Apache Flink

## Parallelism Tuning
Match to Kafka partition count; ensure enough TaskManager slots; avoid idle slots wasting resources

## State Optimization
RocksDB for >1GB state; configure state TTL to expire stale data; minimize key cardinality for distribution

## Checkpoint Tuning
Async checkpoints for state backends; unaligned for backpressure; configure sufficient checkpoint timeout and minimum pause between

## Network Buffers
Increase taskmanager.memory.network.fraction for shuffle-heavy jobs; configure netty threads and buffer sizes
