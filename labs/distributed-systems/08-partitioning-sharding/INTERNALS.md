# Partitioning: Internals

## MongoDB Sharding

### Components
- **mongos**: Query router
- **Config servers**: Metadata storage
- **Shards**: Data storage (replica sets)

### Shard Key
- Immutable after creation
- Hashed or ranged
- Tag-aware sharding for location targeting

### Chunk Splitting
- Data divided into chunks (default 64MB)
- Chunks split when they exceed max size
- Balancer moves chunks between shards

## Cassandra Partitioning

### Partitioners
- **Murmur3Partitioner** (default): Consistent hashing
- **RandomPartitioner**: MD5-based
- **ByteOrderedPartitioner**: Range-based (deprecated)

### Virtual Nodes (vnodes)
- Each node owns 256 (default) virtual nodes
- Better distribution than single token
- Faster bootstrap (distributed load)

## Vitess (YouTube)

### Keyspace
- Logical database
- Sharded or unsharded

### VReplication
- Resharding via continuous replication
- Online schema changes
- Materialized views
