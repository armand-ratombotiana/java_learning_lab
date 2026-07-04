# Distributed ID Generation: Performance

## Performance Comparison

| Method           | Throughput     | Latency   | Storage  | Sortable |
|------------------|---------------|-----------|----------|----------|
| Snowflake        | 10M+ IDs/s    | <1μs      | 64-bit   | Yes      |
| UUID v4          | 1-5M IDs/s    | 1-5μs     | 128-bit  | No       |
| UUID v7          | 1-5M IDs/s    | 1-5μs     | 128-bit  | Yes      |
| DB Sequence      | 10K-100K/s    | 10-50ms   | 64-bit   | Yes      |
| Hi/Lo            | 1M+ IDs/s     | 50ms (hi) | 64-bit   | Yes      |

## Optimization
1. **Batch generation**: Pre-allocate IDs in batches
2. **No synchronization**: Use thread-local generators
3. **Avoid locks**: AtomicLong for sequence
4. **Pre-calculate**: Cache timestamp shifts

## Bottlenecks
- Clock resolution (ms granularity)
- Synchronization contention
- Network calls (DB sequences, ZooKeeper)
