# Why Partitioning Exists

## Scalability Problem
- Single node can't handle all data
- Storage limits exceeded
- Query throughput insufficient
- Working set doesn't fit in memory

## Solution
Sharding divides data so each node handles a subset:
- **Storage**: More nodes = more total capacity
- **Throughput**: More nodes = more parallel operations
- **Latency**: Data closer to relevant users

## Use Cases
- Database sharding (Vitess, Citus, Spanner)
- Distributed caches (Redis Cluster)
- Message queues (Kafka partitions)
- File systems (HDFS blocks)
