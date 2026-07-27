# Mock Interview: Caching

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Infrastructure Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a distributed caching layer for a social media platform.

---

## Transcript

**Interviewer**: "Our social media app serves 500M DAU. Each user's feed requires fetching posts from 100-1000 followed users. Reads are 100:1 vs writes. Design the caching strategy."

**Candidate**: "Let me start by understanding the data access patterns. Feed reads are the dominant traffic, and they're read-heavy with temporal locality — most feed views are for recent content. I'd use a multi-layer caching strategy."

**Interviewer**: "Walk me through the layers."

**Candidate**: "Layer 1 — Client-side cache. The mobile/web app caches the last N feed items locally. Layer 2 — CDN cache for static content (images, video thumbnails). Layer 3 — Application cache (Redis) for feed content. Layer 4 — Database cache (buffer pool)."

**Interviewer**: "Focus on Layer 3. How do you cache feeds?"

**Candidate**: "I'd use a two-part cache: 1) Feed ID cache: user_id → list of post IDs (cached for 60s). 2) Post content cache: post_id → post data (cached for 5min, LRU eviction). When a user loads feed, we: get post IDs from feed cache, batch fetch posts from post content cache, fill misses from DB."

**Interviewer**: "How do you handle cache invalidation when a new post is created?"

**Candidate**: "Fanout-on-write: when a user posts, we push the post ID to all their followers' feed ID caches. We also add it to the post content cache. The fanout is asynchronous — we publish to a queue and worker processes update followers' caches. For celebrities (high follower count), we skip fanout and rely on fanout-on-read."

**Interviewer**: "How do you size the cache cluster?"

**Candidate**: "Cache sizing based on working set. Let's estimate: 500M DAU, but only 200M active in any hour. Each feed entry is ~200 bytes. Each feed shows 50 posts initially, with infinite scroll. Feed ID cache per active user: 50 IDs × 8 bytes = 400 bytes. For 200M active users: 200M × 400B = 80GB. Post content cache: Top 10M posts × 5KB = 50GB. Total: ~150GB. We need replication (2x) and headroom: ~500GB Redis cluster."

**Interviewer**: "How do you handle cache stampede?"

**Candidate**: "When a popular post expires from cache, thousands of requests could hit the DB simultaneously. Solutions: 1) Mutex — first request to miss acquires a lock, loads DB, populates cache. Others wait and read from cache. 2) Probabilistic early recomputation — before TTL expires, probabilistically refresh the cache entry. 3) Stale-while-revalidate — serve stale data and refresh asynchronously."

---

## Key Takeaways

- **Multi-layer caching**: Client → CDN → Application → DB
- **Two-part feed cache**: Post IDs separate from post content
- **Fanout-on-write**: Proactive cache population for most users
- **Working set sizing**: Estimate based on active users, not total users
- **Cache stampede prevention**: Mutex, probabilistic refresh, stale-while-revalidate
