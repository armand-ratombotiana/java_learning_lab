# Quizzes: Optimization Techniques

Test your knowledge of caching, pooling, and algorithmic optimization.

## Quiz 1: Caching and Optimization Rules

**Q1: What is the FIRST rule of optimization?**
- A) Always use a cache.
- B) Always use primitive arrays instead of Collections.
- C) Don't do it. (Write clean, maintainable code first. Only optimize if a benchmark proves there is a bottleneck).
- D) Use Object Pooling for everything.
*Answer: C*

**Q2: You use a `ConcurrentHashMap` as a cache for user profiles. What critical feature is missing that makes this a dangerous production anti-pattern?**
- A) It is not thread-safe.
- B) It lacks an Eviction Policy (like LRU or a maximum size limit). Without it, the map will grow infinitely and eventually cause an `OutOfMemoryError`.
- C) It does not support null values.
- D) It is too slow for read operations.
*Answer: B*

## Quiz 2: Object Pooling and Strings

**Q1: In modern Java, which of the following objects SHOULD be pooled?**
- A) Database Connections
- B) Simple Data Transfer Objects (DTOs)
- C) Strings
- D) ArrayLists
*Answer: A (Only pool objects that are extremely expensive to create or require external network/OS resources. Pooling standard objects defeats the Garbage Collector and degrades performance).*

**Q2: What is the danger of calling `String.intern()` on millions of unique, dynamically generated strings?**
- A) It deletes the strings from memory.
- B) It causes a `StackOverflowError`.
- C) It stuffs the JVM's internal String Pool (a fixed-size hash table) with millions of entries, causing massive hash collisions that degrade performance, and potentially causing memory issues if the strings are never garbage collected.
- D) It converts the strings to uppercase.
*Answer: C*

## Quiz 3: Edge Cases

**Q1: What is a "Cache Stampede" (Thundering Herd)?**
- A) When the cache server crashes.
- B) When a highly requested cache entry expires, and hundreds of concurrent threads all simultaneously miss the cache and hit the database to regenerate the exact same data, overwhelming the database.
- C) When the cache runs out of memory.
- D) When two threads try to write to the cache at the same time.
*Answer: B*