# Deep Dive: Optimization Techniques

## 1. The Rules of Optimization
Before applying any optimization technique, you must follow the three rules of optimization (often attributed to Michael Jackson / Tony Hoare):
1.  **Don't do it.**
2.  (For experts only) **Don't do it yet.** (Wait until you have a valid benchmark proving there is a bottleneck).
3.  **Measure, then optimize.**

Premature optimization leads to complex, unmaintainable code that often performs *worse* because it defeats the JVM's built-in optimizations.

## 2. Algorithmic Optimization (Big O)
The most impactful optimization you can make is algorithmic. No amount of JVM tuning or caching will fix an $O(N^2)$ algorithm applied to a million records.
*   **Data Structure Selection**: Changing a `List.contains()` ($O(N)$) to a `HashSet.contains()` ($O(1)$) is often the only optimization an application needs.
*   **Space-Time Tradeoff**: You can often make an algorithm faster by using more memory (e.g., pre-computing values and storing them in a lookup table).

## 3. Caching Strategies
Caching is the ultimate space-time tradeoff. You store the result of an expensive operation in memory so subsequent requests are instant.

### Cache Eviction Policies
A cache must have a bound, otherwise it becomes a memory leak.
*   **LRU (Least Recently Used)**: Evicts the item that hasn't been accessed for the longest time. (Standard for most application caches).
*   **LFU (Least Frequently Used)**: Evicts the item accessed the fewest number of times.
*   **TTL (Time To Live)**: Evicts items after a certain amount of time, regardless of usage (crucial for data that changes in the database).

### Java Caching
While you can build a simple LRU cache using `LinkedHashMap` (by overriding `removeEldestEntry`), modern Java applications should use robust libraries like **Caffeine** or **Guava Cache**, which provide thread-safe, high-performance, concurrent caching with TTL and size bounds.

## 4. Lazy Initialization
Lazy initialization delays the creation of an object, the calculation of a value, or some other expensive process until the first time it is needed.

*   **Why**: If an application has 50 heavy services, but a user only interacts with 2 of them during a session, initializing all 50 at startup wastes memory and slows down boot time.
*   **How**: We saw this in Module 38 (Double-Checked Locking, Initialization-on-Demand Holder) and Module 48 (Memoization).
*   **Frameworks**: Spring Boot heavily utilizes lazy initialization (`@Lazy`) to speed up application context startup.

## 5. Object Pooling
Object pooling maintains a set of pre-initialized objects ready for use, rather than allocating and destroying them on demand.

### When to use Object Pooling:
In modern Java, object creation is incredibly fast, and the Garbage Collector is highly optimized for short-lived objects. **You should almost never pool standard Java objects.** Pooling standard objects actually hurts performance because it increases the lifespan of the objects, forcing the GC to move them to the Old Generation, causing expensive Major GCs.

You should **ONLY** pool objects that are extremely expensive to create or connect to external resources:
*   **Database Connections** (e.g., HikariCP)
*   **Network Sockets**
*   **Large native memory buffers** (e.g., Netty's `ByteBuf` pool)
*   **Heavy cryptographic ciphers or threads**

## 6. String Optimization
Strings are the most heavily used objects in Java.
*   **String Deduplication (Java 8+)**: A JVM flag (`-XX:+UseStringDeduplication`) that tells the G1 GC to scan the heap for duplicate String byte arrays and point them to the same underlying array, saving massive amounts of memory.
*   **`String.intern()`**: Manually places a string into the JVM's String Pool. Use with caution; interning too many unique strings can cause memory issues or CPU spikes.
*   **`StringBuilder`**: Always use `StringBuilder` (or let the compiler use `StringConcatFactory`) for concatenating strings in a loop.