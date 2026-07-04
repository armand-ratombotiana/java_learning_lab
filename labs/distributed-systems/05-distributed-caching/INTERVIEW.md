# Distributed Caching: Interview Questions

## Q1: Design a distributed cache.
**A**: Use consistent hashing for key distribution. Replicate data across nodes. Support TTL and LRU eviction. Use gossip for node discovery. Handle node failures with automatic rebalancing.

## Q2: How do you prevent cache stampede?
**A**: Request coalescing (only one request loads data, others wait), early recomputation (recompute before expiry), and circuit breakers.

## Q3: Explain Redis Cluster architecture.
**A**: 16384 hash slots distributed across masters. Each master has 0+ replicas. Cluster bus for gossip/health. Automatic failover. Clients use MOVED redirects.

## Q4: How do you invalidate cache across services?
**A**: Use a message broker (Kafka) to broadcast invalidation events. Each service subscribes and invalidates local caches.

## Q5: What's the difference between Redis and Hazelcast?
**A**: Redis is a key-value store with cluster support. Hazelcast is an in-memory data grid with distributed computing features (distributed executors, locks, queues).
