# Internals: Distributed Cache

## Core Components
- **CacheEngine**: In-memory ConcurrentHashMap-based store with eviction support
- **HashRing**: Consistent hashing implementation with virtual nodes (160 virtual nodes per physical node)
- **GossipManager**: UDP-based gossip for cluster membership; TCP for data replication
- **ReplicationManager**: Handles async write replication to replica nodes
- **EvictionManager**: LRU via ConcurrentLinkedDeque + access recency tracking
- **TransportLayer**: Netty-based NIO server for client and inter-node communication

## Consistent Hashing Implementation
- MD5 hash on key + virtual node ID
- TreeMap sorted by hash for O(log N) node lookup
- Replication factor R=3 (primary + 2 replicas clockwise)
- On node add/remove: only reassign keys between affected nodes

## Protocol
- Custom binary protocol (Redis RESP-like but optimized)
- Operations: GET, SET, DELETE, EXISTS, TTL, EXPIRE, INCR, DECR
- Bulk operations: MSET, MGET (pipelining)
