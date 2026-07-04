# Partitioning: Performance

## Performance Characteristics

| Strategy           | Write Throughput | Range Query | Point Query | Rebalance Cost |
|--------------------|------------------|-------------|-------------|----------------|
| Range Sharding     | O(N)             | O(log N)    | O(log N)    | High           |
| Hash Sharding      | O(N)             | O(N)*       | O(1)        | Medium         |
| Consistent Hashing | O(N)             | O(N)*       | O(log V)    | Low            |
| Directory          | O(N)             | O(N)*       | O(1)        | Low            |

*Range queries require scatter/gather across all shards

## Scaling
- Read throughput: increases linearly with shard count
- Write throughput: increases linearly with shard count (with good key distribution)
- Storage: total capacity = sum of all shard capacities

## Optimization
- Pre-split hot shards before they become bottlenecks
- Use composite shard keys for balanced distribution
- Cache shard routing to reduce lookup overhead
