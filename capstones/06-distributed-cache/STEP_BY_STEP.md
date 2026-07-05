# Step by Step: Distributed Cache

## SET Operation

1. Client sends SET key value [TTL] to any node (bootstrap node)
2. Client library computes hash(key) -> determines primary node
3. Client library sends SET directly to primary node
4. Primary CacheEngine stores (key, value, timestamp, TTL) in ConcurrentHashMap
5. Primary ReplicationManager sends replicate to next N-1 nodes clockwise
6. Replicas acknowledge (ACK) or the request is queued for retry
7. Primary responds to client with OK
8. If TTL > 0, an expiration event is added to DelayQueue

## GET Operation

1. Client sends GET key to any node
2. Client library routes to primary node (hash(key) -> node)
3. If primary is available and key exists, return value + refresh LRU position
4. If primary is unavailable, client tries replica nodes in order
5. If key not found in any replica, return nil (cache miss)
6. Key is also checked for TTL expiration before returning

## Node Join

1. New node starts, registers with any existing node (seed)
2. Seed gossips new node info to cluster
3. Hash ring is updated: some key ranges now belong to new node
4. Those keys are migrated from old owners to new node (background)
5. Replication starts replicating to new node as replica for surrounding range
