# Module 41: Caching Strategies - Quizzes

---

## Q1: Cache Invalidation Topologies
In which caching strategy does the application write data to the cache, and then the cache asynchronously updates the database in the background?

A) Cache-Aside (Lazy Loading)
B) Write-Through
C) Write-Behind (Write-Back)
D) Local Caching

**Answer**: C
**Explanation**: Write-Behind (or Write-Back) prioritizes extreme write speeds by returning success as soon as the data hits the cache. The cache engine is then responsible for syncing that data to the slower persistent database asynchronously.

---

## Q2: Distributed vs Local Caching
What is the primary drawback of using a Local (In-Memory) Cache in a distributed microservices environment?

A) It is much slower than a Distributed Cache like Redis.
B) If multiple instances of the service are running, the cache state is not shared between them, leading to inconsistent, stale data across nodes.
C) Local caches cannot store Java objects.
D) Local caches require an internet connection.

**Answer**: B
**Explanation**: Local caches (like Caffeine) live in the heap of a specific JVM. If a load balancer routes Request 1 to Node A (which updates the database and invalidates its local cache), and Request 2 to Node B (which didn't get the invalidation memo), Node B will return stale data to the user.

---

## Q3: Thundering Herd Problem
What causes a "Thundering Herd" (or Cache Stampede) in a caching architecture?

A) Storing images instead of text in the cache.
B) The cache server running out of RAM and crashing.
C) A highly requested cache entry expires (TTL drops), causing hundreds of concurrent threads to experience a cache miss and hit the database simultaneously to regenerate the data.
D) Using Redis instead of Memcached.

**Answer**: C
**Explanation**: When a popular cache key expires, the sudden flood of traffic bypassing the cache and hitting the slower database directly can overwhelm and crash the database. This is mitigated using locking or background cache warming.