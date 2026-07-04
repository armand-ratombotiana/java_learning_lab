# Distributed Locking: Performance

## Performance Comparison

| Lock Service | Acquire Latency | Throughput | Scalability | Fault Tolerance |
|--------------|-----------------|------------|-------------|-----------------|
| ZooKeeper    | 10-50ms         | 1K/s       | Medium      | High            |
| etcd         | 5-20ms          | 10K/s      | High        | High            |
| Redis (1)    | 1-5ms           | 100K/s     | Low         | Low             |
| Redlock      | 5-15ms          | 10K/s      | Medium      | High            |

## Optimization
1. **Lock sharding**: Distribute locks across multiple partitions
2. **Local caching**: Cache lock state where possible
3. **Batched operations**: Combine lock operations
4. **Async release**: Don't block on lock release

## Bottlenecks
- Consensus overhead (ZooKeeper, etcd)
- Network round trips
- Disk sync (ZooKeeper)
- Lock contention under high load
