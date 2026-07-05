# Code Deep Dive: Distributed Cache

## Consistent Hash Ring

`HashRing` maintains a `TreeMap<Long, Node>` mapping hash positions to nodes. On node add, 160 virtual nodes are created per physical node (hash = MD5(host + port + virtualIndex)). `getNode(key)` computes MD5(key), gets `TreeMap.ceilingEntry(hash)`, wraps to first entry if no ceiling. On node removal, entries are reassigned live.

## LRU Eviction

`LRUCache` wraps ConcurrentHashMap with a ConcurrentLinkedDeque for access ordering. On GET, the node is moved to the front of the deque. When memory exceeds maxBytes, nodes are evicted from the tail. A background thread scans every 100ms. TTL expiration runs separately with a `DelayQueue` of expiration events.

## Gossip Protocol

`GossipManager` runs a background thread every 1s. It picks 3 random peers, sends its membership list (heartbeat map). On receive, it merges: higher heartbeat wins. If a node's heartbeat hasn't increased in SUSPICION_TIMEOUT (5s), it's marked SUSPECT. If no recovery after FAIL_TIMEOUT (15s), marked DEAD and removed from hash ring.

## Replication

`ReplicationManager` handles async replication. On SET, the primary writes locally, then sends replicate requests to the next N-1 nodes on the ring (clockwise). Replication uses a separate connection pool with non-blocking IO. If a replica fails, it's retried with exponential backoff (max 3 retries).
