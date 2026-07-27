# LeetCode Pattern Cheat Sheet — System Design Edition

> Map system design concepts to LeetCode problems with Java code templates.
> Each pattern includes: concept mapping, LeetCode references, Java template, company frequency.

---

## Table of Contents

1. [Rate Limiting](#1-rate-limiting)
2. [Caching](#2-caching)
3. [Social Graph](#3-social-graph)
4. [Web Crawler](#4-web-crawler)
5. [Search Engine](#5-search-engine)
6. [Key-Value Store](#6-key-value-store)
7. [Message Queue](#7-message-queue)
8. [Distributed Locking](#8-distributed-locking)
9. [Consistent Hashing](#9-consistent-hashing)
10. [Bloom Filters](#10-bloom-filters)
11. [Load Balancing](#11-load-balancing)
12. [Database Sharding](#12-database-sharding)
13. [Leader Election](#13-leader-election)
14. [Stream Processing](#14-stream-processing)
15. [Circuit Breaker](#15-circuit-breaker)

---

## 1. Rate Limiting

### Concept Mapping
- **DDoS protection**: Token bucket, leaky bucket, sliding window
- **API quotas**: Per-user, per-endpoint rate limits
- **Backpressure**: Queue-based throttling

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 359 - Logger Rate Limiter | Easy | Google, Amazon, Microsoft | Sliding window |
| LC 362 - Hit Counter (Design Hit Counter) | Medium | Google, Amazon, Apple | Rolling window |
| Custom: RateLimiter (LeetCode Discuss) | Medium | Uber, Stripe | Token bucket |

### Java Template: Token Bucket Rate Limiter

```java
class TokenBucket {
    private final long capacity;
    private final double refillRate; // tokens per second
    private double tokens;
    private long lastRefillTimestamp;

    public TokenBucket(long capacity, double refillRate) {
        this.capacity = capacity;
        this.refillRate = refillRate;
        this.tokens = capacity;
        this.lastRefillTimestamp = System.currentTimeMillis();
    }

    public synchronized boolean tryConsume(int numTokens) {
        refill();
        if (tokens >= numTokens) {
            tokens -= numTokens;
            return true;
        }
        return false;
    }

    private void refill() {
        long now = System.currentTimeMillis();
        double elapsed = (now - lastRefillTimestamp) / 1000.0;
        tokens = Math.min(capacity, tokens + elapsed * refillRate);
        lastRefillTimestamp = now;
    }
}
```

### Java Template: Sliding Window Log

```java
class SlidingWindowRateLimiter {
    private final int maxRequests;
    private final long windowSizeInMillis;
    private final Deque<Long> timestamps = new LinkedList<>();

    public SlidingWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public boolean allowRequest() {
        long now = System.currentTimeMillis();
        synchronized (timestamps) {
            while (!timestamps.isEmpty() && timestamps.peekFirst() < now - windowSizeInMillis) {
                timestamps.pollFirst();
            }
            if (timestamps.size() < maxRequests) {
                timestamps.addLast(now);
                return true;
            }
            return false;
        }
    }
}
```

### Distributed Rate Limiting (Redis-based)

```java
class RedisRateLimiter {
    private final Jedis jedis;
    private final String keyPrefix;

    public RedisRateLimiter(Jedis jedis, String keyPrefix) {
        this.jedis = jedis;
        this.keyPrefix = keyPrefix;
    }

    public boolean allow(String userId, int maxRequests, long windowSeconds) {
        String key = keyPrefix + ":" + userId;
        long now = System.currentTimeMillis() / 1000;
        long windowStart = now - windowSeconds;

        String luaScript = 
            "redis.call('ZREMRANGEBYSCORE', KEYS[1], 0, ARGV[1]) " +
            "redis.call('ZADD', KEYS[1], ARGV[2], ARGV[2]) " +
            "redis.call('EXPIRE', KEYS[1], ARGV[3]) " +
            "return redis.call('ZCARD', KEYS[1])";

        long count = (long) jedis.eval(luaScript, 1, key,
            String.valueOf(windowStart), String.valueOf(now), String.valueOf(windowSeconds));
        return count <= maxRequests;
    }
}
```

---

## 2. Caching

### Concept Mapping
- **Cache eviction**: LRU, LFU, FIFO, RR (clock)
- **Cache patterns**: Cache-aside, read-through, write-through, write-behind
- **Distributed cache**: Redis, Memcached, consistent hashing

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 146 - LRU Cache | Medium | Google, Amazon, Meta, Microsoft | LRU eviction |
| LC 460 - LFU Cache | Hard | Amazon, Google, Apple | LFU eviction |
| LC 6040 - Maximum Number of Consecutive Values | Hard | — | Cache optimization |
| Custom: File Cache with TTL | — | — | TTL-based caching |

### Java Template: LRU Cache

```java
class LRUCache {
    private final int capacity;
    private final Map<Integer, Node> map;
    private final DoublyLinkedList list;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.map = new HashMap<>();
        this.list = new DoublyLinkedList();
    }

    public int get(int key) {
        Node node = map.get(key);
        if (node == null) return -1;
        list.moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = map.get(key);
        if (node != null) {
            node.value = value;
            list.moveToHead(node);
        } else {
            if (map.size() == capacity) {
                Node tail = list.removeTail();
                map.remove(tail.key);
            }
            Node newNode = new Node(key, value);
            list.addToHead(newNode);
            map.put(key, newNode);
        }
    }

    static class Node {
        int key, value;
        Node prev, next;
        Node(int key, int value) { this.key = key; this.value = value; }
    }

    static class DoublyLinkedList {
        Node head, tail;
        DoublyLinkedList() { head = new Node(0,0); tail = new Node(0,0); head.next = tail; tail.prev = head; }

        void addToHead(Node node) {
            node.next = head.next; node.prev = head;
            head.next.prev = node; head.next = node;
        }

        void moveToHead(Node node) {
            remove(node); addToHead(node);
        }

        void remove(Node node) {
            node.prev.next = node.next;
            node.next.prev = node.prev;
        }

        Node removeTail() {
            Node node = tail.prev;
            remove(node);
            return node;
        }
    }
}
```

### Java Template: LFU Cache

```java
class LFUCache {
    private int minFreq;
    private final int capacity;
    private final Map<Integer, Node> keyMap;
    private final Map<Integer, LinkedHashSet<Node>> freqMap;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.minFreq = 0;
        this.keyMap = new HashMap<>();
        this.freqMap = new HashMap<>();
    }

    public int get(int key) {
        Node node = keyMap.get(key);
        if (node == null) return -1;
        updateFrequency(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;
        Node node = keyMap.get(key);
        if (node != null) {
            node.value = value;
            updateFrequency(node);
        } else {
            if (keyMap.size() == capacity) {
                evict();
            }
            Node newNode = new Node(key, value);
            keyMap.put(key, newNode);
            addToFrequency(newNode, 1);
            minFreq = 1;
        }
    }

    private void updateFrequency(Node node) {
        removeFromFrequency(node);
        addToFrequency(node, node.freq + 1);
    }

    private void addToFrequency(Node node, int freq) {
        node.freq = freq;
        freqMap.computeIfAbsent(freq, k -> new LinkedHashSet<>()).add(node);
    }

    private void removeFromFrequency(Node node) {
        LinkedHashSet<Node> set = freqMap.get(node.freq);
        set.remove(node);
        if (set.isEmpty() && node.freq == minFreq) minFreq++;
    }

    private void evict() {
        LinkedHashSet<Node> set = freqMap.get(minFreq);
        Node evict = set.iterator().next();
        set.remove(evict);
        keyMap.remove(evict.key);
    }

    static class Node {
        int key, value, freq;
        Node(int key, int value) { this.key = key; this.value = value; this.freq = 0; }
    }
}
```

---

## 3. Social Graph

### Concept Mapping
- **Friend recommendations**: Graph BFS/DFS, mutual friends
- **Social feed**: Graph traversal for relevant content
- **Connection paths**: Shortest path in unweighted graph

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 133 - Clone Graph | Medium | Google, Amazon, Meta, Microsoft | Graph traversal |
| LC 200 - Number of Islands | Medium | Amazon, Google, Meta | Connected components |
| LC 547 - Number of Provinces | Medium | Google, Amazon, Microsoft | Friend circles |
| LC 684 - Redundant Connection | Medium | — | Graph cycle detection |
| LC 207 - Course Schedule | Medium | Google, Amazon, Meta | Topological sort |
| LC 399 - Evaluate Division | Medium | Google, Amazon, Apple | Graph weight computation |

### Java Template: Graph Traversal (Clone Graph)

```java
class Solution {
    private Map<Node, Node> visited = new HashMap<>();

    public Node cloneGraph(Node node) {
        if (node == null) return null;
        if (visited.containsKey(node)) return visited.get(node);

        Node clone = new Node(node.val);
        visited.put(node, clone);

        for (Node neighbor : node.neighbors) {
            clone.neighbors.add(cloneGraph(neighbor));
        }
        return clone;
    }
}
```

### Java Template: Friend Circles (Union-Find)

```java
class UnionFind {
    private int[] parent;
    private int count;

    public UnionFind(int n) {
        parent = new int[n];
        count = n;
        for (int i = 0; i < n; i++) parent[i] = i;
    }

    public int find(int x) {
        while (parent[x] != x) {
            parent[x] = parent[parent[x]]; // path compression
            x = parent[x];
        }
        return x;
    }

    public void union(int x, int y) {
        int rootX = find(x), rootY = find(y);
        if (rootX != rootY) {
            parent[rootX] = rootY;
            count--;
        }
    }

    public int getCount() { return count; }
}

class Solution {
    public int findCircleNum(int[][] isConnected) {
        int n = isConnected.length;
        UnionFind uf = new UnionFind(n);
        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                if (isConnected[i][j] == 1) uf.union(i, j);
            }
        }
        return uf.getCount();
    }
}
```

---

## 4. Web Crawler

### Concept Mapping
- **URL frontier**: BFS, priority queue, politeness
- **HTML parsing**: DOM tree, link extraction
- **De-duplication**: Bloom filter, hash set, URL normalization

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 1236 - Web Crawler | Medium | Google, Amazon, Dropbox | BFS/DFS crawl |
| LC 1462 - Course Schedule IV | Medium | — | Graph dependencies |
| Custom: Multithreaded Web Crawler | Hard | Google, Amazon | Concurrent crawling |

### Java Template: Web Crawler (BFS)

```java
class WebCrawler {
    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        Set<String> visited = new HashSet<>();
        Queue<String> queue = new LinkedList<>();
        String hostname = getHostname(startUrl);

        queue.offer(startUrl);
        visited.add(startUrl);

        while (!queue.isEmpty()) {
            String url = queue.poll();
            for (String nextUrl : htmlParser.getUrls(url)) {
                if (getHostname(nextUrl).equals(hostname) && !visited.contains(nextUrl)) {
                    visited.add(nextUrl);
                    queue.offer(nextUrl);
                }
            }
        }
        return new ArrayList<>(visited);
    }

    private String getHostname(String url) {
        return url.split("/")[2]; // "http://example.com/a" -> "example.com"
    }
}
```

### Java Template: Multithreaded Web Crawler

```java
class MultithreadedWebCrawler {
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    private final Set<String> visited = ConcurrentHashMap.newKeySet();
    private final Queue<Future<List<String>>> futures = new LinkedList<>();

    public List<String> crawl(String startUrl, HtmlParser htmlParser) {
        visited.add(startUrl);
        futures.add(executor.submit(() -> htmlParser.getUrls(startUrl)));

        while (!futures.isEmpty()) {
            Future<List<String>> future = futures.poll();
            try {
                for (String url : future.get()) {
                    if (visited.add(url)) {
                        String finalUrl = url;
                        futures.add(executor.submit(() -> htmlParser.getUrls(finalUrl)));
                    }
                }
            } catch (Exception e) { /* handle */ }
        }
        return new ArrayList<>(visited);
    }
}
```

---

## 5. Search Engine

### Concept Mapping
- **Inverted index**: Map term → postings list
- **Trie (prefix tree)**: Auto-complete, spell checker
- **Ranking**: TF-IDF, PageRank, BM25
- **Fuzzy search**: Edit distance, Levenshtein

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 208 - Implement Trie | Medium | Google, Amazon, Meta, Microsoft | Prefix tree |
| LC 212 - Word Search II | Hard | Google, Amazon, Apple | Trie + backtracking |
| LC 211 - Design Add and Search Words | Medium | Google, Amazon, Meta | Trie with wildcards |
| LC 642 - Design Search Autocomplete | Hard | Google, Amazon, Meta | Autocomplete system |
| LC 425 - Word Squares | Hard | — | Trie-based construction |
| LC 745 - Prefix and Suffix Search | Hard | Microsoft | Weighted word filtering |

### Java Template: Trie (Prefix Tree)

```java
class TrieNode {
    TrieNode[] children = new TrieNode[26];
    boolean isWord;
}

class Trie {
    private TrieNode root = new TrieNode();

    public void insert(String word) {
        TrieNode node = root;
        for (char c : word.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) node.children[idx] = new TrieNode();
            node = node.children[idx];
        }
        node.isWord = true;
    }

    public boolean search(String word) {
        TrieNode node = searchPrefix(word);
        return node != null && node.isWord;
    }

    public boolean startsWith(String prefix) {
        return searchPrefix(prefix) != null;
    }

    private TrieNode searchPrefix(String s) {
        TrieNode node = root;
        for (char c : s.toCharArray()) {
            int idx = c - 'a';
            if (node.children[idx] == null) return null;
            node = node.children[idx];
        }
        return node;
    }
}
```

### Java Template: Word Search II (Trie + Backtracking)

```java
class Solution {
    public List<String> findWords(char[][] board, String[] words) {
        TrieNode root = buildTrie(words);
        Set<String> result = new HashSet<>();
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[0].length; j++) {
                dfs(board, i, j, root, result);
            }
        }
        return new ArrayList<>(result);
    }

    private void dfs(char[][] board, int i, int j, TrieNode node, Set<String> result) {
        if (i < 0 || i >= board.length || j < 0 || j >= board[0].length) return;
        char c = board[i][j];
        if (c == '#' || node.children[c - 'a'] == null) return;

        node = node.children[c - 'a'];
        if (node.word != null) result.add(node.word);

        board[i][j] = '#';
        dfs(board, i+1, j, node, result);
        dfs(board, i-1, j, node, result);
        dfs(board, i, j+1, node, result);
        dfs(board, i, j-1, node, result);
        board[i][j] = c;
    }

    private TrieNode buildTrie(String[] words) {
        TrieNode root = new TrieNode();
        for (String w : words) {
            TrieNode cur = root;
            for (char c : w.toCharArray()) {
                int idx = c - 'a';
                if (cur.children[idx] == null) cur.children[idx] = new TrieNode();
                cur = cur.children[idx];
            }
            cur.word = w;
        }
        return root;
    }

    static class TrieNode {
        TrieNode[] children = new TrieNode[26];
        String word;
    }
}
```

---

## 6. Key-Value Store

### Concept Mapping
- **HashMap design**: Hashing, collision resolution, dynamic resizing
- **LSM tree**: SSTables, MemTable, compaction
- **Consistent hashing**: Virtual nodes, ring distribution
- **Concurrent maps**: Striped locks, lock-free, CAS

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 706 - Design HashMap | Easy | Google, Amazon, Meta | Hash collision |
| LC 1396 - Design Underground System | Medium | Uber, Google, Amazon | Key-value operations |
| LC 981 - Time Based Key-Value Store | Medium | Google, Amazon, Meta | Versioned storage |
| LC 1628 - Design an Expression Tree | Medium | — | Custom data structure |

### Java Template: Design HashMap

```java
class MyHashMap {
    private static final int SIZE = 10000;
    private Entry[] buckets;

    public MyHashMap() { buckets = new Entry[SIZE]; }

    public void put(int key, int value) {
        int idx = getIndex(key);
        Entry entry = buckets[idx];
        while (entry != null) {
            if (entry.key == key) { entry.value = value; return; }
            entry = entry.next;
        }
        Entry newEntry = new Entry(key, value);
        newEntry.next = buckets[idx];
        buckets[idx] = newEntry;
    }

    public int get(int key) {
        int idx = getIndex(key);
        Entry entry = buckets[idx];
        while (entry != null) {
            if (entry.key == key) return entry.value;
            entry = entry.next;
        }
        return -1;
    }

    public void remove(int key) {
        int idx = getIndex(key);
        Entry prev = null, cur = buckets[idx];
        while (cur != null) {
            if (cur.key == key) {
                if (prev == null) buckets[idx] = cur.next;
                else prev.next = cur.next;
                return;
            }
            prev = cur;
            cur = cur.next;
        }
    }

    private int getIndex(int key) { return Integer.hashCode(key) % SIZE; }

    static class Entry {
        int key, value;
        Entry next;
        Entry(int key, int value) { this.key = key; this.value = value; }
    }
}
```

---

## 7. Message Queue

### Concept Mapping
- **Producer-consumer**: Blocking queue, bounded buffer
- **Pub-sub**: Topic-based routing, fanout
- **Exactly-once processing**: Idempotency, deduplication
- **Backpressure**: Bounded queues, rejects

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 1188 - Design Bounded Blocking Queue | Medium | Google, Amazon, Microsoft | Producer-consumer |
| LC 1597 - Build Binary Expression Tree | Hard | — | Expression tree |
| LC 1242 - Web Crawler Multithreaded | Medium | Google, Amazon | Work queue |
| Custom: Kafka-like Message Queue | Hard | Uber, Confluent | Distributed messaging |

### Java Template: Bounded Blocking Queue

```java
class BoundedBlockingQueue {
    private final Queue<Integer> queue;
    private final int capacity;
    private final Lock lock = new ReentrantLock();
    private final Condition notFull = lock.newCondition();
    private final Condition notEmpty = lock.newCondition();

    public BoundedBlockingQueue(int capacity) {
        this.capacity = capacity;
        this.queue = new LinkedList<>();
    }

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

---

## 8. Distributed Locking

### Concept Mapping
- **Mutex**: Inter-thread mutual exclusion
- **Read-write lock**: Concurrent reads, exclusive writes
- **Distributed lock**: Redis Redlock, ZooKeeper, ETCD
- **Deadlock prevention**: Lock ordering, timeout

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 1117 - Building H2O | Medium | — | Thread barrier |
| LC 1115 - Print FooBar Alternately | Medium | Google, Apple | Inter-thread signaling |
| LC 1114 - Print in Order | Easy | — | Thread ordering |
| LC 1195 - Fizz Buzz Multithreaded | Medium | — | Thread synchronization |
| LC 1226 - Dining Philosophers | Medium | Google, Microsoft | Deadlock prevention |

### Java Template: Dining Philosophers (Deadlock-Free)

```java
class DiningPhilosophers {
    private final ReentrantLock[] forks = new ReentrantLock[5];

    public DiningPhilosophers() {
        for (int i = 0; i < 5; i++) forks[i] = new ReentrantLock();
    }

    public void wantsToEat(int philosopher,
                           Runnable pickLeftFork, Runnable pickRightFork,
                           Runnable eat, Runnable putLeftFork, Runnable putRightFork) {
        int left = philosopher;
        int right = (philosopher + 1) % 5;

        // To prevent deadlock: lower-numbered fork first for philosopher 4
        if (philosopher == 4) {
            forks[right].lock(); pickRightFork.run();
            forks[left].lock();  pickLeftFork.run();
        } else {
            forks[left].lock();  pickLeftFork.run();
            forks[right].lock(); pickRightFork.run();
        }

        eat.run();

        putRightFork.run(); forks[right].unlock();
        putLeftFork.run();  forks[left].unlock();
    }
}
```

---

## 9. Consistent Hashing

### Concept Mapping
- **Ring distribution**: Hash ring, virtual nodes
- **Replica placement**: N-way replication
- **Load balancing**: Even distribution, hot spot mitigation

### LeetCode/Discuss Problems
| Problem | Company Freq | Concept |
|---------|-------------|---------|
| (Discuss) Implement Consistent Hash Ring | Google, Amazon, Meta | Ring design |
| (Discuss) Load Balancer with Sharding | Uber, Stripe | Virtual nodes |

### Java Template: Consistent Hash Ring

```java
class ConsistentHashRing {
    private final int virtualNodes;
    private final TreeMap<Integer, String> ring = new TreeMap<>();

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            String key = node + ":" + i;
            ring.put(key.hashCode(), node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            String key = node + ":" + i;
            ring.remove(key.hashCode());
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        int hash = key.hashCode();
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
        if (entry == null) entry = ring.firstEntry();
        return entry.getValue();
    }
}
```

---

## 10. Bloom Filters

### Concept Mapping
- **Set membership**: Probabilistic, no false negatives
- **Cache optimization**: Prevent cache stampede
- **De-duplication**: Web crawler URL tracking

### LeetCode/Discuss Problems
| Problem | Company Freq | Concept |
|---------|-------------|---------|
| (Discuss) Implement Bloom Filter | Google, Amazon, Meta, Uber | Probabilistic data structure |
| (Design) Web Crawler Dedup | Google, Dropbox | URL de-duplication |

### Java Template: Bloom Filter

```java
class BloomFilter {
    private final BitSet bitSet;
    private final int size;
    private final int[] hashSeeds;

    public BloomFilter(int size, int numHashes) {
        this.size = size;
        this.bitSet = new BitSet(size);
        this.hashSeeds = new int[numHashes];
        for (int i = 0; i < numHashes; i++) hashSeeds[i] = i * 31 + 7;
    }

    public void add(String value) {
        for (int seed : hashSeeds) {
            int hash = hash(value, seed) % size;
            bitSet.set(Math.abs(hash));
        }
    }

    public boolean mightContain(String value) {
        for (int seed : hashSeeds) {
            int hash = hash(value, seed) % size;
            if (!bitSet.get(Math.abs(hash))) return false;
        }
        return true;
    }

    private int hash(String value, int seed) {
        int result = value.hashCode() ^ seed;
        // Mixing function to reduce collision
        result = result ^ (result >>> 16);
        result *= 0x45d9f3b;
        result = result ^ (result >>> 16);
        return result;
    }
}
```

---

## 11. Load Balancing

### Concept Mapping
- **Round robin**: Sequential distribution
- **Least connections**: Send to least loaded server
- **IP hash**: Session persistence
- **Weighted distribution**: Capacity-aware routing

### LeetCode/Discuss Problems
| Problem | Company Freq | Concept |
|---------|-------------|---------|
| (Discuss) Design Load Balancer | Google, Amazon | Algorithm design |
| (Concept) Rate Limiter vs Load Balancer | All | Traffic management |

### Java Template: Weighted Round Robin Load Balancer

```java
class WeightedRoundRobin {
    private final List<Server> servers;
    private int current;
    private int currentWeight;

    public WeightedRoundRobin(List<Server> servers) {
        this.servers = servers;
        this.current = 0;
        this.currentWeight = 0;
    }

    public Server next() {
        while (true) {
            for (Server s : servers) {
                if (s.weight >= currentWeight) {
                    currentWeight--;
                    return s;
                }
            }
            currentWeight--;
            if (currentWeight <= 0) currentWeight = servers.stream().mapToInt(s -> s.weight).max().orElse(1);
        }
    }

    static class Server {
        String host;
        int weight;
        Server(String host, int weight) { this.host = host; this.weight = weight; }
    }
}
```

---

## 12. Database Sharding

### Concept Mapping
- **Range sharding**: Partition by key range (even keys, potential hotspots)
- **Hash sharding**: Hash key % N, better distribution
- **Directory-based**: Lookup service for key-to-shard mapping

### LeetCode/Discuss Problems
| Problem | Company Freq | Concept |
|---------|-------------|---------|
| (Design) Sharded Key-Value Store | Amazon, Google | Range vs hash sharding |
| LC 1396 - Design Underground System | Uber, Google | Partition by time/user |

---

## 13. Leader Election

### Concept Mapping
- **Raft**: Term-based leader election, log replication
- **Paxos**: Multi-phase consensus
- **ZooKeeper/ETCD**: Coordination services

### LeetCode/Discuss Problems
| Problem | Company Freq | Concept |
|---------|-------------|---------|
| (Discuss) Implement Leader Election | Google, Microsoft | Raft, Paxos, Zab |
| (Concept) Distributed Consensus | All | CAP theorem |

---

## 14. Stream Processing

### Concept Mapping
- **Window operations**: Tumbling, sliding, session windows
- **Aggregation**: Count, sum, min, max over windows
- **Watermarks**: Handling late events

### LeetCode References
| Problem | Difficulty | Company Freq | Concept |
|---------|-----------|-------------|---------|
| LC 295 - Find Median from Data Stream | Hard | Google, Amazon, Meta | Sliding median |
| LC 346 - Moving Average from Data Stream | Easy | Google, Microsoft | Sliding window avg |
| LC 703 - Kth Largest Element in a Stream | Easy | Google, Amazon | Stream processing |
| LC 480 - Sliding Window Median | Hard | Google, Amazon | Complex window |

### Java Template: Moving Average from Data Stream

```java
class MovingAverage {
    private final int size;
    private final Queue<Integer> window;
    private double sum;

    public MovingAverage(int size) {
        this.size = size;
        this.window = new LinkedList<>();
    }

    public double next(int val) {
        window.offer(val);
        sum += val;
        if (window.size() > size) {
            sum -= window.poll();
        }
        return sum / window.size();
    }
}
```

---

## 15. Circuit Breaker

### Concept Mapping
- **State machine**: CLOSED → OPEN → HALF_OPEN → CLOSED
- **Failure threshold**: Count-based, rate-based
- **Fallback**: Degraded response, cached result

### LeetCode/Discuss Problems
| Problem | Company Freq | Concept |
|---------|-------------|---------|
| (Discuss) Implement Circuit Breaker | Netflix, Amazon, Microsoft | Fault tolerance |
| (Concept) Hystrix Pattern | Netflix | Bulkheading |

### Java Template: Circuit Breaker

```java
enum CircuitState { CLOSED, OPEN, HALF_OPEN }

class CircuitBreaker {
    private CircuitState state = CircuitState.CLOSED;
    private int failureCount = 0;
    private long lastFailureTime;
    private final int threshold;
    private final long timeoutMs;

    public CircuitBreaker(int threshold, long timeoutMs) {
        this.threshold = threshold;
        this.timeoutMs = timeoutMs;
    }

    public <T> T execute(Supplier<T> operation, Supplier<T> fallback) {
        if (state == CircuitState.OPEN) {
            if (System.currentTimeMillis() - lastFailureTime > timeoutMs) {
                state = CircuitState.HALF_OPEN;
            } else {
                return fallback.get();
            }
        }

        try {
            T result = operation.get();
            if (state == CircuitState.HALF_OPEN) state = CircuitState.CLOSED;
            failureCount = 0;
            return result;
        } catch (Exception e) {
            failureCount++;
            lastFailureTime = System.currentTimeMillis();
            if (failureCount >= threshold) state = CircuitState.OPEN;
            return fallback.get();
        }
    }
}
```

---

## Company Frequency Summary

| Company | Most Asked Concepts | Key LC Problems |
|---------|-------------------|-----------------|
| Google | Caching, Trie, Rate Limiting, Graph | 146, 359, 208, 133 |
| Amazon | HashMap, LRU Cache, Trie, Design | 706, 146, 208, 981 |
| Meta (Facebook) | Social Graph, LRU, Trie, Autocomplete | 133, 146, 212, 642 |
| Microsoft | Trie, Load Balance, Thread Sync | 208, 146, 1117, 1188 |
| Apple | Thread Sync, HashMap, Graph | 706, 1117, 133 |
| Netflix | Circuit Breaker, Stream Processing | 295, 346, 703 |
| Uber | Design, Geospatial, Rate Limiter | 1396, 359, 706 |
| Stripe | Rate Limiter, Concurrent Patterns | 359, 1188, 1195 |
| DoorDash | BFS/DFS, Scheduling | 1236, 207, 210 |
| TikTok | Trie, Stream Processing, Graph | 208, 295, 212 |
