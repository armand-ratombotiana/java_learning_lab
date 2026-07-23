# System Design Cheatsheet — Algorithms Academy

## How Algorithms Connect to System Design

### Core Insight
Every system design problem reduces to an algorithm problem at its core. The algorithm you choose determines latency, throughput, consistency, and cost at scale.

---

## Algorithm-to-System Mapping

### Sorting Algorithms in Systems
| Algorithm | Where Used in Systems |
|-----------|----------------------|
| Merge Sort | External sorting (large files, MapReduce shuffle), stable sort needed |
| Quick Sort | In-memory sorting, Arrays.sort() (Dual-Pivot) |
| Heap Sort | Priority scheduling, top-K in streaming |
| TimSort | Real-world data (nearly sorted) — default in Python, Java (objects) |
| Counting Sort | Sorting limited-range integers (IP addresses, grades) |
| Radix Sort | String sorting, fixed-width integer sort |

### Search Algorithms in Systems
| Algorithm | System Component |
|-----------|-----------------|
| Binary Search | Database B-Tree traversal, log search, debugging bisect |
| Interpolation Search | Dictionary lookup (uniformly distributed keys) |
| Ternary Search | Find unimodal function maximum (ML hyperparameter tuning) |
| Exponential Search | Searching unbounded/infinite sorted arrays |
| Fibonacci Search | Cache-optimized search (minimizes memory access) |

### Graph Algorithms in Systems
| Algorithm | System Application |
|-----------|-------------------|
| BFS | Social graph friend recommendations (1st, 2nd degree), web crawler |
| DFS | Dependency resolution, package manager, garbage collection |
| Dijkstra | GPS navigation, network routing (OSPF), content delivery |
| Bellman-Ford | RIP routing protocol, forex arbitrage detection |
| Floyd-Warshall | Transitive closure, database query optimization |
| Topological Sort | Build systems (Make, Bazel), task scheduling, course prerequisites |
| Kruskal/Prim | Network design (laying fiber/minimal cost), clustering |
| A* | Video game pathfinding, robotics navigation |
| PageRank | Web search ranking, recommendation systems |
| Max Flow (Dinic) | Network bandwidth allocation, airline scheduling |

### DP in Systems
| DP Type | System Application |
|---------|-------------------|
| Resource Allocation | Cloud resource allocation, budget planning |
| Sequence Alignment | DNA sequencing, diff tools (git diff) |
| Shortest Path (DP) | Viterbi algorithm in speech recognition, GPS |
| Knapsack | Resource-constrained scheduling, investment portfolio |
| Edit Distance | Spell check, plagiarism detection, version control |
| Text Segmentation | Chinese/Japanese word segmentation, NLP tokenization |

### Greedy Algorithms in Systems
| Algorithm | Where Used |
|-----------|------------|
| Interval Scheduling | Server resource scheduling, ad slot allocation |
| Huffman Coding | Data compression (zip, gzip), image compression |
| Prim/Kruskal | Network design, clustering |
| Dijkstra | Routing (OSPF, BGP) |
| Activity Selection | CPU scheduling, task scheduling |
| Fractional Knapsack | Resource allocation with divisible goods |

---

## Real-World Algorithm Use Cases

### Database Systems
| Operation | Algorithm | Why |
|-----------|-----------|-----|
| Index lookup | B+ Tree search (logarithmic) | Balanced, disk-optimized |
| Query optimization | Dynamic Programming (join ordering) | Finds optimal join order |
| Sort-merge join | Merge Sort + Two-pointer | Efficient for large datasets |
| Hash join | HashMap | Best for equi-joins with sufficient memory |
| Aggregation | HashMap (group by) | O(n) aggregation |
| Page replacement | Clock (Second Chance) / LRU | Approximates LRU efficiently |

### Distributed Systems
| Problem | Algorithm | Why |
|---------|-----------|-----|
| Leader election | Raft / Paxos | Consensus in unreliable network |
| Data partitioning | Consistent Hashing | Minimal rehashing on node changes |
| Distributed sort | External Merge Sort | Handles data larger than memory |
| Distributed join | MapReduce join | Scales horizontally |
| Distributed cache | LRU + Consistent Hashing | Cache affinity with dynamic nodes |
| Distributed counter | CRDT / Count-Min Sketch | Conflict-free, eventually consistent |

### Networking
| Function | Algorithm |
|----------|-----------|
| Routing (OSPF) | Dijkstra (link-state) |
| Routing (RIP) | Bellman-Ford (distance-vector) |
| Congestion control | Additive Increase Multiplicative Decrease (AIMD) |
| Traffic shaping | Token Bucket / Leaky Bucket |
| Load balancing | Round Robin / Least Connections / Consistent Hashing |
| Error detection | CRC (polynomial division) |

### Machine Learning Systems
| Component | Algorithm |
|-----------|----------|
| Feature engineering | Counting sort, hash-based frequency counting |
| Model inference | Matrix multiplication (Strassen/Coppersmith-Winograd) |
| Gradient descent | Backpropagation (DP on computational graph) |
| K-Nearest Neighbors | KD-Tree / Ball Tree (spatial search) |
| Recommendation | Collaborative filtering (matrix factorization) |
| Anomaly detection | Bloom Filter / Count-Min Sketch (frequency) |

---

## Algorithmic Complexity at Scale

### Scaling Patterns
| Data Size | Algorithm Strategy | Example |
|-----------|-------------------|---------|
| 1K - 1M | In-memory algorithms | Quick Sort, HashMap |
| 1M - 1B | External memory algorithms | External Merge Sort, B-Tree |
| 1B - 1T | Distributed algorithms | MapReduce, Spark |
| 1T+ | Streaming / Approximate | Count-Min Sketch, HyperLogLog |

### Latency of Algorithm Operations
| Operation | In-Memory | On Disk | Distributed |
|-----------|-----------|---------|-------------|
| Array access | ~100 ns | ~10 ms | ~100 ms |
| HashMap lookup | ~100 ns | ~10 ms | ~50 ms |
| B-Tree search (3 levels) | ~300 ns | ~30 ms | ~100 ms |
| Dijkstra (100K nodes) | ~100 ms | N/A | ~1 sec |
| External sort (1 GB) | N/A | ~60 sec | ~10 sec |
| BFS (1M nodes) | ~500 ms | N/A | ~5 sec |

---

## Algorithm Design in System Design Interviews

### 4-Step Framework
1. **Capacity estimation**: How much data? Read/write ratio? Latency needs?
2. **Core algorithm**: What's the central algorithmic problem?
3. **Scale the algorithm**: How does it work with 1000x more data?
4. **Optimization**: Can we use approximation, caching, or precomputation?

### Common Algorithm Questions in System Design
| System | Core Algorithm(s) |
|--------|------------------|
| URL shortener | Base62 encoding, HashMap (ID → URL) |
| Search autocomplete | Trie + Top-K Heap |
| Web crawler | BFS + Bloom Filter (dedup) + Priority Queue (politeness) |
| News feed | Fan-out (Push/Pull model), Merge K sorted lists |
| Uber/Lyft | QuadTree (spatial indexing) + Dijkstra (ETA) |
| YouTube/Netflix | B-Tree (catalog) + Merge Sort (recommendation ranking) |
| Dropbox/Google Drive | Merkle Tree (sync/consistency) + Chunking + Dedup |
| Chat system | Queue (messages) + HashMap (online status) |
| Rate limiter | Sliding Window Log (Circular Buffer) / Token Bucket |
| Key-value store | LSM Tree (write) + Bloom Filter (cache optimization) |
| Metrics/monitoring | Time-series DB: LSM Tree + Segmented Queue |
| Distributed lock | Raft/Paxos consensus + Lease mechanism |

---

## Algorithm Approximation vs Exact

### When to Approximate
| Exact | Approximate | Trade-off |
|-------|-------------|-----------|
| HashMap membership | Bloom Filter | 1% false positive vs 10x memory savings |
| Exact count | HyperLogLog | 2% error vs 1000x memory savings |
| Exact frequency | Count-Min Sketch | Overestimate vs memory savings |
| Exact Top-K | Approximate Top-K (Lossy Counting) | Slight accuracy loss vs O(1) memory |
| Full sort | Approximate sort (bucketing) | Order within buckets only |

### When Approximation is Unacceptable
- Financial transactions (exact count, exact sum)
- User-facing search results (must be accurate)
- Medical/healthcare data
- Legal/audit trails

---

## Must-Know Algorithm-to-Design Mappings

| Interview Question | Algorithm | Data Structure |
|-------------------|-----------|---------------|
| Design Twitter | Merge K sorted lists + Fan-out | Heap + Queue + HashMap |
| Design Uber | Spatial search + ETA | QuadTree + Dijkstra |
| Design Web Crawler | BFS + URL dedup | Queue + Bloom Filter |
| Design Autocomplete | Prefix search + Top-K | Trie + Heap |
| Design Key-Value Store | LSM merge + WAL | Skip List + Bloom Filter |
| Design Rate Limiter | Sliding window count | Circular Buffer + Token Bucket |
| Design News Feed | Merge K timeline + Push/Pull | Heap + Queue |
| Design Search Engine | Crawl → Index → Rank | BFS + Inverted Index + PageRank |
| Design YouTube | Video ID + Hot content | B-Tree + LRU Cache |
| Design Dropbox | Chunk + Merkle sync | HashMap + Merkle Tree |
| Design Chat | Ordered messages + presence | Queue + HashMap |
| Design Parking Lot | Nearest spot + Pricing | Heap + MinHeap + PriceGrid |

## Checklist
- [ ] Identify the core algorithmic problem first
- [ ] Estimate data size: choose in-memory vs external vs distributed
- [ ] Consider read/write ratio for DS choice
- [ ] Can you use an approximation for memory savings?
- [ ] Is the bottleneck I/O or CPU?
- [ ] Can you precompute to reduce query time?
- [ ] Does the system need consistent hashing for scaling?
- [ ] What caching strategy (LRU, LFU, TTL)?
