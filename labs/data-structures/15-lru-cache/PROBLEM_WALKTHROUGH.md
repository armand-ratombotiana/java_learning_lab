# Problem Walkthrough: 15-LRU-Cache

## Problem 1: LRU Cache (LC 146) — Meta

### Interview Scenario
"Meta interviewer: 'Design and implement an LRU (Least Recently Used) cache. It supports get and put operations, both in O(1) time.'"

### The Problem
Implement a cache with fixed capacity. `get(key)` returns value and marks the key as most recently used. `put(key, value)` inserts or updates, evicting the LRU item when at capacity.

### Step 1: Clarify (30 seconds)
- **Q:** Key/value types? **A:** Integers for simplicity.
- **Q:** Capacity guarantee? **A:** Positive integer.
- **Q:** What if get is called on a missing key? **A:** Return -1.
- **Q:** Thread safety? **A:** Not required.
- **Q:** What if put updates an existing key? **A:** Update the value and mark as most recently used.
- **Edge cases:** Capacity 1, put to an already full cache, get on evicted key, updating existing key, calling get in a pattern that keeps refreshing the same keys.

### Step 2: Brute Force (2 min)
- Use a list of (key, value) pairs. On get, scan to find key (O(n)), move to front. On put, scan for existing key (O(n)), update or append, evict last if over capacity.
- **Time:** O(n) per operation.
- **Space:** O(n).

### Step 3: Optimize (5 min)
- "Use a HashMap for O(1) key lookup and a doubly linked list for O(1) insertion/deletion in access order. The list keeps most recently used at head, LRU at tail. get: look up node in map, move to head. put: if key exists, update value and move to head. If new, create node at head. If over capacity, remove tail node and its map entry."
- O(1) time for both operations. O(capacity) space.
- **Why Meta values this:** This is the single most commonly asked data structure design question at Meta. It tests your ability to compose two data structures.

### Step 4: Code (10 min)

```java
import java.util.HashMap;
import java.util.Map;

/**
 * LRU cache using HashMap + doubly linked list.
 * <p>
 * Time: O(1) per operation | Space: O(capacity)
 */
public class LRUCache {
    private class Node {
        int key, value;
        Node prev, next;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Node head;
    private final Node tail;

    public LRUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.head = new Node(0, 0);
        this.tail = new Node(0, 0);
        head.next = tail;
        tail.prev = head;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1;
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        Node node = cache.get(key);
        if (node != null) {
            node.value = value;
            moveToHead(node);
        } else {
            node = new Node(key, value);
            cache.put(key, node);
            addToHead(node);
            if (cache.size() > capacity) {
                Node lru = tail.prev;
                removeNode(lru);
                cache.remove(lru.key);
            }
        }
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }
}
```

### Step 5: Test (3 min)
- capacity = 2: put(1,1), put(2,2), get(1) → 1, put(3,3) evicts key 2, get(2) → -1
- capacity = 1: put(1,1), put(2,2), get(1) → -1, get(2) → 2
- Update: put(1,1), put(1,2), get(1) → 2
- **Edge:** put(1,1), put(2,2), get(1), put(3,3) — evicts 2, not 1
- Walk through linked list links after each operation.

### Step 6: Follow-ups
- "Implement using Java's LinkedHashMap?" — Extend LinkedHashMap, override removeEldestEntry.
- "Thread safety?" — Synchronize methods or use ConcurrentHashMap + ReentrantLock.
- "Expiration (TTL)?" — Add timestamp to each node, background thread to evict expired.
- "LFU instead of LRU?" — Use frequency map + nested LinkedHashSets (LC 460).
- **What Meta looks for:** Pointer manipulation under pressure. Doubly linked list insertion/removal must be exactly right.

### Company Evaluation Criteria
- **Meta:** Code correctness, pointer handling, eviction accuracy. Their #1 problem.
- **Google:** Would ask about concurrent LRU and distributed cache design.
- **Amazon:** Would ask about implementing TTL-based eviction on top of LRU.

---

## Problem 2: LFU Cache (LC 460) — Amazon

### Interview Scenario
"Amazon interviewer: 'Design and implement an LFU (Least Frequently Used) cache with get and put in O(1) average time.'"

### The Problem
LFU evicts the least frequently used item. On frequency ties, evict the LRU among items with the same frequency.

### Step 1: Clarify (30 seconds)
- **Q:** How to handle ties? **A:** Evict the least recently used among items with min frequency.
- **Q:** Capacity? **A:** Positive integer > 0.
- **Q:** Frequency of a newly inserted key? **A:** 1.
- **Q:** When does frequency increase? **A:** On both get and put (if key exists).
- **Edge cases:** Capacity 1, put on existing key (freq+1), tie-breaking eviction, large capacity with repeated patterns.

### Step 2: Brute Force (2 min)
- Maintain a list sorted by (frequency, lastAccessTime). Each operation requires sorting or linear scan.
- **Time:** O(n) per operation — too slow.

### Step 3: Optimize (5 min)
- "Use three data structures: HashMap (key -> Node), HashMap (frequency -> LinkedHashSet of keys), and an int tracking minFreq."
- O(1) average time for both operations. LinkedHashSet gives O(1) insertion/removal and O(1) first-element retrieval for LRU among same frequency.
- **Why Amazon values this:** LFU models real-world access patterns better for content delivery (CloudFront CDN). Frequently accessed items stay hot regardless of recent access.

### Step 4: Code (10 min)

```java
import java.util.*;

/**
 * LFU cache using frequency groups.
 * <p>
 * get: O(1) avg | put: O(1) avg | Space: O(capacity)
 */
public class LFUCache {
    private class Node {
        int key, value, freq;

        Node(int key, int value) {
            this.key = key;
            this.value = value;
            this.freq = 1;
        }
    }

    private final int capacity;
    private final Map<Integer, Node> cache;
    private final Map<Integer, LinkedHashSet<Node>> freqMap;
    private int minFreq;

    public LFUCache(int capacity) {
        this.capacity = capacity;
        this.cache = new HashMap<>();
        this.freqMap = new HashMap<>();
        this.minFreq = 0;
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1;
        incrementFreq(node);
        return node.value;
    }

    public void put(int key, int value) {
        if (capacity == 0) return;

        Node node = cache.get(key);
        if (node != null) {
            node.value = value;
            incrementFreq(node);
        } else {
            if (cache.size() >= capacity) {
                evict();
            }
            node = new Node(key, value);
            cache.put(key, node);
            freqMap.computeIfAbsent(1, k -> new LinkedHashSet<>()).add(node);
            minFreq = 1;
        }
    }

    private void incrementFreq(Node node) {
        int oldFreq = node.freq;
        freqMap.get(oldFreq).remove(node);
        if (freqMap.get(oldFreq).isEmpty() && oldFreq == minFreq) {
            minFreq++;
        }
        node.freq++;
        freqMap.computeIfAbsent(node.freq, k -> new LinkedHashSet<>()).add(node);
    }

    private void evict() {
        LinkedHashSet<Node> set = freqMap.get(minFreq);
        Node lru = set.iterator().next();
        set.remove(lru);
        cache.remove(lru.key);
    }
}
```

### Step 5: Test (3 min)
- capacity = 2: put(1,1), put(2,2), get(1) → 1, put(3,3) evicts key 2, get(2) → -1
- capacity = 3: put(1,1), put(2,2), put(3,3), get(1), get(1), get(2), put(4,4) → evicts 3 (freq=1, LRU)
- **Edge:** capacity = 0 → put does nothing, get returns -1
- **Edge:** Same key accessed many times, freq far exceeds others, never evicted

### Step 6: Follow-ups
- "Difference from LRU?" — LRU evicts by recency; LFU by access count. LFU better for stable popularity, worse for burst patterns.
- "LFRU hybrid?" — Some CDNs use both frequency and recency scoring.
- **What Amazon looks for:** minFreq variable management and tie-breaking. Many candidates mis-handle the minFreq update when a bucket empties.

### Company Evaluation Criteria
- **Amazon:** Completeness — tie-breaking and frequency tracking must be exact.
- **Google:** Would ask about dynamic priority scoring.
- **Meta:** Would ask about LFU vs. LRU trade-offs for Facebook content caching.

---

## Problem 3: Design a Cache with Expiration (TTL) — Google

### Interview Scenario
"Google interviewer: 'Design a cache where items expire after a configurable time-to-live (TTL). You need get, put, and automatic eviction of expired items.'"

### The Problem
A cache with TTL per item (or default TTL). Expired items must not be returned and should be cleaned up efficiently.

### Step 1: Clarify (30 seconds)
- **Q:** TTL — per-item or global? **A:** Assume global default, but discuss both.
- **Q:** Cleanup strategy? **A:** Lazy (on access) + background thread for periodic cleanup.
- **Q:** Capacity limit? **A:** Yes, still evict when over capacity (LRU among non-expired).
- **Q:** Precision? **A:** Millisecond precision.
- **Edge cases:** TTL = 0 (always expired), TTL = MAX_VALUE (never expires), many keys expiring simultaneously.

### Step 2: Brute Force (2 min)
- Store with timestamp. On every get, scan all items to check expiration. Background thread full-scan every second.
- **Time:** O(n) per get — impractical.

### Step 3: Optimize (5 min)
- "Use a HashMap for O(1) lookup; each value has an expiration timestamp. Use a min-heap (priority queue) ordered by expiration time for efficient background cleanup. On get: check expiration (lazy eviction). On put: add to map and the expiration heap. A background thread polls the heap and removes expired entries."
- Lazy eviction: O(1) check on get. Background cleanup: O(m log n).
- **Why Google values this:** TTL-based caching is critical at Google — memcache, Bigtable, and Spanner all have TTL. They want efficient cleanup and concurrent access handling.

### Step 4: Code (10 min)

```java
import java.util.*;
import java.util.concurrent.*;

/**
 * Cache with TTL expiration and capacity-based eviction.
 * <p>
 * get: O(1) avg | put: O(log n) | Background cleanup: O(k log n)
 */
public class TTLCache {
    private static class CacheEntry {
        String value;
        long expiresAt;

        CacheEntry(String value, long expiresAt) {
            this.value = value;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }
    }

    private static class ExpiringKey {
        String key;
        long expiresAt;

        ExpiringKey(String key, long expiresAt) {
            this.key = key;
            this.expiresAt = expiresAt;
        }
    }

    private final long defaultTtlMs;
    private final int capacity;
    private final Map<String, CacheEntry> cache;
    private final PriorityQueue<ExpiringKey> expirationQueue;
    private final ScheduledExecutorService cleaner;

    public TTLCache(int capacity, long defaultTtlMs) {
        this.capacity = capacity;
        this.defaultTtlMs = defaultTtlMs;
        this.cache = new ConcurrentHashMap<>();
        this.expirationQueue = new PriorityQueue<>(
            Comparator.comparingLong(e -> e.expiresAt)
        );
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);
    }

    public String get(String key) {
        CacheEntry entry = cache.get(key);
        if (entry == null) return null;
        if (entry.isExpired()) {
            cache.remove(key);
            return null;
        }
        return entry.value;
    }

    public void put(String key, String value) {
        put(key, value, defaultTtlMs);
    }

    public void put(String key, String value, long ttlMs) {
        if (cache.size() >= capacity && !cache.containsKey(key)) {
            evictOne();
        }
        long expiresAt = System.currentTimeMillis() + ttlMs;
        cache.put(key, new CacheEntry(value, expiresAt));
        expirationQueue.offer(new ExpiringKey(key, expiresAt));
    }

    private void evictOne() {
        cleanup();
        if (cache.size() >= capacity) {
            String key = cache.keySet().iterator().next();
            cache.remove(key);
        }
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        while (!expirationQueue.isEmpty()) {
            ExpiringKey ek = expirationQueue.peek();
            if (ek.expiresAt > now) break;
            expirationQueue.poll();
            CacheEntry entry = cache.get(ek.key);
            if (entry != null && entry.isExpired()) {
                cache.remove(ek.key);
            }
        }
    }

    public void shutdown() {
        cleaner.shutdown();
    }
}
```

### Step 5: Test (3 min)
- put("k1", "v1", 100ms) → get("k1") → "v1"
- Thread.sleep(150ms) → get("k1") → null
- put("k1", "v1", Long.MAX_VALUE) → never expires
- **Edge:** TTL = 0 → entry immediately expired
- **Edge:** put beyond capacity → LRU eviction of non-expired entry

### Step 6: Follow-ups
- "Per-item TTL vs. global?" — Per-item stores TTL in CacheEntry, falls back to default.
- "Clock skew in distributed mode?" — Use server-side timestamps or logical clocks.
- "Timing wheel vs. priority queue?" — Timing wheels give O(1) insertion and O(1) per-tick eviction. Better for high-throughput.
- **What Google looks for:** Cleanup efficiency analysis. Trade-off between lazy and background cleanup.

### Company Evaluation Criteria
- **Google:** System design considerations — what happens when cleanup falls behind?
- **Amazon:** Would ask about integrating TTL with DynamoDB's TTL feature.
- **Meta:** Would ask about cache stampede prevention (recompute on miss vs. extend TTL).

---

## Problem 2: LRU Cache with Expiration (Combined) — Meta/Amazon Variant

(Note: LC 146 is Problem 1. This is a common follow-up variant combining LRU and TTL.)

### Interview Scenario
"Design an LRU cache where items also have a TTL. Expired items should be treated as missing and cleaned up."

### The Problem
Combine LRU eviction with time-based expiration.

### Step 1: Clarify (30 seconds)
- **Q:** Eviction — LRU first or expiration first? **A:** Expired items removed on access. If not expired, use LRU.
- **Q:** TTL — global or per-key? **A:** Global default.
- **Q:** Background cleanup? **A:** Lazy (on get/put) + periodic.
- **Edge cases:** Item expired but not yet cleaned up → treat as missing. Item unexpired but cache is full → evict LRU.

### Step 2: Brute Force (2 min)
- Check all items for expiration on every get/put. O(n) — unacceptable.

### Step 3: Optimize (5 min)
- "Combine the LRU doubly linked list with the TTLCache timestamp approach. Each node stores both prev/next pointers and an expiration timestamp. On get: check expiration before moving to head. On put: check expiration of tail before evicting. Background thread sweeps expired nodes."
- O(1) per operation with O(log n) background cleanup using a priority queue.
- **Why this matters:** Real-world caches (Redis, Memcached) support both LRU and TTL.

### Step 4: Code (10 min)

```java
import java.util.concurrent.*;

/**
 * LRU + TTL cache combining access-order eviction with time-based expiration.
 * <p>
 * get: O(1) avg | put: O(1) avg
 */
public class LRUTTLCache {
    private class Node {
        int key, value;
        long expiresAt;
        Node prev, next;

        Node(int key, int value, long expiresAt) {
            this.key = key;
            this.value = value;
            this.expiresAt = expiresAt;
        }

        boolean isExpired() {
            return System.currentTimeMillis() >= expiresAt;
        }
    }

    private final int capacity;
    private final long defaultTtlMs;
    private final ConcurrentHashMap<Integer, Node> cache;
    private final Node head;
    private final Node tail;
    private final ScheduledExecutorService cleaner;

    public LRUTTLCache(int capacity, long defaultTtlMs) {
        this.capacity = capacity;
        this.defaultTtlMs = defaultTtlMs;
        this.cache = new ConcurrentHashMap<>();
        this.head = new Node(0, 0, Long.MAX_VALUE);
        this.tail = new Node(0, 0, Long.MAX_VALUE);
        head.next = tail;
        tail.prev = head;
        this.cleaner = Executors.newSingleThreadScheduledExecutor();
        this.cleaner.scheduleAtFixedRate(this::cleanup, 1, 1, TimeUnit.SECONDS);
    }

    public int get(int key) {
        Node node = cache.get(key);
        if (node == null) return -1;
        if (node.isExpired()) {
            removeNode(node);
            cache.remove(key);
            return -1;
        }
        moveToHead(node);
        return node.value;
    }

    public void put(int key, int value) {
        put(key, value, defaultTtlMs);
    }

    public void put(int key, int value, long ttlMs) {
        Node node = cache.get(key);
        if (node != null) {
            node.value = value;
            node.expiresAt = System.currentTimeMillis() + ttlMs;
            moveToHead(node);
        } else {
            node = new Node(key, value, System.currentTimeMillis() + ttlMs);
            cache.put(key, node);
            addToHead(node);
            if (cache.size() > capacity) {
                Node lru = tail.prev;
                if (!lru.isExpired() || lru == head) {
                    removeNode(lru);
                    cache.remove(lru.key);
                } else {
                    cleanup();
                }
            }
        }
    }

    private void moveToHead(Node node) {
        removeNode(node);
        addToHead(node);
    }

    private void addToHead(Node node) {
        node.next = head.next;
        node.prev = head;
        head.next.prev = node;
        head.next = node;
    }

    private void removeNode(Node node) {
        node.prev.next = node.next;
        node.next.prev = node.prev;
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        Node node = tail.prev;
        while (node != head) {
            Node prev = node.prev;
            if (node.isExpired()) {
                removeNode(node);
                cache.remove(node.key);
            }
            node = prev;
        }
    }

    public void shutdown() {
        cleaner.shutdown();
    }
}
```

### Step 5: Test (3 min)
- put(1, 1, 100ms), get(1) → 1, sleep 150ms, get(1) → -1 (expired before LRU eviction)
- put(1, 1), put(2, 2), put(3, 3) → evicts 1 (LRU), get(2) → 2
- **Edge:** All items expired → cleanup removes all, new item goes in
- **Edge:** put updates existing key's TTL

### Step 6: Follow-ups
- "How to prevent cache stampede?" — Use mutex on cache miss, or extend TTL on access (sliding window).
- "What about using a timing wheel for expiration?" — Better for high-throughput, O(1) per tick.
- "How does Redis handle this?" — Discuss Redis' hybrid eviction (approximate LRU + lazy + periodic expiration).
- **What they look for:** Understanding of real-world caching systems and trade-offs.

### Company Evaluation Criteria
- **Meta/Amazon/Google:** Systems thinking — they want to see you handle the complexity of combining two eviction strategies without violating correctness.

---

## Study Notes

### Key Patterns
- **HashMap + Doubly Linked List:** The canonical LRU design
- **Frequency-layered sets:** LFU with LinkedHashSet per frequency bucket
- **Priority queue for TTL:** Min-heap of expiration times for background cleanup
- **Lazy + periodic cleanup:** Check expiration on access; sweep expired entries in background
- **Dummy head/tail:** Avoid null checks in doubly linked list operations

### Common Mistakes
- Forgetting to remove map entry when evicting from linked list
- Using int[] or ArrayList instead of linked list (only gives O(n) removal)
- Incorrect pointer updates in doubly linked list (order matters: wire new node's links first)
- Not handling minFreq correctly when the last node at a frequency is removed
- Integer division for median in heap problems
- Not considering that TTL expiration can leave slots that LRU will fill

### Time Complexity Cheat Sheet
| Pattern | Time | Space |
|---|---|---|
| LRU (HashMap + DLL) | O(1) per op | O(capacity) |
| LFU (freq layers) | O(1) avg per op | O(capacity) |
| TTL (lazy + heap) | O(1) get, O(log n) put | O(n) |
| LRU + TTL combined | O(1) per op | O(capacity) |
| LinkedHashMap LRU | O(1) per op | O(capacity) |
