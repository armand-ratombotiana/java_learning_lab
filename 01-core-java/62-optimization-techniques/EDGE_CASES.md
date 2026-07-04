# Edge Cases & Pitfalls: Optimization Techniques

Optimizations often trade one resource (like CPU) for another (like Memory). If not carefully monitored, these optimizations become the source of critical production failures.

## 1. The Cache Memory Leak (Unbounded Caches)
*   **The Scenario**: You want to speed up database queries. You create a `static Map<String, User> userCache = new ConcurrentHashMap<>();`.
*   **The Pitfall**: This is the most common memory leak in Java. The cache has no eviction policy and no maximum size limit. Over days or weeks, every user who logs in is added to the map. The map grows infinitely until the JVM crashes with an `OutOfMemoryError`.
*   **Mitigation**: Never use a plain `HashMap` or `ConcurrentHashMap` as a long-lived cache. Always use a caching library (like Caffeine or Guava) and explicitly set a `maximumSize` or `expireAfterWrite` duration.

## 2. The Cache Stampede (Thundering Herd)
*   **The Scenario**: You have a highly trafficked website. You cache the homepage HTML for 60 seconds.
*   **The Pitfall**: At exactly 60 seconds, the cache expires. In the next millisecond, 500 users request the homepage. Because the cache is empty, all 500 requests bypass the cache and hit the database simultaneously to regenerate the HTML. The database is instantly overwhelmed and crashes.
*   **Mitigation**: Implement **Cache Locking** (or "Refresh Ahead"). When the cache expires, only *one* thread is allowed to query the database and update the cache. The other 499 threads must wait for the first thread to finish, or they are served slightly stale data while the background thread updates the cache.

## 3. The Object Pooling Anti-Pattern
*   **The Scenario**: To avoid the "cost" of the `new` keyword, you create an Object Pool for a standard business object: `UserDTO`. When you need a DTO, you take it from the pool. When you are done, you clear its fields and put it back.
*   **The Pitfall**: 
    1.  **Concurrency Bugs**: If you forget to put the object back, the pool empties. If two threads accidentally grab the same object, data is corrupted. If you forget to clear a field before putting it back, the next user gets dirty data.
    2.  **GC Defeat**: By keeping the `UserDTO`s alive in the pool forever, you force the Garbage Collector to promote them to the Old Generation. This increases the frequency of Stop-The-World Major GCs, actually *decreasing* your application's overall throughput.
*   **Mitigation**: Trust the JVM. Only pool expensive external resources (DB connections, Sockets, Threads). Let the GC handle standard Java objects.

## 4. Lazy Initialization Deadlocks
*   **The Scenario**: You use the Double-Checked Locking pattern or a `synchronized` block to lazily initialize a complex service.
*   **The Pitfall**: If the initialization logic of `Service A` happens to call a method that requires the initialization of `Service B`, and `Service B`'s initialization logic calls a method in `Service A`, the two threads attempting to initialize the services will deadlock waiting for each other's initialization locks.
*   **Mitigation**: Keep lazy initialization logic as simple as possible. Avoid complex circular dependencies or network calls inside initialization blocks. If complex wiring is needed, use a framework like Spring, which has built-in mechanisms to detect and break circular dependencies.

## 5. `String.intern()` Metaspace Exhaustion
*   **The Scenario**: You process a massive CSV file and call `String.intern()` on every cell value to save memory by deduplicating strings.
*   **The Pitfall**: Historically (pre-Java 7), interned strings were stored in the PermGen space, which had a very small, fixed size. Calling `intern()` on millions of unique strings would instantly cause an `OutOfMemoryError: PermGen space`. While modern Java stores the String Pool in the main Heap, the pool is implemented as a fixed-size Hash Table. Stuffing millions of strings into it will cause massive hash collisions, turning $O(1)$ lookups into $O(N)$ linked-list traversals, severely degrading performance.
*   **Mitigation**: Use `-XX:+UseStringDeduplication` (G1 GC) for automatic, safe, background deduplication, or use a custom `ConcurrentHashMap` as a local, short-lived deduplication pool that can be garbage collected when the batch job finishes.