# System Design Cheatsheet — Data Structures Academy

## How Data Structures Connect to System Design

### Core Principle
Data structures are the building blocks of system design. Every system design interview requires choosing the right data structure for each component, considering scale, latency, consistency, and cost.

---

## The Data Structure Decision Tree

```
What's the access pattern?
├── Key-value lookup?
│   ├── In-memory, single server → HashMap
│   ├── In-memory, bounded size → LRU Cache (HashMap + DLL)
│   ├── Distributed, high throughput → Consistent Hashing + HashMap
│   └── Persisted on disk → B-Tree / LSM Tree
├── Range queries?
│   ├── Static data → Sorted Array (binary search)
│   ├── Dynamic, in-memory → TreeMap (Red-Black) / Segment Tree
│   ├── Dynamic, on disk → B+ Tree
│   └── Time-series → Time-partitioned B-Tree
├── Ordered data?
│   ├── Need min/max → Heap (PriorityQueue)
│   ├── Need sorted order → TreeMap / TreeSet
│   ├── FIFO → Queue (ArrayDeque, LinkedList)
│   └── LIFO → Stack (ArrayDeque)
├── Full-text search?
│   ├── Prefix matching → Trie
│   ├── Fuzzy search → Trie with edit distance / BK-Tree
│   └── Inverted index → HashMap<Word, List<DocId>>
├── Graph data?
│   ├── Social graph → Adjacency List (HashMap<Node, List<Node>>)
│   ├── PageRank → Adjacency Matrix (sparse/compressed)
│   └── Knowledge graph → Property Graph (multiple index types)
├── Probabilistic? (approximate, memory-efficient)
│   ├── Set membership → Bloom Filter
│   ├── Cardinality estimation → HyperLogLog
│   └── Frequency estimation → Count-Min Sketch
└── Real-time / Streaming?
    ├── Sliding window stats → Circular Buffer / Ring Buffer
    ├── Stream ranking → Heap / Skip List
    └── Stream dedup → Bloom Filter + Cache
```

---

## System Design Rounds: DS per Component

### Web Tier (Load Balancer + Web Server)
| Component | DS Choice | Why |
|-----------|-----------|-----|
| Session store | HashMap (distributed, e.g., Redis) | O(1) lookup, TTL support |
| Rate limiter | Token Bucket (Circular Buffer of timestamps) | Memory-efficient, sliding window |
| Request queue | Queue (ArrayDeque or Distributed Queue) | FIFO ordering, decoupling |
| Cache | LRU Cache (HashMap + DLL) | O(1) get/put, bounded memory |

### Database Tier
| Component | DS Choice | Why |
|-----------|-----------|-----|
| Primary index | B+ Tree | O(log n) search/insert/delete, range queries |
| Secondary index | B+ Tree / Hash Index | Fast lookup by non-primary key |
| In-memory cache | HashMap (sharded) | Microsecond latency |
| Time-series data | LSM Tree (log-structured) | Write-optimized, append-heavy |
| Full-text search | Inverted Index (Trie + HashMap) | Fast token lookup |

### Caching Layer
| Strategy | DS Used | Use Case |
|----------|---------|----------|
| Write-through | LRU Cache | General-purpose caching |
| Write-back | LRU Cache + Write Queue | Write-heavy workloads |
| Cache-aside | HashMap + TTL | Read-heavy, tolerate stale data |
| CDN | Consistent Hash Ring | Geographic distribution |

### Distributed Systems
| Problem | DS Solution | Why |
|---------|-------------|-----|
| Service discovery | Consistent Hash Ring | Minimize rehashing on node changes |
| Task scheduling | Priority Queue (Heap) | Execute by priority/time |
| Message queue | Queue (distributed, e.g., Kafka) | Ordered, persistent, partitioned |
| Leader election | Distributed consensus (Raft/Paxos) | Use log (array of entries) |
| Distributed counter | HyperLogLog / Count-Min Sketch | Approximate, memory-efficient |
| Distributed locking | Redis SETNX (HashMap) | Simple mutual exclusion |

---

## Scale Considerations

### Data Size → DS Choice
| Data Size | Memory | DS Strategy |
|-----------|--------|-------------|
| < 1 GB | Single server RAM | HashMap, TreeMap, in-memory structures |
| 1-100 GB | Several servers | Sharded HashMap, B+ Tree on disk |
| 100 GB - 10 TB | Distributed storage | LSM Tree, Columnar storage |
| > 10 TB | Data warehouse + caching | Tiered storage, approximate DS |

### Latency Budget
| Operation | Target | DS |
|-----------|--------|-----|
| Cache hit | < 1 ms | HashMap (in-memory) |
| Primary key lookup | < 10 ms | B+ Tree index |
| Full-text search | < 100 ms | Inverted index |
| Analytics query | < 10 sec | Columnar store + precomputation |

---

## Real-World DS Choices

### Google
- **Bigtable**: LSM Tree + SSTables + Bloom Filters
- **Chubby**: Paxos-based distributed lock (log-structured)
- **MapReduce**: Distributed sort (external merge sort)
- **PageRank**: Iterative graph algorithm (compressed adjacency list)

### Amazon
- **DynamoDB**: Consistent Hashing + B-Tree + LSM
- **S3**: Distributed key-value store (blob index via B-Tree)
- **ElastiCache**: Redis (HashMap + Sorted Sets + Lists)
- **Kinesis**: Sharded queue (ordered log per shard)

### Meta
- **TAO**: Social graph (Adjacency List in RAM, sharded)
- **Memcached**: Distributed HashMap (simple, fast)
- **Apache Cassandra**: LSM Tree + Consistent Hashing
- **Haystack**: Photo storage (HashMap of offsets into blob store)

### Microsoft
- **Cosmos DB**: Multi-model — B-Tree, Hash, Graph
- **Azure Table Storage**: Partitioned key-value (B-Tree)
- **SQL Server**: B+ Tree clustered index
- **Orleans**: Distributed actor system (HashMap of actors)

---

## Must-Know DS for System Design Interviews

| Data Structure | Why It Matters in System Design |
|---------------|--------------------------------|
| HashMap | Foundation of all caching, sharding, key-value stores |
| B-Tree / B+ Tree | Every database index, sorted on-disk storage |
| LRU Cache | Caching layer in every large system |
| Consistent Hash | Minimizing disruption during scaling |
| Bloom Filter | Cache optimization, spell check, anti-spam |
| Skip List | Redis sorted sets, in-memory sorted storage |
| LSM Tree | Write-optimized storage (Bigtable, Cassandra, LevelDB) |
| Trie | Autocomplete, IP routing, spell checker |
| Segment Tree | Range queries on time-series, inventory |
| Fenwick Tree | Prefix sums in financial systems, analytics |

## Design Question → DS Mapping
| Design Question | Key DS |
|----------------|--------|
| Design URL shortener | HashMap (ID → URL) + Base62 encoding |
| Design Twitter feed | Fan-out on write (Queue) + Merge K sorted (Heap) |
| Design Uber | QuadTree (spatial) + PriorityQueue (nearest drivers) |
| Design chat system | Queue (messages) + HashMap (online status) |
| Design search autocomplete | Trie + Heap (top K suggestions) |
| Design web crawler | Bloom Filter (URL dedup) + Queue (frontier) |
| Design recommendation | Matrix (collaborative filtering) + Graph (social) |
| Design YouTube | B-Tree (video index) + LRU (cache hot videos) |
| Design Dropbox | Merkle Tree (sync verification) + HashMap (file index) |
| Design Netflix | B-Tree (catalog) + Heap (recommendation ranking) |

## The 4 Key Trade-offs
1. **Time vs Space**: HashMap (O(n) space for O(1) lookup) vs Array (O(1) space but O(n) lookup)
2. **Read vs Write**: B-Tree (balanced) vs LSM Tree (write-optimized, read-heavy compaction)
3. **Accuracy vs Memory**: Bloom Filter (false positives, small) vs HashSet (exact, larger)
4. **Complexity vs Performance**: Self-balancing tree (complex, O(log n)) vs Skip List (simpler, amortized O(log n))

## Checklist Before Coding
- [ ] What's the read/write ratio?
- [ ] What's the data size (now and in 2 years)?
- [ ] Is consistency critical or eventual OK?
- [ ] Do we need range queries or just point lookups?
- [ ] Is this single-server or distributed?
- [ ] What's the latency requirement?
- [ ] Can we use an approximate data structure?
