# Distributed Systems → LeetCode Pattern Cheatsheet

> Map every distributed systems concept to specific LeetCode problems with Java template code.

---

## Table of Contents
1. [Consistency Patterns → Concurrency Problems](#consistency-patterns)
2. [Consensus Algorithms → Graph Problems](#consensus-algorithms)
3. [Distributed Caching → Cache Design Problems](#distributed-caching)
4. [Distributed Queues → BFS/Sliding Window](#distributed-queues)
5. [Partitioning → Hash Map Design](#partitioning)
6. [Replication → Tree/Graph Traversal](#replication)
7. [Failure Detection → Union-Find/Cycle Detection](#failure-detection)
8. [Distributed Locks → Concurrency Problems](#distributed-locks)
9. [Distributed Transactions → Database Problems](#distributed-transactions)
10. [Distributed ID Generation → Math Problems](#distributed-id-generation)
11. [Gossip Protocols → Graph Propagation](#gossip-protocols)
12. [Time/Ordering → Sorting/Schedule Problems](#time-ordering)

---

## Consistency Patterns → Concurrency Problems

### Strong Consistency → Synchronized / Locks
**Distributed Concept**: Linearizability, single-copy serializability
**LeetCode Mapping**: Thread-safe data structures, synchronized access

### Problem 1: Design a Thread-Safe Key-Value Store
**LC #**: None (custom), similar to 1242 (Web Crawler Multithreaded)
**Company Frequency**: Amazon (High), Google (High)

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;

class ThreadSafeKVStore<K, V> {
    private final ConcurrentHashMap<K, V> store = new ConcurrentHashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    public V get(K key) {
        lock.readLock().lock();
        try {
            return store.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void put(K key, V value) {
        lock.writeLock().lock();
        try {
            store.put(key, value);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public boolean cas(K key, V expected, V newValue) {
        lock.writeLock().lock();
        try {
            V current = store.get(key);
            if (current == expected || (current != null && current.equals(expected))) {
                store.put(key, newValue);
                return true;
            }
            return false;
        } finally {
            lock.writeLock().unlock();
        }
    }
}
```

### Problem 2: Design a Quorum Reader/Writer
**LC #**: 1242 (Web Crawler Multithreaded)
**Concept**: NWR model - read from R replicas, write to W replicas

```java
import java.util.concurrent.atomic.AtomicInteger;

class QuorumStore {
    private final String[] replicas;
    private final int N, W, R;

    public QuorumStore(int n, int w, int r) {
        this.N = n; this.W = w; this.R = r;
        this.replicas = new String[N];
    }

    public boolean write(int idx, String value) {
        replicas[idx] = value;
        return true;
    }

    public String read() {
        String latest = null;
        int version = -1;
        for (int i = 0; i < N; i++) {
            if (replicas[i] != null) {
                return replicas[i];
            }
        }
        return null;
    }
}
```

### Related LeetCode Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Web Crawler Multithreaded | 1242 | Google | Concurrent access, strong consistency |
| FooBar (Alternating) | 1115 | Google | Ordering guarantees |
| H2O Generation | 1117 | Microsoft | Barrier synchronization |
| FizzBuzz Multithreaded | 1195 | Google | Thread coordination |
| Print Zero Even Odd | 1116 | Amazon | State machine consistency |
| Bounded Blocking Queue | 1188 | Apple | Producer-consumer consistency |

---

## Consensus Algorithms → Graph Problems

### Problem 1: Raft Leader Election Visualization (Graph Traversal)
**LC #**: 207 (Course Schedule), 210 (Course Schedule II), 269 (Alien Dictionary)
**Company Frequency**: Google (High), Confluent (Very High)

**Distributed Concept**: Graph-based leader election, topological ordering of log entries

```java
// Raft-style Majority Check using Union-Find
class MajorityChecker {
    private int[] parent;
    private int[] size;

    public MajorityChecker(int n) {
        parent = new int[n];
        size = new int[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            size[i] = 1;
        }
    }

    public boolean vote(int from, int to) {
        int rootF = find(from);
        int rootT = find(to);
        if (rootF != rootT) {
            if (size[rootF] < size[rootT]) {
                parent[rootF] = rootT;
                size[rootT] += size[rootF];
                return size[rootT] > parent.length / 2;
            } else {
                parent[rootT] = rootF;
                size[rootF] += size[rootT];
                return size[rootF] > parent.length / 2;
            }
        }
        return false;
    }

    private int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }
}
```

### Problem 2: Paxos - Quorum Intersection Check
**LC #**: 684 (Redundant Connection), 721 (Accounts Merge)

```java
class PaxosAcceptor {
    private int promisedBallot = -1;
    private int acceptedBallot = -1;
    private String acceptedValue = null;

    // Phase 1: Prepare
    public synchronized String prepare(int ballot) {
        if (ballot > promisedBallot) {
            promisedBallot = ballot;
            return acceptedValue; // promise + previous value
        }
        return null; // reject
    }

    // Phase 2: Accept
    public synchronized boolean accept(int ballot, String value) {
        if (ballot >= promisedBallot) {
            promisedBallot = ballot;
            acceptedBallot = ballot;
            acceptedValue = value;
            return true;
        }
        return false;
    }
}
```

### Related LeetCode Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Course Schedule II | 210 | Google | Topological order (log ordering) |
| Alien Dictionary | 269 | Facebook, Amazon | Custom ordering (Paxos ordering) |
| Redundant Connection | 684 | Google | Cycle detection (split-brain) |
| Accounts Merge | 721 | Facebook | Union-Find (cluster membership) |
| Evaluate Division | 399 | Google | Graph traversal (dependency resolution) |
| Reconstruct Itinerary | 332 | Uber | Eulerian path (log replication) |
| Minimum Height Trees | 310 | Google | Graph center (leader election) |

---

## Distributed Caching → Cache Design Problems

### Problem 1: LRU Cache
**LC #**: 146 (LRU Cache)
**Company Frequency**: Amazon (Very High), Google (High), Meta (High), Microsoft (High), Apple (High)

```java
import java.util.HashMap;
import java.util.Map;

class LRUCache {
    class Node {
        int key, val;
        Node prev, next;
        Node(int key, int val) { this.key = key; this.val = val; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0);
    private final Node tail = new Node(0, 0);

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        remove(node);
        insert(head, node);
        return node.val;
    }

    public void put(int key, int value) {
        if (map.containsKey(key)) {
            remove(map.get(key));
        }
        if (map.size() == capacity) {
            remove(tail.prev);
        }
        Node node = new Node(key, value);
        map.put(key, node);
        insert(head, node);
    }

    private void remove(Node node) {
        map.remove(node.key);
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void insert(Node head, Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }
}
```

### Problem 2: LFU Cache
**LC #**: 460 (LFU Cache)
**Company Frequency**: Google (High), Amazon (Medium)

```java
import java.util.*;

class LFUCache {
    private int capacity, minFreq;
    private Map<Integer, int[]> keyToValueFreq;
    private Map<Integer, LinkedHashSet<Integer>> freqToKeys;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyToValueFreq = new HashMap<>();
        this.freqToKeys = new HashMap<>();
        freqToKeys.put(1, new LinkedHashSet<>());
    }

    public int get(int key) {
        if (!keyToValueFreq.containsKey(key)) return -1;
        int[] valFreq = keyToValueFreq.get(key);
        int freq = valFreq[1];
        freqToKeys.get(freq).remove(key);
        if (freq == minFreq && freqToKeys.get(freq).isEmpty()) minFreq++;
        valFreq[1]++;
        freqToKeys.computeIfAbsent(freq + 1, k -> new LinkedHashSet<>()).add(key);
        return valFreq[0];
    }

    public void put(int key, int value) {
        if (capacity <= 0) return;
        if (keyToValueFreq.containsKey(key)) {
            keyToValueFreq.get(key)[0] = value;
            get(key);
            return;
        }
        if (keyToValueFreq.size() >= capacity) {
            int evict = freqToKeys.get(minFreq).iterator().next();
            freqToKeys.get(minFreq).remove(evict);
            keyToValueFreq.remove(evict);
        }
        keyToValueFreq.put(key, new int[]{value, 1});
        freqToKeys.get(1).add(key);
        minFreq = 1;
    }
}
```

### Related Cache Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| LRU Cache | 146 | Amazon, Google, Meta | Write-through cache |
| LFU Cache | 460 | Google | Eviction policy |
| Design In-Memory File System | 588 | Amazon | Cache hierarchies |
| Design Browser History | 1472 | Google | Cache prefetching |
| Max Frequency Stack | 895 | Google | Frequency-based access |
| Design Hit Counter | 362 | Google | Sliding window cache |
| Time-Based KV Store | 981 | Google, Amazon | TTL-based cache |

---

## Distributed Queues → BFS/Sliding Window

### Problem 1: Implement a Message Queue
**LC #**: 622 (Design Circular Queue), 641 (Design Circular Deque)
**Company Frequency**: Amazon (High), Google (Medium)

```java
class MessageQueue {
    private final String[] queue;
    private int head, tail, count;
    private final int capacity;

    public MessageQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new String[capacity];
        this.head = 0;
        this.tail = 0;
        this.count = 0;
    }

    public synchronized boolean publish(String message) {
        if (count == capacity) return false;
        queue[tail] = message;
        tail = (tail + 1) % capacity;
        count++;
        notifyAll();
        return true;
    }

    public synchronized String consume() throws InterruptedException {
        while (count == 0) wait();
        String msg = queue[head];
        head = (head + 1) % capacity;
        count--;
        notifyAll();
        return msg;
    }

    public synchronized int size() { return count; }
}
```

### Problem 2: Kafka-Style Partition Consumer
**LC #**: 1188 (Design Bounded Blocking Queue)
**Company Frequency**: Confluent (Very High), LinkedIn (High)

```java
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

class PartitionQueue {
    private final int capacity;
    private final int[] buffer;
    private int readPtr, writePtr, count;
    private final ReentrantLock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public PartitionQueue(int capacity) {
        this.capacity = capacity;
        this.buffer = new int[capacity];
    }

    public void produce(int value) throws InterruptedException {
        lock.lock();
        try {
            while (count == capacity) notFull.await();
            buffer[writePtr] = value;
            writePtr = (writePtr + 1) % capacity;
            count++;
            notEmpty.signal();
        } finally { lock.unlock(); }
    }

    public int consume() throws InterruptedException {
        lock.lock();
        try {
            while (count == 0) notEmpty.await();
            int val = buffer[readPtr];
            readPtr = (readPtr + 1) % capacity;
            count--;
            notFull.signal();
            return val;
        } finally { lock.unlock(); }
    }
}
```

### Problem 3: Sliding Window Rate Limiter
**LC #**: None (System Design - Rate Limiter)
**Company Frequency**: Stripe (Very High), Amazon (High)

```java
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

class SlidingWindowRateLimiter {
    private final Queue<Long> window;
    private final int maxRequests;
    private final long windowDurationMs;

    public SlidingWindowRateLimiter(int maxRequests, long windowDuration, TimeUnit unit) {
        this.maxRequests = maxRequests;
        this.windowDurationMs = unit.toMillis(windowDuration);
        this.window = new ConcurrentLinkedQueue<>();
    }

    public synchronized boolean allow() {
        long now = System.currentTimeMillis();
        long boundary = now - windowDurationMs;
        while (!window.isEmpty() && window.peek() < boundary) {
            window.poll();
        }
        if (window.size() < maxRequests) {
            window.offer(now);
            return true;
        }
        return false;
    }
}
```

### Related Queue Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Design Circular Queue | 622 | Amazon | Bounded queue |
| Design Bounded Blocking Queue | 1188 | Google | Producer-consumer |
| Task Scheduler | 621 | Meta | Distributed scheduling |
| Shortest Subarray with Sum >= K | 862 | Google | Sliding window optimization |
| Sliding Window Maximum | 239 | Amazon, Google | Stream processing |
| Number of Islands | 200 | Amazon, Google | BFS traversal pattern |
| Word Ladder | 127 | Amazon, Google | BFS graph search |
| Bus Routes | 815 | Google | BFS with partitions |

---

## Partitioning → Hash Map Design

### Problem 1: Consistent Hashing
**LC #**: None (custom system design), related to 706 (Design HashMap)
**Company Frequency**: Amazon (Very High), Google (Very High)

```java
import java.util.*;

class ConsistentHashRing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int replicas;
    private final HashFunction hash;

    public ConsistentHashRing(int replicas, HashFunction hash) {
        this.replicas = replicas;
        this.hash = hash;
    }

    public void addNode(String node) {
        for (int i = 0; i < replicas; i++) {
            ring.put(hash.hash(node + "#" + i), node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < replicas; i++) {
            ring.remove(hash.hash(node + "#" + i));
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hashVal = hash.hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hashVal);
        if (entry == null) {
            entry = ring.firstEntry();
        }
        return entry.getValue();
    }

    interface HashFunction {
        int hash(String input);
    }
}
```

### Problem 2: Range Partitioning
**LC #**: 715 (Range Module), 352 (Data Stream as Disjoint Intervals)

```java
import java.util.*;

class RangePartitioner {
    private final TreeMap<Integer, Integer> ranges = new TreeMap<>();

    public void addRange(int left, int right) {
        Map.Entry<Integer, Integer> start = ranges.floorEntry(left);
        Map.Entry<Integer, Integer> end = ranges.floorEntry(right);
        if (start != null && start.getValue() >= left) {
            left = start.getKey();
        }
        if (end != null && end.getValue() > right) {
            right = end.getValue();
        }
        ranges.subMap(left, true, right, false).clear();
        ranges.put(left, right);
    }

    public boolean queryRange(int left, int right) {
        Map.Entry<Integer, Integer> entry = ranges.floorEntry(left);
        return entry != null && entry.getValue() >= right;
    }

    public void removeRange(int left, int right) {
        Map.Entry<Integer, Integer> start = ranges.floorEntry(left);
        Map.Entry<Integer, Integer> end = ranges.floorEntry(right);
        if (start != null && start.getValue() > left) {
            ranges.put(start.getKey(), left);
            if (start.getValue() > right) {
                ranges.put(right, start.getValue());
            }
        }
        if (end != null && end != start && end.getValue() > right) {
            ranges.put(right, end.getValue());
        }
        ranges.subMap(left, true, right, false).clear();
    }
}
```

### Related Partitioning Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Design HashMap | 706 | Amazon, Microsoft | Bucket partitioning |
| Design HashSet | 705 | Google, Amazon | Hash partitioning |
| Range Module | 715 | Google | Range partitioning |
| Data Stream as Intervals | 352 | Google | Dynamic partitioning |
| Insert Delete GetRandom O(1) | 380 | Amazon, Google | Array + hash hybrid |
| Time Based KV Store | 981 | Google | Partition by time |
| Encode and Decode TinyURL | 535 | Meta | ID-based partitioning |
| Shuffle an Array | 384 | Microsoft | Shuffle partitioning |

---

## Replication → Tree/Graph Traversal

### Problem 1: Leader-Follower Replication (Tree Pattern)
**LC #**: 987 (Vertical Order Traversal), 102 (Binary Tree Level Order)
**Company Frequency**: Google (Medium), Amazon (Medium)

```java
import java.util.*;

class ReplicationTree {
    static class Node {
        String id;
        int data;
        int version;
        List<Node> followers;

        Node(String id, int data) {
            this.id = id;
            this.data = data;
            this.version = 0;
            this.followers = new ArrayList<>();
        }
    }

    private final Node leader;

    public ReplicationTree(Node leader) { this.leader = leader; }

    public void write(int newData) {
        leader.data = newData;
        leader.version++;
        propagate(leader);
    }

    private void propagate(Node node) {
        for (Node follower : node.followers) {
            follower.data = node.data;
            follower.version = node.version;
            propagate(follower);
        }
    }

    public int read(Node node) {
        return node.data;
    }
}
```

### Problem 2: Multi-Leader Replication (Merge)
**LC #**: 56 (Merge Intervals), 986 (Interval List Intersections)

```java
import java.util.*;

class MultiLeaderReplicator {
    static class Record {
        String key, value;
        int version, nodeId;

        Record(String key, String value, int version, int nodeId) {
            this.key = key; this.value = value;
            this.version = version; this.nodeId = nodeId;
        }
    }

    private final Map<String, List<Record>> conflicts = new HashMap<>();

    public void sync(String key, String value, int version, int nodeId) {
        conflicts.computeIfAbsent(key, k -> new ArrayList<>())
                .add(new Record(key, value, version, nodeId));
    }

    public String resolve(String key) {
        List<Record> records = conflicts.get(key);
        if (records == null || records.isEmpty()) return null;
        // Last-writer-wins (LWW)
        return records.stream()
                .max(Comparator.comparingInt((Record r) -> r.version)
                        .thenComparingInt(r -> r.nodeId))
                .get().value;
    }
}
```

### Related Replication Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Merge Intervals | 56 | Amazon, Google | Multi-leader merge |
| Interval List Intersections | 986 | Google | Replica sync |
| Clone Graph | 133 | Google, Amazon | Graph replication |
| Copy List with Random Pointer | 138 | Meta | Deep copy replication |
| Serialize and Deserialize BST | 449 | Amazon | State replication |
| Inorder Successor in BST | 285 | Meta | Replica traversal |
| Binary Tree Right Side View | 199 | Meta, Amazon | Quorum view |
| All Nodes Distance K in BT | 863 | Meta, Google | Gossip propagation |

---

## Failure Detection → Union-Find/Cycle Detection

### Problem 1: Heartbeat Failure Detector
**LC #**: 261 (Graph Valid Tree), 684 (Redundant Connection)
**Company Frequency**: Amazon (High), Google (High)

```java
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class FailureDetector {
    private final ConcurrentHashMap<String, Long> heartbeats = new ConcurrentHashMap<>();
    private final long timeoutMs;
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public FailureDetector(long timeoutMs) {
        this.timeoutMs = timeoutMs;
        scheduler.scheduleAtFixedRate(this::check, timeoutMs, timeoutMs / 2, TimeUnit.MILLISECONDS);
    }

    public void heartbeat(String nodeId) {
        heartbeats.put(nodeId, System.currentTimeMillis());
    }

    public boolean isAlive(String nodeId) {
        Long last = heartbeats.get(nodeId);
        return last != null && (System.currentTimeMillis() - last) < timeoutMs;
    }

    private void check() {
        long now = System.currentTimeMillis();
        heartbeats.forEach((node, time) -> {
            if (now - time > timeoutMs) {
                System.out.println("Node " + node + " suspected as failed");
                // Trigger suspicion protocol
            }
        });
    }
}
```

### Problem 2: Gossip-Style Failure Detection
**LC #**: 547 (Number of Provinces), 323 (Number of Connected Components)
**Company Frequency**: Amazon (Medium), Google (Medium)

```java
class GossipFailureDetector {
    private int[] parent;
    private long[] lastHeartbeat;

    public GossipFailureDetector(int n) {
        parent = new int[n];
        lastHeartbeat = new long[n];
        for (int i = 0; i < n; i++) {
            parent[i] = i;
            lastHeartbeat[i] = System.currentTimeMillis();
        }
    }

    public void union(int a, int b) {
        int rootA = find(a);
        int rootB = find(b);
        if (rootA != rootB) parent[rootB] = rootA;
    }

    public int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]];
            x = parent[x];
        }
        return x;
    }

    public boolean suspectFailure(int node, long timeoutMs) {
        return (System.currentTimeMillis() - lastHeartbeat[node]) > timeoutMs;
    }

    public boolean allConnected() {
        int root = find(0);
        for (int i = 1; i < parent.length; i++) {
            if (find(i) != root) return false;
        }
        return true;
    }
}
```

### Related Union-Find Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Number of Provinces | 547 | Amazon | Cluster membership |
| Connected Components | 323 | Google | Network partition |
| Redundant Connection | 684 | Google | Cycle detection (split brain) |
| Accounts Merge | 721 | Meta | Cluster merge |
| Satisfiability of Equations | 990 | Google | Consistency check |
| Number of Islands II | 305 | Google | Dynamic connectivity |
| Longest Consecutive Sequence | 128 | Google, Meta | Failure detection timeout |
| Graph Valid Tree | 261 | Google | Acyclic graph (healthy cluster) |

---

## Distributed Locks → Concurrency Problems

### Problem 1: Distributed Lock Implementation
**LC #**: 1115 (Print FooBar Alternately), 1117 (H2O Generation)
**Company Frequency**: Google (High), Amazon (Medium)

```java
import java.util.concurrent.locks.ReentrantLock;

class DistributedLock {
    private final ReentrantLock lock = new ReentrantLock(true); // fair lock
    private String lockOwner;
    private long leaseEnd;

    public boolean acquire(String owner, long leaseDurationMs) {
        if (lock.tryLock()) {
            try {
                if (leaseEnd < System.currentTimeMillis() || lockOwner == null) {
                    lockOwner = owner;
                    leaseEnd = System.currentTimeMillis() + leaseDurationMs;
                    return true;
                }
            } finally {
                lock.unlock();
            }
        }
        return false;
    }

    public boolean renew(String owner, long leaseDurationMs) {
        if (!lockOwner.equals(owner)) return false;
        lock.lock();
        try {
            leaseEnd = System.currentTimeMillis() + leaseDurationMs;
            return true;
        } finally {
            lock.unlock();
        }
    }

    public boolean release(String owner) {
        if (!lockOwner.equals(owner)) return false;
        lock.lock();
        try {
            lockOwner = null;
            leaseEnd = 0;
            return true;
        } finally {
            lock.unlock();
        }
    }
}
```

### Problem 2: ZooKeeper-Style Ephemeral Lock
**LC #**: 1116 (Print Zero Even Odd), 1195 (FizzBuzz Multithreaded)

```java
import java.util.concurrent.Semaphore;

class EphemeralLock {
    private final Semaphore semaphore = new Semaphore(1, true);
    private final String nodeId;

    public EphemeralLock(String nodeId) { this.nodeId = nodeId; }

    public boolean tryLock() {
        return semaphore.tryAcquire();
    }

    public void lock() {
        semaphore.acquireUninterruptibly();
    }

    public void unlock() {
        semaphore.release();
    }

    public String getNodeId() { return nodeId; }
}
```

### Related Concurrency Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Print FooBar Alternately | 1115 | Google | Lock ordering |
| H2O Generation | 1117 | Microsoft | Barrier lock |
| Print Zero Even Odd | 1116 | Amazon | State machine lock |
| FizzBuzz Multithreaded | 1195 | Google | Coordination |
| Design Bounded Blocking Queue | 1188 | Apple | Condition lock |
| The Dining Philosophers | 1226 | Google | Deadlock prevention |
| Web Crawler Multithreaded | 1242 | Google | Distributed crawling lock |
| Traffic Light Controlled Intersection | 1279 | Amazon | Distributed traffic control |

---

## Distributed Transactions → Database Problems

### Problem 1: Two-Phase Commit
**LC #**: None (System Design), related to 1242 (Web Crawler)
**Company Frequency**: Google (High), Apple (Medium)

```java
import java.util.concurrent.CompletableFuture;

class TwoPhaseCommit {
    static class Participant {
        String id;
        boolean prepared = false;
        boolean committed = false;

        public boolean prepare() {
            // Simulate local resource preparation
            prepared = true;
            return true;
        }

        public boolean commit() {
            if (prepared) {
                committed = true;
                return true;
            }
            return false;
        }

        public boolean abort() {
            prepared = false;
            committed = false;
            return true;
        }
    }

    private final List<Participant> participants = new ArrayList<>();

    public boolean executeTransaction() {
        // Phase 1: Prepare
        for (Participant p : participants) {
            if (!p.prepare()) {
                // Abort all
                for (Participant rollback : participants) {
                    rollback.abort();
                }
                return false;
            }
        }

        // Phase 2: Commit
        for (Participant p : participants) {
            if (!p.commit()) {
                return false; // needs recovery
            }
        }
        return true;
    }
}
```

### Problem 2: SAGA Pattern (Choreography)
**LC #**: 207 (Course Schedule), 210 (Course Schedule II)

```java
class SagaTransaction {
    static class SagaStep {
        String name;
        Runnable execute;
        Runnable compensate;

        SagaStep(String name, Runnable execute, Runnable compensate) {
            this.name = name;
            this.execute = execute;
            this.compensate = compensate;
        }
    }

    private final Stack<SagaStep> executedSteps = new Stack<>();

    public void execute(SagaStep step) {
        step.execute.run();
        executedSteps.push(step);
    }

    public void compensate() {
        while (!executedSteps.isEmpty()) {
            executedSteps.pop().compensate.run();
        }
    }
}
```

### Related Transaction Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Course Schedule II | 210 | Google | Dependency ordering |
| Alien Dictionary | 269 | Meta | Total order |
| Task Scheduler | 621 | Meta | Distributed scheduling |
| Minimum Number of Refueling Stops | 871 | Google | Resource allocation |
| Maximum Profit in Job Scheduling | 1235 | Google | Job scheduling |
| Meeting Rooms II | 253 | Google, Amazon | Resource conflict |
| Employee Free Time | 759 | Microsoft | Scheduling gaps |

---

## Distributed ID Generation → Math Problems

### Problem 1: Snowflake ID Generator
**LC #**: None (custom), related to 535 (Encode and Decode TinyURL)
**Company Frequency**: Twitter/Snowflake pattern (High everywhere)

```java
class SnowflakeIdGenerator {
    private final long datacenterId;
    private final long machineId;
    private final long epoch = 1609459200000L; // 2021-01-01
    private long sequence = 0L;
    private long lastTimestamp = -1L;

    public SnowflakeIdGenerator(long datacenterId, long machineId) {
        this.datacenterId = datacenterId;
        this.machineId = machineId;
    }

    public synchronized long nextId() {
        long timestamp = System.currentTimeMillis();
        if (timestamp < lastTimestamp) {
            throw new RuntimeException("Clock moved backwards");
        }
        if (timestamp == lastTimestamp) {
            sequence = (sequence + 1) & 4095;
            if (sequence == 0) {
                timestamp = waitNextMillis(timestamp);
            }
        } else {
            sequence = 0;
        }
        lastTimestamp = timestamp;

        return ((timestamp - epoch) << 22)
             | (datacenterId << 17)
             | (machineId << 12)
             | sequence;
    }

    private long waitNextMillis(long lastTimestamp) {
        long timestamp = System.currentTimeMillis();
        while (timestamp <= lastTimestamp) {
            timestamp = System.currentTimeMillis();
        }
        return timestamp;
    }
}
```

### Related ID Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Encode and Decode TinyURL | 535 | Meta | ID compression |
| Fraction to Recurring Decimal | 166 | Google | Unique mapping |
| Integer to Roman | 12 | Microsoft | ID formatting |
| Unique Email Addresses | 929 | Google | ID normalization |
| Design Tic-Tac-Toe | 348 | Microsoft | State ID |
| Design Excel Sum Formula | 631 | Google | Formula ID |

---

## Gossip Protocols → Graph Propagation

### Problem 1: Information Propagation (BFS)
**LC #**: 994 (Rotting Oranges), 286 (Walls and Gates)
**Company Frequency**: Amazon (Medium), Google (Medium)

```java
import java.util.*;

class GossipPropagation {
    public int propagate(int[][] grid, int startX, int startY) {
        int m = grid.length, n = grid[0].length;
        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{startX, startY});
        grid[startX][startY] = 0; // mark as infected
        int steps = 0;

        int[][] dirs = {{-1,0}, {1,0}, {0,-1}, {0,1}};
        while (!queue.isEmpty()) {
            int size = queue.size();
            for (int i = 0; i < size; i++) {
                int[] curr = queue.poll();
                for (int[] d : dirs) {
                    int nx = curr[0] + d[0], ny = curr[1] + d[1];
                    if (nx >= 0 && nx < m && ny >= 0 && ny < n && grid[nx][ny] == 1) {
                        grid[nx][ny] = 0;
                        queue.offer(new int[]{nx, ny});
                    }
                }
            }
            steps++;
        }
        return steps;
    }
}
```

### Problem 2: Anti-Entropy (Merkle Tree Sync)
**LC #**: 572 (Subtree of Another Tree), 100 (Same Tree)

```java
class MerkleTreeSync {
    static class TreeNode {
        int val;
        String hash;
        TreeNode left, right;
        TreeNode(int val) { this.val = val; }
    }

    public String computeHash(TreeNode node) {
        if (node == null) return "";
        String leftHash = computeHash(node.left);
        String rightHash = computeHash(node.right);
        node.hash = hash(node.val + leftHash + rightHash);
        return node.hash;
    }

    public List<String> findDifferences(TreeNode a, TreeNode b) {
        List<String> diffs = new ArrayList<>();
        if (a == null && b == null) return diffs;
        if (a == null || b == null || !a.hash.equals(b.hash)) {
            diffs.add("Difference at node " + (a != null ? a.val : b.val));
            diffs.addAll(findDifferences(a != null ? a.left : null, b != null ? b.left : null));
            diffs.addAll(findDifferences(a != null ? a.right : null, b != null ? b.right : null));
        }
        return diffs;
    }

    private String hash(String input) {
        return Integer.toHexString(input.hashCode());
    }
}
```

### Related Gossip Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Rotting Oranges | 994 | Amazon | Multi-source propagation |
| Walls and Gates | 286 | Google | BFS propagation |
| Subtree of Another Tree | 572 | Meta | Merkle tree comparison |
| Same Tree | 100 | Google | Tree sync |
| Network Delay Time | 743 | Google, Amazon | Gossip latency |
| Cheapest Flights Within K Stops | 787 | Google | Gossip with TTL |
| Minimum Time to Infect All | - | Google | Gossip full propagation |
| Time Needed to Inform All Employees | 1376 | Google | Hierarchical gossip |

---

## Time/Ordering → Sorting/Schedule Problems

### Problem 1: Lamport Clocks (Vector Clocks)
**LC #**: 56 (Merge Intervals), 252 (Meeting Rooms)
**Company Frequency**: Amazon (Medium), Google (Medium)

```java
class LamportClock {
    private int counter = 0;

    public int tick() {
        return ++counter;
    }

    public void update(int receivedTimestamp) {
        counter = Math.max(counter, receivedTimestamp) + 1;
    }

    public int getTime() { return counter; }
}

class VectorClock {
    private final int[] clock;

    public VectorClock(int numNodes) {
        this.clock = new int[numNodes];
    }

    public void tick(int nodeId) {
        clock[nodeId]++;
    }

    public void update(VectorClock other) {
        for (int i = 0; i < clock.length; i++) {
            clock[i] = Math.max(clock[i], other.clock[i]);
        }
    }

    public boolean happensBefore(VectorClock other) {
        boolean lessOrEqual = true;
        boolean strictLess = false;
        for (int i = 0; i < clock.length; i++) {
            if (clock[i] > other.clock[i]) lessOrEqual = false;
            if (clock[i] < other.clock[i]) strictLess = true;
        }
        return lessOrEqual && strictLess;
    }
}
```

### Related Ordering Problems
| Problem | # | Company | DS Concept |
|---------|---|---------|------------|
| Meeting Rooms II | 253 | Google | Interval ordering |
| Merge Intervals | 56 | Amazon | Multi-source ordering |
| Insert Interval | 57 | Google | Clock sync ordering |
| Non-overlapping Intervals | 435 | Google | Conflict resolution |
| Minimum Interval | 1851 | Google | Time ordering |
| Car Pooling | 1094 | Google | Resource timeline |
| My Calendar I | 729 | Google | Lease-based scheduling |
| Employee Free Time | 759 | Microsoft | Global time ordering |

---

## Company-Specific Problem Frequencies

### Amazon Top DS-Related LC Problems
| Rank | Problem | # | How They Ask It |
|------|---------|---|----------------|
| 1 | LRU Cache | 146 | "Design an efficient cache for Amazon product data" |
| 2 | Design HashMap | 706 | "Design DynamoDB's partition mapping" |
| 3 | Number of Islands | 200 | "Count service instances after network partition" |
| 4 | Merge Intervals | 56 | "Merge DynamoDB repair intervals" |
| 5 | Course Schedule II | 210 | "Design build dependency system" |

### Google Top DS-Related LC Problems
| Rank | Problem | # | How They Ask It |
|------|---------|---|----------------|
| 1 | LFU Cache | 460 | "Design Google's cache eviction" |
| 2 | Time Based KV Store | 981 | "Design Spanner's version management" |
| 3 | Alien Dictionary | 269 | "Design clock synchronization ordering" |
| 4 | Redundant Connection | 684 | "Detect distributed system cycles" |
| 5 | Network Delay Time | 743 | "Compute gossip propagation time" |

### Meta Top DS-Related LC Problems
| Rank | Problem | # | How They Ask It |
|------|---------|---|----------------|
| 1 | Clone Graph | 133 | "Replicate Facebook's social graph" |
| 2 | Clone N-ary Tree | 1490 | "Data replication across regions" |
| 3 | LRU Cache | 146 | "Design TAO's cache layer" |
| 4 | Task Scheduler | 621 | "Schedule Spark jobs across cluster" |
| 5 | Binary Tree Right Side View | 199 | "Quorum view in distributed system" |

---

## Weekly Practice Plan

### Week 1: Cache & Concurrency (Daily ~3 problems)
- Day 1-2: LRU Cache (146), LFU Cache (460)
- Day 3-4: Thread coordination (1115, 1117, 1116)
- Day 5-7: Blocking queue (1188), Web crawler (1242)

### Week 2: Graph & Consensus (Daily ~3 problems)
- Day 1-2: Union-Find (547, 323, 684)
- Day 3-4: Topological sort (207, 210, 269)
- Day 5-7: Graph traversal (133, 743, 994)

### Week 3: Partitioning & Ordering (Daily ~3 problems)
- Day 1-2: Hash map design (706, 705, 380)
- Day 3-4: Range partitioning (715, 352, 981)
- Day 5-7: Interval ordering (56, 253, 57)

### Week 4: Advanced Patterns (Daily ~2 problems)
- Day 1-3: BFS/Queue (622, 1188, 621)
- Day 4-5: Sliding window (239, 862)
- Day 6-7: Mock interview with all patterns

---

## Quick Reference: Concept → Problem → Company

| Distributed Concept | Key LeetCode | Primary Companies |
|--------------------|-------------|------------------|
| Consistent Hashing | 706 Design HashMap | Amazon, Google |
| Leader Election | 310 Min Height Trees | Google |
| Quorum | 199 Right Side View | Meta |
| Heartbeat | 128 Longest Consecutive | Google, Meta |
| Vector Clock | 56 Merge Intervals | Amazon |
| Gossip | 994 Rotting Oranges | Amazon, Google |
| 2PC | 207 Course Schedule | Google |
| SAGA | 210 Course Schedule II | Google |
| LRU Eviction | 146 LRU Cache | Amazon, Google, Meta |
| Partition Rebalance | 305 Num Islands II | Google |
| CRDT | 572 Subtree of Another | Meta |
| Lamport Clock | 252 Meeting Rooms | Google |
| Snowflake ID | 535 TinyURL | Meta |
| Circuit Breaker | 684 Redundant Connection | Netflix |
| Write-ahead Log | 208 Implement Trie | Google |
| Shuffle | 384 Shuffle Array | Microsoft |
| Snapshot Isolation | 981 Time Based KV | Google |
| Paxos Proposal | 721 Accounts Merge | Meta |
| Raft Log Replication | 332 Reconstruct Itinerary | Uber |

---

> **Strategy**: For each LeetCode problem, ask yourself: "What distributed systems failure mode does this map to?" If you can answer this, you're ready for any DS interview.