# LeetCode Pattern Cheatsheet for SRE / Production Engineering

## How to Use This Cheatsheet

Each pattern includes: Java template code, LeetCode problem references with company frequency, and real-world SRE context connecting the pattern to specific incident-response labs in this academy. Use this alongside `LAB_XX/LEETCODE_CONNECTIONS.md` and `LAB_XX/LEETCODE_SOLUTIONS/*.java` files for each lab.

---

## Section 1: Concurrency & Threading Patterns

### 1.1 Producer-Consumer

**SRE Context:** Message queue processing, Kafka consumer groups, log ingestion pipelines. This pattern is used when debugging production incidents similar to Lab 13 (Kafka consumer lag).

**Java Template:**
```java
import java.util.concurrent.*;
import java.util.*;

class ProducerConsumer<T> {
    private final BlockingQueue<T> queue = new ArrayBlockingQueue<>(1000);
    private volatile boolean running = true;

    public void produce(T item) throws InterruptedException {
        queue.put(item); // blocks if full — backpressure
    }

    public T consume() throws InterruptedException {
        return queue.take(); // blocks if empty
    }

    public void shutdown() { running = false; }
}
```

**LeetCode Problems:**

| Problem | Difficulty | Company Frequency | SRE Connection |
|---------|-----------|-------------------|----------------|
| 1188. Design Bounded Blocking Queue | Medium | Google, Meta, Amazon | Thread pool backpressure — Lab 04, 07 |
| 1594. Maximum Non Negative Product | Medium | Microsoft | Priority inversion in concurrent paths |
| 1242. Web Crawler Multithreaded | Medium | LinkedIn, Uber | Crawl-based service discovery |
| 1117. Building H2O | Medium | Intel, Google | Multi-resource allocation patterns |
| 1226. The Dining Philosophers | Medium | Oracle, Google | Resource deadlock — Lab 02 |

### 1.2 Reader-Writer Lock

**SRE Context:** Cache implementations, configuration hot-reload, metrics aggregation where reads >> writes. Relevant for Lab 08 (cache stampede) and Lab 03 (high CPU from lock contention).

```java
import java.util.concurrent.locks.*;

class ReadWriteResource<T> {
    private final ReentrantReadWriteLock rwLock = new ReentrantReadWriteLock();
    private volatile T data;

    public T read() {
        rwLock.readLock().lock();
        try { return data; }
        finally { rwLock.readLock().unlock(); }
    }

    public void write(T newData) {
        rwLock.writeLock().lock();
        try { this.data = newData; }
        finally { rwLock.writeLock().unlock(); }
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 384. Shuffle an Array | Medium | Amazon, Google | Cache coherency in distributed state |
| 380. Insert Delete GetRandom O(1) | Medium | Amazon, Facebook | Thread-safe metrics registry |

### 1.3 Thread Pool Pattern

**SRE Context:** Connection pool management (Lab 04), request thread pool sizing, async event processing.

```java
import java.util.concurrent.*;

class ManagedThreadPool {
    private final ThreadPoolExecutor executor;

    public ManagedThreadPool(int core, int max, int queueSize) {
        this.executor = new ThreadPoolExecutor(
            core, max, 60, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(queueSize),
            new ThreadPoolExecutor.CallerRunsPolicy() // backpressure
        );
    }

    public Future<?> submit(Runnable task) {
        return executor.submit(task);
    }

    public int getQueueDepth() { return executor.getQueue().size(); }
    public int getActiveCount() { return executor.getActiveCount(); }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 1242. Web Crawler Multithreaded | Medium | Google, Uber | Crawling = parallel task execution |
| 127. Word Ladder | Hard | Amazon, Facebook | BFS = thread pool task fan-out |

### 1.4 Deadlock Detection Pattern

**SRE Context:** Database deadlocks (Lab 05), thread deadlocks (Lab 02). Detecting cycles in resource allocation graphs.

```java
import java.util.concurrent.*;
import java.lang.management.*;

class DeadlockDetector implements Runnable {
    @Override
    public void run() {
        ThreadMXBean threadBean = ManagementFactory.getThreadMXBean();
        while (true) {
            long[] deadlockedIds = threadBean.findDeadlockedThreads();
            if (deadlockedIds != null) {
                ThreadInfo[] infos = threadBean.getThreadInfo(deadlockedIds);
                for (ThreadInfo info : infos) {
                    System.err.println("Deadlock: " + info.getThreadName()
                        + " blocked on " + info.getLockName()
                        + " owned by " + info.getLockOwnerName());
                }
            }
            try { Thread.sleep(5000); } catch (InterruptedException e) { break; }
        }
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 207. Course Schedule | Medium | Google, Amazon, Meta | Cycle detection = deadlock detection — Lab 02 |
| 210. Course Schedule II | Medium | Google, Amazon | Topological order = lock ordering |
| 261. Graph Valid Tree | Medium | LinkedIn, Amazon | Cycle detection in resource graph |

---

## Section 2: System Monitoring Patterns

### 2.1 Sliding Window Statistics

**SRE Context:** Aggregating P50/P95/P99 latency over rolling windows, error rate calculations, SLI measurement (see each lab's `SLI_SLO_METRICS.md`).

```java
import java.util.*;
import java.util.concurrent.*;

class SlidingWindowStats {
    private final long windowNanos;
    private final Queue<Long> events = new ConcurrentLinkedQueue<>();

    public SlidingWindowStats(Duration window) {
        this.windowNanos = window.toNanos();
    }

    public void record() { events.add(System.nanoTime()); evictOld(); }

    public double ratePerSecond() {
        evictOld();
        return events.size() / (windowNanos / 1_000_000_000.0);
    }

    public int count() { evictOld(); return events.size(); }

    private void evictOld() {
        long cutoff = System.nanoTime() - windowNanos;
        while (!events.isEmpty() && events.peek() < cutoff) events.poll();
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 346. Moving Average from Data Stream | Easy | Google, Meta | P50 latency calculation |
| 295. Find Median from Data Stream | Hard | Google, Amazon, Meta | P50/P99 percentile tracking |
| 480. Sliding Window Median | Hard | Google, Meta | Real-time latency percentile computation |
| 1423. Maximum Points You Can Obtain | Medium | no direct | Sliding window patterns in metrics |

### 2.2 Rate Limiting Algorithms

**SRE Context:** API rate limiting (Lab 14), DDoS mitigation (Lab 10), connection throttling (Lab 04).

```java
class TokenBucketRateLimiter {
    private final long capacity;
    private final long tokensPerSecond;
    private long tokens;
    private long lastRefillNanos;

    public TokenBucketRateLimiter(long capacity, long tokensPerSecond) {
        this.capacity = capacity;
        this.tokensPerSecond = tokensPerSecond;
        this.tokens = capacity;
        this.lastRefillNanos = System.nanoTime();
    }

    public synchronized boolean allowRequest() {
        refill();
        if (tokens > 0) { tokens--; return true; }
        return false;
    }

    private void refill() {
        long now = System.nanoTime();
        long elapsed = now - lastRefillNanos;
        tokens = Math.min(capacity, tokens
            + (elapsed * tokensPerSecond / 1_000_000_000L));
        lastRefillNanos = now;
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 359. Logger Rate Limiter | Easy | Google, Meta, Amazon | Per-key rate limiting — Lab 14 |
| AtCoder/Custom: Token Bucket | N/A | System Design | Token bucket for API throttling |
| LeetCode 362. Design Hit Counter | Medium | Dropbox, Google | Sliding window counter — Lab 14 |

### 2.3 Anomaly Detection

**SRE Context:** Identifying metric spikes, error budget burn rate alerts, detecting capacity exhaustion (Lab 09).

```java
class AnomalyDetector {
    private final double thresholdMultiplier;
    private final Deque<Double> recentValues = new LinkedList<>();
    private double mean = 0;
    private double stdDev = 0;

    public AnomalyDetector(double multiplier) {
        this.thresholdMultiplier = multiplier;
    }

    public boolean isAnomalous(double value) {
        double upperBound = mean + thresholdMultiplier * stdDev;
        return value > upperBound;
    }

    public void update(double value) {
        recentValues.addLast(value);
        if (recentValues.size() > 100) recentValues.removeFirst();
        mean = recentValues.stream().mapToDouble(d -> d).average().orElse(0);
        double variance = recentValues.stream()
            .mapToDouble(d -> Math.pow(d - mean, 2)).average().orElse(0);
        stdDev = Math.sqrt(variance);
    }
}
```

---

## Section 3: Networking Patterns

### 3.1 Socket Programming / TCP Patterns

**SRE Context:** Debugging connection timeouts (Lab 04), load balancer health checks (Lab 12), TLS handshake failures (Lab 11).

```java
import java.net.*;
import java.io.*;

class TcpHealthCheck {
    public static boolean check(String host, int port, int timeoutMs) {
        try (Socket socket = new Socket()) {
            socket.connect(new InetSocketAddress(host, port), timeoutMs);
            return true;
        } catch (IOException e) { return false; }
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 20. Valid Parentheses | Easy | All companies | Stack = TCP connection state machine |
| 316. Remove Duplicate Letters | Medium | Google | Lexicographic ordering = packet ordering |

### 3.2 DNS Resolution / IP Patterns

**SRE Context:** Debugging DNS propagation delays, split-horizon DNS, service discovery failures.

```java
class DnsResolver {
    public static List<String> resolve(String hostname) {
        try {
            InetAddress[] addresses = InetAddress.getAllByName(hostname);
            return Arrays.stream(addresses)
                .map(InetAddress::getHostAddress).collect(Collectors.toList());
        } catch (UnknownHostException e) { return List.of(); }
    }
}
```

---

## Section 4: Data Structures for SRE

### 4.1 Ring Buffer / Circular Log

**SRE Context:** Log tailing (Lab 09 — disk space), metrics ring buffer, rolling log files, JDBC connection pool wrappers.

```java
class RingBuffer<T> {
    private final Object[] buffer;
    private int head = 0, tail = 0, size = 0;

    public RingBuffer(int capacity) {
        this.buffer = new Object[capacity];
    }

    public synchronized void add(T item) {
        buffer[tail] = item;
        tail = (tail + 1) % buffer.length;
        if (size == buffer.length) head = (head + 1) % buffer.length;
        else size++;
    }

    @SuppressWarnings("unchecked")
    public synchronized List<T> drain() {
        List<T> items = new ArrayList<>();
        while (size > 0) {
            items.add((T) buffer[head]);
            buffer[head] = null;
            head = (head + 1) % buffer.length;
            size--;
        }
        return items;
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 622. Design Circular Queue | Medium | Amazon, Google | Ring buffer for metrics — Lab 03, 09 |
| 641. Design Circular Deque | Medium | Amazon | Double-ended log buffer |
| 170. Two Sum III - Data Structure | Easy | LinkedIn | Internal buffer patterns in databases |

### 4.2 Bloom Filter

**SRE Context:** Cache optimization (don't cache one-hit-wonders), spam detection, URL deduplication, security scanning (Lab 10).

```java
import java.util.*;

class BloomFilter {
    private final BitSet bits;
    private final int size;
    private final int[] hashSeeds;

    public BloomFilter(int size, int numHashes) {
        this.size = size;
        this.bits = new BitSet(size);
        this.hashSeeds = new Random().ints(numHashes, 1, Integer.MAX_VALUE).toArray();
    }

    public void add(String value) {
        for (int seed : hashSeeds) {
            bits.set(Math.abs(value.hashCode() ^ seed) % size);
        }
    }

    public boolean mightContain(String value) {
        for (int seed : hashSeeds) {
            if (!bits.get(Math.abs(value.hashCode() ^ seed) % size)) return false;
        }
        return true; // false positive possible
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 705. Design HashSet | Easy | Amazon, Microsoft | Bit-level storage — Lab 10 security scanning |
| 706. Design HashMap | Easy | Meta, Amazon | Hash function design for caching |

### 4.3 Consistent Hashing

**SRE Context:** Cache shard distribution (Lab 08 — cache stampede), database sharding, load balancer routing, distributed key-value stores.

```java
class ConsistentHashRing {
    private final TreeMap<Integer, String> ring = new TreeMap<>();
    private final int virtualNodes;

    public ConsistentHashRing(int virtualNodes) {
        this.virtualNodes = virtualNodes;
    }

    public void addNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.put(hash(node + ":" + i), node);
        }
    }

    public void removeNode(String node) {
        for (int i = 0; i < virtualNodes; i++) {
            ring.remove(hash(node + ":" + i));
        }
    }

    public String getNode(String key) {
        if (ring.isEmpty()) return null;
        Integer hash = hash(key);
        Map.Entry<Integer, String> entry = ring.ceilingEntry(hash);
        return (entry == null) ? ring.firstEntry().getValue() : entry.getValue();
    }

    private int hash(String key) { return key.hashCode() & 0x7FFFFFFF; }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 355. Design Twitter | Medium | Amazon, Google | News feed sharding — Lab 08 |
| 642. Design Search Autocomplete | Hard | Google, Meta | Prefix-based shard routing |

### 4.4 LRU Cache

**SRE Context:** Local cache eviction (Lab 08), connection pool management (Lab 04), database query cache.

```java
import java.util.*;

class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;

    public LRUCache(int capacity) {
        super(capacity, 0.75f, true); // access-order=true
        this.capacity = capacity;
    }

    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }

    public V get(K key) { return super.get(key); }
    public void put(K key, V value) { super.put(key, value); }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 146. LRU Cache | Medium | Google, Meta, Amazon, Microsoft | Cache eviction — Lab 08 |
| 460. LFU Cache | Hard | Google, Amazon | Frequency-based eviction — Lab 08 |
| 432. All O`one Data Structure | Hard | Amazon, Meta | Frequency tracking in caches |
| 1625. Lexicographically Smallest String | Medium | no direct | Underlying Deque patterns in IO buffers |

---

## Section 5: Database Recovery Patterns

### 5.1 Transaction Log / WAL Pattern

**SRE Context:** Database crash recovery (Lab 05 — slow query), replication lag detection, point-in-time recovery.

```java
class WriteAheadLog {
    private final List<String> log = new ArrayList<>();
    private final Object lock = new Object();

    public void append(String entry) {
        synchronized (lock) {
            log.add(entry);  // fsync in real implementation
            apply(entry);    // apply to in-memory state
        }
    }

    public void replay() {
        synchronized (lock) {
            for (String entry : log) apply(entry);
        }
    }

    private void apply(String entry) { /* apply to state */ }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 208. Implement Trie | Medium | Google, Amazon, Meta | Log-structured merge tree patterns |
| 211. Design Add and Search Word | Medium | Meta, Amazon | Prefix-matching in DB indexes |

### 5.2 Replication Lag Detection

**SRE Context:** Monitoring replica lag (Lab 05), eventual consistency issues, stale read detection.

```java
class ReplicationLagMonitor {
    private long lastAppliedEventId = 0;
    private long lastReceivedEventId = 0;

    public long getLag() { return lastReceivedEventId - lastAppliedEventId; }

    public void onEventReceived(long eventId) { this.lastReceivedEventId = eventId; }
    public void onEventApplied(long eventId) { this.lastAppliedEventId = eventId; }
}
```

---

## Section 6: Distributed Systems Patterns

### 6.1 Leader Election

**SRE Context:** Kafka consumer group coordinator (Lab 13), ZooKeeper leader election for HA databases, Kubernetes controller manager election.

```java
class LeaderElection {
    private final String nodeId;
    private final ZkClient zk;
    private final String leaderPath = "/cluster/leader";

    public LeaderElection(String nodeId, ZkClient zk) {
        this.nodeId = nodeId;
        this.zk = zk;
    }

    public boolean tryBecomeLeader() {
        try {
            zk.create(leaderPath, nodeId.getBytes(), CreateMode.EPHEMERAL);
            return true;
        } catch (NodeExistsException e) { return false; }
    }

    public String getLeader() {
        return new String(zk.getData(leaderPath));
    }
}
```

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 179. Largest Number | Medium | Amazon, Meta | Ordering = leader sequencing |
| 295. Find Median from Data Stream | Hard | Google, Amazon | Distributed percentile with coordinator |

### 6.2 Distributed Consensus / Quorum

**SRE Context:** Write quorum in DynamoDB/Cassandra, Raft/Paxos implementations, split-brain prevention (Lab 15 — DR failover).

```java
class QuorumReader<T> {
    private final int readQuorum; // R
    private final List<Node<T>> nodes; // N nodes total

    public QuorumReader(int readQuorum, List<Node<T>> nodes) {
        this.readQuorum = readQuorum;
        this.nodes = nodes;
    }

    public T read(String key) {
        List<T> results = nodes.parallelStream()
            .map(n -> n.get(key))
            .collect(Collectors.toList());
        // In real impl: compare timestamps, pick latest version
        T latest = results.get(0);
        long latestVersion = 0;
        for (T result : results) {
            // version comparison logic
        }
        return latest;
    }
}
```

### 6.3 Gossip Protocol

**SRE Context:** Service discovery, failure detection, cluster membership changes (Lab 12 — Kubernetes pod health).

```java
class GossipNode {
    private final Set<String> knownPeers = ConcurrentHashMap.newKeySet();
    private final Random random = new Random();
    private long heartbeat = 0;

    public void gossip() {
        for (String peer : selectRandomPeers(3)) {
            sendHeartbeat(peer);
        }
    }

    public void receiveGossip(String fromNode, long theirHeartbeat) {
        knownPeers.add(fromNode);
        heartbeat = Math.max(heartbeat, theirHeartbeat) + 1;
    }

    private List<String> selectRandomPeers(int count) {
        List<String> shuffled = new ArrayList<>(knownPeers);
        Collections.shuffle(shuffled);
        return shuffled.subList(0, Math.min(count, shuffled.size()));
    }
}
```

---

## Section 7: Security Patterns

### 7.1 Token Bucket Rate Limiting

**SRE Context:** API rate limiting (Lab 14), DDoS mitigation (Lab 10), login attempt throttling.

(See Section 2.2 for full Java template)

| Problem | Difficulty | Frequency | SRE Context |
|---------|-----------|-----------|-------------|
| 359. Logger Rate Limiter | Easy | Google, Amazon | Per-key rate limiting — Lab 14 |
| Design a URL Shortener | N/A (System Design) | Companies ask | Key generation with collision avoidance |

### 7.2 Authentication & Authorization Patterns

**SRE Context:** TLS mutual auth (Lab 11), OAuth token validation, API key rotation (Lab 10).

```java
class TokenAuthFilter {
    private final Set<String> validTokens = ConcurrentHashMap.newKeySet();
    private final long tokenExpiryMs;

    public boolean validateRequest(String token) {
        return validTokens.contains(token) && !isExpired(token);
    }

    public void rotateTokens() {
        // Issue new tokens, mark old as expired
    }
}
```

---

## Section 8: Observability Patterns

### 8.1 Metrics Aggregation

**SRE Context:** Counter, gauge, histogram patterns for Prometheus/Graphite. Used in every lab's `MONITORING.md`.

```java
interface Metric {
    void record(double value);
    double getValue();
}

class Counter implements Metric {
    private final AtomicLong count = new AtomicLong(0);
    public void record(double value) { count.incrementAndGet(); }
    public double getValue() { return count.get(); }
}

class Gauge implements Metric {
    private volatile double value;
    public void record(double value) { this.value = value; }
    public double getValue() { return value; }
}

class Histogram {
    private final LongAdder[] buckets;
    private final long[] bounds;

    public Histogram(long[] bounds) {
        this.bounds = bounds;
        this.buckets = new LongAdder[bounds.length + 1];
        for (int i = 0; i < buckets.length; i++) buckets[i] = new LongAdder();
    }

    public void record(long value) {
        int idx = Arrays.binarySearch(bounds, value);
        buckets[idx >= 0 ? idx : -idx - 1].increment();
    }
}
```

### 8.2 Log Parsing Pattern

**SRE Context:** Splunk/ELK query patterns, error log aggregation, structured logging (every lab).

```java
class LogParser {
    private static final Pattern LOG_PATTERN =
        Pattern.compile("(?<timestamp>\\S+) (?<level>\\S+) (?<message>.*)");

    public record LogEntry(String timestamp, String level, String message) {}

    public LogEntry parse(String logLine) {
        Matcher m = LOG_PATTERN.matcher(logLine);
        if (m.matches()) {
            return new LogEntry(m.group("timestamp"), m.group("level"), m.group("message"));
        }
        return null;
    }
}
```

### 8.3 Trace Sampling

**SRE Context:** Distributed tracing (Jaeger/Zipkin), head-based vs tail-based sampling, probability sampling.

```java
class TraceSampler {
    private final double samplingRate;
    private final Random random = new Random();

    public TraceSampler(double samplingRate) {
        this.samplingRate = samplingRate;
    }

    public boolean shouldSample(String traceId) {
        // Consistent sampling based on traceId hash
        int hash = traceId.hashCode();
        return (hash & 0x7FFFFFFF) / (double) Integer.MAX_VALUE < samplingRate;
    }
}
```

---

## Top 30 LeetCode Problems for SRE (with Lab Connections)

| # | Problem | Difficulty | SRE Reason | Related Lab |
|---|---------|-----------|-----------|-------------|
| 1 | 146. LRU Cache | Medium | Cache eviction — core caching concept | Lab 08 |
| 2 | 207. Course Schedule | Medium | Cycle detection = deadlock detection | Lab 02 |
| 3 | 210. Course Schedule II | Medium | Topological sort = lock ordering | Lab 02 |
| 4 | 346. Moving Average | Easy | Sliding window stats = P50 latency | Lab 03 |
| 5 | 295. Find Median | Hard | P50/P99 percentile computation | Lab 03 |
| 6 | 359. Logger Rate Limiter | Easy | Rate limiting algorithm | Lab 14 |
| 7 | 622. Design Circular Queue | Medium | Ring buffer for rolling logs | Lab 09 |
| 8 | 355. Design Twitter | Medium | Consistent hashing, sharding | Lab 08 |
| 9 | 1242. Web Crawler Multithreaded | Medium | Thread pool, producer-consumer | Lab 02 |
| 10 | 1226. Dining Philosophers | Medium | Resource deadlock prevention | Lab 02 |
| 11 | 1117. Building H2O | Medium | Multi-resource coordination | Lab 02 |
| 12 | 1188. Design Bounded Blocking Queue | Medium | Thread pool backpressure | Lab 04 |
| 13 | 384. Shuffle an Array | Medium | Cache coherency patterns | Lab 08 |
| 14 | 705. Design HashSet | Easy | Bloom filter fundamentals | Lab 10 |
| 15 | 706. Design HashMap | Easy | Hash function design for sharding | Lab 08 |
| 16 | 460. LFU Cache | Hard | Frequency-based cache eviction | Lab 08 |
| 17 | 380. Insert Delete GetRandom | Medium | Thread-safe metrics registry | Lab 03 |
| 18 | 208. Implement Trie | Medium | WAL / LSM-tree patterns | Lab 05 |
| 19 | 127. Word Ladder | Hard | BFS = thread pool fan-out | Lab 02 |
| 20 | 20. Valid Parentheses | Easy | Stack = TCP state machine | Lab 11 |
| 21 | 261. Graph Valid Tree | Medium | Cycle detection in resources | Lab 02, 05 |
| 22 | 642. Design Search Autocomplete | Hard | Prefix-based shard routing | Lab 07 |
| 23 | 432. All O`one Data Structure | Hard | Frequency tracking for caches | Lab 08 |
| 24 | 480. Sliding Window Median | Hard | Real-time latency percentile | Lab 03 |
| 25 | 316. Remove Duplicate Letters | Medium | Packet ordering patterns | Lab 11 |
| 26 | 641. Design Circular Deque | Medium | Double-ended log buffer | Lab 09 |
| 27 | 211. Design Add and Search Word | Medium | DB index prefix matching | Lab 05 |
| 28 | 179. Largest Number | Medium | Leader sequencing/ordering | Lab 13 |
| 29 | 170. Two Sum III - Data Structure | Easy | Internal DB buffer patterns | Lab 05 |
| 30 | 1625. Lexicographically Smallest String | Medium | Deque patterns in I/O buffers | Lab 09 |

---

## How to Practice

1. For each lab, review the `LAB_XX/LEETCODE_CONNECTIONS.md` first
2. Examine the `LAB_XX/LEETCODE_SOLUTIONS/*.java` files (3 per lab)
3. Implement the LeetCode problems listed in this cheatsheet
4. Ask: "How would this algorithm help me debug Lab XX?"
5. Practice explaining the real-world SRE context in interviews

**Example flow:** Study Lab 08 (cache stampede) → Read its `LEETCODE_CONNECTIONS.md` → Review `LRUCache.java` in its solutions → Solve LeetCode 146 (LRU Cache) → Explain in interview: "This is the same eviction policy we use in Memcached clusters. In Lab 08, the cache stampede occurred because TTL-based expiry caused all keys to expire simultaneously — adding jitter and consistent hashing prevents this."
