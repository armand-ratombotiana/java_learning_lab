$labDir = "C:\Users\jratombo-adm\Desktop\java_learning_lab\labs\algorithms\21-caching-algorithms"

function wf($name, $content) {
    Set-Content -Path (Join-Path $labDir $name) -Value $content
}

wf "README.md" @"
# Caching Algorithms — Overview

This lab covers cache eviction policies: FIFO, LRU (Least Recently Used), LFU (Least Frequently Used), Adaptive Replacement Cache (ARC), and Clock (Second Chance). Caching is critical for performance in operating systems, databases, web servers, CDNs, and CPU architectures.

## Learning Objectives

- Implement FIFO, LRU, and LFU cache eviction policies
- Understand the ARC algorithm for adaptive caching
- Implement the Clock algorithm (Second Chance)
- Analyze cache hit rates under different access patterns
- Understand working set theory and cache performance

## Prerequisites

- Java Collections Framework (LinkedHashMap, PriorityQueue)
- Hash map and linked list data structures
- Understanding of time and space tradeoffs
"@

wf "THEORY.md" @"
# Caching Algorithms — Theoretical Foundation

## Cache Eviction Problem

A cache stores a limited number of items. When the cache is full and a new item needs to be stored, some existing item must be evicted. The goal is to minimize the cache miss rate by evicting items that are least likely to be accessed in the future.

## FIFO (First-In, First-Out)

FIFO evicts the item that was inserted earliest. It is simple to implement with a queue but performs poorly because it does not consider access frequency or recency. FIFO can evict frequently accessed items that happen to be old.

## LRU (Least Recently Used)

LRU evicts the item that was accessed least recently. It assumes that items accessed recently are likely to be accessed again soon (temporal locality). LRU is widely used but can be expensive to maintain (O(1) updates require a hash map + doubly linked list).

## LFU (Least Frequently Used)

LFU evicts the item with the lowest access frequency. It assumes that frequently accessed items should remain cached. LFU requires frequency tracking and can suffer from "cache pollution" where old high-frequency items remain cached despite no longer being accessed.

## ARC (Adaptive Replacement Cache)

ARC dynamically balances between recency and frequency. It maintains four lists: recent entries (recency-focused), frequent entries (frequency-focused), and ghost entries (evicted items metadata). ARC adapts to changing access patterns by adjusting the balance between recency and frequency caching.

## Clock Algorithm (Second Chance)

The Clock algorithm approximates LRU with lower overhead. It organizes cache entries in a circular buffer with a reference bit. On eviction, it scans the buffer: if the reference bit is 1, clear it and continue; if 0, evict that entry. This provides a "second chance" to recently accessed items.

## Working Set Theory

Peter Denning's working set theory states that programs exhibit a set of pages they actively reference (the working set). Optimal cache size equals the working set size. Cache performance degrades significantly when the cache is smaller than the working set.
"@

wf "WHY_IT_EXISTS.md" @"
# Why Caching Algorithms Exist

Caching is a fundamental performance optimization technique across all levels of computing. The principle is simple: store frequently accessed data in a faster storage tier. However, deciding which data to keep when the cache is full is an important algorithmic problem.

The memory hierarchy in computers (registers, L1/L2/L3 caches, main memory, disk, network storage) relies on caching at every level. A CPU cache miss is 10-100x more expensive than a cache hit. A disk cache miss is 100,000x more expensive than a main memory access.

Early operating systems used simple FIFO policies for page replacement. But studies showed that FIFO performed poorly because it could evict a frequently used page simply because it was loaded early. This led to the development of LRU.

LRU became the standard for decades. It is optimal for workloads with good temporal locality. However, maintaining exact LRU ordering requires updating a linked list on every access, which has overhead. The Clock algorithm was developed to approximate LRU with lower overhead.

LFU addresses a different access pattern: stable popularity distributions. In content delivery networks, some files are extremely popular and should stay cached regardless of recency. LFU handles this well but struggles with "bursty" access patterns.

ARC, developed at IBM Almaden Research Center, was a breakthrough. It dynamically adapts between recency-focused (LRU-like) and frequency-focused (LFU-like) behavior based on observed access patterns. ARC is used in IBM mainframes, the ZFS filesystem, and PostgreSQL's buffer manager.

Modern caching research focuses on machine learning-based policies (LRB, HALP) that predict future accesses based on historical patterns. Deep learning models can outperform traditional policies by 10-30% on complex workloads.
"@

wf "WHY_IT_MATTERS.md" @"
# Why Caching Algorithms Matter

Caching algorithms directly impact the performance of every system you use. Your web browser caches pages and images. Your operating system caches file data. Your database caches query results. Content delivery networks cache content globally. CPU caches are essential for processor performance.

## Real-World Impact

Amazon reported that every 100ms of latency costs 1% in sales. Proper caching reduces latency by orders of magnitude. Google's search cache handles billions of queries daily. CDNs like Cloudflare cache content at hundreds of edge locations to reduce latency from seconds to milliseconds.

## Performance at Scale

For a database with 1TB of data and 100GB of cache, a 1% improvement in cache hit rate saves 10GB of disk I/O. For a web server handling 10,000 requests/second, a cache hit takes 1ms vs 100ms for a cache miss. At scale, caching algorithms directly affect infrastructure costs and user experience.

## Specific Applications

- CDNs: Wikipedia, Netflix, YouTube cache content at edge servers
- DNS: Recursive DNS resolvers cache query results
- Databases: PostgreSQL, MySQL, Oracle use buffer pool managers
- Operating Systems: Virtual memory systems use page replacement policies
- Web Servers: Memcached, Redis provide distributed caching
- CPU: L1/L2/L3 caches use adaptive replacement policies
"@

wf "HISTORY.md" @"
# History of Caching Algorithms

1965: Belady's MIN algorithm — optimal cache eviction with knowledge of future references (theoretical optimum).

1966: FIFO and Random page replacement used in early operating systems.

1968: Denning's working set model formalized program locality.

1969: LRU stack algorithms and the Least Recently Used (LRU) policy.

1970s: Clock algorithm (Second Chance) introduced for Multics and VMS.

1980s: LFU variants developed for database buffer management.

1987: 2Q algorithm (Johnson and Shasha) — simple two-queue approximation of ARC.

1990s: Web caching drove new algorithms: Greedy-Dual-Size (GDS), LRU-K.

1994: O'Neil et al. introduced LRU-2 (track last two accesses).

2003: ARC (Adaptive Replacement Cache) by Megiddo and Modha at IBM.

2006: CAR (Clock with Adaptive Replacement) combines Clock and ARC.

2012: LIRS (Low Inter-reference Recency Set) addresses sequential scan pollution.

2018: Learning-based caching policies (LRB, HALP) using ML models.

2020s: Learned caches with neural network predictions for complex workloads.
"@

wf "MENTAL_MODELS.md" @"
# Mental Models for Caching Algorithms

## FIFO — "The Grocery Line"

Customers (cache entries) join a line. When the line is full, the customer at the front leaves first. It doesn't matter if a customer needs more items (is more frequently accessed) — they still leave in order of arrival.

## LRU — "The Book Pile"

Imagine a pile of books on your desk. The most recently used book is on top. When you need space, you discard books from the bottom of the pile (least recently used). If you use a book from the middle, it goes to the top. This is LRU.

## LFU — "The Popularity Contest"

Items are ranked by how many times they've been accessed. The least popular (fewest accesses) is evicted. However, an item that was very popular in the past but now forgotten stays in the cache, like a once-popular song no one listens to anymore.

## ARC — "The Adaptive Balance"

ARC has two sides: recency (LRU-like) and frequency (LFU-like). It monitors which side is working better based on ghost misses (when an item would have been found if the cache were larger). It dynamically shifts capacity toward the better-performing side. It's like a thermostat regulating between two caching strategies.

## Clock — "The Circular Patrol"

Imagine a circular lazy Susan with dishes. Each dish has a flag. Periodically, a pointer moves around the circle. If a dish's flag is up (recently used), lower it and move on. If the flag is down, clear the dish. This simulates LRU without the overhead of moving items.
"@

wf "HOW_IT_WORKS.md" @"
# How Caching Algorithms Work

## LRU Cache Example

Capacity = 3
Access sequence: A, B, C, D, A, E

1. A: miss, cache = [A]
2. B: miss, cache = [A, B]
3. C: miss, cache = [A, B, C]
4. D: miss, evict A, cache = [B, C, D]
5. A: miss, evict B, cache = [C, D, A]
6. E: miss, evict C, cache = [D, A, E]

Hits: 0, Misses: 6

## LFU Cache Example

Same sequence:
1. A: miss, freq[A]=1, cache = [A]
2. B: miss, freq[B]=1, cache = [A, B]
3. C: miss, freq[C]=1, cache = [A, B, C]
4. D: miss, evict one with freq=1 (oldest: A), cache = [B, C, D]
5. A: miss, freq[A]=1, evict B (freq=1, oldest), cache = [C, D, A]
6. E: miss, freq[E]=1, evict C (freq=1, oldest), cache = [D, A, E]

Hits: 0, Misses: 6

## Clock: Circular Buffer

reference bits: [A:1, B:1, C:1] hand at A
Access D: miss, hand clears A's bit (now 0), moves to B
  clears B (now 0), moves to C, clears C (now 0), moves to A
  evicts A (bit 0), inserts D with bit 1
  Clock: [D:1, B:0, C:0] hand at B
"@

wf "INTERNALS.md" @"
# Caching Algorithms — Internal Mechanics

## LRU Cache with LinkedHashMap

```java
class LRUCache<K, V> extends LinkedHashMap<K, V> {
    private final int capacity;
    
    public LRUCache(int capacity) {
        super(capacity, 0.75f, true);  // access-order=true
        this.capacity = capacity;
    }
    
    @Override
    protected boolean removeEldestEntry(Map.Entry<K, V> eldest) {
        return size() > capacity;
    }
}
```

## Manual LRU with HashMap + Doubly Linked List

```java
class LRUCache<K, V> {
    private final Map<K, Node<K, V>> map;
    private final Node<K, V> head, tail;  // sentinels
    private final int capacity;
    
    public V get(K key) {
        Node<K, V> node = map.get(key);
        if (node == null) return null;
        moveToFront(node);
        return node.value;
    }
    
    private void moveToFront(Node<K, V> node) {
        remove(node);
        addFirst(node);
    }
}
```

## LFU Cache with Frequency Groups

```java
class LFUCache<K, V> {
    private final Map<K, V> values;
    private final Map<K, Integer> counts;
    private final Map<Integer, LinkedHashSet<K>> freqBuckets;
    private int minFreq;
}
```
"@

wf "MATH_FOUNDATION.md" @"
# Math Foundation for Caching Algorithms

## Belady's MIN Algorithm

Optimal cache eviction with future knowledge: evict the item whose next access is furthest in the future. Achieves the optimal hit rate but requires knowing future accesses (impossible in practice). Used as a theoretical upper bound.

## Cache Hit Rate Analysis

For LRU with a cache of size k on a random access pattern with n distinct items and Zipf distribution (parameter s), the hit rate is approximately 1 - (k+1)^(1-s) / (n+1)^(1-s) for s != 1.

## Competitive Ratio

An online algorithm A has competitive ratio c if for all sequences, cost_A <= c * cost_OPT + b. For deterministic caching, LRU achieves competitive ratio k (cache size). No deterministic algorithm can do better. Randomized algorithms achieve O(log k).

## ARC Convergence

ARC adapts to changing access patterns by maintaining ghost entries that track recent evictions without storing actual data. The algorithm dynamically adjusts between recency and frequency focus based on whether ghost entries would have been hits if the cache were larger.
"@

wf "VISUAL_GUIDE.md" @"
# Visual Guide — Caching Algorithms

## Clock Algorithm Visualization

Initial: [A:1] [B:1] [C:1] hand->A
Access D (miss): hand at A, bit=1 -> clear to 0, hand->B
                hand at B, bit=1 -> clear to 0, hand->C
                hand at C, bit=1 -> clear to 0, hand->A
                hand at A, bit=0 -> evict A, insert D:1, hand->B
After: [D:1] [B:0] [C:0] hand->B

Access A (miss): hand at B, bit=0 -> evict B, insert A:1, hand->C
After: [D:1] [A:1] [C:0] hand->C

Access D (hit): set D's bit to 1, no eviction
"@

wf "CODE_DEEP_DIVE.md" @"
# Code Deep Dive — Caching Algorithms

## ARC Implementation

ARC maintains four lists: T1 (recent), T2 (frequent), B1 (ghost recent), B2 (ghost frequent). The algorithm adjusts a target size p for T1 based on ghost hits: a B1 hit increases p; a B2 hit decreases p.

```java
class ARCache<K, V> {
    private final LinkedHashMap<K, V> T1, T2;  // actual cache
    private final LinkedHashSet<K> B1, B2;      // ghost entries
    private final int c;  // total capacity
    private int p;        // target size for T1
}
```

## Second Chance Buffer Overshoot

A potential issue with the Clock algorithm is that scanning the entire circular buffer on every miss becomes O(n) per miss. The number of scans per eviction is at most 2 * (cache size) in the worst case. For large caches, this overhead can be significant.

## LFU Frequency Decay

LFU can suffer from "cache pollution" where old high-frequency items never get evicted. Solutions include: (1) periodic frequency halving, (2) window-based LFU (track frequency only in recent window), (3) LFU with aging (decrease frequencies over time).
"@

wf "STEP_BY_STEP.md" @"
# Step-by-Step — Caching Algorithm Implementation

## LRU Cache Steps (Manual)

1. Create a hash map key -> Node
2. Create doubly linked list with head and tail sentinels
3. On get(key):
   a. Look up node in map
   b. If found, move node to front of list (remove + addFirst)
   c. Return value or null
4. On put(key, value):
   a. If key exists, update value, move to front
   b. If new key and full, remove tail node from list and map
   c. Create new node, add to front, add to map

## ARC Steps

1. On access to key x:
   a. If in T1 or T2: move to T2 front (cache hit)
   b. Else if in B1: adapt p = min(c, p + max(1, |B2|/|B1|)), move to T2
   c. Else if in B2: adapt p = max(0, p - max(1, |B1|/|B2|)), move to T2
   d. Else (miss): if |T1|+|B1| == c, replace; add to T1
"@

wf "COMMON_MISTAKES.md" @"
# Common Mistakes — Caching Algorithms

- **LinkedHashMap access order false** — Must set accessOrder=true for LRU behavior
- **LFU: synchronized on frequency update** — Concurrent access requires careful synchronization
- **ARC ghost list overflow** — Ghost entries must be bounded to prevent memory growth
- **Clock: forgetting reference bit** — The reference bit is essential for the algorithm
- **Cache stampede** — Multiple concurrent requests for the same missing item all compute the value
- **Size tracking overflow** — Using int for cache size that may exceed 2^31 entries
- **Not handling null keys/values** — Most cache implementations forbid null keys
- **Thread safety** — LRU with LinkedHashMap is not thread-safe; use ConcurrentHashMap-based implementations
- **Evicting wrong item in LFU ties** — When multiple items have same frequency, need tiebreaker (LRU or FIFO)
- **Memory overhead of tracking** — Each cache entry has metadata overhead; account for this in capacity planning
"@

wf "DEBUGGING.md" @"
# Debugging — Caching Algorithms

## Hit/Miss Ratio Tracking

```java
class CacheStats {
    final AtomicLong hits = new AtomicLong();
    final AtomicLong misses = new AtomicLong();
    
    public double hitRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0.0 : (double) hits.get() / total;
    }
}
```

## Trace Individual Evictions

```java
// Log each eviction
void evict(K key, V value, String reason) {
    System.out.println("Evicted " + key + " reason: " + reason);
}
```

## Simulate with Known Traces

Use the UMass Storage Server Trace Repository or Wikipedia access traces to compare caching policies against real-world access patterns.
"@

wf "REFACTORING.md" @"
# Refactoring — Caching Algorithms

## Strategy Pattern

```java
interface Cache<K, V> {
    V get(K key);
    void put(K key, V value);
    void clear();
}
```

Create LRUCache, LFUCache, ARCache, ClockCache implementing the interface for easy benchmarking.

## Generic Cache Loader

```java
interface Cache<K, V> {
    V computeIfAbsent(K key, Function<K, V> loader);
}
```

Handle the common pattern of computing a value on cache miss transparently.

## Decorator for Metrics

Wrap any cache implementation with a metrics decorator that tracks hit rates, eviction counts, and access latency.

## Observable Cache

Add listeners for eviction events to support statistics collection and logging.
"@

wf "PERFORMANCE.md" @"
# Performance — Caching Algorithms

## Algorithm Comparison

| Algorithm | Get | Put | Evict | Memory Overhead |
|-----------|-----|-----|-------|-----------------|
| FIFO | O(1) | O(1) | O(1) | Low |
| LRU | O(1) | O(1) | O(1) | Medium (2 pointers per entry) |
| LFU | O(1) | O(log n) | O(log n) | High (frequency heap) |
| ARC | O(1) | O(1) | O(1) | Medium (4 linked lists) |
| Clock | O(1) | O(1) | O(n) worst | Low (reference bits) |

## Hit Rate Comparison

For Zipfian access patterns with 1000 items and cache size 100:
- FIFO: ~50-55%
- LRU: ~65-75%
- LFU: ~60-70%
- ARC: ~70-80%
- OPT (Belady): ~85-90%

## Optimization Tips

- Use ConcurrentHashMap and ConcurrentLinkedDeque for high-throughput LRU
- Batch ghost entry management in ARC
- Use primitive collections for frequency tracking to reduce boxing overhead
- Consider off-heap storage for very large caches
"@

wf "SECURITY.md" @"
# Security — Caching Algorithms

## Cache Poisoning

An attacker can intentionally access a pattern of keys to evict other users' cached data (cache eviction attack). This degrades performance and may leak information through timing side channels.

## Cache Timing Side Channels

The time difference between a cache hit and a cache miss can leak information about what data other users have accessed. This has been exploited in CPU cache attacks (Spectre, Meltdown) and web application attacks.

## Cache Pollution

Untrusted user input can cause cache pollution if used as keys. Validate or sanitize keys to prevent cache poisoning.

## Memory Exhaustion

An unbounded cache can exhaust memory. Always enforce a maximum capacity with proper eviction. Use soft references for non-critical cached data.

## TTL-Based Evasion

If TTL is used alongside eviction, ensure that TTL expiry doesn't conflict with eviction policy. An item that is frequently accessed but TTL-expired should still be removed.
"@

wf "ARCHITECTURE.md" @"
# Architecture — Caching Algorithms

## Layered Cache Architecture

```
Application
  └── Local L1 Cache (LRU, fast)
        └── Distributed L2 Cache (Redis, Memcached)
              └── Database / Origin
```

## Cache-Aside Pattern

Application checks cache first. On miss, loads from database and stores in cache. This is the most common caching pattern.

## Write-Through / Write-Behind

Write-through cache updates the database synchronously. Write-behind cache updates asynchronously. The choice affects consistency guarantees.

## Cache Invalidation

- Explicit invalidation: application notifies cache when data changes
- TTL-based: data expires after a fixed time
- Version-based: cache stores version with data
"@

wf "EXERCISES.md" @"
# Exercises — Caching Algorithms

## Beginner
1. Implement a simple FIFO cache with a Queue and Map
2. Implement LRU cache using LinkedHashMap
3. Track cache hit/miss ratio for a sample workload
4. Compare FIFO and LRU on a simple access sequence

## Intermediate
5. Implement LRU with HashMap and doubly linked list
6. Implement LFU cache with frequency tracking
7. Implement the Clock algorithm with reference bits
8. Benchmark LRU vs Clock on random access patterns

## Advanced
9. Implement full ARC with ghost lists
10. Implement a thread-safe LRU cache
11. Compare all cache policies on Wikipedia access trace
12. Implement a two-tier cache (L1 LRU, L2 LFU)
"@

wf "QUIZ.md" @"
# Quiz — Caching Algorithms

1. What is temporal locality and how does LRU exploit it?
2. Why does FIFO perform poorly for many workloads?
3. What problem does LFU have with "bursty" access patterns?
4. How does ARC adapt between recency and frequency?
5. What is the purpose of ghost entries in ARC?
6. How does the Clock algorithm approximate LRU?
7. What is Belady's MIN algorithm and why is it unimplementable?
8. What is a cache stampede and how can it be prevented?
"@

wf "FLASHCARDS.md" @"
# Flashcards — Caching Algorithms

- Q: LRU eviction policy? -> A: Evict least recently accessed item
- Q: LFU eviction policy? -> A: Evict least frequently accessed item
- Q: Clock algorithm also called? -> A: Second Chance
- Q: ARC full form? -> A: Adaptive Replacement Cache
- Q: Belady's MIN uses? -> A: Future knowledge (theoretical)
- Q: Cache stampede? -> A: Multiple concurrent misses for same key
- Q: Write-through? -> A: Cache and DB updated simultaneously
- Q: Working set? -> A: Set of pages actively being referenced
"@

wf "INTERVIEW.md" @"
# Interview Questions — Caching Algorithms

1. "Design an LRU cache." — Classic interview question (HashMap + Doubly Linked List)
2. "How would you implement an LFU cache?" — Frequency tracking with min-heap
3. "Compare LRU and LFU. When would you use each?" — Recency vs frequency tradeoffs
4. "Design a web cache for a CDN." — Distributed caching with ARC
5. "How does Redis handle eviction?" — Multiple policies: LRU, LFU, TTL, random
6. "What caching strategy would you use for a social media feed?" — Time-decayed relevance
"@

wf "REFLECTION.md" @"
# Reflection — Caching Algorithms

- Why is there no single best caching policy for all workloads?
- How does the optimal cache policy depend on the access pattern?
- What are the tradeoffs between policy sophistication and implementation overhead?
- How would you design a cache for a workload you don't understand?
- What is the role of caching in the memory hierarchy of modern computers?
"@

wf "REFERENCES.md" @"
# References — Caching Algorithms

- Megiddo, N., Modha, D.S. "ARC: A Self-Tuning, Low Overhead Replacement Cache." FAST, 2003.
- Denning, P.J. "The Working Set Model for Program Behavior." Communications of the ACM, 1968.
- Belady, L.A. "A Study of Replacement Algorithms for Virtual Storage Computers." IBM Systems Journal, 1966.
- Johnson, T., Shasha, D. "2Q: A Low Overhead High Performance Buffer Management Replacement Algorithm." VLDB, 1994.
- Corbato, F.J. "A Paging Experiment with the Multics System." MIT, 1968.
- Tanenbaum, A.S., Bos, H. "Modern Operating Systems." Pearson, 4th Edition, 2015.
"@

Write-Host "21-caching-algorithms: All 24 markdown files created"
