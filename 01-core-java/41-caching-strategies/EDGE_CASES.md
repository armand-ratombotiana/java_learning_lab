# Module 41: Caching Strategies - Edge Cases & Pitfalls

---

## Pitfall 1: Thundering Herd (Cache Stampede)

### ❌ Wrong
Setting a hard TTL of 5 minutes on a highly requested, computationally expensive piece of data (like the homepage of a major news site). When the 5 minutes expire, the data is evicted. Suddenly, 1,000 concurrent user requests hit the server, experience a cache miss, and all 1,000 threads attempt to query the database simultaneously to rebuild the cache, crashing the database.

### ✅ Correct
Implement a "Mutex Lock" or "Cache Warming" strategy. When the cache expires, only *one* thread is allowed to query the database and rebuild the cache. The other 999 threads are either briefly blocked waiting for the new cache, or served slightly stale data while the background thread updates the cache asynchronously.

---

## Pitfall 2: Local Cache Consistency in a Cluster

### ❌ Wrong
Using an in-memory local cache (like Caffeine or `ConcurrentHashMap`) in a microservice deployed with 5 instances behind a Load Balancer. If a user updates their profile on Instance A, Instance A invalidates its local cache. However, Instances B, C, D, and E still hold the stale profile data in their local memory. Subsequent requests routed to those instances will return stale data.

### ✅ Correct
If your application runs in a cluster and data consistency is critical, use a Distributed Cache (like Redis) so all instances share the exact same state. If you *must* use local caches for extreme speed, implement a Pub/Sub mechanism (like Redis Pub/Sub or Kafka) to broadcast cache invalidation events to all nodes simultaneously.

---

## Pitfall 3: Caching User-Specific Data Globally

### ❌ Wrong
Caching a dashboard response using a generic key like `dashboard_data` when the dashboard contains personalized user information (e.g., account balance). 
```java
@Cacheable("dashboard")
public Dashboard getDashboard(Long userId) { ... } // ❌ Missing key!
```
User A visits the dashboard, caching their data. User B visits the dashboard, gets a cache hit, and sees User A's private account balance!

### ✅ Correct
Always ensure that the cache key includes a unique identifier for the user or session when caching personalized data.
```java
@Cacheable(value = "dashboard", key = "#userId")
public Dashboard getDashboard(Long userId) { ... } // ✅ Safe
```