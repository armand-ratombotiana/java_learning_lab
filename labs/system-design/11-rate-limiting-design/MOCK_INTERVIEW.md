# Mock Interview: Rate Limiting Design

> System Design Mock Interview — 45-minute session

---

## Setup

**Role**: Platform Engineer Interviewer  
**Candidate Level**: Senior Engineer (L5)  
**Problem**: Design a distributed rate limiter for a public API platform.

---

## Transcript

**Interviewer**: "We're building a public API that's consumed by thousands of developers. Each developer has a rate limit: X requests per second/minute/hour/day. Design the rate limiter."

**Candidate**: "Rate limiting needs to be: accurate (enforce the limit exactly), fast (add <1ms latency), distributed (work across multiple servers), and configurable (different limits per API key)."

**Interviewer**: "Start with the algorithm choice."

**Candidate**: "I'd use a combination: Token bucket for per-second rate limiting (simple, allows bursts) + Sliding window log for per-minute/hour limits (accurate, prevents boundary crossing abuse). The sliding window ensures fair counting — better than fixed windows where a user can send 100 requests at 11:59:59 and another 100 at 12:00:00."

**Interviewer**: "How do you implement this?"

**Candidate**: "The token bucket is implemented in Redis: `INCR key` for counter, `EXPIRE key 2` for TTL sliding. For the sliding window, I'd use a sorted set: `ZADD key timestamp timestamp` + `ZREMRANGEBYSCORE key -inf current_window_start` + `ZCARD key`. This gives accurate count in the last window. But sorted sets use O(log n) per operation — for very high QPS, I'd switch to the sliding window counter (Lua script with sub-window counters)."

**Interviewer**: "Let's talk about the distributed aspect."

**Candidate**: "If we have 10 API servers and each has a local rate limiter, a user could send 100 requests per second and get through (10 requests per server). Solution: use Redis as the centralized counter. All servers check against the same Redis key. Challenge: Redis becomes a single point of failure and could be a bottleneck. Mitigation: 1) Redis Cluster for scalability, 2) Local caching with best-effort accuracy for warm requests."

**Interviewer**: "How do you handle Redis failure?"

**Candidate**: "Circuit breaker pattern. If Redis is unreachable, fall back to local rate limiting. The local limiter uses a more conservative limit (e.g., 80% of the actual limit). When Redis recovers, synchronize the local state. This trades some accuracy for availability — acceptable because rate limiting is a soft enforcement."

**Interviewer**: "What headers should the response include?"

**Candidate**: "Standard rate limit headers: `X-RateLimit-Limit` (max requests per window), `X-RateLimit-Remaining` (remaining in current window), `X-RateLimit-Reset` (time when window resets, Unix timestamp). When exceeded: 429 Status + `Retry-After` header. The response body includes `{error: "rate_limit_exceeded", retry_after_seconds: 45}`."

**Interviewer**: "How do you support different rate limit tiers?"

**Candidate**: "Configurable rate limit policies stored in a database or config file. API key → plan → limits per duration. Example: `{api_key_123: {second: 10, minute: 100, hour: 1000, day: 10000}}`. The rate limiter loads the config for the API key, applies the most restrictive limit that is exceeded. For burst handling, we allow `minute_limit / 60` average with `burst_limit` maximum (token bucket)."

---

## Key Takeaways

- **Token bucket + sliding window**: Combines burst tolerance with accurate counting
- **Redis-backed**: Centralized counter for distributed rate limiting
- **Local fallback**: Best-effort limiting when Redis is unavailable
- **Rate limit headers**: Tell clients exactly what they consumed
- **Multi-tier limits**: Per API key, configurable per duration
