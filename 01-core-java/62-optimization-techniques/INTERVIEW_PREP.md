# Interview Preparation: Optimization Techniques

This document covers advanced questions related to Big O, caching strategies, and object pooling.

## Q1: What are the three rules of optimization?
**Answer:**
1.  **Don't do it.** (Write clean, readable, maintainable code first).
2.  **Don't do it yet.** (Wait until the application is feature-complete).
3.  **Measure, then optimize.** (Never guess where the bottleneck is. Use a profiler like JFR to find the exact line of code causing the issue, establish a baseline, make one change, and measure again to prove it worked).

## Q2: Explain the "Cache Stampede" (Thundering Herd) problem and how to solve it.
**Answer:**
A Cache Stampede occurs when a highly requested cache entry expires (TTL) or is evicted. Suddenly, 500 concurrent threads all check the cache, see a miss, and simultaneously hit the backend database to regenerate the exact same data. This instantly overwhelms and crashes the database.
**Solution**: Use **Cache Locking** or a `FutureTask` wrapper. When a cache miss occurs, only *one* thread is allowed to proceed to the database. The other 499 threads must wait (block) on the `Future.get()` call of the first thread. When the first thread finishes, all 500 threads receive the data simultaneously with only one database query executed.

## Q3: Why is Object Pooling considered an anti-pattern for standard Java objects?
**Answer:**
In the 1990s, creating objects was slow, so developers pooled them. Modern JVMs allocate small objects in microseconds and use generational Garbage Collection (like G1GC) that cleans up millions of short-lived objects incredibly efficiently.
If you pool standard objects (like DTOs):
1.  You introduce complex concurrency bugs (threads grabbing the same object, forgetting to return it).
2.  You introduce data corruption (forgetting to clear dirty fields before returning the object to the pool).
3.  **You defeat the GC**: By keeping objects alive permanently in a pool, the GC promotes them to the Old Generation. This fills up the Old Gen, triggering expensive, Stop-The-World Major GCs, which degrades overall application throughput.
**Exception**: You should *only* pool objects that are extremely expensive to create or tie up external OS resources (Database Connections, Threads, Sockets).

## Q4: How does `LinkedHashMap` facilitate building an LRU cache?
**Answer:**
`LinkedHashMap` maintains a doubly-linked list running through all its entries, preserving the insertion order.
If you pass `accessOrder = true` to its constructor, it changes its behavior: every time an element is accessed (read or written), it is moved to the end of the linked list. The element at the head of the list is therefore the Least Recently Used (LRU).
Furthermore, it provides a protected method `removeEldestEntry(Map.Entry eldest)`. If you override this method to return `true` when `size() > capacity`, the map will automatically delete the head (LRU) element every time you add a new element that exceeds the capacity.

## Q5: What is the difference between Algorithmic Optimization and Micro-Optimization?
**Answer:**
*   **Micro-Optimization**: Tweaking small bits of code (e.g., changing `i++` to `++i`, or using bitwise shifts instead of division). In Java, this is almost always a waste of time because the JIT compiler does this automatically at runtime.
*   **Algorithmic Optimization**: Changing the fundamental data structure or mathematical approach (e.g., changing a nested loop over an `ArrayList` ($O(N^2)$) to a single loop using a `HashSet` lookup ($O(N)$)).
Algorithmic optimization is the only optimization that matters for scaling. The JIT compiler cannot change your algorithm; it can only make your bad algorithm run slightly faster.