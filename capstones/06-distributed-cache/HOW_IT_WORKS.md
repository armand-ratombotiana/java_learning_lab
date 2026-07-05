# How It Works: Distributed Cache

1. Client connects to any node in the cluster (seed node)
2. Node returns cluster topology (list of nodes and their hash ranges)
3. Client computes hash(key) -> determines which node owns the key
4. Client sends GET/SET/DELETE request directly to the owning node
5. On SET: primary node writes locally, replicates to N-1 replica nodes (async or sync)
6. On GET: primary node reads from local cache (or from replica if primary unavailable)
7. Cache eviction runs periodically (LRU) or on memory pressure
8. TTL expiration scans run every 100ms to remove stale entries
9. Gossip protocol runs every 1s: exchange heartbeat counters with random peers
10. On node failure detection: replicas promoted, hash ring updated, membership broadcast
