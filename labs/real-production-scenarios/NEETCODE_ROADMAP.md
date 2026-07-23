# NeetCode Roadmap — SRE / Production Engineering Interview Preparation

## How to Use This Roadmap

This document maps NeetCode 150, Blind 75, and Grind 75 problem categories to the 15 incident-response labs. Each problem category teaches skills directly applicable to debugging and resolving specific production incidents. Use this as a study guide: when you practice a LeetCode category, immediately connect it to the relevant lab.

---

## NeetCode 150 + Blind 75 + Grind 75 → Incident Lab Mapping

### Arrays & Hashing

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Two Sum | Easy | Understanding hash map memory overhead | Lab 01 (memory leak — unbounded maps) |
| Group Anagrams | Medium | Hash distribution patterns | Lab 08 (cache sharding) |
| Contains Duplicate | Easy | Set growth monitoring | Lab 01 (ThreadLocalMap unbounded growth) |
| Valid Anagram | Easy | String comparison internals | Lab 11 (certificate fingerprinting) |

**SRE Context:** Hash map design teaches you about memory allocation, collision resolution, and capacity planning. When debugging Lab 01's memory leak, you're essentially tracing hash map growth in ThreadLocalMap.

### Two Pointers

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Valid Palindrome | Easy | Bounded memory processing | Lab 09 (log processing with limited buffers) |
| Container With Most Water | Medium | Capacity optimization | Lab 09 (disk capacity planning) |

**SRE Context:** Two-pointer patterns teach bounded window processing — critical for processing logs and metrics in fixed-memory environments (Lab 09).

### Sliding Window

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Best Time to Buy Stock | Easy | Rolling metric windows | Lab 03 (P50/P99 latency over time) |
| Longest Substring Without Repeating | Medium | Sliding window metrics | Lab 03, Lab 14 (rate limiting windows) |
| Sliding Window Maximum | Hard | Rolling aggregations | Lab 03 (max latency detection) |

**SRE Context:** Sliding window algorithms are the foundation of production monitoring. Every latency percentile, error rate, and throughput calculation uses a sliding window. Lab 03 teaches you to interpret these windows in production dashboards.

### Stack

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Valid Parentheses | Easy | TCP state machine | Lab 11 (TLS handshake state) |
| Min Stack | Easy | Stack depth monitoring | Lab 02 (call stack depth analysis) |
| Daily Temperatures | Medium | Pending request tracking | Lab 04 (connection queue depth) |

**SRE Context:** Stack patterns teach you about LIFO ordering, which maps to TCP connection state (SYN, SYN-ACK, ACK — Lab 11) and thread call stack analysis (Lab 02).

### Binary Search

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Binary Search | Easy | Log search in structured logs | Every lab (log analysis) |
| Search Rotated Array | Medium | Finding anomalies in time series | Lab 03 (detecting latency shift points) |
| Time Based Key-Value Store | Medium | Time-series data lookup | Lab 13 (offset-based Kafka consumer lag) |

### Linked List

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| LRU Cache | Medium | Cache eviction policy | Lab 08 (cache stampede prevention) |
| Merge Two Sorted Lists | Easy | Log merge from multiple sources | Every lab (log aggregation) |
| Reverse Linked List | Easy | Replay/undo operations | Lab 06 (deployment rollback as reversal) |

**SRE Context:** The LRU Cache (LeetCode 146) is the single most relevant LeetCode problem for SRE. It directly maps to cache eviction policies in Lab 08, connection pool management in Lab 04, and database query caching in Lab 05.

### Trees

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Invert Binary Tree | Easy | Dependency inversion | Lab 07 (circuit breaker decoupling) |
| Maximum Depth of Tree | Easy | Call stack depth | Lab 02 (thread dump analysis) |
| Same Tree | Easy | Config comparison | Lab 06 (deployment diff analysis) |
| Subtree of Another Tree | Easy | Subgraph matching | Lab 01 (object graph traversal) |

### Tries

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Implement Trie | Medium | LSM-tree / WAL pattern | Lab 05 (database write-ahead log) |
| Word Search II | Hard | Pattern matching in logs | Lab 10 (security log pattern detection) |

**SRE Context:** Trie prefix-matching is analogous to how LSM-trees (used by LevelDB, RocksDB, Cassandra) organize data. Lab 05's database recovery patterns rely on understanding LSM-tree compaction.

### Heap / Priority Queue

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Kth Largest Element | Medium | Priority queuing | Lab 07 (circuit breaker priority) |
| Find Median from Data Stream | Hard | P50 latency calculation | Lab 03 (latency percentile monitoring) |
| Top K Frequent Elements | Medium | Top error analysis | Lab 10 (top security threats) |

**SRE Context:** Finding the median from a data stream (LeetCode 295) teaches how to compute P50/P99 latency percentiles — the core of SLO monitoring in every lab's `SLI_SLO_METRICS.md`.

### Backtracking

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Permutations | Medium | Configuration space exploration | Lab 06 (rollback options) |
| Subsets | Medium | Feature flag combinations | Lab 06 (canary configurations) |

### Graphs

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Course Schedule | Medium | Deadlock cycle detection | Lab 02 (thread deadlock) |
| Number of Islands | Medium | Service isolation boundaries | Lab 12 (pod health domains) |
| Course Schedule II | Medium | Lock ordering | Lab 02, Lab 05 (deadlock prevention) |
| Graph Valid Tree | Medium | Cycle detection | Lab 02 (resource allocation graph) |

**SRE Context:** Graph cycle detection (Course Schedule, LeetCode 207) is the exact algorithm behind deadlock detection in Lab 02. Thread dumps show a "wait-for graph" — finding cycles in that graph identifies deadlocked threads.

### Dynamic Programming

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Climbing Stairs | Easy | Incremental capacity planning | Lab 09 (capacity growth modeling) |
| Longest Increasing Subsequence | Medium | Event ordering in logs | Every lab (timeline reconstruction) |

### Greedy

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Jump Game | Medium | Failover decision making | Lab 15 (DR failover decisions) |
| Jump Game II | Medium | Minimum steps to recovery | Lab 15 (optimizing MTTR) |

### Intervals

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Merge Intervals | Medium | On-call schedule overlap | On-call scheduling |
| Insert Interval | Medium | Incident timeline merging | Every lab (timeline documentation) |
| Non-overlapping Intervals | Medium | Avoiding maintenance windows overlap | Lab 15 (planned failover windows) |

### Bit Manipulation

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Number of 1 Bits | Easy | Bloom filter hash functions | Lab 08, Lab 10 (Bloom filters for caching/security) |

### Math & Geometry

| NeetCode Problem | Difficulty | Production Skill | Related Lab |
|-----------------|-----------|-----------------|-------------|
| Rotate Image | Medium | Data rebalancing | Lab 13 (partition rebalancing) |

---

## SRE-Specific LeetCode Problem Recommendations (Top 30)

| Rank | Problem | Why It Matters | Lab Connection | Company Frequency |
|------|---------|---------------|----------------|-------------------|
| 1 | 146. LRU Cache | Cache eviction fundamentals | Lab 08 | Google, Meta, Amazon, Microsoft |
| 2 | 207. Course Schedule | Deadlock cycle detection | Lab 02 | Google, Amazon, Meta |
| 3 | 210. Course Schedule II | Lock ordering, topological sort | Lab 02, Lab 05 | Google, Amazon |
| 4 | 346. Moving Average | Sliding window = P50 latency | Lab 03 | Google, Meta |
| 5 | 295. Find Median | P50/P99 percentile computation | Lab 03 | Google, Amazon, Meta |
| 6 | 359. Logger Rate Limiter | Token bucket rate limiting | Lab 14 | Google, Amazon |
| 7 | 622. Circular Queue | Ring buffer for logs/metrics | Lab 09 | Amazon, Google |
| 8 | 355. Design Twitter | Consistent hashing, sharding | Lab 08 | Amazon, Google |
| 9 | 1242. Web Crawler Multithreaded | Thread pool, producer-consumer | Lab 02, Lab 04 | Google, LinkedIn |
| 10 | 1226. Dining Philosophers | Resource deadlock prevention | Lab 02 | Oracle, Google |
| 11 | 1117. Building H2O | Multi-resource coordination | Lab 02 | Intel, Google |
| 12 | 1188. Bounded Blocking Queue | Thread pool backpressure | Lab 04 | Google, Meta, Amazon |
| 13 | 384. Shuffle an Array | Cache coherency | Lab 08 | Amazon, Google |
| 14 | 705. Design HashSet | Bloom filter fundamentals | Lab 10 | Amazon, Microsoft |
| 15 | 706. Design HashMap | Hash function for sharding | Lab 08 | Meta, Amazon |
| 16 | 460. LFU Cache | Frequency-based eviction | Lab 08 | Google, Amazon |
| 17 | 380. Insert Delete GetRandom | Thread-safe metrics registry | Lab 03 | Amazon, Meta |
| 18 | 208. Implement Trie | LSM-tree / WAL patterns | Lab 05 | Google, Amazon, Meta |
| 19 | 127. Word Ladder | BFS = thread pool fan-out | Lab 02 | Amazon, Meta |
| 20 | 20. Valid Parentheses | TCP state machine | Lab 11 | All companies |
| 21 | 261. Graph Valid Tree | Cycle detection in resources | Lab 02 | LinkedIn, Amazon |
| 22 | 642. Design Search Autocomplete | Prefix-based shard routing | Lab 07 | Google, Meta |
| 23 | 432. All O`one Data Structure | Frequency tracking for caches | Lab 08 | Amazon, Meta |
| 24 | 480. Sliding Window Median | Real-time latency percentile | Lab 03 | Google, Meta |
| 25 | 316. Remove Duplicate Letters | Packet ordering | Lab 11 | Google |
| 26 | 641. Design Circular Deque | Double-ended log buffer | Lab 09 | Amazon |
| 27 | 211. Design Add and Search Word | DB index prefix matching | Lab 05 | Meta, Amazon |
| 28 | 179. Largest Number | Leader sequencing | Lab 13 | Amazon, Meta |
| 29 | 170. Two Sum III | Internal buffer patterns | Lab 05 | LinkedIn |
| 30 | 1625. Lexicographically Smallest | Deque in I/O buffers | Lab 09 | No direct |

---

## Study Plans

### 4-Week SRE Interview Crash Course (Urgent: Interview in 1 Month)

| Week | Daily Time | Focus | Labs | Coding | System Design |
|------|-----------|-------|------|--------|---------------|
| 1 | 2-3h | JVM + Concurrency | 01, 02, 03 | Top 5: LRU Cache, Course Schedule, Moving Average, Logger Rate Limiter, Circular Queue | Monitoring basics (4 golden signals, RED, USE) |
| 2 | 2-3h | Database + Deployment | 04, 05, 06 | Top 5: Find Median, Design Twitter, Bounded Blocking Queue, Dining Philosophers, Trie | Incident response system design |
| 3 | 2-3h | Distributed Systems | 07, 08, 13 | Top 5: LFU Cache, Web Crawler, Building H2O, Design HashSet, Add/Search Word | Capacity planning + DR design |
| 4 | 2-3h | Security + Behavioral | 09, 10, 11, 12, 14, 15 | Review all 20 + system design coding | Mock interviews + company research |

**Interview-ready outcome:** You can systematically debug any of the 15 incident types, solve medium-difficulty LeetCode problems, and design basic monitoring/incident response systems.

### 8-Week Production Engineering Mastery (Target: Meta, Amazon, Uber)

| Week | Focus | Technical Content | Soft Skills |
|------|-------|-------------------|-------------|
| 1 | JVM Deep Dive | Lab 01 + LeetCode Easy + GC tuning | Identify 2 STAR stories from past work |
| 2 | Concurrency | Lab 02, 03 + LeetCode Medium concurrency | Practice explaining deadlocks |
| 3 | Database | Lab 04, 05 + LeetCode Medium DB patterns | System design: monitoring |
| 4 | Deployment | Lab 06, 07 + LeetCode Medium design | System design: incident response |
| 5 | Caching | Lab 08, 09 + LeetCode Hard (LRU, LFU) | System design: capacity planning |
| 6 | Security | Lab 10, 11 + LeetCode Medium | Behavioral: on-call experience |
| 7 | Infrastructure | Lab 12, 13 + LeetCode Hard | System design: observability stack |
| 8 | Integration | Lab 14, 15 + Review all 30 LeetCode | Full mock interview day |

### 12-Week SRE Expert (Target: Google, Netflix, Stripe)

| Phase | Weeks | Focus | Deliverable |
|-------|-------|-------|-------------|
| Foundations | 1-3 | Labs 01-05 deep dive + LeetCode Concurrency (Course Schedule, Dining Philosophers, Building H2O, Bounded Blocking Queue + all 3 solutions per lab), Implement each fix from scratch | Can reproduce each lab's root cause and fix from memory |
| Distributed Systems | 4-6 | Labs 06-10 deep dive + LeetCode Cache/Design (LRU Cache, Design Twitter, LFU Cache, Design HashSet + Bloom filter implementation), Study Google SRE book chapters 1-10 | Can draw architecture diagrams for each lab scenario |
| Infrastructure | 7-9 | Labs 11-15 deep dive + LeetCode Advanced (Find Median, Trie, Word Ladder, Circular Queue), Study system design patterns | Can design production systems with failure mode analysis |
| Mastery | 10-12 | Mock interviews (6+ full-length), Advanced system design (monitoring 10K services, DR for global platform, chaos engineering design), Behavioral STAR preparation for 15 lab scenarios | Interview-ready across all 3 dimensions: coding, system design, behavioral |

---

## Company-Specific SRE Prep

### Google SRE — Bonus Rounds + Incident Analysis

**Google-specific focus areas:**
- **Troubleshooting round:** Depth over breadth. Drill into one scenario completely.
- **System design round:** Emphasis on tradeoff analysis. Every design choice must include "and here's what we lose."
- **Behavioral:** Google values "technical humility" — be willing to say "I don't know" and explain how you'd learn.

**Labs to master (Google SRE priority order):** Lab 01 (memory), Lab 02 (deadlock), Lab 03 (high CPU), Lab 04 (connection pool), Lab 15 (DR failover), Lab 09 (capacity)

**Additional reading:** Google SRE Book (free), Google SRE Workbook, Site Reliability Engineering at Google blog

### Meta ProdEng — Coding + Debugging Heavy

**Meta-specific focus areas:**
- **Production debugging simulation:** Must parse noisy dashboards under time pressure
- **Coding round:** Medium-to-hard algorithms with optimization focus
- **System design:** Meta-scale systems (billions of users)

**Labs to master (Meta priority order):** Lab 02 (deadlock — thread dumps), Lab 03 (high CPU — dashboard reading), Lab 04 (connection pool — thread analysis), Lab 05 (slow query), Lab 08 (cache stampede)

**Additional reading:** Meta Engineering blog, Preplib for PE-specific questions

### Amazon Systems — Scalability + Operational Excellence

**Amazon-specific focus areas:**
- **Leadership Principles:** Every answer must connect to at least one LP
- **Operational Excellence:** Deep dive into monitoring, deployment, incident response
- **Scale:** Everything at Amazon scale (millions of requests per second)

**Labs to master (Amazon priority order):** Lab 04 (connection pool — RDS scenario), Lab 05 (slow query — DynamoDB/Aurora), Lab 09 (disk space — capacity planning), Lab 14 (rate limiting), Lab 15 (DR failover)

**Additional reading:** AWS Well-Architected Framework, Amazon Builders' Library

### Microsoft Azure SRE — Azure Depth

**Microsoft-specific focus areas:**
- **Azure-specific knowledge:** App Service, Azure Monitor, Cosmos DB, AKS
- **Incident response methodology:** Methodical, documented, structured
- **Azure Well-Architected Framework:** Reliability pillar deep dive

**Labs to master (Microsoft priority order):** Lab 06 (deployment rollback — Azure DevOps), Lab 11 (TLS cert expiry), Lab 12 (Kubernetes crashloop — AKS), Lab 15 (DR failover — Azure traffic manager)

**Additional reading:** Azure Well-Architected Framework, Microsoft Azure Architecture Center

### Netflix Cloud Engineer — Chaos Engineering + Resilience

**Netflix-specific focus areas:**
- **Chaos engineering design:** Fault injection, blast radius, automated recovery
- **Culture fit:** Freedom and Responsibility — how you operate autonomously
- **System design:** Resilience patterns, circuit breakers, bulkheads

**Labs to master (Netflix priority order):** Lab 07 (circuit breaker), Lab 08 (cache stampede), Lab 15 (DR failover)

**Additional reading:** Netflix Tech Blog, Chaos Engineering book, Principles of Chaos

---

## Quick Reference: 15 Labs to NeetCode Categories

| Lab | Primary NeetCode Category | Secondary Category |
|-----|--------------------------|-------------------|
| 01 — Memory Leak | Arrays & Hashing (maps) | Graphs (object graph traversal) |
| 02 — Thread Deadlock | Graphs (cycle detection) | Concurrency patterns |
| 03 — High CPU | Heap / Priority Queue (percentiles) | Sliding Window |
| 04 — Connection Pool | Concurrency patterns | Design (bounded buffer) |
| 05 — Slow Query | Tries (LSM-tree) | Trees (index structures) |
| 06 — Deployment Rollback | Linked List (reversal) | Greedy (rollback decisions) |
| 07 — Circuit Breaker | Heap (priority queuing) | Graphs (dependency graph) |
| 08 — Cache Stampede | Linked List (LRU Cache) | Design (consistent hashing) |
| 09 — Disk Space | Two Pointers (bounded memory) | Heap (log rotation) |
| 10 — Security Breach | Bit Manipulation (Bloom filter) | Tries (pattern matching) |
| 11 — TLS Certificate | Stack (state machine) | Trees (certificate chain) |
| 12 — K8s CrashLoop | Graphs (health check DAG) | Arrays (probe scheduling) |
| 13 — Kafka Lag | Intervals (partition assignment) | Design (queue, consumer group) |
| 14 — Rate Limiting | Sliding Window (counters) | Design (token bucket) |
| 15 — DR Failover | Greedy (failover decisions) | Graphs (dependency routing) |

---

## Study Tools and Resources

| Resource | What It Provides | How to Use |
|----------|-----------------|------------|
| NeetCode 150 (neetcode.io) | Curated problem list with video solutions | Solve 1-2 per day for 3 months |
| Blind 75 (teamblind.com) | Most commonly asked problems | Complete before on-site interviews |
| Grind 75 (grind75.com) | Time-based prep (customizable weeks) | Use for 4-week crash course |
| Each lab's LEETCODE_SOLUTIONS/*.java | 3 Java solutions per lab | Reverse-engineer: understand the SRE context first, then the code |
| Each lab's LEETCODE_CONNECTIONS.md | Direct LeetCode-to-lab mapping | Read before attempting the LeetCode problem |

**Final advice:** The most important skill you'll develop in this academy is the ability to connect production incident patterns to algorithmic thinking. When an interviewer asks you to "solve LeetCode 207 — Course Schedule," being able to immediately say "This is the cycle detection algorithm I use when analyzing thread dump deadlocks (Lab 02)" is what separates SRE candidates from general software engineers.
