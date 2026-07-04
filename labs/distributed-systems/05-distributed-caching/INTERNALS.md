# Distributed Caching: Internals

## Redis Cluster Architecture

### Sharding
- 16384 hash slots across cluster
- Keys hashed with CRC16(key) % 16384
- Each node manages subset of slots

### Replication
- Each master has 0+ replicas
- Replicas asynchronously replicate from master
- Automatic failover via cluster bus

### Cluster Bus
- Gossip protocol for node discovery
- Failure detection (PING/PONG)
- Configuration propagation

## Hazelcast Architecture

### Partition Groups
- 271 partitions by default
- Data distributed via consistent hashing
- Each partition has primary and backup

### Operation System
- Operations sent to partition owner
- Read operations may read from backups
- Write operations go to primary

### Invalidation
- Near cache invalidation via events
- Distributed executor for cluster-wide operations
