# Interview: Distributed Cache

## Common Questions

### Q: Design a distributed cache like Redis Cluster.
Use consistent hashing with 16384 hash slots (like Redis). Each node owns a subset of slots. Gossip protocol for cluster membership. Async replication to replicas. Client library routes by slot. Automatic failover when primary is unreachable.

### Q: How does consistent hashing differ from simple modulo hashing?
Modulo-N requires reshuffling all keys when N changes. Consistent hashing only moves K/N keys. Consistent hashing also allows each node to have different weights (via virtual nodes).

### Q: How do you handle cache stampede (thundering herd)?
Use mutex: only one thread/request loads the value, others wait. Or use probabilistic early expiration (adds random jitter to TTL). Or use a separate loading service.

### Q: How would you implement cross-datacenter replication?
Async replication with conflict resolution (last-write-wins or CRDTs). Each DC has a full cluster. Writes go to local cluster, async replicated. Read-your-writes via sticky sessions.

### Q: How do you handle large values (>1MB)?
Off-heap storage (DirectByteBuffer). Chunk values across multiple nodes. Warn about performance impact. Consider compressing before storage.
