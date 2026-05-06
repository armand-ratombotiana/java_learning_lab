# Lab 14: Concurrent Collections

## 📋 Lab Header

| Aspect | Details |
|--------|---------|
| **Difficulty** | Intermediate-Advanced |
| **Estimated Time** | 4 hours |
| **Real-World Context** | Building a thread-safe cache system |
| **Prerequisites** | Lab 13: Thread Pools and Executors |
| **Learning Type** | Hands-on, Project-based |

## 🎯 Learning Objectives

By completing this lab, you will be able to:

1. **Understand concurrent collections** and their benefits
2. **Use ConcurrentHashMap** effectively
3. **Work with CopyOnWriteArrayList** for thread safety
4. **Implement BlockingQueue** for producer-consumer patterns
5. **Optimize concurrent data structures** for performance
6. **Build a thread-safe cache system**

## 📚 Prerequisites

- Lab 13: Thread Pools and Executors completed
- Understanding of synchronization
- Knowledge of collections
- Familiarity with thread pools

## 🧠 Concept Theory

### 1. ConcurrentHashMap

Thread-safe map with segment-based locking:

```java
import java.util.concurrent.*;

// ConcurrentHashMap - better than synchronized HashMap
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// Put and get
map.put("key1", 100);
Integer value = map.get("key1");

// Atomic operations
map.putIfAbsent("key2", 200);
map.replace("key1", 100, 150);

// Iteration is thread-safe
for (String key : map.keySet()) {
    System.out.println(key + ": " + map.get(key));
}

// Compute operations
map.compute("key1", (k, v) -> v == null ? 0 : v + 1);
map.computeIfAbsent("key3", k -> 300);
map.computeIfPresent("key1", (k, v) -> v * 2);

// Merge
map.merge("key1", 50, Integer::sum);

// Performance comparison
// HashMap: O(1) but not thread-safe
// Hashtable: O(1) but fully synchronized (slow)
// ConcurrentHashMap: O(1) with segment locking (fast and thread-safe)
```

### 2. CopyOnWriteArrayList

Thread-safe list for read-heavy workloads:

```java
// CopyOnWriteArrayList - thread-safe for reads
CopyOnWriteArrayList<String> list = new CopyOnWriteArrayList<>();

// Add elements
list.add("A");
list.add("B");
list.add("C");

// Iteration is thread-safe (snapshot)
for (String item : list) {
    System.out.println(item);
}

// Modifications create new copy
list.add("D");  // Creates new array
list.remove(0);  // Creates new array

// Performance characteristics
// Read: O(1) - very fast
// Write: O(n) - slower due to copying
// Best for: Read-heavy workloads
// Worst for: Write-heavy workloads
```

### 3. BlockingQueue

Queue for producer-consumer patterns:

```java
// LinkedBlockingQueue
BlockingQueue<Integer> queue = new LinkedBlockingQueue<>(10);

// Put and take (blocking)
queue.put(1);  // Blocks if full
Integer value = queue.take();  // Blocks if empty

// Offer and poll (non-blocking)
boolean added = queue.offer(2);  // Returns false if full
Integer polled = queue.poll();  // Returns null if empty

// Offer and poll with timeout
boolean added2 = queue.offer(3, 1, TimeUnit.SECONDS);
Integer polled2 = queue.poll(1, TimeUnit.SECONDS);

// Peek without removing
Integer peeked = queue.peek();

// Size and capacity
int size = queue.size();
int remaining = queue.remainingCapacity();

// Producer-Consumer example
BlockingQueue<String> workQueue = new LinkedBlockingQueue<>();

// Producer
new Thread(() -> {
    try {
        for (int i = 0; i < 5; i++) {
            workQueue.put("Task " + i);
            System.out.println("Produced: Task " + i);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();

// Consumer
new Thread(() -> {
    try {
        while (true) {
            String task = workQueue.take();
            System.out.println("Consumed: " + task);
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    }
}).start();
```

### 4. ConcurrentLinkedQueue

Thread-safe queue without blocking:

```java
// ConcurrentLinkedQueue - non-blocking
ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

// Add and remove
queue.offer(1);  // Add to tail
Integer value = queue.poll();  // Remove from head

// Peek
Integer peeked = queue.peek();

// Size
int size = queue.size();

// Iteration
for (Integer item : queue) {
    System.out.println(item);
}

// Use cases
// - High-throughput scenarios
// - Non-blocking operations needed
// - No capacity limits
```

### 5. ConcurrentSkipListMap

Sorted concurrent map:

```java
// ConcurrentSkipListMap - sorted and thread-safe
ConcurrentSkipListMap<String, Integer> map = new ConcurrentSkipListMap<>();

// Put and get
map.put("apple", 1);
map.put("banana", 2);
map.put("cherry", 3);

// Sorted iteration
for (String key : map.keySet()) {
    System.out.println(key);  // Alphabetical order
}

// Range operations
Map<String, Integer> subMap = map.subMap("apple", "cherry");

// First and last
String first = map.firstKey();
String last = map.lastKey();

// Performance
// Get/Put: O(log n)
// Iteration: O(n)
// Better than TreeMap for concurrent access
```

### 6. Atomic Variables

Atomic operations without locks:

```java
import java.util.concurrent.atomic.*;

// AtomicInteger
AtomicInteger counter = new AtomicInteger(0);
counter.incrementAndGet();
counter.decrementAndGet();
counter.addAndGet(5);
counter.getAndSet(10);

// AtomicLong
AtomicLong longCounter = new AtomicLong(0);
longCounter.incrementAndGet();

// AtomicBoolean
AtomicBoolean flag = new AtomicBoolean(false);
flag.set(true);
boolean wasSet = flag.getAndSet(false);

// AtomicReference
AtomicReference<String> ref = new AtomicReference<>("initial");
ref.set("updated");
boolean swapped = ref.compareAndSet("updated", "new");

// Performance
// Atomic: O(1) without locks
// Synchronized: O(1) with locks
// Atomic is faster for simple operations
```

### 7. Performance Comparison

Comparing concurrent data structures:

```java
// Synchronization overhead comparison
long startTime;
int iterations = 1000000;

// HashMap with synchronization
Map<String, Integer> syncMap = Collections.synchronizedMap(new HashMap<>());
startTime = System.nanoTime();
for (int i = 0; i < iterations; i++) {
    syncMap.put("key" + i, i);
}
long syncTime = System.nanoTime() - startTime;

// ConcurrentHashMap
ConcurrentHashMap<String, Integer> concMap = new ConcurrentHashMap<>();
startTime = System.nanoTime();
for (int i = 0; i < iterations; i++) {
    concMap.put("key" + i, i);
}
long concTime = System.nanoTime() - startTime;

System.out.println("Synchronized: " + syncTime);
System.out.println("Concurrent: " + concTime);
System.out.println("Speedup: " + (syncTime / (double) concTime) + "x");
```

### 8. Thread-Safe Caching

Implementing thread-safe caches:

```java
// Simple thread-safe cache
class Cache<K, V> {
    private ConcurrentHashMap<K, V> map = new ConcurrentHashMap<>();
    
    public V get(K key) {
        return map.get(key);
    }
    
    public void put(K key, V value) {
        map.put(key, value);
    }
    
    public V getOrCompute(K key, Function<K, V> function) {
        return map.computeIfAbsent(key, function);
    }
}

// Usage
Cache<String, Integer> cache = new Cache<>();
cache.put("key1", 100);
Integer value = cache.getOrCompute("key2", k -> 200);
```

### 9. Best Practices

Concurrent collection guidelines:

```java
// ❌ Bad: Using synchronized collections
Map<String, Integer> map = Collections.synchronizedMap(new HashMap<>());

// ✅ Good: Using concurrent collections
ConcurrentHashMap<String, Integer> map = new ConcurrentHashMap<>();

// ❌ Bad: Iterating while modifying
for (String key : map.keySet()) {
    if (someCondition(key)) {
        map.remove(key);  // ConcurrentModificationException
    }
}

// ✅ Good: Using iterator or concurrent collection
Iterator<String> iterator = map.keySet().iterator();
while (iterator.hasNext()) {
    String key = iterator.next();
    if (someCondition(key)) {
        iterator.remove();
    }
}

// ❌ Bad: Compound operations
if (!map.containsKey(key)) {
    map.put(key, value);  // Race condition
}

// ✅ Good: Atomic operations
map.putIfAbsent(key, value);
```

### 10. Choosing the Right Collection

Selection guide:

```java
// Single-threaded: Use regular collections
List<String> list = new ArrayList<>();
Map<String, Integer> map = new HashMap<>();

// Multi-threaded, read-heavy: CopyOnWriteArrayList
List<String> readHeavy = new CopyOnWriteArrayList<>();

// Multi-threaded, balanced: ConcurrentHashMap
Map<String, Integer> balanced = new ConcurrentHashMap<>();

// Multi-threaded, sorted: ConcurrentSkipListMap
Map<String, Integer> sorted = new ConcurrentSkipListMap<>();

// Producer-Consumer: BlockingQueue
BlockingQueue<String> queue = new LinkedBlockingQueue<>();

// High-throughput, non-blocking: ConcurrentLinkedQueue
Queue<String> highThroughput = new ConcurrentLinkedQueue<>();

// Simple atomic operations: Atomic variables
AtomicInteger counter = new AtomicInteger(0);
```

---

## 💻 Step-by-Step Coding Tasks

### Task 1: Use Concurrent Collections

**Objective**: Implement concurrent data structures

**Acceptance Criteria**:
- [ ] ConcurrentHashMap usage
- [ ] CopyOnWriteArrayList usage
- [ ] BlockingQueue usage
- [ ] Thread safety verified
- [ ] Code compiles without errors

**Instructions**:
1. Create concurrent collections
2. Add elements from multiple threads
3. Verify thread safety
4. Test performance
5. Compare with synchronized collections

### Task 2: Implement Producer-Consumer

**Objective**: Use BlockingQueue for coordination

**Acceptance Criteria**:
- [ ] Producer thread
- [ ] Consumer thread
- [ ] BlockingQueue usage
- [ ] Proper synchronization
- [ ] No deadlocks

**Instructions**:
1. Create BlockingQueue
2. Implement producer
3. Implement consumer
4. Test coordination
5. Verify correctness

### Task 3: Build Thread-Safe Cache

**Objective**: Create efficient caching system

**Acceptance Criteria**:
- [ ] Cache implementation
- [ ] Thread safety
- [ ] Performance optimization
- [ ] Eviction policy
- [ ] Monitoring

**Instructions**:
1. Design cache structure
2. Implement thread safety
3. Add eviction policy
4. Test with multiple threads
5. Measure performance

---

## 🎨 Mini-Project: Thread-Safe Cache System

### Project Overview

**Description**: Create a comprehensive thread-safe cache system with eviction policies and monitoring.

**Real-World Application**: Caching systems, data stores, performance optimization.

**Learning Value**: Master concurrent collections and caching patterns.

### Project Structure

```
thread-safe-cache-system/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/learning/
│   │           ├── CacheEntry.java
│   │           ├── Cache.java
│   │           ├── CacheStats.java
│   │           ├── CacheMonitor.java
│   │           └── Main.java
│   └── test/
│       └── java/
│           └── com/learning/
│               └── CacheTest.java
├── pom.xml
└── README.md
```

### Implementation Guide

#### Step 1: Create CacheEntry Class

```java
package com.learning;

import java.time.LocalDateTime;

/**
 * Represents a cache entry.
 */
public class CacheEntry<V> {
    private V value;
    private LocalDateTime createdAt;
    private LocalDateTime lastAccessedAt;
    private long accessCount;
    
    /**
     * Constructor for CacheEntry.
     */
    public CacheEntry(V value) {
        this.value = value;
        this.createdAt = LocalDateTime.now();
        this.lastAccessedAt = LocalDateTime.now();
        this.accessCount = 0;
    }
    
    // Getters
    public V getValue() { return value; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastAccessedAt() { return lastAccessedAt; }
    public long getAccessCount() { return accessCount; }
    
    /**
     * Record access.
     */
    public void recordAccess() {
        this.lastAccessedAt = LocalDateTime.now();
        this.accessCount++;
    }
}
```

#### Step 2: Create Cache Class

```java
package com.learning;

import java.util.concurrent.*;
import java.util.function.Function;

/**
 * Thread-safe cache implementation.
 */
public class Cache<K, V> {
    private ConcurrentHashMap<K, CacheEntry<V>> cache;
    private int maxSize;
    private CacheStats stats;
    
    /**
     * Constructor for Cache.
     */
    public Cache(int maxSize) {
        this.cache = new ConcurrentHashMap<>();
        this.maxSize = maxSize;
        this.stats = new CacheStats();
    }
    
    /**
     * Get value from cache.
     */
    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            entry.recordAccess();
            stats.recordHit();
            return entry.getValue();
        }
        stats.recordMiss();
        return null;
    }
    
    /**
     * Put value in cache.
     */
    public void put(K key, V value) {
        if (cache.size() >= maxSize && !cache.containsKey(key)) {
            evictLRU();
        }
        cache.put(key, new CacheEntry<>(value));
    }
    
    /**
     * Get or compute value.
     */
    public V getOrCompute(K key, Function<K, V> function) {
        return cache.computeIfAbsent(key, k -> {
            V value = function.apply(k);
            return new CacheEntry<>(value);
        }).getValue();
    }
    
    /**
     * Evict least recently used entry.
     */
    private void evictLRU() {
        cache.entrySet().stream()
            .min((e1, e2) -> e1.getValue().getLastAccessedAt()
                .compareTo(e2.getValue().getLastAccessedAt()))
            .ifPresent(entry -> cache.remove(entry.getKey()));
    }
    
    /**
     * Get cache size.
     */
    public int size() {
        return cache.size();
    }
    
    /**
     * Clear cache.
     */
    public void clear() {
        cache.clear();
    }
    
    /**
     * Get statistics.
     */
    public CacheStats getStats() {
        return stats;
    }
}
```

#### Step 3: Create CacheStats Class

```java
package com.learning;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Cache statistics.
 */
public class CacheStats {
    private AtomicLong hits = new AtomicLong(0);
    private AtomicLong misses = new AtomicLong(0);
    
    /**
     * Record cache hit.
     */
    public void recordHit() {
        hits.incrementAndGet();
    }
    
    /**
     * Record cache miss.
     */
    public void recordMiss() {
        misses.incrementAndGet();
    }
    
    /**
     * Get hit count.
     */
    public long getHits() {
        return hits.get();
    }
    
    /**
     * Get miss count.
     */
    public long getMisses() {
        return misses.get();
    }
    
    /**
     * Get hit rate.
     */
    public double getHitRate() {
        long total = hits.get() + misses.get();
        return total == 0 ? 0 : (hits.get() * 100.0) / total;
    }
    
    @Override
    public String toString() {
        return "CacheStats{" +
                "hits=" + hits.get() +
                ", misses=" + misses.get() +
                ", hitRate=" + String.format("%.2f%%", getHitRate()) +
                '}';
    }
}
```

#### Step 4: Create CacheMonitor Class

```java
package com.learning;

/**
 * Monitors cache performance.
 */
public class CacheMonitor<K, V> {
    private Cache<K, V> cache;
    
    /**
     * Constructor for CacheMonitor.
     */
    public CacheMonitor(Cache<K, V> cache) {
        this.cache = cache;
    }
    
    /**
     * Display cache statistics.
     */
    public void displayStats() {
        System.out.println("\n========== CACHE STATS ==========");
        System.out.println("Size: " + cache.size());
        System.out.println(cache.getStats());
        System.out.println("=================================\n");
    }
}
```

#### Step 5: Create Main Class

```java
package com.learning;

/**
 * Main entry point for Thread-Safe Cache System.
 */
public class Main {
    public static void main(String[] args) throws InterruptedException {
        // Create cache
        Cache<String, Integer> cache = new Cache<>(5);
        CacheMonitor<String, Integer> monitor = new CacheMonitor<>(cache);
        
        // Add initial data
        cache.put("key1", 100);
        cache.put("key2", 200);
        cache.put("key3", 300);
        
        // Simulate concurrent access
        Thread[] threads = new Thread[5];
        for (int i = 0; i < 5; i++) {
            final int threadNum = i;
            threads[i] = new Thread(() -> {
                for (int j = 0; j < 100; j++) {
                    String key = "key" + ((j % 3) + 1);
                    Integer value = cache.get(key);
                    if (value == null) {
                        cache.put(key, threadNum * 1000 + j);
                    }
                }
            });
        }
        
        // Start threads
        for (Thread t : threads) {
            t.start();
        }
        
        // Wait for completion
        for (Thread t : threads) {
            t.join();
        }
        
        // Display statistics
        monitor.displayStats();
    }
}
```

#### Step 6: Create Unit Tests

```java
package com.learning;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Cache.
 */
public class CacheTest {
    
    private Cache<String, Integer> cache;
    
    @BeforeEach
    void setUp() {
        cache = new Cache<>(3);
    }
    
    @Test
    void testPutAndGet() {
        cache.put("key1", 100);
        assertEquals(100, cache.get("key1"));
    }
    
    @Test
    void testCacheMiss() {
        assertNull(cache.get("nonexistent"));
    }
    
    @Test
    void testEviction() {
        cache.put("key1", 100);
        cache.put("key2", 200);
        cache.put("key3", 300);
        cache.put("key4", 400);  // Should evict key1
        
        assertNull(cache.get("key1"));
        assertEquals(400, cache.get("key4"));
    }
    
    @Test
    void testGetOrCompute() {
        Integer value = cache.getOrCompute("key1", k -> 100);
        assertEquals(100, value);
    }
    
    @Test
    void testCacheStats() {
        cache.put("key1", 100);
        cache.get("key1");  // Hit
        cache.get("key2");  // Miss
        
        CacheStats stats = cache.getStats();
        assertEquals(1, stats.getHits());
        assertEquals(1, stats.getMisses());
    }
}
```

### Running the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Run the application
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

---

## 📝 Exercises

### Exercise 1: LRU Cache Implementation

**Objective**: Implement LRU cache with eviction

**Task Description**:
Create LRU cache with proper eviction policy

**Acceptance Criteria**:
- [ ] LRU eviction
- [ ] Thread safety
- [ ] Performance metrics
- [ ] Proper cleanup
- [ ] Tests pass

### Exercise 2: Concurrent Queue Processor

**Objective**: Process items from concurrent queue

**Task Description**:
Create system to process items from BlockingQueue

**Acceptance Criteria**:
- [ ] Queue processing
- [ ] Multiple consumers
- [ ] Error handling
- [ ] Metrics tracking
- [ ] Graceful shutdown

### Exercise 3: Thread-Safe Counter

**Objective**: Implement high-performance counter

**Task Description**:
Create counter optimized for concurrent access

**Acceptance Criteria**:
- [ ] Atomic operations
- [ ] High performance
- [ ] Correct results
- [ ] No locks
- [ ] Benchmarks

---

## 🧪 Quiz

### Question 1: What is ConcurrentHashMap?

A) A synchronized HashMap  
B) A map with segment-based locking  
C) A map that copies on write  
D) A blocking map  

**Answer**: B) A map with segment-based locking

### Question 2: When should you use CopyOnWriteArrayList?

A) For write-heavy workloads  
B) For read-heavy workloads  
C) For balanced workloads  
D) Never  

**Answer**: B) For read-heavy workloads

### Question 3: What does BlockingQueue.take() do?

A) Removes and returns element  
B) Blocks if queue is empty  
C) Both A and B  
D) Neither  

**Answer**: C) Both A and B

### Question 4: What is the benefit of AtomicInteger?

A) Thread safety without locks  
B) Better performance  
C) Both A and B  
D) Neither  

**Answer**: C) Both A and B

### Question 5: Which collection is best for sorted concurrent access?

A) ConcurrentHashMap  
B) CopyOnWriteArrayList  
C) ConcurrentSkipListMap  
D) BlockingQueue  

**Answer**: C) ConcurrentSkipListMap

---

## 🚀 Advanced Challenge

### Challenge: Complete Caching Framework

**Difficulty**: Advanced

**Objective**: Build comprehensive caching framework

**Requirements**:
- [ ] Multiple eviction policies
- [ ] TTL support
- [ ] Statistics tracking
- [ ] Monitoring
- [ ] Persistence
- [ ] Scalability

---

## 🏆 Best Practices

### Concurrent Collections

1. **Choose Right Collection**
   - Read-heavy: CopyOnWriteArrayList
   - Balanced: ConcurrentHashMap
   - Sorted: ConcurrentSkipListMap
   - Queue: BlockingQueue

2. **Performance**
   - Minimize lock contention
   - Use atomic operations
   - Profile and optimize

3. **Thread Safety**
   - Avoid compound operations
   - Use atomic methods
   - Handle exceptions

---

## 🔗 Next Steps

**Next Lab**: [Lab 15: Lock Mechanisms](../15-lock-mechanisms/README.md)

---

## ✅ Completion Checklist

- [ ] Completed all coding tasks
- [ ] Built cache system
- [ ] Solved all exercises
- [ ] Passed quiz (80%+)
- [ ] Attempted advanced challenge
- [ ] Reviewed best practices

---

**Congratulations on completing Lab 14! 🎉**

You've mastered concurrent collections. Ready for advanced locking? Move on to [Lab 15: Lock Mechanisms](../15-lock-mechanisms/README.md).