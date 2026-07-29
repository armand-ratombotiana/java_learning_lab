# System Design Cheatsheet: Advanced Data Structures

## Overview

This document maps each of the 10 advanced data structures to real-world systems and architectures. Use these references in system design interviews to demonstrate deep understanding of how low-level DS choices impact large-scale systems.

## 01 Trie (Prefix Tree)

### Where It Appears in Systems

| System | Usage | Why Trie |
|--------|-------|----------|
| **Google Search** | Autocomplete / typeahead | Prefix queries in O(L), shared prefix storage |
| **DNS** | Domain name resolution | Longest prefix match (reverse domain trie) |
| **IP Routing** | Route lookup (RIB/FIB) | IP prefix match — Patricia trie (compressed) |
| **Redis** | `MATCH` command in SCAN | Prefix-based key iteration |
| **Git** | Commit ID lookup (prefix) | Abbreviated SHA-1 matching |
| **Web Crawlers** | URL dedup (with Bloom) | Prefix-based URL normalisation |
| **Mobile Keyboards** | Autocorrect / suggestions | On-device memory-efficient word trie |

### Design Considerations

```
Memory: 26 children array → map children for sparse alphabets
Scale: Shard by prefix range (a-m, n-z)
Rebuild: Offline building with versioned swap
Concurrency: Read-copy-update (RCU) for persistent trie
```

### Design Question Example

> Design Google Search's autocomplete system (typeahead suggestion).

**Components:**
1. **Data**: Query logs → frequency-counted prefix map
2. **Storage**: Trie with top-k suggestions per node (precomputed)
3. **Caching**: Hot prefixes in Redis (top 1% of traffic)
4. **Rebuild**: Hadoop/Spark job every 30 min → serialise trie → deploy to servers
5. **Bloom filter**: Check if prefix has any suggestions before hitting trie

```
Client → Load Balancer → [Bloom Filter] → [Cache (Redis)] → [Trie Cluster]
                               |                |
                          (quick no)       (hot results)
```

---

## 02 Bloom Filter

### Where It Appears in Systems

| System | Usage | Key Benefit |
|--------|-------|-------------|
| **Chrome Safe Browsing** | Malicious URL check | 10M URLs → 1MB filter on device |
| **Cassandra / HBase** | Bloom filter per SSTable | Skip I/O for non-existent keys |
| **Redis** | Bloom module (RedisBloom) | O(1) memory-efficient existence check |
| **Apache Spark** | HyperLogLog + Bloom for joins | Reduce shuffle size |
| **Cuckoo Filter** | Bloom variant | Supports delete, better space |
| **Bitcoin SPV** | Bloom filter for transaction matching | Light client privacy |
| **Content Delivery (CDN)** | Cache stampede prevention | Probabilistic early expiry |
| **Spam Detection** | Known spammer check | 100M entries in memory |
| **Weak Password Check** | Signup password validation | 1B passwords → 1.5GB filter |

### Design Considerations

```
FP Rate Formula: P = (1 - e^{-kn/m})^k
Optimal k = (m/n) * ln(2)
Memory: m = -n * ln(P) / (ln(2))^2

Scalable Bloom: Start with one filter, add new as needed
Counting Bloom: 4-bit counters for delete support
```

### Design Question Example

> Design Chrome's Safe Browsing feature that checks URLs against a known-bad list on-device.

**Components:**
1. **Bloom filter**: 1MB, built server-side from 10M bad URLs
2. **Update**: Every 30 min, Chrome downloads new filter
3. **Check**: URL → hash k times → check bits → if any bit 0, safe (guaranteed)
4. **Server fallback**: If Bloom says "maybe bad", send hash prefix to server for verification
5. **Privacy**: Server never sees full URL, only hash prefix

```
URL → [SHA-256] → hash → split into k indices → check Bloom bits
     → [all 1?] → YES → send first 32 bits of hash to Safe Browsing server
     → [any 0?] → NO → URL is safe (safe)
```

---

## 03 Suffix Array

### Where It Appears in Systems

| System | Usage | Why Suffix Array |
|--------|-------|------------------|
| **Genome Aligners (BWA, Bowtie)** | Short read mapping | O(m log n) search in genome |
| **Plagiarism Detection (MOSS)** | Document similarity | LCP for substring matching |
| **Compression (bzip2)** | Burrows-Wheeler Transform | Suffix array for BWT construction |
| **Full-text Search** | Inverted index variant | Phrase queries via LCP |
| **String Databases** | Pattern matching at scale | Smaller memory than suffix tree |

### Design Considerations

```
Build:
  O(n log n) — sort suffixes
  O(n) — SA-IS (complex)
  O(n log n) — prefix doubling (good trade-off)

LCP Array: Kasai algorithm O(n)
Applications:
  - Longest repeated substring: LCP max
  - Distinct substrings: n(n+1)/2 - sum(LCP)
  - K-th lexicographic substring: prefix sum on LCP
```

### Design Question Example

> Design a genome search system that finds where a DNA read (short sequence) maps in a reference genome.

**Components:**
1. **Preprocess**: Build suffix array + LCP array for reference genome (3B base pairs)
2. **Input**: DNA read (100-150 base pairs)
3. **Search**: Binary search on suffix array → find all matches
4. **Results**: Return genome coordinates for each match

```
Reference Genome → [Build SA] → [Build LCP] → [Index Storage]
DNA Read → [Binary Search SA] → [Coordinates + Quality Score]
```

---

## 04 Fenwick Tree (Binary Indexed Tree)

### Where It Appears in Systems

| System | Usage | Why BIT |
|--------|-------|---------|
| **Online Analytics** | Real-time counters | Point update, prefix sum in O(log n) |
| **Stock Trading** | Order book cumulative volume | Cumulative quantity at each price level |
| **Compression** | Arithmetic coding | Cumulative frequency table |
| **Image Processing** | Histogram equalisation | Cumulative distribution function |
| **Databases** | Running aggregates | Materialised path prefix sums |
| **Inversion Count** | Collaborative filtering | Count rank correlations |

### Design Considerations

```
Space: O(n) — array of size n+1
Indexing: 1-indexed (LSB = i & -i)
Range Sum: prefixSum(r) - prefixSum(l-1)
Range Update: add v to [l, r] → add v at l, -v at r+1
2D BIT: O(log² n), good for sparse matrices
```

### Design Question Example

> Design a real-time analytics system tracking page view counts per minute for a website with 100K URLs.

**Components:**
1. **BIT per URL**: Index = minute-of-day (1440 slots)
2. **Update**: Each view increments index (minute) by 1 — O(log 1440) = O(11)
3. **Query**: Sum over [start_minute, end_minute] — O(log n)
4. **Storage**: BIT arrays in Redis sorted sets, persisted to HBase
5. **Aggregation**: Roll-up to hourly BITs for long-range queries

```
View Event → [URL Hash] → [Minute Index] → BIT.update(minute, 1)
Dashboard → [URL + Time Range] → BIT.rangeSum(start, end) → Chart
```

---

## 05 Segment Tree

### Where It Appears in Systems

| System | Usage | Why Segment Tree |
|--------|-------|------------------|
| **RDBMS (range queries)** | Index range scanning | O(log n) for any range operation |
| **MapReduce** | Range partitioning | Coordinate compression + segment tree |
| **Game Engines** | Spatial partitioning | Quadtree = 2D segment tree |
| **Calendar Systems** | Overlap detection (meetings) | Range max with lazy propagation |
| **Stock Exchange** | VWAP calculation | HFT volume-weighted price ranges |
| **DNS** | IP range blocking | CIDR range queries |
| **Ad Serving** | Budget pacing | Date-range budget consumption |

### Design Considerations

```
Space: 4n array (recursive), 2n array (iterative)
Lazy Propagation:
  - push() before recursing
  - apply() and mark lazy on full overlap
  - Range update in O(log n)

Variants:
  - Segment tree + coordinate compression (sparse data)
  - Persistent segment tree (versioning)
  - 2D segment tree (spatial)
```

### Design Question Example

> Design a meeting room booking system that finds available time slots.

**Components:**
1. **Segment tree**: 1440 leaf nodes (minutes), value = room count in use
2. **Book meeting**: Range update [start, end] += 1
3. **Check availability**: Range query MAX over [start, end] — if max < total rooms, available
4. **Lazy propagation**: Update entire overlapping segments
5. **Coordinate compression**: Only store booked interval boundaries (sparse)

```
Book(start, end):
  SegmentTree.rangeUpdate(start, end, +1)

Check(start, end):
  max = SegmentTree.rangeQuery(start, end)
  return max < totalRooms

Room status at any time = SegmentTree.pointQuery(time)
```

---

## 06 Skip List

### Where It Appears in Systems

| System | Usage | Why Skip List |
|--------|-------|---------------|
| **Redis** | Sorted Set (ZSET) | O(log n) insert/search, range scan, easy concurrency |
| **LevelDB / RocksDB** | Memtable | In-memory sorted structure before flushing to SST |
| **ConcurrentSkipListMap (Java)** | Concurrent sorted map | Lock-free (CAS), no contention |
| **Lucene** | Inverted index posting list | Skip pointers for faster AND queries |
| **Event Processing** | Time-ordered event buffer | Insert with sorted key, scan forward |
| **Leaderboards** | Gaming score ranking | Insert/update score, rank query in O(log n) |

### Design Considerations

```
Level probability: p = 0.5 (fair coin), max height = 16 (for log₂ 65K)
Head node: sentinel with max level, value = -∞
Insert: search → generate level → update pointers
CAS concurrency: mark arrays with volatile for lock-free

Memory overhead: sum(p^k) = 1/(1-p) pointers per node ≈ 2x with p=0.5
```

### Design Question Example

> Design a real-time gaming leaderboard handling 10M players with score updates 100QPS.

**Components:**
1. **Skip list**: Player ID → (score, rank)
2. **Update score**: Remove from skip list, re-insert with new score
3. **Get rank**: Skip list search returns position (index tracking)
4. **Range**: Get top N players — traverse from tail backwards
5. **Concurrency**: Use Java's ConcurrentSkipListMap<String, Long>
6. **Persistence**: Snapshot to DB every 5 min, WAL for crash recovery

```
Player scores → [ConcurrentSkipListMap<Score, PlayerID>]
GetRank(playerId) → search by score → return position
GetTopN(n) → descending iterator → collect top n
Update(pid, score) → remove(pid) → insert(score, pid)
```

---

## 07 Red-Black Tree

### Where It Appears in Systems

| System | Usage | Why Red-Black |
|--------|-------|---------------|
| **Linux Kernel** | Completely Fair Scheduler (CFS) | O(log n) sorted task management |
| **Java TreeMap / TreeSet** | Standard library | Balanced BST with guaranteed O(log n) |
| **C++ std::map / std::set** | Standard library | Guaranteed O(log n) |
| **Nginx** | Event timer management | Red-black timer tree |
| **PostgreSQL** | B-tree index (variant) | Red-black is precursor to B-tree |
| **Memcached** | Slab rebalancing | Ordering of slab classes |

### Design Considerations

```
Properties:
  1. Every node is red or black
  2. Root is black
  3. NULL leaves are black
  4. Red node's children are black (no two reds in a row)
  5. All paths from node to leaves have same black count

Insert Fixup Cases:
  - Uncle is red: recolor parent, uncle, grandparent
  - Uncle is black, node is inner child: rotate parent
  - Uncle is black, node is outer child: rotate grandparent + recolor

Delete Fixup: More complex, see implementation
```

### Design Question Example

> Design a consistent hashing ring for a distributed cache (Memcached).

**Components:**
1. **Hash ring**: Red-black tree stores server hash values
2. **Virtual nodes**: Each server replicates k times on ring for even distribution
3. **Key lookup**: Find successor (next larger hash) in RB tree — O(log n)
4. **Server add/remove**: Insert/delete k virtual nodes — O(k log n)
5. **Consistent hashing**: Adding/removing server only affects k/n fraction of keys

```
Server hash → Insert into RB tree → O(log n)
Key hash → RB tree.ceiling(keyHash) → target server
Server failure → Remove from RB tree → Rebalance keys
Use TreeMap<Long, Server> for Java implementation
```

---

## 08 Treap (Randomised BST)

### Where It Appears in Systems

| System | Usage | Why Treap |
|--------|-------|-----------|
| **Treap (generic)** | Order-statistic tree | k-th smallest in O(log n) |
| **Collaborative Editing** | OT/CRDT sequence | Implicit treap for cursor positions |
| **Competitive Programming** | Problem solving | Fast implementation (easy to code) |
| **Rope Data Structure** | Text editor (string ops) | Implicit treap for split/merge/concat |
| **Data Streams** | Sliding window median | Order statistics with insert/delete |
| **Syndication Feed** | Ranking with decay | Priority-based ordering |

### Design Considerations

```
Priority: Random number (high-quality RNG important)
Operations:
  - split(root, key): (L, R) where all L ≤ key < R
  - merge(L, R): combine where all L < R
  - insert: split → new node → merge(L, merge(node, R))
  - delete: split(key-1) → split(R, 1) → merge(L, R_rest)

Implicit Treap (no key, array indexed by size):
  - split(root, k): (first k, rest)
  - range-reverse: lazy flag
  - insert at position, delete at position
```

### Design Question Example

> Design a data structure for a collaborative text editor that supports insert/delete at any position and undo/redo.

**Components:**
1. **Implicit treap**: Each node = character (or string fragment)
2. **Split by position**: split(root, pos) → left, right
3. **Insert**: split(pos) → merge(L, merge(NewNode, R))
4. **Delete**: split(pos-1) → split(mid, 1) → merge(L, R_rest)
5. **Undo**: Persist treap versions (persistent variant)
6. **Cursor**: Maintain cursor position, split at cursor

```
Insert(pos, char):
  left, right = split(root, pos)
  root = merge(merge(left, Node(char)), right)

Delete(pos):
  left, mid, right = split(root, pos-1, 1)
  root = merge(left, right)

MoveCursor(pos):
  left, right = split(root, pos)
  // operation on left/right
  root = merge(left, right)
```

---

## 09 Union-Find (Disjoint Set Union)

### Where It Appears in Systems

| System | Usage | Why DSU |
|--------|-------|---------|
| **Graph Databases** | Community detection | Connected components in social graph |
| **Image Processing** | Connected component labelling | Pixel connectivity in binary images |
| **Network Routing** | Spanning tree protocol | Minimum spanning tree (Kruskal's) |
| **Kruskal's MST** | Minimum spanning tree | Greedy MST with cycle detection |
| **Percolation Theory** | Physics/material science | Grid connectivity threshold |
| **Game Development** | Region detection | Map connectivity, territory control |
| **Dynamic Connectivity** | Networking | Edge additions over time |
| **Path Compression** | Compiler register allocation | Interference graph colouring |

### Design Considerations

```
Optimisations:
  - Path compression (almost O(1))
  - Union by rank/size (O(log n) tree height)
  - Combined: O(α(n)) — inverse Ackermann

Variants:
  - DSU with rollback (undoable union)
  - DSU with deletions (hard — use divide and conquer)
  - Persistent DSU (versioned)
  - Offline dynamic connectivity (DSU + divide and conquer)
```

### Design Question Example

> Design a social network friend recommendation system that finds connected components.

**Components:**
1. **DSU**: Each user is a node. Friendship = union(a, b)
2. **Connectivity**: find(a) == find(b) means same component
3. **Recommendations**: Users in same component connected through >1 path
4. **Scale**: 2B users → hash-based DSU (HashMap<Long, Long>)
5. **Batch processing**: MapReduce to build DSU from friendship edges

```
Friend(a, b): DSU.union(a, b)

SameComponent(a, b): return DSU.find(a) == DSU.find(b)

FriendOfFriend(a):
  component = DSU.find(a)
  return users in component not directly connected to a

MST(Kruskal):
  sort edges by weight
  for each edge (u, v, w):
    if DSU.find(u) != DSU.find(v):
      DSU.union(u, v)
      include edge in MST
```

---

## 10 Merkle Tree (Hash Tree)

### Where It Appears in Systems

| System | Usage | Why Merkle Tree |
|--------|-------|-----------------|
| **Bitcoin** | Block header commitment | Efficient verification of transactions in block |
| **Git** | Content-addressable filesystem | Verify tree/file integrity |
| **Certificate Transparency** | Signed Certificate Timestamp | Verifiable log of issued certs |
| **Cassandra / DynamoDB** | Anti-entropy / repair | Compare replica trees for consistency |
| **ZFS / Btrfs** | Checksum tree | Detect silent data corruption |
| **Blockchain (Ethereum)** | Merkle Patricia Trie | State, transaction, receipt trees |
| **Distributed Databases** | Consistency check | Replica sync verification |
| **Binary Repositories** | Package integrity | Verify downloaded packages |

### Design Considerations

```
Hash: SHA-256 minimum, SHA-512 for higher security
Leaf: hash(data_block)
Internal: hash(left_hash + right_hash)
Root: Merkle Root (single 256-bit value)

Proof (Merkle Proof):
  - Path from leaf to root
  - Sibling hashes at each level
  - O(log n) size, O(log n) verification

SPV (Simplified Payment Verification):
  - Light client stores only block headers (Merkle roots)
  - Full node provides Merkle proof for transaction
  - Light client verifies proof against header root
```

### Design Question Example

> Design a peer-to-peer file sync system (like BitTorrent) that efficiently detects which blocks differ between two replicas.

**Components:**
1. **File split**: Split file into 4KB blocks
2. **Merkle tree**: Build tree over all block hashes
3. **Replica A → B**: A sends Merkle root to B
4. **B compares**: If roots match, files are identical
5. **If different**: B sends Merkle proof back to identify differing subtree
6. **Recurse**: Only need to transfer blocks in differing branches
7. **Efficiency**: O(log n) comparison for n blocks, O(d log n) diff for d differences

```
File → [Split 4KB blocks] → [Hash each] → [Build Merkle Tree] → [Root]
Compare Roots:
  - Same → files identical, done
  - Different → compare child hashes recursively
    → identify differing leaf blocks
    → transfer only differing blocks (delta sync)
```

## Cross-System Architecture Patterns

### Pattern 1: Cache-Miss Optimisation

```
[Request]
   ↓
[Bloom Filter] — exists? — NO → [Return empty / default]
   | YES
   ↓
[Cache (Redis)] — hit? — YES → [Return cached]
   | MISS
   ↓
[Database] → [Build response]
   ↓
[Update cache + (optionally update Bloom)]
```

### Pattern 2: Search at Scale

```
[Query]
   ↓
[Load Balancer]
   ↓
[Prefix Router → which trie shard]
   ↓
[Bloom Filter] — prefix exists?
   ↓ YES
[Cache] — hit? → [Return suggestions]
   ↓ MISS
[Trie Query] → [Top-k suggestions] → [Cache]
   ↓
[Return results]
```

### Pattern 3: Data Integrity in Distributed Systems

```
[Write Request]
   ↓
[Primary Node] → [WAL Log]
   ↓
[Replicas] → [Build Merkle Tree]
   ↓
[Periodic Anti-Entropy]
   | Compare Merkle roots between replicas
   | If mismatch, find differing subtree recursively
   | Repair only differing blocks
   ↓
[Consistent State]
```

### Pattern 4: Real-Time Aggregation

```
[Events Stream]
   ↓
[Partition by Key (URL hash)]
   ↓
[Per-Partition Fenwick Tree] — 1440 slot minute window
   ↓
[Sliding Window Aggregation]
   ↓
[Analytics Dashboard (range queries)]
```

## System Design Interview Quick Script

For any system design question involving one of these 10 DS:

1. **Clarify scale**: Reads/sec, writes/sec, data size, latency SLA
2. **Map to DS**: Based on requirements (prefix search → trie, membership → Bloom, range → segment tree)
3. **Justify choice**: "I chose [DS] because it gives O([complexity]) for [operation], and we need [requirement]"
4. **Describe components**: Storage, compute, caching tiers
5. **Trade-offs**: "The trade-off is [negative] but we accept it because [positive]"
6. **Alternatives**: "If we needed [different requirement], we could use [alternative DS] instead"