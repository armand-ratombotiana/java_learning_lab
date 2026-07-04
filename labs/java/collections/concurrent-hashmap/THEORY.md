# ConcurrentHashMap Theory

## 💡 The Concurrency Problem
A standard `HashMap` is not thread-safe. If multiple threads attempt to mutate a `HashMap` simultaneously, several critical failures can occur:
1. **Lost Updates**: Thread A and Thread B both try to insert a node at the same bucket simultaneously. One overwrites the other.
2. **Infinite Loops (Pre-Java 8)**: During a resize operation, if two threads attempt to rehash the map concurrently, the linked list chains can form a cyclic graph. Subsequent `get()` operations will enter an infinite loop, consuming 100% CPU.
3. **Visibility Issues**: Changes made by Thread A might not be immediately visible to Thread B due to CPU caching.

## 🧱 The Evolution of Solutions

### Generation 1: `Hashtable` and `Collections.synchronizedMap()`
The earliest solution was simply to slap a `synchronized` block around every method (`get`, `put`, `remove`).
- **The Problem**: This creates a massive bottleneck. Only one thread can access the map at a time, regardless of which bucket they are trying to access. It serializes all operations.

### Generation 2: Lock Striping (Java 7 `ConcurrentHashMap`)
Java 7 introduced **Lock Striping**. Instead of one lock for the entire map, the map was divided into 16 "Segments". Each segment had its own lock.
- **The Benefit**: Up to 16 threads could write to the map simultaneously, as long as their keys hashed to different segments.
- **The Problem**: It was still locking-heavy, and sizing the segments was rigid.

### Generation 3: CAS and Node Locking (Java 8+ `ConcurrentHashMap`)
Java 8 completely overhauled the architecture. It abandoned Segments entirely.
Instead, it locks at the **Node level** (the head of the bucket chain) using a `synchronized` block on the first node, and uses **CAS (Compare-And-Swap)** hardware instructions for lock-free insertions into empty buckets.
- **The Benefit**: Massive concurrency. Thousands of threads can write simultaneously as long as they are writing to different buckets. Reads are almost entirely lock-free.