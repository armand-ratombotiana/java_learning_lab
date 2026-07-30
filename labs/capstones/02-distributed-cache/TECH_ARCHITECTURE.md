# Technical Architecture: Distributed Cache

## Architecture Overview

```
                        ┌──────────────────────┐
                        │   Client Library       │
                        │   (Consistent Hashing  │
                        │    + Replication Aware)│
                        └──────────┬───────────┘
                                   │
       ┌───────────────────────────┼───────────────────────────┐
       ▼                           ▼                           ▼
┌──────────────┐          ┌──────────────┐          ┌──────────────┐
│  Cache Node 1 │          │  Cache Node 2 │          │  Cache Node 3 │
│  (Primary)    │◄────────►│  (Replica)    │◄────────►│  (Replica)    │
├──────────────┤ Gossip  ├──────────────┤ Gossip  ├──────────────┤
│ • Hash Ring  │ Protocol │ • Hash Ring  │ Protocol │ • Hash Ring  │
│ • LRU Cache  │◄────────►│ • LFU Cache  │◄────────►│ • TTL Cache  │
│ • Replication│          │ • Replication│          │ • Replication│
│ • Anti-      │          │ • Anti-      │          │ • Anti-      │
│   Entropy    │          │   Entropy    │          │   Entropy    │
└──────────────┘          └──────────────┘          └──────────────┘

Key Space: [0, 2^64)
Virtual Nodes: 160 per physical node
Replication Factor: 3
```

## Component Breakdown

### 1. Consistent Hash Ring
- **Purpose**: Distribute keys across cluster nodes with minimal redistribution on topology changes
- **Structure**: Sorted map of hash → node (ConcurrentSkipListMap for O(log N) lookups)
- **Virtual nodes**: 160 per physical node — provides statistical uniformity even with few nodes
- **Replica placement**: Primary + N-1 subsequent nodes on ring for N replicas; skip nodes on same rack/zone for fault isolation
- **Impact of node failure**: Only 1/N keys are remapped (where N = number of nodes), compared to N/N for naive modulo

### 2. Cache Storage Engine
- **Per-namespace stores**: Each cache namespace gets its own CacheStore instance with configurable eviction policy
- **Memory budget**: Configurable max memory per namespace; namespace cannot exceed 50% of total node memory
- **Segmented LRU**: Production implementation uses segmented LRU (two queues: probationary and protected) to prevent cache pollution from one-hit-wonders
- **TTL sweeper**: Background thread (runs every 100ms) sweeps expired entries; avoids lazy expiration overhead on gets

### 3. Replication Layer
- **Write path**: Client writes to primary node → primary acknowledges → async replication to replicas
- **Read path**: Client reads from primary → if miss, try replicas → if found, read-repair primary
- **Consistency model**: Read-committed for normal operations; tunable consistency (ONE, QUORUM, ALL) for critical data
- **Hinted handoff**: If replica is down, another node accepts and stores the write; when replica recovers, hinted write is forwarded

### 4. Gossip Protocol
- **Communication**: Each node gossips with 3 random peers per round (500ms interval)
- **State**: Node ID, heartbeat counter, incarnation number (to distinguish fresh from stale DOWN messages)
- **Failure detection**: Phi Accrual detector — maintains a sliding window of inter-arrival times; phi = -log10(1 - P(interval)); phi > 8 = DOWN
- **Update propagation**: A node learns about another node's state within O(log N) gossip rounds

### 5. Anti-Entropy / Repair
- **Merkle trees**: Each node maintains a Merkle tree for each key range; compare tree roots with replicas to find divergent keys
- **Repair strategy**: For each divergent key, primary's value wins (last-write-wins with timestamp comparison)
- **Full sync**: Runs every 60 minutes; incremental sync runs every 5 minutes for recently modified keys
- **Read repair**: On read, compare digest of returned value with other replicas; repair if mismatch detected

## Data Flow

### Write Operation
```
1. Client computes hash(key) = 0xA3B2C1
2. Ring lookup: ceiling(0xA3B2C1) → virtual node VN_42 → physical node Node5
3. Client connects to Node5 (primary)
4. Node5 stores the entry; returns success to client
5. Node5 determines replicas: next 2 unique nodes on ring (Node8, Node2)
6. Node5 asynchronously sends replication requests to Node8 and Node2
7. Node8 and Node2 acknowledge; replication complete
8. Total client latency: GET 0.3ms, PUT 0.8ms (primary only)
```

### Read Operation
```
1. Client computes hash(key) = 0xA3B2C1
2. Ring lookup → primary = Node5
3. Client reads from Node5
4. If Node5 returns value → return to client (hit)
5. If Node5 returns miss → query Node8 (replica 1)
6. If Node8 has value → read-repair: send value back to Node5, return to client
7. If Node8 also miss → query Node2 (replica 2)
8. If all replicas miss → return null (miss)
9. Total client latency: hit=0.3ms, miss=2.0ms, read-repair=2.5ms
```

## Deployment Topology

```
Cluster: dcache-prod (10 nodes)

Node Layout:
  ┌─────────────── Rack A ─────────────────┐
  │  cache-01  cache-02  cache-03  cache-04 cache-05 │
  └─────────────────────────────────────────┘
  ┌─────────────── Rack B ─────────────────┐
  │  cache-06  cache-07  cache-08  cache-09 cache-10 │
  └─────────────────────────────────────────┘

  Replication: Each key stored on 3 nodes across 2 racks
  Cross-rack latency: < 0.5ms

Hardware:
  CPU: 16 cores (Intel Xeon)
  RAM: 128 GB per node
  Network: 25 Gbps
  Storage: Local NVMe (2 TB) for persistence
```

## Tech Stack

| Component | Technology | Purpose |
|-----------|------------|---------|
| Language | Java 21 | Runtime |
| Networking | Netty 4.1 | Non-blocking TCP |
| Serialization | Protocol Buffers | Cross-language |
| Hashing | MD5 | Consistent hashing |
| Concurrency | ConcurrentHashMap, ForkJoinPool | Thread safety |
| Metrics | Micrometer + Prometheus | Observability |
| Failure Detection | Phi Accrual (custom) | Adaptive detection |
| Persistence | RocksDB JNI | Crash recovery |

## Configuration

```yaml
cluster:
  name: dcache-prod
  nodeId: cache-01
  seedNodes:
    - cache-01:9876
    - cache-02:9876
    - cache-03:9876

consistentHashing:
  virtualNodeCount: 160
  replicationFactor: 3

storage:
  maxMemory: 64GB
  evictionPolicy: SEGMENTED_LRU
  ttlSweepIntervalMs: 100
  persistencePath: /data/dcache/rocksdb

gossip:
  intervalMs: 500
  peersPerRound: 3
  phiSuspectThreshold: 5.0
  phiDownThreshold: 8.0

replication:
  writeMode: ASYNC
  antiEntropyIntervalMin: 60
  hintedHandoffTtlMin: 30

performance:
  ioThreads: 4
  workerThreads: 8
  tcpBacklog: 1024
  maxConnections: 10000
```

## Security

- **Authentication**: TLS client certificates; nodes present cert on connection
- **Authorization**: Node whitelist; only known node IDs can join cluster
- **Encryption**: TLS 1.3 for all inter-node communication
- **Audit**: All membership changes logged to system audit table
