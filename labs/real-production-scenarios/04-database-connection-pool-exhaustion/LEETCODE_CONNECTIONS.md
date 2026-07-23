# Lab 04 — Connection Pool Exhaustion: LeetCode Connections

## Algorithmic Concepts

| Concept | Connection Pool Application |
|---------|---------------------------|
| Producer-Consumer | Threads produce DB work, pool provides connections (consumed resources) |
| Semaphore / Bounded buffer | Connection pool is a bounded buffer with semaphores for capacity control |
| Queue theory (M/M/c) | Connection pool as a multi-server queue |
| Leaky bucket | Connection acquisition rate limiting |
| Resource allocation | Allocating finite connections among competing threads |
| Reference counting | Tracking how many threads are using each connection |

## LeetCode Problems

**Q1: Design Bounded Blocking Queue (LeetCode 1188)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Thread-safe bounded queue with blocking put/take |
| **Pool Connection** | A connection pool IS a bounded blocking queue. Threads take connections (block if none available) and put them back. HikariCP uses a LinkedBlockingQueue internally. Understanding the producer-consumer pattern is essential. |
| **Algorithmic Lesson** | The bounded blocking queue is the fundamental data structure of connection pools. Pool exhaustion = queue empty + all threads blocked. |

**Q2: LRU Cache (LeetCode 146)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Least Recently Used cache with eviction |
| **Pool Connection** | Connection pools have maxLifetime and idleTimeout — they "evict" connections that exceed these limits. The LRU eviction policy is similar to how pools select which idle connection to evict. |
| **Algorithmic Lesson** | Cache eviction policies mirror connection lifecycle management. Understanding eviction helps configure pool parameters. |

**Q3: The Dining Philosophers (LeetCode 1226)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Avoid deadlock when philosophers need two forks |
| **Pool Connection** | If a thread acquires a connection and then waits for another connection (e.g., two databases), you have a resource pool deadlock — the exact Dining Philosophers problem. |
| **Algorithmic Lesson** | Resource acquisition ordering prevents pool deadlocks, just like fork ordering prevents philosopher deadlock. |

**Q4: Traffic Light Controlled Intersection (LeetCode 1279)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Control which traffic direction can proceed |
| **Pool Connection** | The connection pool controls which threads get connections. Think of each connection as a "green light" for one thread to proceed with DB work. Pool exhaustion = all lights red. |
| **Algorithmic Lesson** | Resource allocation (which thread gets the next connection) is a scheduling problem. Fairness, priority, and starvation are all considerations. |

**Q5: Web Crawler Multithreaded (LeetCode 1242)**

| Aspect | Connection |
|--------|-----------|
| **Problem** | Crawl URLs in parallel with bounded thread pool |
| **Pool Connection** | Like the crawler's thread pool, a connection pool is a shared finite resource across multiple workers. The crawler must handle rate limiting, failure handling, and backpressure — just like connection pools. |
| **Algorithmic Lesson** | Backpressure in a crawler (don't submit more URLs than workers can handle) is the same pattern as connection pool monitoring (don't exceed pool capacity). |
