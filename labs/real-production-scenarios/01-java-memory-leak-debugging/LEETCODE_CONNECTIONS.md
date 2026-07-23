# Lab 01 — Java Memory Leak: LeetCode Connections

## How Algorithmic Thinking Helps Debug Memory Leaks

### Core Algorithmic Concepts in Memory Leak Debugging

| Algorithmic Concept | Memory Leak Application |
|--------------------|------------------------|
| Graph traversal (DFS/BFS) | Tracing reference chains from GC roots to leaking objects |
| Cycle detection | Detecting circular references that prevent GC |
| Hash map internals | Understanding ThreadLocalMap as a hash map with weak keys |
| Reference counting | Understanding how garbage collectors track object reachability |
| Queue/Stack overflow | Understanding call stack depth and its impact on thread-local storage |
| LRU cache | Understanding how cache entry eviction relates to object retention |

## LeetCode Problems That Relate to Memory Leaks

### Easy

**Q1: Two Sum (LeetCode 1)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find two indices that sum to target |
| **Memory Connection** | The hash map approach stores array values for O(1) lookup. If the map is stored in a static field and never cleared, it becomes a memory leak. This is exactly how ThreadLocal leaks work — maps that grow unboundedly. |
| **Algorithmic Lesson** | Any data structure that grows unboundedly is a potential memory leak. Always consider: what is the maximum size? When is it cleaned? |

**Q2: Valid Parentheses (LeetCode 20)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Check if parentheses are balanced using a stack |
| **Memory Connection** | The stack in this problem grows dynamically. In a multithreaded context, if each thread had its own stack (like ThreadLocal) and stacks were never cleared between requests, the application would leak memory. The pattern of "push at start, pop at end" is exactly the try-finally pattern needed for ThreadLocal cleanup. |
| **Algorithmic Lesson** | The stack-based approach mirrors the request lifecycle: allocate on entry, deallocate on exit. Missing a pop is like missing a ThreadLocal.remove(). |

**Q3: Contains Duplicate (LeetCode 217)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Check if array contains duplicates using a set |
| **Memory Connection** | The set grows unboundedly. If this set were thread-local and cleared between invocations, it's fine. If it were static and never cleared, it's a memory leak. The HashSet internal structure (hash table with buckets) is similar to ThreadLocalMap's internal structure. |
| **Algorithmic Lesson** | Always bound the size of hash-based data structures. ThreadLocalMap also uses open-addressing hash tables that degrade in performance as they fill up. |

**Q4: Merge Two Sorted Lists (LeetCode 21)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Merge linked lists |
| **Memory Connection** | Each node is an object that needs GC when the list is discarded. If references to list nodes are held by ThreadLocal values, the entire merged list leaks. The reference chain: ThreadLocalMap → Entry.value → ListNode objects. |
| **Algorithmic Lesson** | Object graphs (like linked lists) can be retained by a single unreleased reference. Always consider the entire reachable graph when evaluating a potential leak. |

### Medium

**Q5: LRU Cache (LeetCode 146)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Design a Least Recently Used cache with O(1) get/put |
| **Memory Connection** | The LRU cache has a fixed capacity and evicts old entries. Most ThreadLocal leaks occur because there is no cap on ThreadLocalMap entries and no eviction policy. If ThreadLocalMap had an LRU eviction policy, many leaks would self-heal. |
| **Algorithmic Lesson** | Any unbounded map needs an eviction policy. Consider adding capacity limits or TTL-based eviction to hash maps in production systems. The LRU pattern (doubly linked list + hash map) is useful for bounded caches. |

**Q6: Course Schedule II (LeetCode 210) — Topological Sort**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find order of courses given prerequisites (graph with dependencies) |
| **Memory Connection** | ClassLoader leaks create a dependency DAG: ClassLoader A depends on Parent ClassLoader B. A thread references A, preventing B from being GC'd. Understanding the dependency graph of ClassLoaders is like topological sorting — you need to know which ClassLoaders must be freed before others. |
| **Algorithmic Lesson** | Graph traversal helps understand ClassLoader hierarchies and identify the critical path to GC root. Use DFS to trace the leak from any suspect object back to its retaining thread. |

**Q7: Find All Duplicates in an Array (LeetCode 442)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find all duplicates in array using O(n) time and O(1) space |
| **Memory Connection** | The challenge limits space to O(1). In production, unbounded thread-local storage is the opposite — it grows without bound. The constraint of bounded memory forces efficient data structure design. |
| **Algorithmic Lesson** | When designing thread-local storage, set explicit limits on the number of entries. Just like this problem uses the array itself as storage, ThreadLocalMap uses the Thread object's internal map. |

### Hard

**Q8: Word Ladder II (LeetCode 126)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find shortest transformation sequence (BFS with path reconstruction) |
| **Memory Connection** | BFS queue stores visited nodes to avoid cycles. In GC terms, the GC root set is the "visited" set — objects reachable from this set are "visited" (alive). Memory leaks occur when the "visited" set grows to include objects that should be "unvisited" (GC'd). |
| **Algorithmic Lesson** | BFS/DFS graph traversal is exactly how the garbage collector works: starting from GC roots, traverse all reachable objects. A leak is an object that should be unreachable but is still connected to the graph. |

**Q9: All O`one Data Structure (LeetCode 432)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Design a data structure for O(1) inc/dec/getMax/getMin |
| **Memory Connection** | This problem designs a specialized map with frequency tracking. ThreadLocalMap is also a specialized map — designed for per-thread storage. Understanding hash map internals (capacity, load factor, collision resolution) is critical for understanding how ThreadLocal leaks accumulate. |
| **Algorithmic Lesson** | Specialized data structures have specialized memory characteristics. ThreadLocalMap uses open addressing and linear probing, which means stale entries (cleared keys) waste space and degrade lookup performance. This is why remove() is important — it not only frees memory but also prevents performance degradation. |

**Q10: Median of Two Sorted Arrays (LeetCode 4)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Find median of two sorted arrays in O(log(m+n)) |
| **Memory Connection** | This problem uses divide and conquer, constantly refining the search space while keeping memory O(1). Memory leaks are the opposite — the search space (what objects keep) grows unboundedly over time. The contrast highlights why good memory management requires clear boundaries on state retention. |
| **Algorithmic Lesson** | Every algorithm should define its state retention boundaries. If an algorithm uses O(n) space, the n must be bounded. In production, "n" is often request count or user sessions — unbounded unless explicitly limited. |

## How Specific Algorithms Help in Debugging

### Reference Chain Tracing = DFS on Object Graph

When you "trace path to GC root" in Eclipse MAT, you are doing a DFS from the leak suspect object back to root threads/static fields. Understanding DFS helps you:
- Predict which objects are likely GC roots (stack frames, static fields, JNI handles)
- Recognize that the shortest path to GC root is not always the most informative — sometimes you need all paths
- Understand why GC roots can be elusive (cross-thread references, finalizers)

### WeakReference and ReferenceQueue = Observer Pattern

The WeakReference mechanism is an observer pattern where:
- The observed object = referent
- Observer = ReferenceQueue
- Notification = Reference enqueued when referent becomes phantom-reachable

This pattern allows building caches that automatically clean up. The ThreadLocalMap uses WeakReference for its keys — an elegant solution that prevents ThreadLocal objects themselves from leaking, but not their values.

### Hash Table Collision Resolution

ThreadLocalMap uses open addressing (linear probing) rather than separate chaining. This means:
- When a ThreadLocal's entry is cleared (key becomes null), the entry becomes "stale" but occupies space
- The table degrades as stale entries accumulate
- Only remove() or set() with new keys triggers cleanup of stale entries

Understanding hash table collision resolution helps explain why ThreadLocal leaks degrade performance before causing OOM.

### The "5 Whys" Root Cause Analysis

The 5 Whys technique is essentially a recursive algorithm:
- Start with symptom (OOM)
- Ask "why" recursively until reaching the root cause
- Each "why" is a function call that returns the deeper cause
- Base case: systemic/process root cause (not a technology limitation)

## How to Use LeetCode Problems for Interview Preparation

When asked a LeetCode-style question in a production debugging interview:

**Example:** "Given a reference to an object, find all GC root paths."

```java
// Algorithmic approach to leak detection
// This is essentially DFS on the object graph
Set<Object> visited = new HashSet<>();
List<List<Object>> allPaths = new ArrayList<>();

void findAllGCRootPaths(Object current, List<Object> path) {
    if (current == null) return;
    if (isGCRoot(current)) {
        path.add(current);
        allPaths.add(new ArrayList<>(path));
        path.remove(path.size() - 1);
        return;
    }
    if (!visited.add(current)) return;
    path.add(current);
    for (Object referrer : getReferrers(current)) {
        findAllGCRootPaths(referrer, path);
    }
    path.remove(path.size() - 1);
}
```

This shows you understand the algorithmic foundation of memory analysis tools.

## Summary Table

| LeetCode Problem | Production Scenario | Key Insight |
|-----------------|-------------------|-------------|
| Two Sum (1) | Unbounded hash map growth | All maps need bounds or cleanup |
| LRU Cache (146) | Cache eviction policy | ThreadLocalMap needs eviction |
| Course Schedule II (210) | ClassLoader dependency graph | Understand graph topology for leaks |
| Word Ladder II (126) | GC root traversal | DFS on object graph = GC tracing |
| Valid Parentheses (20) | Try-finally resource cleanup | Push/pop pattern matches set/remove |
| All O`one Data Structure (432) | ThreadLocalMap internals | Specialized maps need specialized cleanup |
| Contains Duplicate (217) | HashSet growth | Static sets need bounded size |
| Median of Two Arrays (4) | State retention boundaries | Every algorithm needs memory limits |

## Practice Problems for Interview Preparation

1. **Implement a ThreadLocal with automatic cleanup** (medium): Create a ThreadLocal variant that uses a ReferenceQueue to detect when the thread dies and cleans up automatically.

2. **Design a bounded ThreadLocalMap** (hard): Create a ThreadLocalMap variant with maximum size and LRU eviction policy.

3. **Detect stale ThreadLocal entries** (medium): Write code that iterates a thread's ThreadLocalMap and reports entries that are > 5 seconds old.

4. **Simulate GC root tracing** (medium): Given an object graph (adjacency list), implement a function that finds all root objects (no incoming edges from non-root objects) and all paths from a given node to any root.

5. **WeakReference cache with cleanup** (easy): Implement a simple cache using WeakHashMap that automatically removes entries when keys are no longer strongly reachable.
