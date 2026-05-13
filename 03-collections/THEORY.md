# Java Collections Theory

## First Principles

### What are Collections?

Collections are data structures - ways to organize and store multiple items. Unlike arrays (fixed-size, primitive language feature), collections are library-provided, dynamically sized structures with rich functionality.

**Why Collections?**
- Arrays require manual size management and provide limited operations
- Collections provide pre-built solutions for common data management needs
- They abstract away memory management and provide optimized implementations

### Collections vs Data Structures

In Java, these terms are often used interchangeably, but:
- **Data Structure**: The abstract concept (list, tree, graph)
- **Collection**: The Java library implementation (ArrayList, TreeSet)

---

## Big-O Notation

Algorithm efficiency is expressed using Big-O notation, describing how operations scale with input size:

| Notation | Name | Example |
|----------|------|---------|
| O(1) | Constant | Array index access |
| O(log n) | Logarithmic | Binary search |
| O(n) | Linear | Linear search, iteration |
| O(n log n) | Linearithmic | Merge sort |
| O(n²) | Quadratic | Nested loops |
| O(2^n) | Exponential | Recursive fibonacci |

**Why It Matters**: Understanding complexity guides collection selection based on access patterns.

---

## Core Interfaces

### Collection Hierarchy

```
Iterable
    ├── Collection
    │     ├── List (ordered, indexed)
    │     │     ├── ArrayList, LinkedList, Vector
    │     ├── Set (no duplicates)
    │     │     ├── HashSet, LinkedHashSet, TreeSet
    │     └── Queue (FIFO operations)
    │           ├── PriorityQueue, ArrayDeque
    └── Map (key-value)
          ├── HashMap, LinkedHashMap, TreeMap, Hashtable
```

### List Interface

**Characteristics**: Ordered sequence, allows duplicates, index-accessible.

| Implementation | get() | add() | remove() | Use Case |
|----------------|-------|-------|----------|----------|
| ArrayList | O(1) | O(1)* | O(n) | Random access, less insertion |
| LinkedList | O(n) | O(1) | O(1) | Frequent insertion/deletion |
| Vector | O(1) | O(1)* | O(n) | Thread-safe ArrayList |

*Amortized - occasional resize costs O(n)

**Theory**: ArrayList uses dynamic array - grows by ~50% when full. LinkedList uses doubly-linked nodes - memory overhead but constant-time insertions.

### Set Interface

**Characteristics**: No duplicates, mathematical set operations.

| Implementation | add() | contains() | order | Use Case |
|----------------|-------|------------|-------|----------|
| HashSet | O(1) | O(1) | None | Fast membership testing |
| LinkedHashSet | O(1) | O(1) | Insertion | LRU cache, ordered iteration |
| TreeSet | O(log n) | O(log n) | Sorted | Range queries, ordering |

**Theory**: HashSet uses hash table - hash function maps elements to bucket, collisions handled via equals(). TreeSet uses red-black tree - self-balancing BST maintaining O(log n) operations.

### Map Interface

**Characteristics**: Key-value associations, unique keys.

| Implementation | get() | put() | order | Use Case |
|----------------|-------|-------|-------|----------|
| HashMap | O(1) | O(1) | None | Fast lookup table |
| LinkedHashMap | O(1) | O(1) | Insertion | Cache with ordering |
| TreeMap | O(log n) | O(log n) | Sorted | Range queries |

**Theory**: HashMap stores entries (key-value pairs), hashes the key to find bucket. TreeMap maintains sorted keys using R-B tree.

### Queue Interface

**Characteristics**: FIFO (First-In-First-Out), with variations.

| Implementation | offer() | poll() | peek() |
|----------------|---------|--------|--------|
| ArrayDeque | O(1) | O(1) | O(1) |
| PriorityQueue | O(log n) | O(log n) | O(1) |
| LinkedList | O(1) | O(1) | O(1) |

**Theory**: PriorityQueue uses binary heap - parent always smaller/larger than children. ArrayDeque uses circular buffer for efficient两端 operations.

---

## Implementation Selection Guide

### Choosing a Collection

Answer these questions to find the right collection:

1. **Duplicates allowed?** → No → Set; Yes → List/Map
2. **Need ordering?** → Sorted → TreeSet/TreeMap; Insertion → LinkedHash; None → HashSet/HashMap
3. **Access pattern?** → Frequent search → Hash*; Frequent insertion → LinkedList/LinkedHashMap
4. **Thread safety needed?** → Yes → Concurrent collections

| Need | Recommended |
|------|--------------|
| Fast search | HashSet, HashMap |
| Sorted iteration | TreeSet, TreeMap |
| Frequent insert/delete | LinkedList |
| Single-thread array | ArrayList |
| Thread-safe | ConcurrentHashMap |

---

## Advanced Collections

### Concurrent Collections

Thread-safe without synchronization overhead:

| Collection | Use Case |
|------------|----------|
| ConcurrentHashMap | High-concurrency map |
| CopyOnWriteArrayList | Read-heavy, rare writes |
| BlockingQueue | Producer-consumer patterns |
| ConcurrentSkipListSet/Map | Thread-safe sorted sets/maps |

**Theory**: ConcurrentHashMap uses lock striping - divides map into segments, each with independent lock, allowing concurrent access with minimal contention.

### Weak and Soft References

```java
WeakHashMap<String, Object>  // Values cleared when keys GC'd
SoftReference<Object>        // Cleared before OutOfMemoryError
```

---

## Performance Characteristics Summary

### Time Complexity Table

| Operation | ArrayList | LinkedList | HashSet | TreeSet | HashMap | TreeMap |
|-----------|-----------|------------|---------|---------|---------|---------|
| add() | O(1)* | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| remove() | O(n) | O(1) | O(1) | O(log n) | O(1) | O(log n) |
| contains() | O(n) | O(n) | O(1) | O(log n) | O(1) | O(log n) |
| get(index) | O(1) | O(n) | N/A | N/A | N/A | N/A |

*Amortized

### Space Complexity

| Collection | Space |
|------------|-------|
| ArrayList | n * element_size + overhead |
| LinkedList | n * (node_size) + 2 refs per node |
| HashSet/HashMap | n * (entry_size) + bucket array |
| TreeSet/TreeMap | n * (node_size) + tree overhead |

---

## Why It Works This Way

### Hash Function Theory

Hash tables achieve O(1) average performance through:
1. **Uniform distribution**: Hash function spreads keys evenly
2. **Collision handling**: Chaining or open addressing
3. **Load factor**: Resize when fill ratio exceeds threshold (~0.75)

**Collisions are inevitable** - with finite buckets and infinite possible keys, pigeonhole principle guarantees collisions. Good hash functions minimize clustering.

### Tree Self-Balancing

Red-black trees maintain balance through:
1. Node coloring (red/black)
2. Rotation operations
3. Rebalancing after insertions/deletions

This guarantees O(log n) operations regardless of insertion order - the tree never degrades to a linked list.

### Memory Locality

ArrayList has better cache performance than LinkedList:
- Elements stored contiguously in memory
- CPU prefetches sequential data
- LinkedList nodes scattered - more cache misses

---

## Common Misconceptions

1. **"LinkedList is always faster for insertions"**: Only true when inserting at ends or traversing to position. Random access is O(n).
2. **"ConcurrentHashMap is always thread-safe"**: True for operations on the map, but compound operations (check-then-act) need external sync.
3. **"TreeMap is always sorted"**: Yes, but sorting has overhead - HashMap is faster for non-ordered access.
4. **"Default capacity doesn't matter"**: ArrayList grows 50%, HashMap grows double - wrong initial capacity causes unnecessary resizing.

---

## Further Theory

### From Here

- **Module 4 (Streams)**: Functional-style collection processing
- **Module 5 (Concurrency)**: Concurrent collections for thread-safe access
- **Module 12 (Performance)**: Collection performance optimization

### Deep Dive Resources

- **Java Collections Documentation**: Official API docs
- **Effective Java**: Item 28-35 covers collections best practices
- **Algorithms (Sedgewick)**: Classic data structures text