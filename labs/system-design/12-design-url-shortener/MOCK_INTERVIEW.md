# Mock Interview: URL Shortener Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Senior Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a URL shortener like TinyURL or bit.ly.

---

## Transcript

**Interviewer**: "Design a URL shortener. Users submit a long URL and get a short alias. When they visit the short URL, they're redirected to the original. Support 100M URL creations/month and 10B redirects/month."

**Candidate**: "Let me clarify requirements. Do we need custom aliases? Analytics? Expiration? User accounts?"

**Interviewer**: "Good questions. Yes to custom aliases (optional). Yes to basic analytics (click count, referrer, geo). No to expiration by default. Yes to user accounts for managing URLs."

**Candidate**: "Estimation: 100M creations/month → ~35 QPS writes. 10B redirects/month → ~3,500 QPS reads. 30:1 read:write ratio. Storage: 100M × 500B (key + URL + metadata) = 50GB/month → 600GB/year. Cache: 80% hit rate means 2,800 QPS to cache, 700 QPS to DB."

**Interviewer**: "How do you generate the short key?"

**Candidate**: "Base62 encoding (a-z, A-Z, 0-9) of a unique ID. For 100B URLs, 7 characters gives 62^7 = 3.5 trillion combinations. ID generation: Snowflake-style unique ID generator (timestamp + worker ID + sequence). Alternative: hash the URL (MD5 truncated to 7 chars) then handle collisions. I'd prefer unique ID generation — no collision handling needed, it's simpler."

**Interviewer**: "Design the write path."

**Candidate**: "POST /shorten { url, custom_alias?, ttl? } → API server generates unique ID → Base62 encodes → Stores in DB (id, short_key, long_url, user_id, created_at, click_count). Custom alias uses a separate namespace — if already taken, return 409 Conflict. The API returns { short_url: "https://short.ly/abc1234" }."

**Interviewer**: "Design the read path (redirect)."

**Candidate**: "GET /abc1234 → CDN edge (if cached) → API server checks Redis cache → if miss, check DB → store in cache → return 301 redirect. 301 (permanent) is chosen over 302 because browsers cache it, reducing load. For analytics tracking, we use a redirect service that logs the redirect before responding, or a tracking pixel."

**Interviewer**: "How do you handle cache stampede for popular URLs?"

**Candidate**: "Popular URLs (going viral on social media) could see millions of hits in minutes. The first miss loads from DB and populates cache. To prevent stampede: 1) Proactive caching — pre-heat cache for known popular URLs via analytics, 2) Locking — first miss acquires distributed lock, loads DB, populates cache, others wait. 3) Stale-while-revalidate — serve cached value even if slightly expired, refresh asynchronously."

**Interviewer**: "How do you scale the database?"

**Candidate**: "Reads are dominant (30:1). Start with read replicas — all writes to master, reads to replicas. At higher scale, shard by short_key hash. Each shard has its own master + replicas. For the cache, Redis Cluster with consistent hashing. CDN (CloudFront/Cloudflare) for static responses at edge locations — reduces cache server load."

**Interviewer**: "What about analytics?"

**Candidate**: "Analytics are write-heavy (one write per redirect). We don't update the counter on every redirect — that would overwhelm the DB. Instead: 1) Log redirect events to Kafka, 2) Streaming processor aggregates events (count, referrer domain, geo), 3) Periodically batch-update the main DB (every 5 min for click count). For real-time analytics, query the streaming store (e.g., ClickHouse) directly."

---

## Key Takeaways

- **Base62 encoding**: Compact keys from sequential IDs
- **301 vs 302**: 301 for browser caching, 302 when analytics needed per request
- **Multi-layer caching**: CDN → Redis → DB
- **Cache stampede prevention**: Locking/proactive caching for viral URLs
- **Async analytics**: Kafka → streaming processor → batch DB updates
- **Sharding**: Hash-based when read replicas aren't enough
