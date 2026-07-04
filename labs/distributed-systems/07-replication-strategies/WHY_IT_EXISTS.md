# Why Replication Exists

## Goals
1. **High Availability**: Data survives node failures
2. **Scalability**: Distribute read load across replicas
3. **Latency Reduction**: Serve data from geographically close nodes
4. **Durability**: Data persists despite disk failures

## Without Replication
- Single node failure causes data loss
- Read capacity limited to single node
- Geographic latency for global users

## Use Cases
- Database clusters (PostgreSQL, MySQL replication)
- Key-value stores (Redis, Cassandra)
- Distributed file systems (HDFS)
- Configuration stores (etcd, ZooKeeper)
