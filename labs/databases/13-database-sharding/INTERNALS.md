# Internals: Database Sharding

## Internal Architecture

### Component Diagram
```
+----------------------------------------------------+
|                    Router Layer                     |
|  +----------+  +----------+  +------------------+  |
|  |Hash Ring |  |Partition |  |Scatter-Gather    |  |
|  |Manager   |  |Manager   |  |Engine            |  |
|  +----------+  +----------+  +------------------+  |
|  +----------+  +----------+  +------------------+  |
|  |Health    |  |Connection|  |Metrics Collector |  |
|  |Checker   |  |Pool      |  |                  |  |
|  +----------+  +----------+  +------------------+  |
+----------------------------------------------------+
```

### Hash Ring Manager Internals
**Data Structure:**
```java
SortedMap<Long, VirtualNode> ring = new TreeMap<>();
record VirtualNode(String physicalNode, int vnodeIndex, long hash) {}

public String findNode(String key) {
    long keyHash = hashFunction.hash(key);
    SortedMap<Long, VirtualNode> tail = ring.tailMap(keyHash);
    if (tail.isEmpty()) return ring.get(ring.firstKey()).physicalNode();
    return tail.get(tail.firstKey()).physicalNode();
}
```

**Memory:** TreeMap O(log N). For 100 physical nodes x 150 vnodes = 15K entries (~2-3 MB).

### Connection Pool Internals
Uses BlockingQueue for available connections, Set for active ones. Supports borrow with timeout and return.

### Scatter-Gather Engine
1. Accept query, extract routing keys
2. Route to specific nodes if keys present, broadcast if not
3. Execute in parallel via CompletableFuture with virtual threads
4. Collect results with timeout, merge (sort, limit, aggregate)

### Rebalancing Engine
**State Machine:** IDLE -> ANALYZING -> PLANNING -> MIGRATING -> VERIFYING -> UPDATING -> CLEANUP -> IDLE
**On error:** ROLLING_BACK state with compensating actions.

### Monitoring
**Metrics:** Per-node QPS, latency (P50/P99/P999), error rate, data size
**Exposure:** JMX MBeans, Micrometer/Prometheus integration, structured JSON logs
