# LeetCode Architecture Patterns Cheatsheet

> Architecture patterns and system design concepts that appear in LeetCode problems and technical interviews.

---

## Table of Contents

1. [Design Patterns in LeetCode](#1-design-patterns-in-leetcode)
2. [System Design Coding Problems](#2-system-design-coding-problems)
3. [Concurrency Patterns for Distributed Systems](#3-concurrency-patterns-for-distributed-systems)
4. [Data Structures for System Design](#4-data-structures-for-system-design)
5. [Real-World System Design Problems](#5-real-world-system-design-problems)

---

## 1. Design Patterns in LeetCode

### LRU Cache (LC 146)

**Pattern**: Least Recently Used (LRU) eviction policy using doubly-linked list + hashmap.

```java
class LRUCache {
    class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    private final int capacity;
    private final Map<Integer, Node> map = new HashMap<>();
    private final Node head = new Node(0, 0); // dummy
    private final Node tail = new Node(0, 0); // dummy

    public LRUCache(int capacity) {
        this.capacity = capacity;
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            node = new Node(key, value);
            map.put(key, node);
            addToHead(node);
            if (map.size() > capacity) {
                Node removed = removeTail();
                map.remove(removed.key);
            }
        }
    }

    private void addToHead(Node node) { /* insert after head */ }
    private void removeNode(Node node) { /* unlink */ }
    private void moveToHead(Node node) { removeNode(node); addToHead(node); }
    private Node removeTail() { Node node = tail.prev; removeNode(node); return node; }
}
```

**Architecture parallel**: Cache eviction policies in distributed caches (Redis, Memcached). LRU is the most common policy for CDN caches, database buffer pools, and CPU caches.

**Interview application**: When designing a caching layer, mention LRU vs LFU vs FIFO eviction and why you'd choose each.

### Design Twitter (LC 355)

**Pattern**: News feed system with follower graph, push/pull timeline mixing.

```java
class Twitter {
    private int timestamp = 0;
    private Map<Integer, User> users = new HashMap<>();

    class Tweet {
        int id;
        int time;
        Tweet next;
        Tweet(int id) { this.id = id; this.time = timestamp++; }
    }

    class User {
        int id;
        Set<Integer> followed;
        Tweet tweetHead;
        User(int id) { this.id = id; this.followed = new HashSet<>(); follow(id); }

        void follow(int userId) { followed.add(userId); }
        void unfollow(int userId) { followed.remove(userId); }
        void post(int tweetId) {
            Tweet t = new Tweet(tweetId);
            t.next = tweetHead;
            tweetHead = t;
        }
    }

    public void postTweet(int userId, int tweetId) {
        users.computeIfAbsent(userId, User::new).post(tweetId);
    }

    public List<Integer> getNewsFeed(int userId) {
        User user = users.get(userId);
        if (user == null) return List.of();
        PriorityQueue<Tweet> pq = new PriorityQueue<>((a, b) -> b.time - a.time);
        for (int followeeId : user.followed) {
            Tweet t = users.get(followeeId).tweetHead;
            if (t != null) pq.offer(t);
        }
        List<Integer> res = new ArrayList<>();
        while (!pq.isEmpty() && res.size() < 10) {
            Tweet t = pq.poll();
            res.add(t.id);
            if (t.next != null) pq.offer(t.next);
        }
        return res;
    }

    public void follow(int followerId, int followeeId) {
        users.computeIfAbsent(followerId, User::new);
        users.computeIfAbsent(followeeId, User::new);
        users.get(followerId).follow(followeeId);
    }

    public void unfollow(int followerId, int followeeId) {
        users.computeIfAbsent(followerId, User::new);
        users.computeIfAbsent(followeeId, User::new);
        users.get(followerId).unfollow(followeeId);
    }
}
```

**Architecture parallel**: News feed systems (Facebook, Twitter, Instagram) use push (fanout-on-write) and pull (fanout-on-read) strategies. This implementation is fanout-on-read (pull model).

**Interview application**: When designing a social feed, discuss fanout strategies — push for high-profile users (celebrity accounts), pull for regular users.

### Design Circular Queue (LC 622)

**Pattern**: Fixed-size circular buffer using array and two pointers.

```java
class MyCircularQueue {
    private int[] data;
    private int head, tail, size, capacity;

    public MyCircularQueue(int k) {
        this.capacity = k;
        this.data = new int[k];
        this.head = -1;
        this.tail = -1;
        this.size = 0;
    }

    public boolean enQueue(int value) {
        if (isFull()) return false;
        tail = (tail + 1) % capacity;
        data[tail] = value;
        if (head == -1) head = tail;
        size++;
        return true;
    }

    public boolean deQueue() {
        if (isEmpty()) return false;
        if (head == tail) { head = -1; tail = -1; }
        else head = (head + 1) % capacity;
        size--;
        return true;
    }

    public int Front() { return isEmpty() ? -1 : data[head]; }
    public int Rear() { return isEmpty() ? -1 : data[tail]; }
    public boolean isEmpty() { return size == 0; }
    public boolean isFull() { return size == capacity; }
}
```

**Architecture parallel**: Ring buffers in logging systems (Log4j 2 async logger), Kafka's in-memory buffer, audio/video streaming buffers.

### Rate Limiter (LC 359 — Logger Rate Limiter)

**Pattern**: Sliding window rate limiting using hashmap with timestamps.

```java
class Logger {
    private Map<String, Integer> cache = new HashMap<>();
    private static final int INTERVAL = 10;

    public boolean shouldPrintMessage(int timestamp, String message) {
        if (timestamp < cache.getOrDefault(message, -INTERVAL) + INTERVAL) {
            return false;
        }
        cache.put(message, timestamp);
        return true;
    }
}
```

**Architecture parallel**: API rate limiting in gateways (token bucket, sliding window, leaky bucket).

**Interview application**: Discuss token bucket (simple, bursty) vs sliding window (smoother, more accurate). Implementation differences for distributed rate limiting (Redis with Lua scripting).

### Design HashMap (LC 706)

**Pattern**: Hash table with collision handling (separate chaining or open addressing).

**Architecture parallel**: Consistent hashing in distributed caches, database sharding.

### Design Browser History (LC 1472)

**Pattern**: Dual-stack navigation with forward/backward support.

**Architecture parallel**: Command-pattern undo/redo in distributed systems. Event sourcing with snapshot/replay.

---

## 2. System Design Coding Problems

### Serialize and Deserialize Binary Tree (LC 297)

**Pattern**: Tree serialization for network transmission or storage.

```java
public String serialize(TreeNode root) {
    StringBuilder sb = new StringBuilder();
    serializeHelper(root, sb);
    return sb.toString();
}

private void serializeHelper(TreeNode node, StringBuilder sb) {
    if (node == null) {
        sb.append("null,");
        return;
    }
    sb.append(node.val).append(",");
    serializeHelper(node.left, sb);
    serializeHelper(node.right, sb);
}

public TreeNode deserialize(String data) {
    Queue<String> nodes = new LinkedList<>(Arrays.asList(data.split(",")));
    return deserializeHelper(nodes);
}
```

**Architecture parallel**: Data serialization (Protocol Buffers, Avro, Thrift) for storage or wire transfer. Schema evolution, forward/backward compatibility.

### Design Tic-Tac-Toe (LC 348)

**Pattern**: Efficient win detection using row/column/diagonal tracking.

```java
class TicTacToe {
    private int[] rows, cols;
    private int diagonal, antiDiagonal;
    private int n;

    public TicTacToe(int n) {
        this.n = n;
        rows = new int[n];
        cols = new int[n];
    }

    public int move(int row, int col, int player) {
        int add = player == 1 ? 1 : -1;
        rows[row] += add;
        cols[col] += add;
        if (row == col) diagonal += add;
        if (row + col == n - 1) antiDiagonal += add;
        if (Math.abs(rows[row]) == n || Math.abs(cols[col]) == n
            || Math.abs(diagonal) == n || Math.abs(antiDiagonal) == n)
            return player;
        return 0;
    }
}
```

**Architecture parallel**: Distributed game state management (multiplayer games), consistency checking in distributed systems.

### Design In-Memory File System (LC 588)

**Pattern**: Trie-like file system with mkdir, ls, addContentToFile, readContentFromFile.

**Architecture parallel**: Distributed file systems (GFS, HDFS). Path-based routing, directory service.

---

## 3. Concurrency Patterns for Distributed Systems

### Producer-Consumer

**LeetCode Problem**: Design Bounded Blocking Queue (LC 1188)

```java
class BoundedBlockingQueue {
    private Queue<Integer> queue = new LinkedList<>();
    private int capacity;
    private Lock lock = new ReentrantLock();
    private Condition notFull = lock.newCondition();
    private Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) { this.capacity = capacity; }

    public void enqueue(int element) throws InterruptedException {
        lock.lock();
        try {
            while (queue.size() == capacity) notFull.await();
            queue.offer(element);
            notEmpty.signal();
        } finally { lock.unlock(); }
    }

    public int dequeue() throws InterruptedException {
        lock.lock();
        try {
            while (queue.isEmpty()) notEmpty.await();
            int val = queue.poll();
            notFull.signal();
            return val;
        } finally { lock.unlock(); }
    }

    public int size() { lock.lock(); try { return queue.size(); } finally { lock.unlock(); } }
}
```

**Architecture parallel**: Message queue (Kafka, RabbitMQ). This is the core concurrency pattern in event-driven architecture.

### Read-Write Lock

**LeetCode Pattern**: Multiple readers, single writer synchronization.

```java
class ReadWriteLock {
    private int readers = 0;
    private int writers = 0;

    public synchronized void acquireRead() throws InterruptedException {
        while (writers > 0) wait();
        readers++;
    }

    public synchronized void releaseRead() {
        readers--;
        notifyAll();
    }

    public synchronized void acquireWrite() throws InterruptedException {
        while (readers > 0 || writers > 0) wait();
        writers = 1;
    }

    public synchronized void releaseWrite() {
        writers = 0;
        notifyAll();
    }
}
```

**Architecture parallel**: Database read replicas vs write master. Snapshot isolation (MVCC) in PostgreSQL.

### Dining Philosophers (LC 1226)

**Pattern**: Resource allocation without deadlock.

```java
class DiningPhilosophers {
    private ReentrantLock[] forks = new ReentrantLock[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) forks[i] = new ReentrantLock();
    }

    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork,
                           Runnable pickRightFork,
                           Runnable eat,
                           Runnable putLeftFork,
                           Runnable putRightFork) {
        int left = philosopher;
        int right = (philosopher + 1) % 5;
        if (philosopher % 2 == 0) {
            forks[left].lock();
            forks[right].lock();
        } else {
            forks[right].lock();
            forks[left].lock();
        }
        pickLeftFork.run(); pickRightFork.run();
        eat.run();
        putLeftFork.run(); putRightFork.run();
        forks[left].unlock(); forks[right].unlock();
    }
}
```

**Architecture parallel**: Distributed lock management (ZooKeeper, etcd). Deadlock detection in distributed transactions.

### Print FooBar Alternately (LC 1115)

**Pattern**: Thread coordination for ordered execution.

```java
class FooBar {
    private int n;
    private Semaphore fooS = new Semaphore(1);
    private Semaphore barS = new Semaphore(0);

    public FooBar(int n) { this.n = n; }

    public void foo(Runnable printFoo) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            fooS.acquire();
            printFoo.run();
            barS.release();
        }
    }

    public void bar(Runnable printBar) throws InterruptedException {
        for (int i = 0; i < n; i++) {
            barS.acquire();
            printBar.run();
            fooS.release();
        }
    }
}
```

**Architecture parallel**: Choreographed saga pattern. Event ordering guarantees (Kafka partition ordering).

---

## 4. Data Structures for System Design

### Trie (Prefix Tree) — LC 208

**Architecture parallel**: Autocomplete (Google Search), route matching in API gateways, IP routing tables.

### Union-Find (Disjoint Set) — LC 721

**Architecture parallel**: Social graph connectivity, cluster management, network partition detection.

### Binary Indexed Tree / Segment Tree — LC 307, 315

**Architecture parallel**: Time-series database indexing, range query optimization, fraud detection.

### Heap (Priority Queue) — LC 295, 347

**Architecture parallel**: Task scheduling (job queues), stream processing (top K), real-time ranking.

### Bloom Filter — conceptual (not on LeetCode)

**Architecture parallel**: Cache filtering (avoid cache misses), spam detection, medium.com recommendation dedup.

**Key insight**: Bloom filter says "definitely not in set" (no false negative) or "probably in set" (false positive). Classic interview topic for scalable system design.

### Consistent Hashing — conceptual (not on LeetCode)

**Architecture parallel**: Distributed caching (Redis Cluster), database sharding, load balancing.

**Key points**: Minimizes remapping when nodes change. Virtual nodes for load distribution.

---

## 5. Real-World System Design Problems

### Design a URL Shortener (TinyURL)

**LeetCode**: LC 535 (Encode and Decode TinyURL)

**Key architecture decisions:**
- **Hash function**: Base62 encoding (62^7 = 3.5 trillion URLs) vs hash (MD5, SHA256) with collision handling
- **Storage**: Key-value store for <shortcode, longURL> mapping
- **Redirect**: 301 (permanent) vs 302 (temporary) redirect
- **Analytics**: Event-driven tracking for click analytics

### Design an Autocomplete System (Search Suggestions)

**LeetCode**: LC 642 (Design Search Autocomplete System)

**Key architecture decisions:**
- **Trie**: Store prefixes with frequency
- **Top-k**: Maintain top suggestions per prefix; update on query
- **Distribution**: Split trie across servers by prefix range or use consistent hashing
- **Personalization**: Per-user trie or global trie with user-specific re-ranking

### Design an Online Election System

**LeetCode**: LC 911 (Online Election)

**Key architecture decisions:**
- **Query optimization**: Precompute leader at each time interval
- **Real-time**: Stream processing for vote counting
- **Consistency**: Strong consistency for election results; eventually consistent for live updates

### Design a File System

**LeetCode**: LC 588 (Design In-Memory File System)

**Key architecture decisions:**
- **Trie structure**: Efficient path traversal
- **Content storage**: Lazy loading, chunked storage for large files
- **Replication**: GFS-style: master for metadata, chunkservers for data

---

## LeetCode-to-Architecture Mapping Cheat Sheet

| LeetCode Problem | Architecture Concept | Apply When... |
|-----------------|---------------------|---------------|
| LRU Cache (146) | Cache eviction policy | Designing caching layer |
| Twitter (355) | News feed, push/pull | Social feed design |
| Rate Limiter (359) | API throttling | API gateway design |
| Serialize Tree (297) | Data serialization | gRPC/Protobuf design |
| Circular Queue (622) | Ring buffer | Logging, streaming |
| Trie (208) | Prefix search, routing | Autocomplete, API routing |
| Union-Find (721) | Cluster management | Distributed systems design |
| Bounded Queue (1188) | Message queue | Event-driven architecture |
| ReadWrite Lock | MVCC, read replicas | Database architecture |
| Dining Philosophers (1226) | Distributed locks | Consensus, coordination |
| Tic-Tac-Toe (348) | State validation | Game design, consensus |
| In-Memory FS (588) | Distributed file system | Storage design |
| Autocomplete (642) | Trie + top-k ranking | Search, recommendations |
| Online Election (911) | Real-time aggregation | Stream processing |

---

## Practice Blueprint for Staff+ Interviews

### Week 1-2: Foundation
- Complete the 20 lab patterns in this guide
- Implement each from scratch
- Time yourself (30 minutes per problem)

### Week 3-4: System Context
- For each LeetCode problem, write a 5-minute explanation of how it maps to a real distributed system
- Practice discussing trade-offs (space vs time, consistency vs availability)

### Week 5-6: Mock Interviews
- Combine LeetCode pattern + system design in one session
- Example: "Design LRU Cache" → "Now scale it across 10 servers"
- Practice writing clean code while discussing architecture

### Key Reminders for Staff+
- LeetCode coding at L6+ is about demonstrating algorithmic depth, not just AC (Accepted)
- Always discuss trade-offs and alternative approaches
- Connect your solution to real-world distributed systems patterns
- Code quality matters: clean abstractions, error handling, testability

---

*This cheatsheet bridges LeetCode patterns with real-world distributed system architecture. Use it to practice the translation between algorithmic problems and system design discussions.*
