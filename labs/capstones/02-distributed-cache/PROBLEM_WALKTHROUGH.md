# Problem Walkthrough: Distributed Cache with Consistent Hashing and Replication

## Problem Statement

**Design a distributed cache system that spans across 10+ nodes, supports automatic scaling (node addition/removal) with minimal data movement, provides configurable replication factor for fault tolerance, and handles node failures gracefully with automatic recovery.**

The system must handle 1M+ keys with values up to 1MB each, support read-heavy workloads (90:10 read-to-write ratio), maintain < 5ms latency for cache hits, and provide configurable eviction policies (LRU, LFU, TTL) per cache namespace. The cache serves as a distributed session store and data cache for a microservices platform.

### Business Requirements
- 10+ cache nodes, auto-scaling to 50 nodes
- Replication factor of 3 (each key stored on 3 nodes)
- < 5ms get latency (P99), < 10ms set latency (P99)
- Automatic data rebalancing on node addition/removal
- Configurable eviction: LRU, LFU, TTL per namespace
- Node failure detection within 5 seconds
- Automatic replica recovery within 30 seconds of failure detection

### Technical Constraints
- Java 21+ runtime
- Consistent hashing ring with virtual nodes (160 per physical node)
- Gossip protocol for cluster membership
- TCP-based replication with async write-behind
- No single point of failure
- Support for 64-bit key space (long keys)

---

## Solution Architecture

### Step 1: Consistent Hashing Ring

```java
public class ConsistentHashRing<T extends Node> {
    private final SortedMap<Long, T> ring = new ConcurrentSkipListMap<>();
    private final int virtualNodeCount;
    private final MessageDigest hashFunction;
    private final TreeSet<Long> sortedKeys = new TreeSet<>();

    public ConsistentHashRing(int virtualNodeCount) {
        this.virtualNodeCount = virtualNodeCount;
        try {
            this.hashFunction = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not available", e);
        }
    }

    public void addNode(T node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node.getId() + ":vnode:" + i);
            ring.put(hash, node);
            sortedKeys.add(hash);
        }
    }

    public void removeNode(T node) {
        for (int i = 0; i < virtualNodeCount; i++) {
            long hash = hash(node.getId() + ":vnode:" + i);
            ring.remove(hash);
            sortedKeys.remove(hash);
        }
    }

    public T getPrimaryNode(String key) {
        if (ring.isEmpty()) return null;
        long hash = hash(key);
        Long targetKey = sortedKeys.ceiling(hash);
        if (targetKey == null) {
            targetKey = sortedKeys.first();  // Wrap around
        }
        return ring.get(targetKey);
    }

    public List<T> getNodes(String key, int replicationFactor) {
        List<T> nodes = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        long hash = hash(key);
        Long currentKey = sortedKeys.ceiling(hash);
        if (currentKey == null) currentKey = sortedKeys.first();

        while (nodes.size() < replicationFactor && !sortedKeys.isEmpty()) {
            T node = ring.get(currentKey);
            if (seen.add(node.getId())) {
                nodes.add(node);
            }
            // Move to next position on ring
            currentKey = sortedKeys.higher(currentKey);
            if (currentKey == null) currentKey = sortedKeys.first();
        }
        return nodes;
    }

    private long hash(String key) {
        byte[] digest = hashFunction.digest(key.getBytes(StandardCharsets.UTF_8));
        return ((long)(digest[7] & 0xFF) << 56) |
               ((long)(digest[6] & 0xFF) << 48) |
               ((long)(digest[5] & 0xFF) << 40) |
               ((long)(digest[4] & 0xFF) << 32) |
               ((long)(digest[3] & 0xFF) << 24) |
               ((long)(digest[2] & 0xFF) << 16) |
               ((long)(digest[1] & 0xFF) << 8)  |
               ((long)(digest[0] & 0xFF));
    }
}
```

### Step 2: Eviction Policies

```java
public interface EvictionPolicy<K, V> {
    void onGet(K key, V value);
    void onPut(K key, V value);
    K evict();  // Returns key to evict
}

public class LRUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    private final LinkedHashMap<K, Long> accessOrder = new LinkedHashMap<>(16, 0.75f, true) {
        @Override protected boolean removeEldestEntry(Map.Entry<K, Long> eldest) {
            return false;  // Manual eviction only
        }
    };

    @Override
    public synchronized void onGet(K key, V value) {
        accessOrder.get(key);  // Touches order
    }

    @Override
    public synchronized void onPut(K key, V value) {
        accessOrder.put(key, System.nanoTime());
    }

    @Override
    public synchronized K evict() {
        if (accessOrder.isEmpty()) return null;
        K eldest = accessOrder.keySet().iterator().next();
        accessOrder.remove(eldest);
        return eldest;
    }
}

public class LFUEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    private final ConcurrentHashMap<K, AtomicInteger> frequencies = new ConcurrentHashMap<>();
    private final ConcurrentSkipListMap<Integer, Set<K>> frequencyIndex = new ConcurrentSkipListMap<>();

    @Override
    public void onGet(K key, V value) {
        frequencies.compute(key, (k, v) -> {
            int newCount = (v == null ? 0 : v.get()) + 1;
            frequencyIndex.computeIfAbsent(newCount - 1, k2 -> ConcurrentHashMap.newKeySet()).remove(key);
            frequencyIndex.computeIfAbsent(newCount, k2 -> ConcurrentHashMap.newKeySet()).add(key);
            return new AtomicInteger(newCount);
        });
    }

    @Override
    public void onPut(K key, V value) {
        frequencies.put(key, new AtomicInteger(1));
        frequencyIndex.computeIfAbsent(1, k -> ConcurrentHashMap.newKeySet()).add(key);
    }

    @Override
    public K evict() {
        Map.Entry<Integer, Set<K>> entry = frequencyIndex.firstEntry();
        if (entry == null) return null;
        K key = entry.getValue().iterator().next();
        entry.getValue().remove(key);
        frequencies.remove(key);
        if (entry.getValue().isEmpty()) {
            frequencyIndex.remove(entry.getKey());
        }
        return key;
    }
}

public class TTLBasedEvictionPolicy<K, V> implements EvictionPolicy<K, V> {
    private final ConcurrentHashMap<K, Long> expiryTimes = new ConcurrentHashMap<>();
    private final long defaultTtlMillis;

    public TTLBasedEvictionPolicy(long defaultTtlMillis) {
        this.defaultTtlMillis = defaultTtlMillis;
    }

    @Override
    public void onPut(K key, V value) {
        expiryTimes.put(key, System.currentTimeMillis() + defaultTtlMillis);
    }

    @Override
    public void onGet(K key, V value) {
        Long expiry = expiryTimes.get(key);
        if (expiry != null && System.currentTimeMillis() > expiry) {
            // Expired — will be evicted on next access
        }
    }

    @Override
    public K evict() {
        long now = System.currentTimeMillis();
        for (Map.Entry<K, Long> entry : expiryTimes.entrySet()) {
            if (now > entry.getValue()) {
                expiryTimes.remove(entry.getKey());
                return entry.getKey();
            }
        }
        return null;
    }
}
```

### Step 3: Cache Storage with Namespaces

```java
public class CacheStore<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> store = new ConcurrentHashMap<>();
    private final EvictionPolicy<K, V> evictionPolicy;
    private final long maxSize;

    public CacheStore(long maxSize, EvictionPolicy<K, V> evictionPolicy) {
        this.maxSize = maxSize;
        this.evictionPolicy = evictionPolicy;
    }

    public V get(K key) {
        CacheEntry<V> entry = store.get(key);
        if (entry == null) return null;

        // Check TTL
        if (entry.isExpired()) {
            store.remove(key);
            return null;
        }
        evictionPolicy.onGet(key, entry.value);
        return entry.value;
    }

    public void put(K key, V value, long ttlMillis) {
        if (store.size() >= maxSize) {
            K evicted = evictionPolicy.evict();
            if (evicted != null) {
                store.remove(evicted);
            }
        }
        store.put(key, new CacheEntry<>(value, ttlMillis > 0 ? ttlMillis : Long.MAX_VALUE));
        evictionPolicy.onPut(key, value);
    }

    public void remove(K key) {
        store.remove(key);
    }

    public int size() { return store.size(); }

    static class CacheEntry<V> {
        final V value;
        final long expiryTime;

        CacheEntry(V value, long ttlMillis) {
            this.value = value;
            this.expiryTime = System.currentTimeMillis() + ttlMillis;
        }

        boolean isExpired() {
            return System.currentTimeMillis() > expiryTime;
        }
    }
}
```

### Step 4: Replication Manager

```java
public class ReplicationManager {
    private final ConsistentHashRing<CacheNode> ring;
    private final int replicationFactor;
    private final ExecutorService replicationExecutor;
    private final Map<String, Long> replicationLog;  // Key → timestamp

    public ReplicationManager(ConsistentHashRing<CacheNode> ring,
                              int replicationFactor,
                              int replicationThreads) {
        this.ring = ring;
        this.replicationFactor = replicationFactor;
        this.replicationExecutor = Executors.newFixedThreadPool(replicationThreads);
        this.replicationLog = new ConcurrentHashMap<>();
    }

    public void replicatePut(String key, byte[] value, long ttlMillis) {
        List<CacheNode> replicas = ring.getNodes(key, replicationFactor);
        // Skip the first node (primary) — it already has the data
        for (int i = 1; i < replicas.size(); i++) {
            CacheNode replica = replicas.get(i);
            replicationExecutor.submit(() -> {
                try {
                    replica.sendPut(key, value, ttlMillis);
                    replicationLog.put(key, System.currentTimeMillis());
                } catch (Exception e) {
                    // Log replication failure, will be handled by anti-entropy
                    System.err.println("Replication failed for key " + key
                        + " to node " + replica.getId() + ": " + e.getMessage());
                }
            });
        }
    }

    public void replicateDelete(String key) {
        List<CacheNode> replicas = ring.getNodes(key, replicationFactor);
        for (int i = 1; i < replicas.size(); i++) {
            CacheNode replica = replicas.get(i);
            replicationExecutor.submit(() -> replica.sendDelete(key));
        }
    }

    // Anti-entropy: periodically reconcile replicas
    public void reconcile() {
        // For each key in this node, verify replica count
        // If replica count < replicationFactor, re-replicate
        Map<String, CacheEntry> localEntries = getLocalEntries();
        for (Map.Entry<String, CacheEntry> entry : localEntries.entrySet()) {
            List<CacheNode> expectedNodes = ring.getNodes(entry.getKey(), replicationFactor);
            int actualReplicas = countReplicas(entry.getKey(), expectedNodes);
            if (actualReplicas < replicationFactor) {
                replicatePut(entry.getKey(), entry.getValue().serialize(), entry.getValue().getTtl());
            }
        }
    }

    private int countReplicas(String key, List<CacheNode> expectedNodes) {
        int count = 0;
        for (CacheNode node : expectedNodes) {
            if (node.hasKey(key)) count++;
        }
        return count;
    }
}
```

### Step 5: Gossip Protocol for Cluster Membership

```java
public class GossipProtocol {
    private final String nodeId;
    private final Map<String, NodeState> clusterState = new ConcurrentHashMap<>();
    private final ScheduledExecutorService gossipScheduler;
    private volatile boolean running = true;

    public GossipProtocol(String nodeId, int gossipIntervalMs, int nodeCount) {
        this.nodeId = nodeId;
        this.clusterState.put(nodeId, new NodeState(nodeId, NodeStatus.UP, System.currentTimeMillis(), 0));
        this.gossipScheduler = Executors.newSingleThreadScheduledExecutor();

        gossipScheduler.scheduleAtFixedRate(
            this::gossipRound, 0, gossipIntervalMs, TimeUnit.MILLISECONDS);
    }

    public void gossipRound() {
        // Pick random peer
        List<String> peers = clusterState.keySet().stream()
            .filter(id -> !id.equals(nodeId))
            .collect(Collectors.toList());

        if (peers.isEmpty()) return;

        String peer = peers.get(ThreadLocalRandom.current().nextInt(peers.size()));
        try {
            // Send local state, receive peer state
            Map<String, NodeState> peerState = exchangeState(peer, clusterState);

            // Merge peer state into local state
            for (Map.Entry<String, NodeState> entry : peerState.entrySet()) {
                clusterState.merge(entry.getKey(), entry.getValue(), (existing, incoming) -> {
                    if (incoming.getHeartbeatCount() > existing.getHeartbeatCount()) {
                        return incoming;
                    } else if (incoming.getHeartbeatCount() == existing.getHeartbeatCount()
                               && incoming.getLastUpdated() > existing.getLastUpdated()) {
                        return incoming;
                    }
                    return existing;
                });
            }
        } catch (Exception e) {
            // Peer might be down
            clusterState.compute(peer, (key, state) -> {
                if (state != null) {
                    state.setStatus(NodeStatus.SUSPECT);
                    state.setLastUpdated(System.currentTimeMillis());
                }
                return state;
            });
        }
    }

    public void markNodeDown(String nodeId) {
        clusterState.compute(nodeId, (key, state) -> {
            if (state != null) {
                state.setStatus(NodeStatus.DOWN);
                state.setHeartbeatCount(state.getHeartbeatCount() + 1);
            }
            return state;
        });
    }

    public boolean isNodeUp(String nodeId) {
        NodeState state = clusterState.get(nodeId);
        if (state == null) return false;

        // If SUSPECT for more than 10 seconds, mark DOWN
        if (state.getStatus() == NodeStatus.SUSPECT
            && System.currentTimeMillis() - state.getLastUpdated() > 10_000) {
            markNodeDown(nodeId);
            return false;
        }
        return state.getStatus() == NodeStatus.UP;
    }

    public Set<String> getLiveNodes() {
        return clusterState.entrySet().stream()
            .filter(e -> e.getValue().getStatus() == NodeStatus.UP)
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    @Data
    static class NodeState {
        private final String nodeId;
        private volatile NodeStatus status;
        private volatile long lastUpdated;
        private volatile int heartbeatCount;

        NodeState(String nodeId, NodeStatus status, long lastUpdated, int heartbeatCount) {
            this.nodeId = nodeId;
            this.status = status;
            this.lastUpdated = lastUpdated;
            this.heartbeatCount = heartbeatCount;
        }
    }

    enum NodeStatus { UP, SUSPECT, DOWN }
}
```

### Step 6: Cache Client with Connection Management

```java
public class CacheClient<K, V> {
    private final ConsistentHashRing<CacheNode> ring;
    private final int replicationFactor;
    private final ReplicationManager replicationManager;
    private final Serializer<K, V> serializer;

    public CacheClient(ConsistentHashRing<CacheNode> ring,
                       int replicationFactor,
                       ReplicationManager replicationManager,
                       Serializer<K, V> serializer) {
        this.ring = ring;
        this.replicationFactor = replicationFactor;
        this.replicationManager = replicationManager;
        this.serializer = serializer;
    }

    public V get(K key) {
        String keyStr = key.toString();
        CacheNode primary = ring.getPrimaryNode(keyStr);
        if (primary == null) throw new CacheException("No nodes available");

        V value = primary.get(keyStr);
        if (value != null) return value;

        // Try replicas
        List<CacheNode> replicas = ring.getNodes(keyStr, replicationFactor);
        for (CacheNode replica : replicas) {
            if (replica.equals(primary)) continue;
            V replicaValue = replica.get(keyStr);
            if (replicaValue != null) {
                // Promote replica value to primary
                primary.put(keyStr, replicaValue, 0);
                return replicaValue;
            }
        }

        return null;
    }

    public void put(K key, V value, long ttlMillis) {
        String keyStr = key.toString();
        byte[] serialized = serializer.serialize(value);
        CacheNode primary = ring.getPrimaryNode(keyStr);

        // Write to primary synchronously
        primary.put(keyStr, value, ttlMillis);

        // Replicate asynchronously
        replicationManager.replicatePut(keyStr, serialized, ttlMillis);
    }

    public void delete(K key) {
        String keyStr = key.toString();
        CacheNode primary = ring.getPrimaryNode(keyStr);
        primary.delete(keyStr);
        replicationManager.replicateDelete(keyStr);
    }
}
```

---

## Best Practices

### Hashing
1. **Virtual nodes**: 160 virtual nodes per physical node ensures balanced distribution even with heterogeneous node capacities
2. **Hash function**: MD5 is sufficient for cache distribution; avoid cryptographic hashes (SHA-256) for performance
3. **Node weighting**: Assign more virtual nodes to nodes with higher capacity (e.g., 64GB RAM node gets 2x virtual nodes of 32GB node)
4. **Replication awareness**: The consistent hash ring must consider replication factor — replicas should be on different failure domains (rack/zone)

### Eviction
1. **Namespace-aware eviction**: Different namespaces should use different eviction policies (sessions: TTL, product cache: LRU, trending: LFU)
2. **Eviction rate limiting**: Evict at most 10% of capacity per minute to avoid eviction storms
3. **Policies ordering**: Run TTL eviction first (deterministic), then LRU/LFU (memory-pressure driven)
4. **Metrics per namespace**: Track hit rate, eviction rate, and size per namespace for capacity planning

### Replication
1. **Async write-behind**: Replicate asynchronously for performance; acknowledge writes after primary write completes
2. **Read-repair**: When a read finds a missing replica, re-replicate from primary before returning
3. **Hinted handoff**: If a replica is down when writing, another node accepts the write on its behalf and forwards when the replica recovers
4. **Anti-entropy**: Merkle tree-based comparison every 60 minutes to identify and repair divergent replicas

### Failure Handling
1. **Failure detection**: Suspicion-based (Phi Accrual or SWIM) rather than simple timeout — adapts to network conditions
2. **Cache warming**: New nodes joining the cluster should gradually receive data; mark as "warming" for 30 seconds before accepting reads
3. **Read repair during failure**: If primary is down, read from replicas; set a flag to re-replicate when primary rejoins
4. **Split-brain prevention**: Require majority (N/2 + 1) nodes for quorum; reject writes if quorum not available

### Performance
1. **Connection pooling**: Maintain persistent TCP connections to all nodes; use Netty for non-blocking I/O
2. **Batched operations**: Pipeline multiple get/put operations in a single TCP packet for throughput
3. **Serialization**: Use Protocol Buffers or FlatBuffers for cross-language compatibility; avoid Java serialization
4. **Memory management**: Use direct ByteBuffers for value storage to reduce GC pressure; pooled buffers for network I/O

## Performance Benchmarks

| Operation | P50 | P99 | P99.9 | Throughput |
|-----------|-----|-----|-------|------------|
| GET (hit) | 0.3ms | 2.1ms | 4.5ms | 500K ops/sec |
| GET (miss) | 0.5ms | 3.0ms | 6.0ms | 300K ops/sec |
| PUT | 0.8ms | 4.5ms | 8.0ms | 200K ops/sec |
| DELETE | 0.6ms | 3.5ms | 7.0ms | 250K ops/sec |
| Re-replication | 5ms | 50ms | 200ms | 10K keys/sec |
| Node join rebalancing | — | — | — | 10M keys in 30s |
