# Lab 14 — API Rate Limiting Breach: Interview Questions

**Q1: What is API rate limiting and why is it important?**

**Answer:** Rate limiting controls how many requests a client can make to an API within a time window. Prevents: 1) Abuse (DDoS, scraping, credential stuffing). 2) Resource exhaustion (database overload, CPU saturation). 3) Cost spikes (pay-per-request services, cloud costs). 4) Fairness (one noisy tenant affecting others). Implemented at API gateway, load balancer, or application layer. Common algorithms: token bucket, leaky bucket, sliding window, fixed window counter.

**Q2: Compare the token bucket vs. sliding window rate limiting algorithms.**

**Answer:** Token bucket: tokens are added at a fixed rate (refill rate). Each request consumes a token. Burst allowed up to bucket size. Simple, efficient but allows bursts up to bucket size. Sliding window: tracks requests in a rolling time window (e.g., last 60 seconds). More accurate but requires more memory (tracking request timestamps). Token bucket is better for: simple APIs, known burst patterns. Sliding window is better for: strict rate limits, preventing burst abuse at window boundaries.

**Q3: What HTTP status codes are used for rate limiting?**

**Answer:** 429 Too Many Requests — the standard rate limit response. Includes Retry-After header indicating when to retry (seconds or HTTP-date). Some APIs return 503 Service Unavailable for rate limiting (less standard). Best practice: always return 429 with Retry-After header, plus informational headers: X-RateLimit-Limit (max requests), X-RateLimit-Remaining (remaining in window), X-RateLimit-Reset (when window resets — Unix timestamp).

**Q4: How do you implement distributed rate limiting across multiple API gateway instances?**

**Answer:** Use a centralized counter service: Redis with SET NX + TTL for atomic counters. Redis Lua scripting ensures atomic increment-and-check. For higher throughput: 1) Local counters with periodic sync (eventual consistency, some over-limit). 2) Redis Cluster for scalability. 3) Rate limit in-memory with probabilistic sync. 4) Use cloud API gateway (AWS API Gateway, Azure API Management, Google Apigee) which handles distributed rate limiting natively.

**Q5: How would you rate limit a GraphQL API differently from a REST API?**

**Answer:** REST rate limiting: per-endpoint (POST /login has lower limit than GET /search). GraphQL: more complex — all requests hit a single endpoint. Rate limit by: 1) Query complexity score (each field has cost, reject if total > limit). 2) Query depth (max nesting depth). 3) Response size (max bytes). 4) Per-client (API key/tenant). GraphQL often uses a "cost" model: each resolved field costs N points; reject requests that exceed the cost budget. This prevents expensive queries from abusing the API.

**Q6: Your API gateway shows a client exceeding the rate limit despite having a valid quota. What do you investigate?**

**Answer:** 1) Is the rate limit key correct? Client identified by IP, API key, or authenticated user? 2) Are there multiple clients behind the same IP (NAT, shared proxy)? Use API key instead of IP. 3) Is the rate limiter state consistent across instances? Check Redis/node synchronization. 4) Check for clock drift between instances (affects sliding window). 5) Check if rate limit is per-instance or global. 6) Check if the client is retrying too aggressively (no backoff). 7) Check if the client is legitimately fast (their quota may be too low).

**Q7: Design a multi-tier rate limiting system.**

**Answer:** Tier 1 (Global): 1M requests/second at the CDN/global load balancer. Tier 2 (API Gateway): per-IP at 10K/min, per-API-key at 100K/min. Tier 3 (Application): per-user at 1K/min, per-endpoint at 500/min (stricter for expensive endpoints). Tier 4 (Database): connection pool limits, query rate limits. Each tier has different granularity and cost. Lower tiers (CDN) are coarse but inexpensive to check. Higher tiers (app) are fine-grained but more expensive.

**Q8: You're being DDoSed — rate limiting isn't helping because traffic is distributed across many IPs. What do you do?**

**Answer:** 1) Application-layer DDoS: rate limit by API key (authenticated users) — unauthenticated requests get much stricter limits. 2) WAF rules: block requests with known attack signatures. 3) CAPTCHA for suspicious traffic. 4) Geographic IP blocking if attack is from unexpected regions. 5) Challenge-based rate limiting: serve JavaScript challenge (Proof of Work) before allowing requests. 6) Anycast-based DDoS protection (Cloudflare, AWS Shield, Akamai). 7) Throttle or block based on request pattern (User-Agent, headers, request timing).

**Q9: How do you set rate limit quotas for different customer tiers?**

**Answer:** Tier-based quotas: Free (10 req/min), Pro (1000 req/min), Enterprise (100K req/min). Implementation: API key identifies tier, rate limiter applies per-tier limits. Design: 1) Hard limit (always enforced) + soft limit (triggers warning, not block). 2) Burst allowance: Enterprise gets larger burst bucket. 3) Over-query: 429 for hard limit exceeded, but offer paid overage. 4) Rate limit headers let clients self-regulate. 5) Monitoring: track utilization per tier, adjust limits based on actual usage patterns.

**Q10: Tell me about a rate limiting incident you handled. (STAR)**

**Answer:** Situation: During a sale event, legitimate users received 429 errors because our rate limiters were triggered by the traffic spike. Task: Adjust rate limits to accommodate legitimate traffic while still preventing abuse. Action: I analyzed traffic patterns — 95% was legitimate users with valid API keys, 5% was scrapers. I implemented tiered rate limiting: higher limits for authenticated users with good history, lower for unauthenticated. I added dynamic rate limiting: auto-increase limits for trusted clients during events, auto-decrease for suspicious patterns. I also added burst allowance for flash sales (double the usual limit). Result: Legitimate users no longer hit 429s. Scrapers still blocked. Sale event completed successfully.
