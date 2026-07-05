# Theory: Distributed Cache

## Consistent Hashing
Maps keys to nodes in a ring topology. Adding/removing nodes only affects K/N keys (where K = total keys, N = nodes). Virtual nodes improve load distribution. Hash ring: hash(key) -> position on ring, assign to nearest clockwise node.

## Replication
Data is replicated to R nodes for fault tolerance. Read from any replica (eventual consistency) or from primary (strong consistency). Write: write to all replicas or quorum of W nodes.

## Cache Eviction Policies
- **LRU** (Least Recently Used): Evict least recently accessed items
- **LFU** (Least Frequently Used): Evict least frequently accessed items
- **TTL** (Time-To-Live): Remove expired entries
- **FIFO** (First In, First Out): Evict oldest entries

## Cluster Membership
Gossip protocol for node discovery and failure detection. Each node maintains a membership list with heartbeat counters. Nodes periodically exchange membership info. Suspicion mechanism before declaring node dead.

## Cache Invalidation
- Write-through: Write to cache + database synchronously
- Write-behind: Write to cache, async write to database
- Cache-aside (lazy loading): Check cache first, miss -> load from DB -> populate cache
- Write-invalidate: Update database, invalidate cache entry
