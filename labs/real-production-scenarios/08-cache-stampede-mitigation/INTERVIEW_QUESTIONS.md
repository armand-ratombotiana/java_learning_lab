# Lab 08 — Cache Stampede Mitigation: Interview Questions

**Q1: What is a cache stampede and how does it happen?**

**Answer:** A cache stampede (or cache avalanche) occurs when a popular cache key expires and thousands of concurrent requests try to regenerate it simultaneously. Each request hits the database at the same time, causing a spike in database load — often overwhelming it. This cascades: database becomes slow, all requests timeout, cache is empty, retries amplify the load further. Common triggers: cache TTL expiration on a popular key, cache cluster restart, mass key invalidation.

**Q2: What strategies prevent cache stampedes?**

**Answer:** 1) Locking/mutex: only one process regenerates the cache, others wait or serve stale data. 2) Early recalculation: regenerate cache before TTL expires (probabilistic early expiration). 3) Stale-while-revalidate: serve stale data while asynchronously refreshing the cache. 4) Jitter/TTL spread: add random variation to TTL to prevent mass expiration. 5) Cache warming: pre-populate cache before expected traffic spikes. 6) Read-through cache: cache library handles regeneration atomically.

**Q3: How does probabilistic early expiration (PEE) work?**

**Answer:** Instead of setting a fixed TTL, each request checks if the cache entry's age exceeds a threshold. If so, there's a probability (based on age and expected request rate) that this request will regenerate the cache. The probability increases as the entry ages. This ensures only a few requests regenerate while most get stale data. Expected number of regeneration requests = requests per second × probability. Tune so that expected regenerators = 1 or 2.

**Q4: What is the "thundering herd" problem and how is it related to cache stampede?**

**Answer:** Thundering herd is the same concept: many processes simultaneously try to regenerate the same resource. Cache stampede is the thundering herd applied to caching. The term originated in operating systems (many processes awakened by one event all try to acquire the same resource). Both solved by: lock/mutex (only one acquires), cohort scheduling (staggered), or randomization (probabilistic expiration).

**Q5: How would you implement a cache with stale-while-revalidate pattern?**

**Answer:** Store two values: fresh data + TTL for fresh data, stale data + extended TTL for stale data. When cache expires (fresh TTL exceeded): 1) Return stale data immediately to the request. 2) Asynchronously regenerate the cache in the background. 3) Update both fresh and stale values. 4) Requests during regeneration get stale data instantly. This provides zero-latency reads during regeneration at the cost of slightly stale data for the regeneration period.

**Q6: Design a distributed cache locking mechanism to prevent cache stampede.**

**Answer:** Use a distributed lock (Redis SET NX with TTL). When cache entry is missing: 1) Try to acquire lock for the key. 2) If acquired: regenerate cache, store the result, release the lock. 3) If not acquired: wait briefly for the cache to be populated (spin with timeout), or serve stale data if available. Lock TTL should be short (1-2 seconds) to handle process crashes. Use Redlock for distributed environments.

**Q7: Tell me about a cache stampede incident you handled. (STAR)**

**Answer:** Situation: Black Friday traffic spike caused our product catalog cache to expire — all 500 app instances hit the database simultaneously. Database CPU went to 100%, queries took 30s, all requests failed. Task: Restore service and prevent recurrence. Action: I immediately dropped traffic to database (connection pool kill), served stale cache for all requests. Then I implemented: 1) Stale-while-revalidate pattern (serve stale, async refresh). 2) Probabilistic early expiration. 3) Cache warming before peak hours. 4) Database connection throttling from cache regeneration. Result: Zero database overload during subsequent Black Friday — cache served from stale + background refresh.

**Q8: How do you calculate the optimal TTL for a cache entry?**

**Answer:** Consider: 1) Data freshness requirements (how stale can data be?) 2) Data change frequency. 3) Regeneration cost (how expensive is a cache miss?) 4) Request rate (how many requests hit the cache?) 5) Acceptable probability of stale data. For expensive-to-regenerate data: use longer TTL + stale-while-revalidate. For time-sensitive data: use shorter TTL + early recalculation. Formula: TTL = max acceptable staleness / factor of safety.

**Q9: How does a CDN prevent cache stampede at the edge?**

**Answer:** CDNs (CloudFront, Cloudflare) implement: 1) Shield: a dedicated layer that collapses multiple simultaneous requests for the same object into one origin request. 2) Stale-while-revalidate: serve stale content while fetching fresh from origin. 3) Request collapsing: if 100 requests arrive simultaneously for the same key, only one goes to origin. 4) Predictive prefetching: CDN predicts content needs and pre-warms cache. These are the CDN's built-in cache stampede protections.

**Q10: What monitoring would you set up to detect cache stampede conditions?**

**Answer:** 1) Cache hit ratio per key — sudden drop indicates mass expiration. 2) Database query rate per key — correlation with cache miss events. 3) Cache regeneration latency — slow regeneration increases stampede risk. 4) Request rate per key — high request rate × low hit ratio = stampede risk. 5) Time since last cache regeneration per key — approaching TTL. 6) Number of concurrent regenerations per key — should be 1 at all times.
