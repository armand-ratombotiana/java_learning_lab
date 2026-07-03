# Module 41: Caching Strategies - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: What is the difference between Write-Through and Write-Behind caching?
**Answer**:
- **Write-Through**: Every time the application updates data, it updates the cache and synchronously writes the data to the underlying database. The application only moves forward once *both* are updated. This ensures 100% data consistency but introduces latency on write operations.
- **Write-Behind (Write-Back)**: The application updates the cache, and the cache immediately returns success to the user. The cache then asynchronously writes the data to the database in the background. This provides lightning-fast write performance (ideal for high-frequency updates like tracking views on a video), but risks permanent data loss if the cache node crashes before the background sync completes.

### Q2: Why is a Distributed Cache (like Redis) generally preferred over Local In-Memory Cache (like `ConcurrentHashMap`) in a Microservices architecture?
**Answer**:
In a cloud environment, a single microservice might be scaled out to 10 instances behind a load balancer. 
If you use a Local Cache:
1. User A updates their profile. The request hits Node 1. Node 1 updates the DB and clears its local cache.
2. User A refreshes the page. The load balancer routes the request to Node 2.
3. Node 2 still has the *old* profile data in its local memory. It returns stale data to the user.
A Distributed Cache centralizes the memory state. When Node 1 updates the DB, it updates Redis. When Node 2 reads, it reads from Redis, guaranteeing all 10 instances share a perfectly consistent view of the data.

### Q3: What is Cache Eviction, and what does LRU mean?
**Answer**:
Caches exist in RAM, which is highly limited compared to hard drive space. When the cache reaches its memory limit, it must "evict" (delete) old data to make room for new data.
**LRU (Least Recently Used)** is the most common eviction algorithm. It keeps track of when each item was last accessed. When space is needed, it deletes the item that has gone the longest amount of time without being read or written to, assuming that recently used data is more likely to be requested again soon.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: The Cache Stampede (Thundering Herd)
**Problem**: You manage a popular news website. The homepage data is cached in Redis with a Time-To-Live (TTL) of 60 seconds. Generating the homepage requires 5 seconds of intensive database aggregation. What happens when the 60 seconds expire, and how do you prevent the system from crashing?

**Solution**:
When the TTL expires, the key is removed from Redis. In that exact second, 10,000 users request the homepage. Because the cache is empty, all 10,000 requests bypass the cache and hit the database simultaneously to run the 5-second aggregation query. The database is instantly overwhelmed and crashes. This is a **Cache Stampede**.

**Prevention**:
1. **Mutex Locks**: When the cache misses, the first thread to notice acquires a distributed lock (e.g., using Redis `SETNX`). Only that single thread is allowed to query the database. The other 9,999 threads must wait a few milliseconds and poll the cache again.
2. **Soft TTL / Background Refresh**: Instead of waiting for the key to expire, store a "Soft Expiry" timestamp inside the payload. When a thread reads the payload and notices the Soft Expiry has passed, it returns the slightly stale data to the user immediately, but triggers an asynchronous background thread to recalculate the data and update Redis. No users are ever blocked, and the database only receives 1 query.