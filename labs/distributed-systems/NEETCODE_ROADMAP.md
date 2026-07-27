# NeetCode & Distributed Systems Roadmap

> Map NeetCode 150, Blind 75, and study plans for distributed systems interview prep.

---

## NeetCode 150: Distributed Systems Categorization

### Array / Hash (Partitioning & Distributed Storage)

| # | Problem | Level | DS Concept | Key Skill |
|---|---------|-------|------------|-----------|
| 1 | Contains Duplicate | Easy | Partition HashMap | Shard data by hash |
| 2 | Valid Anagram | Easy | Hash-based routing | Consistent hashing |
| 3 | Two Sum | Easy | HashMap indexing | Partition lookup |
| 4 | Group Anagrams | Medium | Hash grouping | MapReduce reduce phase |
| 5 | Top K Frequent | Medium | Count + sort | Distributed counting |
| 6 | Product of Array Except Self | Medium | Prefix/Suffix | Distributed aggregation |
| 7 | Valid Sudoku | Medium | Hash validation | Grid partitioning |
| 8 | Encode/Decode Strings | Medium | Serialization | Network message format |
| 9 | Longest Consecutive Seq | Medium | Set + scanning | Failure detection timeout |
| 10 | LRU Cache | Medium | HashMap + DLL | Distributed cache eviction |

### Stack (Distributed Consensus & Ordering)

| # | Problem | Level | DS Concept | Key Skill |
|---|---------|-------|------------|-----------|
| 20 | Valid Parentheses | Easy | LIFO ordering | Request ordering |
| 22 | Generate Parentheses | Medium | Recursive generation | State machine replication |
| 150 | Evaluate Reverse Polish | Medium | Stack evaluation | Log execution |
| 155 | Min Stack | Medium | Dual stack | State snapshots |
| 739 | Daily Temperatures | Medium | Monotonic stack | Timeout ordering |
| 853 | Car Fleet | Medium | Stack ordering | Event ordering in logs |

### Two Pointers (Replication Sync)

| # | Problem | Level | DS Concept |
|---|---------|-------|------------|
| 125 | Valid Palindrome | Easy | Read replica validation |
| 167 | Two Sum II | Medium | Range partitioning scan |
| 15 | 3Sum | Medium | Consensus verification |
| 11 | Container With Most Water | Medium | Load balancing |
| 42 | Trapping Rain Water | Hard | Data distribution |

### Sliding Window (Distributed Queues & Streaming)

| # | Problem | Level | DS Concept | Company |
|---|---------|-------|------------|---------|
| 121 | Best Time Buy/Sell | Easy | Window state | Confluent |
| 3 | Longest Substring No Repeat | Medium | Sliding consistency | Google |
| 424 | Character Replacement | Medium | Adaptive window | Netflix |
| 567 | Permutation in String | Medium | Exact match window | Meta |
| 76 | Minimum Window Substring | Hard | Multi-source merge | Google |
| 239 | Sliding Window Maximum | Hard | Stream processing | Amazon |
| 862 | Shortest Subarray Sum K | Hard | Sliding rate limit | Google |

### Linked List (Log Replication)

| # | Problem | Level | DS Concept |
|---|---------|-------|------------|
| 206 | Reverse List | Easy | Log reversal for recovery |
| 21 | Merge Two Lists | Easy | Log merging |
| 141 | Linked List Cycle | Easy | Log cycle detection (split brain) |
| 143 | Reorder List | Medium | Log compaction |
| 19 | Remove Nth Node | Medium | Log truncation |
| 138 | Copy List Random | Medium | Deep copy replication |
| 25 | Reverse K Group | Hard | Batch replication |
| 23 | Merge K Lists | Hard | Multi-source merge |

### Trees (Hierarchical System Design)

| # | Problem | Level | DS Concept |
|---|---------|-------|------------|
| 100 | Same Tree | Easy | Replica comparison |
| 101 | Symmetric Tree | Easy | Mirror replication |
| 104 | Max Depth | Easy | Tree depth = gossip rounds |
| 110 | Balanced Tree | Easy | Load balancing check |
| 226 | Invert Tree | Easy | State transformation |
| 235 | LCA BST | Easy | Geo-routing |
| 98 | Validate BST | Medium | Consistency validation |
| 102 | Level Order | Medium | Gossip propagation |
| 199 | Right Side View | Medium | Quorum view |
| 105 | Construct BT | Medium | Log reconstruction |
| 124 | Max Path Sum | Hard | Critical path analysis |
| 297 | Serialize Tree | Hard | State checkpoint |
| 987 | Vertical Order | Hard | Data range partitioning |

### Union-Find (Failure Detection & Cluster Membership)

| # | Problem | Level | DS Concept | Company |
|---|---------|-------|------------|---------|
| 323 | Connected Components | Medium | Cluster membership | Google |
| 261 | Graph Valid Tree | Medium | Acyclic cluster | Google |
| 547 | Number of Provinces | Medium | Failure isolation | Amazon |
| 684 | Redundant Connection | Medium | Cycle = split brain | Google |
| 721 | Accounts Merge | Medium | Anti-entropy merge | Meta |
| 990 | Equations Possible | Medium | Consistency validation | Google |
| 305 | Number Islands II | Hard | Dynamic cluster | Google |
| 128 | Longest Consecutive | Medium | Heartbeat timeouts | Google, Meta |

### Graphs (Distributed System Topology)

| # | Problem | Level | DS Concept | Company |
|---|---------|-------|------------|---------|
| 133 | Clone Graph | Medium | Graph replication | Meta |
| 207 | Course Schedule | Medium | 2PC/Dependency | Google |
| 210 | Course Schedule II | Medium | SAGA pattern | Google |
| 269 | Alien Dictionary | Hard | Logical clocks | Meta, Google |
| 310 | Min Height Trees | Medium | Leader election | Google |
| 329 | Longest Increasing Path | Hard | Topological TSort | Google |
| 332 | Reconstruct Itinerary | Hard | Log replication | Uber |
| 399 | Evaluate Division | Medium | Dependency resolution | Google |
| 743 | Network Delay Time | Medium | Gossip latency | Google |
| 787 | Cheapest Flights K Stops | Medium | Gossip with TTL | Google |
| 815 | Bus Routes | Hard | Multi-hop routing | Google |

### Heap / Priority Queue (Scheduling & Consensus)

| # | Problem | Level | DS Concept |
|---|---------|-------|------------|
| 703 | Kth Largest | Easy | Leaderboard quorum |
| 1046 | Last Stone Weight | Easy | Resource consolidation |
| 973 | K Closest Points | Medium | Data locality sorting |
| 215 | Kth Largest Element | Medium | Partition selection |
| 621 | Task Scheduler | Medium | Distributed scheduling |
| 355 | Design Twitter | Medium | Fan-out feed |
| 295 | Median Data Stream | Hard | Time-series aggregation |
| 358 | Rearrange K Distance | Hard | Distributed lock scheduling |

### Tries (Distributed Indexing)

| # | Problem | Level | DS Concept |
|---|---------|-------|------------|
| 208 | Implement Trie | Medium | Write-ahead log / Index |
| 211 | Search Add Word | Medium | Fuzzy search index |
| 212 | Word Search II | Hard | Distributed full-text search |

### Intervals (Time & Ordering)

| # | Problem | Level | DS Concept | Company |
|---|---------|-------|------------|---------|
| 252 | Meeting Rooms | Easy | Lamport clock | Google |
| 253 | Meeting Rooms II | Medium | Resource scheduling | Google |
| 56 | Merge Intervals | Medium | Clock merge/Vector clocks | Amazon |
| 57 | Insert Interval | Medium | Clock sync | Google |
| 435 | Non-overlapping | Medium | Conflict resolution | Google |
| 1851 | Min Interval | Hard | Time ordering | Google |

### DP (Distributed Computation)

| # | Problem | Level | DS Concept |
|---|---------|-------|------------|
| 70 | Climbing Stairs | Easy | Pipeline steps |
| 300 | Longest Increasing | Medium | Monotonic log |
| 1143 | Longest Common | Medium | Diff/Reconciliation |
| 72 | Edit Distance | Hard | CRDT merge |
| 312 | Burst Balloons | Hard | Dependency ordering |

---

## Blind 75: Distributed Systems Angle

### Array
| Problem | DS Angle |
|---------|----------|
| Two Sum | Partition routing |
| Best Time Buy/Sell | Window state |
| Product Except Self | Distributed aggregation prefix |
| Maximum Subarray | Consensus max value |
| Maximum Product Subarray | Replication product |

### Binary
| Problem | DS Angle |
|---------|----------|
| Sum of Two Integers | Distributed counter |
| Number of 1 Bits | Merkle tree hash |
| Missing Number | Gap detection |
| Reverse Bits | Bit serialization |

### Tree
| Problem | DS Angle |
|---------|----------|
| Maximum Depth | Leader election depth |
| Same Tree | Replica verificaion |
| Invert Tree | State transformation |
| Serialize Tree | State snapshot |
| Subtree of Tree | Merkle tree |
| Level Order | Gossip rounds |
| Validate BST | Consistency validation |
| LCA BST | Geo-routing |

### Graph
| Problem | DS Angle |
|---------|----------|
| Clone Graph | Replication |
| Course Schedule | Transaction ordering |
| Course Schedule II | Build dependencies |
| Pacific Atlantic | Multi-source BFS |
| Number of Islands | Partitioned services |
| Alien Dictionary | Clock ordering |

### Interval
| Problem | DS Angle |
|---------|----------|
| Insert Interval | Clock sync |
| Merge Intervals | Vector clocks |
| Meeting Rooms | Lamport clock |
| Non-overlapping | Deadlock prevention |

### Heap
| Problem | DS Angle |
|---------|----------|
| Merge K Sorted | Multi-source merge |
| Top K Frequent | Distributed counting |

---

## System Design: Question → Pattern Map

| System Design Question | DS Patterns Required |
|-----------------------|---------------------|
| Design WhatsApp | Consistent hashing, Multi-leader replication, Presence |
| Design Uber | Consistent hashing, Geo-partitioning, FIFO queues |
| Design Twitter | Fan-out (push/pull), Timeline caching, Distributed counters |
| Design YouTube | CDN, Transcoding queuing, Blob storage |
| Design Dropbox | CRDTs, Chunking, Delta sync |
| Design Netflix | CDN, Chaos engineering, Recommendation pipeline |
| Design Slack | Real-time messaging, Channel partitioning, Presence |
| Design Instagram | Photo Blob storage (Haystack), Timeline caching |
| Design Tinder | Geo-hashing, Swipe matching, Proximity ranking |
| Design Amazon | Distributed KV store, Session mgmt, Shopping cart |

---

## 4-Week Study Plan

### Week 1: Foundations
**Goal**: Master basic DS concepts with NeetCode Easy

| Day | Topic | NeetCode Problems | Reading |
|-----|-------|-------------------|---------|
| 1 | CAP Theorem | None (theory) | Lab 01, DDIA Ch 7 |
| 2 | Consistency Models | 3, 100, 252 | Lab 02 |
| 3 | Consensus Basics | 207, 210 | Lab 03, Raft paper |
| 4 | Transactions | 56, 57 | Lab 04 |
| 5 | Caching | 146, 355 | Lab 05 |
| 6 | Messaging | 622, 232 | Lab 06 |
| 7 | Review | 3 problem mock | All week topics |

### Week 2: System Design Patterns
**Goal**: Align patterns with NeetCode Medium

| Day | Topic | NeetCode Problems | Design Practice |
|-----|-------|-------------------|----------------|
| 8 | Replication | 133, 138 | Design Leader-follower |
| 9 | Partitioning | 706, 705 | Design Consistent Hashing |
| 10 | ID Generation | 535, 166 | Design Snowflake |
| 11 | Time/Order | 981, 253 | Design Logical Clocks |
| 12 | Locking | 1115, 1117 | Design ZooKeeper Locks |
| 13 | Failure Detect | 547, 684, 128 | Design Gossip FD |
| 14 | Review | 3 system designs | Mock interview |

### Week 3: System Design Practice
**Goal**: 2 full system designs per day

| Day | System Design 1 | System Design 2 |
|-----|----------------|-----------------|
| 15 | Design URL Shortener | Design KV Store |
| 16 | Design Twitter Feed | Design Search Autocomplete |
| 17 | Design Rate Limiter | Design Instant Messenger |
| 18 | Design Dropbox | Design YouTube |
| 19 | Design Uber Backend | Design Netflix |
| 20 | Design WhatsApp | Design Distributed Cache |
| 21 | Mock day | Full system design round |

### Week 4: Company-Specific
**Goal**: Target specific companies

| Day | Company | Focus | Systems to Practice |
|-----|---------|-------|-------------------|
| 22 | Google | Spanner, GFS, Borg | Design GFS, Bigtable |
| 23 | Amazon | DynamoDB, S3, Lambda | Design DynamoDB, Shopping Cart |
| 24 | Meta | TAO, Haystack, Presto | Design News Feed, Messenger |
| 25 | Microsoft | Cosmos DB, Azure | Design Cosmos DB, Azure Storage |
| 26 | Netflix | Open Connect, Chaos | Design CDN, Recommendation |
| 27 | Apple | iCloud, CloudKit | Design iCloud, iMessage |
| 28 | Mock + Review | Full day | 3 mock interviews |

---

## 8-Week Study Plan

### Phase 1 (Weeks 1-2): Foundations + LeetCode
- Complete all 20 lab content
- Solve 50 NeetCode problems from DS categories
- Read DDIA chapters 5-9

### Phase 2 (Weeks 3-4): System Design Deep Dive
- 16 system design problems (2/day)
- Implement 1 distributed system component (Raft, Consistent Hash, etc.)
- Study 2 company-specific architectures

### Phase 3 (Weeks 5-6): Company Specialization
- Deep dive into target companies (3 companies)
- Each company: 3 system designs, 5 behavioral stories
- Mock interviews with peers

### Phase 4 (Weeks 7-8): Refinement
- Weak area review
- Full-day mock interviews (4 hours)
- Compensation research + negotiation prep

---

## 12-Week Comprehensive Plan

### Months 1: Distributed Systems Deep Dive
- Weeks 1-4: All 20 labs with implementations
- Write a mini distributed system (distributed kv store or Raft implementation)
- Read 5 seminal papers: Dynamo, Bigtable, Spanner, GFS, Kafka

### Months 2: System Design + LeetCode Mastery
- Weeks 5-8: 100+ LeetCode problems, 16 system designs
- Study system design of 8 target companies
- Implement CRDT library in Java

### Months 3: Interview Simulation
- Weeks 9-10: Full mock interview loops (3 per week)
- Behavioral story preparation (10 STAR stories)
- Company-specific research

### Weeks 11-12
- 2 weeks of targeted review
- Schedule real interviews
- Negotiation preparation

---

## Company-Specific Study Paths

### Google
**Systems to Study**: Spanner, GFS, Bigtable, Borg, Omega, Monarch
**LeetCode Focus**: Graph, Union-Find, Design, Concurrency
**Key Problems**: 207, 210, 269, 310, 547, 684, 743, 981, 146, 460
**Patterns**: Paxos, TrueTime, 2PC, Chubby locks

### Amazon
**Systems to Study**: DynamoDB, S3, Lambda, Kinesis, SQS
**LeetCode Focus**: HashMap, Heap, Tree, Interval
**Key Problems**: 146, 706, 200, 253, 547, 621, 127
**Patterns**: Consistent hashing, Gossip, NWR, Vector clocks

### Meta
**Systems to Study**: TAO, Haystack, Presto, Unicorn, Scuba
**LeetCode Focus**: Trees, Graph, DFS/BFS
**Key Problems**: 133, 146, 199, 138, 572, 200, 621
**Patterns**: Fan-out, Cache-aside, Graph traversal

### Netflix
**Systems to Study**: Open Connect, Chaos Monkey, Hystrix, Eureka, Zuul
**LeetCode Focus**: BFS, Design patterns
**Key Problems**: 146, 355, 621, 706, 994
**Patterns**: Circuit breaker, Bulkhead, CDN, Leaderless

### Microsoft
**Systems to Study**: Cosmos DB, Azure Storage, Azure SQL
**LeetCode Focus**: Design, Concurrency, Arrays
**Key Problems**: 146, 706, 1117, 706, 380
**Patterns**: Multi-master, Consistency levels, Geo-replication

### Apple
**Systems to Study**: iCloud, CloudKit, APNs, iMessage
**LeetCode Focus**: Trees, Design
**Key Problems**: 146, 706, 100, 297, 133
**Patterns**: CRDTs, End-to-end encryption, Chunking

### Uber
**Systems to Study**: H3, Ringpop, Schemaless, Peloton
**LeetCode Focus**: Graphs, BFS
**Key Problems**: 332, 743, 787, 815, 706
**Patterns**: Geo-hashing, Consistent hashing, State machine

---

## Mock Interview Schedule

### Format
- **System Design**: 45-60 min
  - 5 min: Requirements clarification
  - 5 min: Estimations
  - 10 min: Data model, API design
  - 15 min: High-level design
  - 10 min: Deep dive into 1 component
  - 5 min: Tradeoffs, scaling

- **LeetCode**: 30-45 min
  - 5 min: Problem understanding
  - 5 min: Examples, edge cases
  - 10 min: Solution approach
  - 15 min: Write code
  - 5 min: Testing

### Partner Roles
- **Interviewer**: Choose problem, ask follow-ups, evaluate
- **Candidate**: Solve, verbalize, ask clarifying questions
- **Observer**: Take notes, give feedback

---

## Recommended Weekly Schedule

```
Monday:    2 LeetCode (DS-focused) + Read 1 lab
Tuesday:   1 System Design + 2 LeetCode
Wednesday: 1 Lab implementation in Java
Thursday:  2 LeetCode + Review system design
Friday:    Mock system design interview
Saturday:  Company research + DDIA reading
Sunday:    Rest / Weak area review
```

---

> **Key insight**: Every distributed system concept maps to a LeetCode problem. Every LeetCode problem teaches a distributed systems pattern. Master the connection, and you master the interview.