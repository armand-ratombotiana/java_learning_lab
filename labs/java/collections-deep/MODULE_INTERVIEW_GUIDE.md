# Collections Deep Dive — Module Interview Guide

## Company-Specific Questions

### Google
- "Implement HashMap from scratch. How does Java 8 improve collision handling with TreeNode?"
- "Compare ArrayList vs LinkedList memory layout. What happens at the CPU cache level?"
- "How does Arrays.sort() work? When does it switch from Dual-Pivot QuickSort to TimSort?"
- "Explain the internal structure of ConcurrentHashMap in Java 8+. How does it differ from Java 7?"

### Amazon
- "Design a bounded blocking queue using a linked list. How do you handle timeout?"
- "How would you implement a thread-safe LRU cache that supports concurrent reads and writes?"
- "What is the memory overhead of an ArrayList of 1 million Integers vs int[]?"

### Meta
- "Implement a custom iterator that filters, maps, and limits elements."
- "How does PriorityQueue maintain the heap property? What is the runtime of each operation?"
- "Compare CopyOnWriteArrayList vs synchronized ArrayList for read-heavy workloads."

### Apple
- "What is the memory layout of an ArrayList vs an array? How does ArrayList's growth factor work?"
- "How does HashMap's load factor affect memory? What load factor would you choose for 90% memory utilization?"

### Oracle
- "Explain the exact contract of hashCode() and equals(). How does HashMap use both?"
- "What changes to Collections were made in Java 8, 9, 11, 17, 21?"
- "How does identityHashCode() work? How is it related to object header mark word?"

## LeetCode Problems

| Problem | Category | Collections Concept Tested |
|---------|----------|--------------------------|
| 146 LRU Cache | Design | LinkedHashMap, O(1) access patterns |
| 380 Insert Delete GetRandom O(1) | Design | HashMap + ArrayList combo |
| 295 Find Median from Data Stream | Heap | PriorityQueue, two-heap technique |
| 460 LFU Cache | Design | HashMap + TreeSet / LinkedHashSet |
| 706 Design HashMap | Design | Array of buckets, collision resolution |
| 895 Maximum Frequency Stack | Design | HashMap + Stack mapping |
| 155 Min Stack | Design | Deque tracking |
| 239 Sliding Window Maximum | Deque | ArrayDeque, monotonic queue |
| 1462 Course Schedule IV | Graph + Proc | Topological order with bitmask sets |
| 981 Time Based Key-Value Store | Map | TreeMap.floorKey() |

## FAANG Interview Stories

**Story 1: Google — HashMap Implementation**
> *"The interviewer asked me to implement a HashMap from scratch, then add concurrency support. I started with separate chaining. When I used a linked list, they asked me to switch to a balanced tree when chains grow beyond 8 (like Java 8). Then they asked about load factor and resizing. At the end they asked: 'How would you make this cache-friendly for modern CPUs?'"* — L4 SWE, Google

**Story 2: Amazon — LRU Cache Design**
> *"I implemented an LRU cache using LinkedHashMap in 5 minutes. The interviewer said 'That's cheating. Do it without LinkedHashMap.' I had to implement a doubly-linked list + HashMap from scratch. Then they asked about concurrent access, time-based expiration, and what happens if the cache is full."* — SDE II, Amazon

**Story 3: Microsoft — PriorityQueue**
> *"The question was 'Find the top K frequent words in a stream.' I used a min-heap. They asked: 'What's the time complexity? How does the heap grow? When would you use a max-heap instead? Show me the siftUp and siftDown operations on the board.'"* — SDE II, Microsoft

## Senior vs Staff Deep Dive

### Senior-Level
- "Explain HashMap resize mechanics. Why does the capacity double? How does rehashing work?"
- "Compare ArrayList vs LinkedList for: insertion at middle, iteration, memory overhead, cache behavior."
- "How does ConcurrentHashMap compute the size? What is the LongAdder approach in Java 8?"

### Staff-Level
- "Design a concurrent collection that supports snapshot iteration without copying. What tradeoffs exist?"
- "Explain how TreeMap's Red-Black tree works. What are the insertion and deletion invariants?"
- "How would you implement a persistent (immutable) collection in Java? Skew heaps, finger trees?"
- "Analyze the garbage collection behavior of LinkedList vs ArrayList in a long-running application."

## System Design Connections

| System | Collections in Play |
|--------|-------------------|
| Cache layer | LinkedHashMap, ConcurrentHashMap, LRU eviction |
| Message queue | ConcurrentLinkedQueue, BlockingQueue variants |
| Rate limiter | Token bucket (ConcurrentHashMap + ScheduledExecutorService) |
| Leaderboard | PriorityQueue, TreeSet for sorted scores |
| Distributed counter | LongAdder, ConcurrentHashMap with compute() |
| Job scheduler | PriorityQueue/DelayedQueue for sorted execution |

## Code Review Scenarios

**Scenario 1**: Developer uses `ArrayList.indexOf()` in a loop over 100K elements.
- Issue: O(n²) — each indexOf scans the list
- Fix: Use `Set.contains()` or build a `HashMap` for lookups

**Scenario 2**: `HashMap<Integer, List<String>>` manual initialization missing `computeIfAbsent`.
```java
// Before — verbose and error-prone
if (!map.containsKey(key)) { map.put(key, new ArrayList<>()); }
map.get(key).add(value);

// After — atomic and clean
map.computeIfAbsent(key, k -> new ArrayList<>()).add(value);
```

**Scenario 3**: `ConcurrentHashMap` used with `putIfAbsent` but no `remove` in cleanup.
- Issue: Memory leak if keys accumulate without removal
- Fix: Use `CacheBuilder` from Caffeine or implement TTL with scheduled cleanup

## Debugging Scenarios

**Scenario 1**: `ConcurrentModificationException` in production.
- Root cause: Iterating over `ArrayList` while another thread modifies it
- Fix: Use `ConcurrentHashMap` or `CopyOnWriteArrayList`, or synchronize iteration

**Scenario 2**: HashMap with poor hashCode() causes O(n) lookup in production.
- Debug: Add JVM flag `-XX:+PrintClassHistogram` to see distribution
- Fix: Implement proper hashCode(), or use `TreeMap` for O(log n) worst case

**Scenario 3**: PriorityQueue not respecting FIFO for equal-priority elements.
- Root cause: PriorityQueue doesn't guarantee order for ties
- Fix: Add a secondary field (timestamp, sequence number) to Comparator
