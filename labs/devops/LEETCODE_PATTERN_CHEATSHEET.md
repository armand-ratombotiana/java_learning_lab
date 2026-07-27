# DevOps LeetCode Pattern Cheatsheet

> Concurrency patterns, scheduling algorithms, distributed systems, and networking problems relevant to DevOps/SRE/Platform interviews.

---

## Table of Contents
1. Concurrency Patterns
2. Scheduling Algorithms
3. Distributed Systems
4. Networking
5. General Coding Patterns (DevOps-relevant)

---

## 1. Concurrency Patterns

### 1.1 Print in Order (LeetCode 1114)
**Problem**: Three threads print "first", "second", "third" in order.

**Pattern**: Use a barrier or mutex + flag.

```
Thread 1: print "first" → set flag1
Thread 2: wait until flag1, print "second" → set flag2
Thread 3: wait until flag2, print "third"
```

**Solutions**:
- `std::atomic` flags + busy wait (C++).
- `Lock` + `Condition` (Java).
- `threading.Event` (Python).
- `sync.Mutex` + `sync.Cond` (Go).

**Relevance**: Ordering deployment steps, ensuring initialization sequence.

### 1.2 Print FooBar Alternately (LeetCode 1115)
**Problem**: Two threads print "foo" and "bar" alternately.

**Pattern**: Mutex + condition variable or semaphore.

```
Semaphore fooSema(1), barSema(0)
Thread 1 (foo): wait fooSema, print, signal barSema
Thread 2 (bar): wait barSema, print, signal fooSema
```

**Relevance**: Alternating operations (e.g., health check → deploy → health check).

### 1.3 Zero Even Odd (LeetCode 1116)
**Problem**: Three threads print zero, even, odd in sequence.

**Pattern**: Three semaphores or condition variables.

```
zeroSema(1), evenSema(0), oddSema(0)
Zero: wait zeroSema, print, signal even or odd
Even: wait evenSema, print, signal zeroSema
Odd: wait oddSema, print, signal zeroSema
```

**Relevance**: Coordinating multiple limited resources (e.g., deployment phase transitions).

### 1.4 Building H2O (LeetCode 1117)
**Problem**: Two hydrogen atoms and one oxygen atom form water.

**Pattern**: Barrier + semaphore pair.

```
H: acquire oxygen, release hydrogenBarrier twice
O: acquire hydrogenBarrier twice, release oxygen
```

**Relevance**: Resource coordination in distributed builds (e.g., wait for all dependencies).

### 1.5 Dining Philosophers (LeetCode 1226)
**Problem**: 5 philosophers, 5 forks, avoid deadlock.

**Patterns**:
1. Global mutex (only one eats at a time).
2. Pick up lowest-numbered fork first.
3. Limit concurrent philosophers (max 4).
4. Footman (semaphore of 4).

```
Semaphore footman(4)
Semaphore forks[5] (all 1)
Pick up left fork, pick up right fork, eat, put down right, put down left
```

**Relevance**: Distributed resource locking, avoiding deadlocks in multi-threaded infrastructure tools.

### 1.6 Traffic Light Controlled Intersection (LeetCode 1279)
**Problem**: Cars arrive on two roads, control traffic lights.

**Pattern**: Mutex + flag per road.

```
if wrong road → wait; else → cross, set flag, notify
```

**Relevance**: Canary deployment traffic control, gradually shifting traffic.

### 1.7 Web Crawler Multithreaded (LeetCode 1242)
**Problem**: Crawl URLs in parallel, avoid duplicates.

**Pattern**: Thread pool + concurrent hash set.

```
ThreadPool pool(N)
ConcurrentSet visited
pool.submit: crawl(url), add links, submit new
```

**Relevance**: Parallel build/test execution, dependency graph traversal.

### 1.8 Fizz Buzz Multithreaded (LeetCode 1195)
**Problem**: Four threads print fizz, buzz, fizzbuzz, number.

**Pattern**: Semaphore per thread, count-based coordination.

```
if i % 15 == 0 → trigger fizzbuzz
else if i % 3 == 0 → trigger fizz
else if i % 5 == 0 → trigger buzz
else → print number
```

**Relevance**: Conditional pipeline stages, parallel task execution.

---

## 2. Scheduling Algorithms

### 2.1 Task Scheduler (LeetCode 621)
**Problem**: Schedule tasks with cooldown between same tasks.

**Pattern**: Greedy — schedule most frequent task first.

```
count per task → max frequency and max count
result = max(len(tasks), (maxFreq - 1) * (n + 1) + maxCount)
```

**Time**: O(N log N) or O(N). **Space**: O(1) (26 letters).

**Relevance**: Kubernetes scheduler bin packing, CI/CD pipeline job scheduling with cooldowns.

### 2.2 Least Number of Unique Integers After K Removals (LeetCode 1481)
**Problem**: Remove K elements to minimize unique integers.

**Pattern**: Count frequencies, sort, remove smallest first.

**Relevance**: Resource optimization, reducing dependency bloat.

### 2.3 Meeting Rooms II (LeetCode 253)
**Problem**: Minimum number of conference rooms.

**Pattern**: Chronological ordering + min-heap.

```
Sort intervals by start time.
Min-heap of end times.
For each interval: if start >= heap.top → pop, push new end.
Result: heap size.
```

**Time**: O(N log N). **Space**: O(N).

**Relevance**: Kubernetes scheduler resource allocation, CI runner pool sizing, on-call shift scheduling.

### 2.4 Minimum Number of Arrows to Burst Balloons (LeetCode 452)
**Problem**: Minimum arrows to burst overlapping balloons.

**Pattern**: Sort by end, greedy.

```
Sort by end coordinate. Cur arrow at first end.
If balloon start > arrow pos → new arrow at its end.
```

**Time**: O(N log N). **Space**: O(1).

**Relevance**: Resource consolidation, minimizing concurrent infrastructure changes.

### 2.5 Network Delay Time (LeetCode 743)
**Problem**: Shortest time for signal to reach all nodes.

**Pattern**: Dijkstra's algorithm (single-source shortest path).

```
Graph adjacency list. Min-heap of (time, node).
Pop min, relax neighbors. Vistited set.
Max distance = max of all distances.
```

**Time**: O(E log V). **Space**: O(V + E).

**Relevance**: CDN latency optimization, network routing in K8s service mesh.

### 2.6 Course Schedule II (LeetCode 210)
**Problem**: Find order to take courses given prerequisites.

**Pattern**: Topological sort (Kahn's algorithm or DFS).

```
Kahn: in-degree array. Queue of 0 in-degree.
Process, reduce neighbors, add to queue if in-degree 0.
Result array. If cycle → empty array.
```

**Time**: O(V + E). **Space**: O(V + E).

**Relevance**: Infrastructure provisioning order (dependencies), deployment pipeline DAG.

### 2.7 Task Scheduler with Multiple Workers (Variant)
**Problem**: Assign tasks to workers to minimize total time.

**Pattern**: Load distribution — greedy or DP.

```
Sort tasks by duration descending.
Assign longest task to least loaded worker.
Or: partition problem (NP-hard, approximate).
```

**Relevance**: CI/CD parallel job assignment, Kubernetes bin packing.

### 2.8 Single-Threaded CPU (LeetCode 1834)
**Problem**: Process tasks with enqueue time and processing time.

**Pattern**: Min-heap with two stages.

```
Sort by enqueue time. Min-heap by processing time.
Time cursor: while enqueue ≤ time → push to heap.
Pop shortest processing time task, advance time.
```

**Time**: O(N log N). **Space**: O(N).

**Relevance**: Kubernetes scheduler with priority queues, CI pipeline stage ordering.

---

## 3. Distributed Systems

### 3.1 Design a Distributed Key-Value Store (System Design)
**Patterns**:
- Consistent hashing (ring of virtual nodes).
- Data partitioning: hash key → partition.
- Replication: quorum (W+R > N).
- CAP trade-off: CP (strong consistency) vs AP (eventual).
- Conflict resolution: LWW (last-write-wins) or CRDT.

**Relevance**: etcd, Consul KV, Redis Cluster, DynamoDB.

### 3.2 Design a Distributed Lock
**Patterns**:
- Based on Redis (Redlock): lock N instances, majority for success.
- Based on ZK/etcd: ephemeral sequential nodes.
- Lease: client must renew. Auto-expire on failure.

**Relevance**: Terraform state locking, leader election in K8s controllers, CronJob mutes.

### 3.3 Leader Election (Implementation)
**Patterns**:
- **Bully algorithm**: Highest-ID node becomes leader.
- **Raft**: Random timeout → request votes → become leader.
- **etcd/Consul lease**: Acquire lease, renew, leader holds lease.

```
Raft simplified:
1. All nodes start as followers.
2. Election timeout → become candidate.
3. Request votes. If majority → leader.
4. Heartbeats prevent new elections.
```

**Relevance**: Kubernetes controller manager, control plane components.

### 3.4 Distributed Counter (LeetCode 1242 Variant)
**Pattern**: CRDT (Conflict-free Replicated Data Type).

- **G-Counter**: Grow-only. Each node has own value. Merge = max per node.
- **PN-Counter**: Two G-counters (increments, decrements).

**Relevance**: Prometheus counter aggregation, distributed metrics collection.

### 3.5 Rate Limiter (System Design + Code)
**Algorithm**: Token bucket with concurrency-safe implementation.

```
type TokenBucket struct {
    mu sync.Mutex
    tokens int
    maxTokens int
    refillInterval time.Duration
    lastRefill time.Time
}
func (tb *TokenBucket) Allow() bool {
    tb.mu.Lock()
    defer tb.mu.Unlock()
    tb.refill()
    if tb.tokens > 0 {
        tb.tokens--
        return true
    }
    return false
}
```

**Relevance**: API gateway rate limiting, Kubernetes QoS, CI rate limiting.

### 3.6 Consensus Protocol (Raft Step-by-Step)
**Key steps**:
1. **Leader election**: Followers → candidates → leader (majority).
2. **Log replication**: Leader receives command → appends to log → replicates to followers → commit.
3. **Safety**: At most one leader per term. Log Matching Property.

**Relevance**: etcd (underlying K8s), Consul, Vault HA.

---

## 4. Networking

### 4.1 DNS Resolution (Concept)
**Flow**:
```
Browser → hosts file → DNS resolver → root NS → TLD NS → authoritative NS → IP
```

**Caching**: Browser → OS → ISP → recursive resolver. TTL determines cache duration.

**Relevance**: Debugging Docker DNS, Kubernetes CoreDNS, service discovery.

### 4.2 TCP Handshake (Concept)
```
Client → SYN (seq=x) → Server
Client ← SYN-ACK (seq=y, ack=x+1) ← Server
Client → ACK (seq=x+1, ack=y+1) → Server
```

**Connection teardown**: FIN → FIN-ACK → ACK (4-way handshake).

**Relevance**: Debugging network issues, load balancer tuning.

### 4.3 HTTP/1.1 vs HTTP/2 vs HTTP/3

| Version | Features | Limitations |
|---------|----------|-------------|
| HTTP/1.1 | Persistent connections, chunked transfer | Head-of-line blocking, multiple connections |
| HTTP/2 | Multiplexing, server push, header compression | TCP HOL blocking lost |
| HTTP/3 | QUIC (UDP), 0-RTT, no HOL | Higher CPU, less mature |

**Relevance**: Load balancer tuning, API gateway configuration, CDN optimization.

### 4.4 TLS Handshake (Concept)
```
Client Hello (ciphers, TLS version) → Server
Server Hello + Certificate + Key Exchange ← Server
Client Key Exchange + Change Cipher Spec + Finished → Server
Change Cipher Spec + Finished ← Server
```

**Relevance**: Ingress TLS termination, mTLS in service mesh, Vault PKI.

---

## 5. General Coding Patterns (DevOps-relevant)

### 5.1 Two Pointers
- **Sorted Two Sum**: O(N) with left/right pointers.
- **Container With Most Water**: Greedy two pointers.
- **Trapping Rain Water**: Left/right max.

**Relevance**: PromQL aggregation windowing, log stream comparison.

### 5.2 Sliding Window
- **Maximum Subarray**: Kadane's algorithm.
- **Longest Substring Without Repeating**: Sliding window + hash set.
- **Minimum Window Substring**: Two pointers + hash map.

**Relevance**: Rolling deployment windows, log analysis on time windows.

### 5.3 Binary Search
- **Search in Rotated Array**: Modified binary search.
- **Find Peak Element**: Binary search on derivative.
- **Kth Smallest Element**: Binary search on value range.

**Relevance**: Binary search in logs, finding configuration thresholds.

### 5.4 Depth-First Search (DFS)
- **Number of Islands**: Grid DFS with visited set.
- **Clone Graph**: DFS/BFS with hash map.
- **Word Search**: Backtracking DFS.

**Relevance**: Service dependency graph traversal, Docker layer analysis.

### 5.5 Breadth-First Search (BFS)
- **Binary Tree Level Order**: Queue-based BFS.
- **Word Ladder**: BFS on transformation graph.
- **Shortest Path in Grid**: BFS with visited set.

**Relevance**: CI/CD pipeline level ordering, network topology discovery.

### 5.6 Hash Map
- **Group Anagrams**: Sorted word as key.
- **Top K Frequent Elements**: Frequency map + min-heap.
- **LRU Cache**: Hash map + doubly linked list.

**Relevance**: Config caching, secret lookups, package deduplication.

### 5.7 Stack
- **Valid Parentheses**: Stack matching.
- **Min Stack**: Two stacks or tuple stack.
- **Basic Calculator**: Postfix conversion.

**Relevance**: HCL/JSON parsing, shell script parsing, Dockerfile layer evaluation.

### 5.8 Trie (Prefix Tree)
- **Implement Trie**: Insert, search, startsWith.
- **Word Search II**: Trie + DFS grid.

**Relevance**: DNS domain matching, role-based access control (RBAC) prefix matching.

### 5.9 Union Find (Disjoint Set)
- **Number of Provinces**: find + union operations.
- **Accounts Merge**: union email accounts.
- **Redundant Connection**: detect cycle in graph.

**Relevance**: Service dependency clustering, container group isolation, network partition detection.

### 5.10 Dynamic Programming
- **0/1 Knapsack**: Classic DP.
- **Longest Increasing Subsequence**: Binary search DP.
- **Edit Distance**: 2D DP.

**Relevance**: Resource allocation optimization, CI/CD build time optimization.

### 5.11 Greedy
- **Jump Game II**: Greedy BFS.
- **Gas Station**: Running sum, start from deficit.
- **Candy Distribution**: Two-pass greedy.

**Relevance**: Autoscaling decisions, canary release percentage progression.

### 5.12 Graph - Topological Sort
- **Course Schedule II**: Kahn's algorithm.
- **Alien Dictionary**: Graph of character order.
- **Parallel Courses**: BFS with prerequisites.

**Relevance**: Helm chart dependency ordering, Terraform module DAG, CI/CD pipeline stages.

---

## DevOps Coding Challenges (Practice Problems)

| Category | Problem | Difficulty | Time to Solve |
|----------|---------|------------|---------------|
| Concurrency | Print in Order | Easy | 20 min |
| Concurrency | FooBar Alternately | Medium | 25 min |
| Concurrency | Dining Philosophers | Medium | 30 min |
| Concurrency | H2O | Medium | 30 min |
| Scheduling | Task Scheduler | Medium | 25 min |
| Scheduling | Meeting Rooms II | Medium | 20 min |
| Scheduling | Single-Threaded CPU | Medium | 30 min |
| Scheduling | Course Schedule II | Medium | 25 min |
| Graph | Network Delay Time | Medium | 25 min |
| Graph | Alien Dictionary | Hard | 35 min |
| DP | 0/1 Knapsack | Medium | 25 min |
| DP | Edit Distance | Hard | 30 min |
| System Design | Rate Limiter | Medium | 40 min |
| System Design | Distributed Counter | Medium | 30 min |
| System Design | Leader Election (Raft) | Hard | 45 min |

---

## Quick Reference: Time Complexities

| Algorithm | Average | Worst | Space |
|-----------|---------|-------|-------|
| Quick Sort | O(N log N) | O(N^2) | O(log N) |
| Merge Sort | O(N log N) | O(N log N) | O(N) |
| Heap Sort | O(N log N) | O(N log N) | O(1) |
| Binary Search | O(log N) | O(log N) | O(1) |
| DFS/BFS | O(V + E) | O(V + E) | O(V) |
| Dijkstra | O(E log V) | O(E log V) | O(V) |
| Topological Sort | O(V + E) | O(V + E) | O(V) |
| Union Find | O(alpha(N)) | O(alpha(N)) | O(N) |

---

## DevOps-Specific LeetCode Practice Set

1. **Task Scheduler** (621) — Canary deployment cadence.
2. **Meeting Rooms II** (253) — CI runner pool sizing.
3. **Network Delay Time** (743) — CDN latency optimization.
4. **Course Schedule II** (210) — Terraform/K8s dependency graph.
5. **Design HashMap** (706) — Config store design (like etcd).
6. **LRU Cache** (146) — Docker layer caching, Kubernetes informer cache.
7. **Min Stack** (155) — Rollback stack for deployment versions.
8. **Serialize and Deserialize BST** (449) — Terraform state serialization.
9. **LFU Cache** (460) — Container image eviction in registries.
10. **Design T