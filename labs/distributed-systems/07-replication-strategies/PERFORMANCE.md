# Replication: Performance

## Performance Characteristics

| Strategy       | Write Latency | Read Throughput | Durability | Consistency |
|----------------|---------------|-----------------|------------|-------------|
| Async (RF=3)   | Low           | Very High       | Medium     | Eventual    |
| Sync (RF=3)    | High          | Very High       | High       | Strong      |
| Quorum R=2,W=2 | Medium        | High            | High       | Strong      |
| Multi-Leader   | Low           | Very High       | Medium     | Eventual    |

## Optimization
1. **Batch replication**: Group multiple changes into one replication message
2. **Parallel replication**: Apply changes concurrently on followers
3. **Reduce replication factor**: RF=2 for less critical data
4. **Use SSDs**: Faster binlog/relay log I/O

## Bottlenecks
- Network bandwidth (replication traffic)
- Disk I/O (binlog writes, relay log reads)
- Leader CPU (serializing changes)
