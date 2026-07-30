# Interview Deep-Dive: Rate Limiting

## Common Questions

### Q1: Why is fixed window problematic? How do sliding windows fix it?
**Answer**: Fixed window allows double the request rate at window boundaries. A client can send N requests at the end of one window and N at the start of the next, achieving 2N in a short period. Sliding windows prevent this by evaluating a rolling time window at each request.

### Q2: How do you implement distributed rate limiting without a single point of failure?
**Answer**: 
1. Use Redis Cluster with replica shards (no single Redis)
2. Local rate limiting with periodic Redis sync (approximate global rate)
3. Client-side rate limiting with server enforcement as backup
4. Each node independently tracks local + receives global quota updates via gossip

### Q3: How do you rate limit at the API gateway level vs application level?
**Answer**: API gateway rate limiting is coarser and applies across all services (e.g., 1000 req/min per API key). Application-level limiting is finer-grained (e.g., 10 writes/sec per user). Typically both are used: gateway for global protection, application for business logic limits.

## System Design Whiteboard

**Design a rate limiter for a video upload API with 100K RPM.**
- **Algorithm**: Sliding window with Redis sorted sets
- **Keys per client**: `ratelimit:upload:{userId}:{endpoint}`
- **Limits**: Free tier 10/min, Pro 100/min, Enterprise 1000/min
- **Redis**: Cluster mode with 6 shards, read from replicas
- **Local cache**: Token bucket with 1-second sync for low-latency reads
- **Headers returned**: `X-RateLimit-Limit`, `X-RateLimit-Remaining`, `X-RateLimit-Reset`
- **HTTP 429**: Retry-After header with exponential backoff hint

## Key Trade-offs to Discuss
- Accuracy vs memory (sliding log vs fixed window)
- Latency vs consistency (local vs distributed)
- Precision vs simplicity (token bucket vs leaky bucket)
