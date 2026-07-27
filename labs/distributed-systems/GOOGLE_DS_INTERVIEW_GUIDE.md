# Google Distributed Systems Interview Guide

> Complete preparation guide for distributed systems roles at Google.

---

## How Google Tests Distributed Systems Knowledge

Google's interview methodology for distributed systems is deeply rooted in their internal infrastructure. Every SWE and SRE interview will test distributed systems knowledge at some level.

### Interview Rounds That Test DS

1. **System Design (2 rounds for L4+, 1 for L3)**: The primary DS evaluation round.
2. **Coding**: Expect DS-adjacent problems (LRU Cache, concurrent programming).
3. **Googleyness**: Leadership stories about distributed systems projects.
4. **SRE-Specific**: Debugging, automation, incident response at scale.

### Google's Unique DS Focus

- **Consistency above all**: Google prioritizes strong consistency. Spanner, Chubby, and Megastore all prioritize correctness.
- **Global scale**: Every design must work globally. Single-region solutions are insufficient.
- **Infrastructure ownership**: Google engineers own the full stack from hardware to application.
- **Paper culture**: Interviewers expect you to know Google's published papers (GFS, Bigtable, Spanner, Borg, Omega, Monarch).

### Google's Internal Systems You Must Know

| System | What It Does | Why It Matters for Interviews |
|--------|-------------|------------------------------|
| GFS/Colossus | Distributed file system | Chunk-based storage, append-heavy workloads |
| Bigtable | Wide-column NoSQL | SSTable, Memtable, compaction, Bloom filters |
| Spanner | Globally distributed SQL | TrueTime, 2PC, Paxos, external consistency |
| Chubby | Distributed lock service | Paxos-based, ZooKeeper-like |
| Borg/Omega | Cluster management | Declarative scheduling, admission control |
| MapReduce | Batch processing | Shuffle, sort, combine phases |
| Pregel | Graph processing | Bulk synchronous parallel |
| Dremel | Interactive analytics | Columnar storage, nested queries |
| Monarch | Time-series monitoring | Global monitoring at Google scale |

---

## Top 15 Distributed Systems Questions at Google

### Question 1: Design Google File System (GFS)
**Difficulty**: Hard | **Frequency**: Very High

**Answer Framework**:
```
Requirements:
- Large files (multi-GB), append-heavy, read-once workloads
- 100s of MB/s aggregate throughput
- Fault-tolerant, auto-recovery

Architecture:
- Single Master (metadata): namespace, chunk locations, access control
- Chunk Servers (data): 64MB chunks replicated 3x, stored on Linux FS
- Clients: interact with master for metadata, chunkservers for data

Design Details:
- Lease-based mutation order: master grants lease to primary replica
- Data flow decoupled from control flow: data pipelined along chunk servers
- Chunk version numbers for stale replica detection
- Shadow masters for read-only metadata access

Consistency Model:
- Mutations are atomic (append semantics)
- Defined: client sees all data written before concurrent mutations
- Undefined: concurrent mutations from different clients
- Applications use record appends + checkpointing

Failure Handling:
- Master: operation log + shadow masters + cold standby
- Chunk server: heartbeat detection, chunk re-replication
- Data integrity: checksums per 64KB block

Tradeoffs:
- Single master is bottleneck for small files (metadata hot spot)
- 64MB chunks cause internal fragmentation for small files
- Record append has at-least-once semantics (duplicates possible)
```

### Question 2: Design Google Spanner
**Difficulty**: Very Hard | **Frequency**: High

**Answer Framework**:
```
Core innovation: TrueTime API - exposes clock uncertainty
Using GPS + atomic clocks for bounded clock error (ε = 1-7ms)

Architecture:
- Universe: global deployment
- Zones: unit of administrative isolation (each zone has 1+ datacenters)
- Each zone runs: 1+ spanservers + Paxos state machines
- Each spanserver manages 100-1000 tablets
- Directory: set of contiguous key ranges (unit of data movement)

Replication:
- Per-zone Paxos for synchronous replication within zone
- Cross-region replication via 2PC across Paxos groups
- Leader leases for read scalability

Consistency:
- External consistency (linearizability) via TrueTime
- Snapshot reads: any timestamp in the past
- Stale reads: read from any replica at sufficiently old timestamp

Transactions:
- Reads: lock-free (snapshot isolation)
- Writes: 2PC across participant Paxos groups
- Read-write: require lock acquisition

Query Language:
- SQL-based with extensions for distributed execution
- Distributed query execution across tablets
```

### Question 3: Design Bigtable
**Difficulty**: Hard | **Frequency**: High

**Answer Framework**:
```
Data Model:
- (row:string, column:string, time:int64) -> string
- Column families for access control/caching
- Timestamps for versioning

Storage Layer (GFS + SSTable):
- SSTable: immutable 64MB blocks with Bloom filters
- Memtable: in-memory buffer (like write-ahead log)
- Compaction: minor (memtable -> SSTable), merging, major

Architecture:
- Tablet servers: manage 100-1000 tablets each
- Master: assign tablets, detect failures, rebalance
- Chubby: leader election, tablet server discovery, schema storage

Serving:
- Write: log to commit log -> update memtable
- Read: merge memtable + SSTable sequence (with Bloom filter skip)
- Compaction runs continuously for read performance

Failure Handling:
- Tablet server failure: master redistributes tablets
- Commit log split across new servers for recovery
- SSTable immutable = simple recovery
```

### Question 4: Design YouTube
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Upload**: sharded upload servers, resumable upload protocol
- **Transcoding**: job queue, worker fleet, multiple output formats (360p, 720p, 1080p, 4K)
- **Storage**: GFS/Colossus for video files, Bigtable for metadata
- **CDN**: edge caches close to users, geographical load balancing
- **Recommendation**: MapReduce/Flume-based pipeline, collaborative filtering
- **Thumbnail**: pre-generate thumbnails during upload processing

### Question 5: Design Google Search
**Difficulty**: Hard | **Frequency**: High

**Answer Components**:
- **Crawling**: URL frontier, politeness (rate limiting per domain), duplicate detection
- **Indexing**: Inverted index built via MapReduce, document processing pipeline
- **Storage**: Bigtable for index, GFS for raw content
- **Serving**: Index sharding across many servers, replica sets for scale
- **Ranking**: PageRank (batch), real-time signals, query rewriting
- **Caching**: Query cache at serving layer, snippet cache

### Question 6: Design Google Maps
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Tile Generation**: Render map tiles at multiple zoom levels
- **Vector Tiles**: Modern approach - serve geometry, render client-side
- **Routing**: Graph partitioning for distributed A* computation
- **Traffic**: Real-time ingestion from Android, congestion modeling
- **Geocoding**: Address normalization, geospatial indexing
- **Places**: Bigtable-backed with geo-indexing

### Question 7: Design a Distributed Lock Service
**Difficulty**: Medium | **Frequency**: Very High

**Answer Framework**:
```
Requirements:
- Mutual exclusion across machines
- Fault-tolerant: locks held during GC pauses
- Deadlock detection

Design:
- Use Chubby/ZooKeeper approach: ephemeral zNodes
- Lock path: /locks/{resource-name}
- Create ephemeral sequential zNode under lock path
- Lowest sequential number wins the lock
- Watch preceding zNode for release notification
- Fencing tokens for stale lock protection

Failure Scenarios:
- Client crash: ephemeral zNode auto-deleted
- Network partition: clarify timeout vs session expiry
- Slow client: lock lease with periodic renewal

Tradeoffs:
- Performance bottleneck under heavy contention
- ZooKeeper ensemble requires maintenance
```

### Question 8: Design a Distributed Queue (Pub/Sub)
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Topics**: partitioned for scale
- **Subscriptions**: pull (consumer pulls) or push (server pushes)
- **Message Model**: at-least-once, at-most-once, exactly-once
- **Storage**: per-partition segmented log (like Kafka)
- **Acknowledgments**: sliding window with deadline
- **Dead Letter Queue**: for failed messages

### Question 9: Design a Distributed Cache
**Difficulty**: Medium | **Frequency**: Very High

**Answer Components**:
- **Consistent Hashing**: for cache key distribution
- **Replication**: 2-3 replicas per key for fault tolerance
- **Eviction**: LRU, LFU, TTL-based
- **Hot Key Handling**: replication, local caching
- **Write Strategy**: write-through, write-behind
- **Failure**: cache miss storm protection, circuit breakers

### Question 10: Design a Monitoring System
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Data Collection**: agents on every machine, push/pull model
- **Storage**: time-series database (Monarch-like)
- **Query Language**: for aggregation, filtering
- **Alerting**: rule evaluation engine, deduplication, routing
- **Dashboard**: visualization with hierarchical aggregation
- **Global Rollup**: cross-region metric aggregation

### Question 11: Design Distributed Rate Limiter
**Difficulty**: Medium | **Frequency**: High

**Answer Components**:
- **Algorithm**: token bucket, sliding window, leaky bucket
- **Distributed Coordination**: Redis cluster with lua scripts, consistent hashing
- **Hierarchy**: per-user, per-API, per-region limits
- **Response**: HTTP 429 with Retry-After header

### Question 12: Design a Distributed Key-Value Store
**Difficulty**: Hard | **Frequency**: High

**Answer Components**:
- **Partitioning**: consistent hashing with virtual nodes
- **Replication**: quorum-based (NWR), hinted handoff
- **Consistency**: configurable (eventual to strong)
- **Failure**: gossip protocol for membership, Merkle trees for sync
- **Recovery**: hinted handoff, read repair, anti-entropy

### Question 13: Design Google's Load Balancer
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Global**: DNS-based traffic steering, Anycast
- **Regional**: flow-based consistent hashing, health checks
- **Layer 4/7**: TCP/UDP termination, HTTP routing
- **Auto-scaling**: integrate with Borg for capacity management
- **Circuit Breaking**: protect backend during failures

### Question 14: Design a Distributed Job Scheduler
**Difficulty**: Hard | **Frequency**: Medium

**Answer Components**:
- **Queuing**: priority queues, fair scheduling
- **Placement**: resource constraints, bin packing
- **Execution**: worker pools, task distribution
- **Fault Tolerance**: task retry, checkpointing
- **Monitoring**: job progress, resource utilization

### Question 15: Design a Distributed File System for ML Training
**Difficulty**: Hard | **Frequency**: Low-Medium

**Answer Components**:
- **Data Parallelism**: sharded dataset across workers
- **Prefetching**: overlapping I/O with computation
- **Caching**: local SSD/NVMe caching for hot data
- **Consistency**: read-your-writes for checkpointing

---

## System Design Problems Google Asks

| Problem | What They Test |
|---------|---------------|
| Design GFS | Append-heavy, large file, fault tolerance |
| Design Bigtable | Wide-column, sparse, time-series data |
| Design Spanner | Global consistency, TrueTime, 2PC |
| Design YouTube | Video pipeline, CDN, transcoding |
| Design Google Search | Inverted index, MapReduce, ranking |
| Design Distributed Crawler | URL frontier, politeness, deduplication |
| Design Google Maps | Geospatial data, tile serving, routing |
| Design Pub/Sub | At-least-once, ordering, partitioning |
| Design Distributed Cache | Consistent hashing, eviction |
| Design Monitoring | Time-series, alerting, aggregation |
| Design Rate Limiter | Distributed counters, scalability |
| Design Shopping Platform | Session, catalog, cart |
| Design Social Network Feed | Fan-out, ranking, storage |
| Design Distributed Lock | Mutual exclusion, fencing |
| Design Event Processing | Stream processing, windowing |

---

## Java Code Examples for Google Interviews

### Implementation: Thread-Safe Cache with Write-Through

```java
public class WriteThroughCache<K, V> {
    private final ConcurrentHashMap<K, V> cache = new ConcurrentHashMap<>();
    private final Database<K, V> db;

    public WriteThroughCache(Database<K, V> db) { this.db = db; }

    public V get(K key) {
        V val = cache.get(key);
        if (val == null) {
            val = db.read(key);
            if (val != null) cache.putIfAbsent(key, val);
        }
        return val;
    }

    public void put(K key, V value) {
        db.write(key, value);
        cache.put(key, value);
    }

    public interface Database<K, V> {
        V read(K key);
        void write(K key, V value);
    }
}
```

### Implementation: Consistent Hashing

```java
public class ConsistentHashRing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int virtualNodes;
    private final HashFunction hash;

    public ConsistentHashRing(int virtualNodes, HashFunction hash) {
        this.virtualNodes = virtualNodes;
        this.hash = hash;
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hash.hash(node + "#" + i), node);
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hashVal = hash.hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hashVal);
        return (entry != null) ? entry.getValue() : ring.firstEntry().getValue();
    }

    public interface HashFunction { int hash(String input); }
}
```

---

## Company-Specific Evaluation Criteria

### Google SWE Evaluation

| Criteria | Weight | What They Look For |
|----------|--------|-------------------|
| System Design | 40% | Scale, consistency, fault tolerance |
| Coding | 30% | Clean, correct, efficient |
| Googleyness | 15% | Ambiguity, collaboration, humility |
| Leadership | 15% | Initiative, influence, ownership |

### Google SRE Evaluation

| Criteria | Weight | What They Look For |
|----------|--------|-------------------|
| System Design | 35% | Infrastructure, reliability, automation |
| Troubleshooting | 25% | Debugging distributed systems |
| Coding | 20% | Scripting, automation, infra code |
| Leadership | 20% | Incident command, project leadership |

---

## Study Plan for Google DS Interviews

### Week 1-2: Google Papers Deep Dive
- Read GFS, Bigtable, Spanner, Chubby, Borg papers
- Understand design decisions and tradeoffs
- Create comparison table of all Google storage systems

### Week 3-4: System Design Practice
- Practice 8 Google-specific system designs
- Focus on: global scale, strong consistency, fault tolerance
- Time-box each design to 45 minutes

### Week 5: Implementation
- Implement Raft consensus in Java
- Implement a distributed cache with consistent hashing
- Implement a thread-safe lock service

### Week 6: Mock Interviews
- 3 full mock system design interviews
- 3 full mock coding interviews (focus on concurrency)
- Practice Googleyness stories

### Key LeetCode Problems
| Problem | # | Why It Matters for Google |
|---------|---|-------------------------|
| LFU Cache | 460 | Cache design - asked frequently |
| LRU Cache | 146 | Cache design - asked frequently |
| Time Based KV Store | 981 | Multi-version concurrency |
| Alien Dictionary | 269 | Ordering in distributed systems |
| Redundant Connection | 684 | Cycle detection = split brain |
| Network Delay Time | 743 | Gossip propagation time |
| Course Schedule II | 210 | Dependency ordering |
| Web Crawler Multithreaded | 1242 | Distributed crawling |
| The Dining Philosophers | 1226 | Deadlock in distributed locks |
| Bounded Blocking Queue | 1188 | Producer-consumer at scale |

---

## Sample Answers Framework

### For System Design: The Google-Specific Template

1. **Requirements**: "What's the scale? 100M DAU? 1B searches/day?"
2. **Estimations**: "100M DAU * 10 searches/day * 1KB/result = 1TB/day"
3. **Data Model**: "Let's use Bigtable-like model with row key = user_id reversed"
4. **Architecture**: "I'll start with a simple design and then optimize"
5. **Deep Dive**: "The biggest challenge is cross-region consistency..."
6. **Tradeoffs**: "We could use eventual consistency for this use case..."
7. **Failure Mode**: "If the master fails, we use shadow master + operation log..."

### For Coding: The Google Template

1. **Clarify**: "Can the input be null? What's the expected output?"
2. **Examples**: "If input is [1,2,3], output should be..."
3. **Brute Force**: "The naive approach is O(n^2)..."
4. **Optimize**: "We can improve to O(n) using a hash map..."
5. **Write Code**: Write clean, well-structured code
6. **Test**: "Let's test with edge cases: empty input, duplicates..."

---

> **Google Tip**: Google interviewers value depth over breadth. If you claim to know Paxos, be ready to explain it at protocol level, including how it handles leader failure, ballot numbering, and quorum intersection.