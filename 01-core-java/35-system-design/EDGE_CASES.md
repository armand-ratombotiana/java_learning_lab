# Module 35: System Design - Edge Cases & Pitfalls

---

## Pitfall 1: Over-Engineering from Day One

### ❌ Wrong
Designing an architecture meant to support millions of concurrent users (using Kafka, Cassandra, Kubernetes, and microservices) for an MVP that expects 100 users a day. This causes excessive development delays and maintenance nightmares.

### ✅ Correct
Start with a simple, monolithic architecture (or a well-modularized monolith) and a single relational database. Design the system so that boundaries are clear, making it easier to extract services and scale horizontally *when* the traffic actually demands it.

---

## Pitfall 2: Ignoring Cache Invalidation

### ❌ Wrong
Implementing a cache but leaving out a strategy to invalidate or update the cached data when the underlying database is updated. This results in users seeing stale or incorrect data.

### ✅ Correct
Use strategies like Cache-Aside (Lazy Loading) combined with Time-To-Live (TTL) expirations, or Write-Through caching where the cache is updated concurrently with the database.

---

## Pitfall 3: Assuming the Network is Reliable

### ❌ Wrong
Writing distributed code that assumes HTTP calls to microservices or database queries will always succeed quickly.

### ✅ Correct
Embrace the "Fallacies of Distributed Computing". Implement timeouts, retries with exponential backoff, and circuit breakers (like Resilience4j) to prevent a slow network dependency from bringing down the entire system.