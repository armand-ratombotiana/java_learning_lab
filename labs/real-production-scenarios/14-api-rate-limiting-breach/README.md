# Lab 14: API Rate Limiting Breach — Client Blockade

## Situation Overview

**Scenario**: GitHub/Twitter-style — API clients blocked en masse, revenue-affecting downtime

**Severity**: P0 (Critical) / SEV1

**Impact Assessment**:
- 3,427 API clients blocked by aggressive rate limiting within 15 minutes
- All paid-tier API consumers (enterprise partners) receiving HTTP 429 responses
- Revenue-affecting incident: 12 enterprise customers unable to process transactions
- API gateway (Kong) CPU at 98% due to rate limiter processing overhead
- Redis cluster (rate limiter backend) reaching memory capacity (7.2 GB / 8 GB)
- Customer support ticket volume: 2,100+ in first hour
- Incident duration: 2 hours 15 minutes (08:45 UTC — 11:00 UTC)

**Affected Systems**:
- API Gateway: Kong Ingress Controller (12 nodes behind a load balancer)
- Rate Limiter Backend: Redis Cluster (6 nodes, 8 GB each)
- All API endpoints under `/api/v2/*` and `/api/v3/*`
- Rate limit configuration: 100 requests/minute for paid tier
- Client usage analytics pipeline (real-time dashboard)
- Revenue: $3.8M/day in API processing fees (including enterprise contracts)

**Detection**: Kong's rate limiter plugin logs show 429 responses increasing from 0.01% to 87% of all responses within 10 minutes. PagerDuty alert `KongRateLimitExceeded` fires at 08:49 UTC.

**Business Context**: The incident was triggered when an enterprise client's misconfigured integration began sending requests at 1,500 req/min (15x the paid tier limit of 100 req/min). The rate limiter correctly blocked this client, but because the rate limit was configured with no burst allowance and no graduated limiting, the client's retry logic (without exponential backoff) immediately re-sent all rejected requests, creating a self-amplifying retry storm. Additionally, the rate limiter's token bucket was configured with bucket size = limit (no burst capacity), meaning even momentary traffic spikes triggered blocking.

**Engineering Teams Involved**:
- API Platform Team (Kong configuration, rate limiter tuning)
- Enterprise Support Team (client communication)
- SRE Team (incident management, Redis performance)
- Security Team (abuse detection, DDoS analysis)
- Client Integration Team (partner API usage patterns)

## References

1. GitHub API Rate Limiting Documentation — https://docs.github.com/en/rest/overview/resources-in-the-rest-api
2. Twitter API Rate Limits — https://developer.twitter.com/en/docs/twitter-api/rate-limits
3. Kong API Gateway Rate Limiting Plugin — https://docs.konghq.com/hub/kong-inc/rate-limiting/
4. Google Cloud Armor Rate Limiting — https://cloud.google.com/armor/docs/rate-limiting-overview
5. Amazon API Gateway Throttling — https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-request-throttling.html
6. Google SRE Book — Chapter 21: Handling Overload — https://sre.google/sre-book/handling-overload/
7. NGINX Rate Limiting — https://www.nginx.com/blog/rate-limiting-nginx/
8. Redis Rate Limiting Patterns — https://redis.io/commands/INCR/

## Key Metrics

| Metric | Value | Normal Range |
|--------|-------|-------------|
| 429 responses | 87% of all API responses | < 0.01% |
| Active clients blocked | 3,427 | 0 |
| Rate limit per client | 100 req/min (paid tier) | Should allow burst |
| Redis memory usage | 7.2 GB / 8 GB | < 6 GB |
| Kong CPU utilization | 98% | < 60% |
| API success rate | 12.8% | 99.95% |
| Support tickets | 2,100+ / hour | < 50 / hour |
| Time to detect | 4 minutes (auto) | < 1 minute |
| Time to remediate | 2h 15m | < 15 minutes |

## Detailed Impact Analysis

### Client Blocking Cascade Timeline

| Time | Clients Blocked | 429 Rate | Redis Memory | Event |
|------|----------------|----------|-------------|-------|
| 08:42 | 1 | 0.3% | 4.2 GB (52%) | MegaRetail hits limit |
| 08:45 | 1 | 2.1% | 4.8 GB (60%) | Retry storm begins |
| 08:47 | 12 | 12% | 5.7 GB (71%) | Redis eviction starts |
| 08:48 | 347 | 34% | 6.5 GB (81%) | Collateral blocking escalates |
| 08:49 | 1,247 | 52% | 7.0 GB (88%) | PagerDuty alert fires |
| 08:52 | 2,847 | 71% | 7.6 GB (95%) | Incident declared |
| 08:55 | 3,427 | 87% | 7.9 GB (99%) | Peak impact |
| 09:05 | 3,427 | 87% | 7.2 GB (90%) | Redis analysis |
| 09:18 | 3,427 | 65% | 5.5 GB (69%) | MegaRetail limit increased |
| 09:30 | 847 | 23% | 4.0 GB (50%) | Redis memory increased |
| 09:50 | 47 | 5% | 3.8 GB (48%) | Graduated limiting applied |
| 10:30 | 12 | 2% | 3.5 GB (44%) | Recovery in progress |
| 11:00 | 0 | 0.3% | 3.2 GB (40%) | Incident resolved |

### Client Distribution by Impact Level

| Impact Level | Number of Clients | Total Revenue Impact | SLA Breach |
|-------------|-------------------|---------------------|------------|
| Full block (429 every request) | 3,427 | $285,000 | Yes (12 enterprise) |
| Partial block (50%+ 429 rate) | 847 | $68,000 | Yes (8 enterprise) |
| Intermittent blocking | 1,200 | $23,000 | No |
| No impact | 8,000+ | $0 | No |
| **Total affected** | **3,427** | **$376,000** | **12 SLA breaches** |

### Rate Limiter Resource Consumption

| Resource | Baseline (Normal) | During Incident | Peak |
|----------|------------------|-----------------|------|
| Redis memory | 3.2 GB (40%) | 7.9 GB (99%) | Capped at 8 GB |
| Redis keys | 82,000 | 2,100,000 | 25x normal |
| Redis INCR/s | 50/sec | 8,500/sec | 170x normal |
| Redis CPU | 20% | 92% | 4.6x normal |
| Kong CPU | 45% | 98% | 2.2x normal |
| Kong memory | 1.2 GB | 3.8 GB | 3.2x normal |
| Network bandwidth | 50 Mbps | 850 Mbps | 17x normal |

### Redis Key Analysis

The rate limiter creates one key per minute per (client × endpoint) combination:

```
Key pattern: ratelimit:{consumer_id}:{route_id}:{YYYYMMDDHHMM}

Normal state (8,000 clients × 24 endpoints):
  Keys created: 8,000 × 24 = 192,000 per minute
  Keys expired: 192,000 per minute (60s TTL)
  Steady state: ~192,000 keys in Redis

During incident:
  Keys created: (8,000 + 3,427 abusive retries) × 24 = 274,248 per minute
  MegaRetail alone: 3,000 req/min × 24 endpoints = 72,000 keys/minute
  Total keys accumulating: 274,248 - 192,000 (expired) = 82,248 net new per minute
  After 15 minutes: 1,234,000 additional keys (82,248 × 15)
  Total keys: 192,000 + 1,234,000 = 1,426,000

But actual was 2.1M keys — indicating the retry storm created even more keys
due to Redis eviction causing key recreation.
```

### Rate Limit Configuration Comparison

| Aspect | Before (Failing) | After (Fixed) | Improvement |
|--------|-----------------|---------------|-------------|
| Base limit | 100 req/min | 100 req/min | Same |
| Burst capacity | 0 (implicit) | 50 req | Allows spikes |
| Effective limit | 100 req/min | 150 req/min (burst) | +50% |
| Graduated warning | None | 80 req/min | Early notification |
| Retry-After header | Not set | Required | Guides clients |
| Redis eviction policy | allkeys-lru | noeviction | No data loss |
| Fault tolerant | false | true | Graceful degradation |

### Retry Storm Analysis

MegaRetail's retry pattern before fix:

```
Original request rate: 1,500 req/min (15x limit)
429 response: Blocks all requests
Retry behavior: Immediate retry (no backoff)
Retry rate: 1,500 req/min (same as original)
Total rate on rate limiter: 3,000 req/min (original + retries)

Retry cycle:
  t=0: 25 requests → all 429
  t=0.2s: 25 retries → all 429 (same second)
  t=0.4s: 25 more retries → all 429
  ... repeats every 200ms
  
After fix (exponential backoff):
  t=0: 25 requests → all 429 with Retry-After: 60
  t=1s: 25 retries → 429
  t=2s: 25 retries → 429
  t=4s: 25 retries → 429
  t=8s: 25 retries → 429
  t=16s: 25 retries → 429 (backoff maxed)
  t=60s: 25 retries → 200 (rate limit expires, or limit increased)
```

## Lessons Learned

1. **No burst allowance**: Token bucket bucket size = limit (100 tokens, refill 100/minute). Zero burst capacity means even momentary spikes cause blocking.

2. **No graduated limiting**: The system had only a hard limit (100 req/min). No warning stage (e.g., 80 req/min → return 429 with Retry-After) or soft limit (e.g., 90 req/min → add delay).

3. **Aggressive retry loop**: Blocked clients without proper exponential backoff retried immediately, creating a self-amplifying storm that consumed rate limiter resources.

4. **No client isolation**: Rate limiting was per-client but all clients shared the same limiter pool. One abusive client could degrade the rate limiter for all clients.

5. **No quota increase workflow**: Enterprise clients had no mechanism to request or automatically receive higher rate limits.

6. **Redis memory pressure**: Rate limiter used Redis INCR with 60-second TTL per key. At 3,427 clients × multiple endpoints, key count consumed 90% of Redis memory.

7. **No fault tolerance**: Kong rate limiter configured with `fault_tolerant: false` meant Redis failures cascaded to Kong failures.

8. **No retry behavior requirements**: API terms of service did not mandate exponential backoff or Retry-After header consumption.

9. **Single Redis cluster**: Rate limiting shared a Redis cluster with caching, session storage, and other workloads.

10. **No capacity planning**: Redis memory for rate limiting was sized for normal traffic, not accounting for abusive client behavior.

### Appendix A: Kong Rate Limiting Plugin Reference

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| `second` | number | null | Requests per second |
| `minute` | number | null | Requests per minute |
| `hour` | number | null | Requests per hour |
| `day` | number | null | Requests per day |
| `month` | number | null | Requests per month |
| `year` | number | null | Requests per year |
| `limit_by` | string | consumer | How to identify clients: consumer, ip, service, header |
| `policy` | string | cluster | Rate limiting policy: local, cluster, redis |
| `fault_tolerant` | boolean | true | Allow requests when Redis/DB unavailable |
| `redis_host` | string | | Redis server hostname |
| `redis_port` | int | 6379 | Redis server port |
| `redis_timeout` | int | 2000 | Redis connection timeout (ms) |
| `redis_database` | int | 0 | Redis database index |
| `hide_client_headers` | boolean | false | Hide X-RateLimit headers |
| `burst` | number | 0 | Burst capacity above the limit |

### Appendix B: Token Bucket Algorithm

```
Token Bucket Algorithm:
  1. Bucket capacity = limit + burst
  2. Tokens refill at rate/second
  3. Each request consumes 1 token
  4. If no tokens available → 429

Example: limit=100, burst=50, refill=100/minute
  Bucket: 150 tokens
  Refill: 1.67 tokens/second
  Request at t=0: consume 1 token → 149 remaining
  Request at t=1: consume 1 token → 148.67 remaining (partial token)
  ...
  Burst: Can serve 150 requests in first second
  Sustained: 100 requests per minute after startup
  
  vs Without burst (limit=100, burst=0):
  Bucket: 100 tokens
  Refill: 1.67 tokens/second
  101st request in same minute: 429 Too Many Requests
```

### Appendix C: Graduated Rate Limiting Implementation

```
Tier 1: WARNING (80% of limit = 80 req/min)
  Response: 
    HTTP 200 OK
    Headers:
      X-RateLimit-Limit: 100
      X-RateLimit-Remaining: 20
      X-RateLimit-Warning: approaching_rate_limit
      X-RateLimit-Reset: 1623456789
  Action: Notify client via response header only

Tier 2: SOFT LIMIT (100% of limit = 100 req/min)
  Response:
    HTTP 200 OK (with artificial delay)
    Headers:
      X-RateLimit-Limit: 100
      X-RateLimit-Remaining: 0
      X-RateLimit-Delayed: true
      X-RateLimit-Delay: 100ms
  Action: Add 100ms artificial delay to each request
          Encourages client to slow down without blocking

Tier 3: HARD LIMIT (150% of limit with burst = 150 req/min)
  Response:
    HTTP 429 Too Many Requests
    Headers:
      X-RateLimit-Limit: 100
      X-RateLimit-Remaining: 0
      X-RateLimit-Reset: 1623456789
      Retry-After: 60
  Action: Block request with clear retry guidance
```

### Appendix D: Redis Key Design for Rate Limiting

```
Key pattern: ratelimit:{consumer}:{endpoint}:{window}

Sliding window implementation:
  window = current minute (epoch / 60)
  key = ratelimit:{consumer}:{endpoint}:{window}
  INCR key
  EXPIRE key 60

Memory calculation:
  1 key ≈ 100 bytes (overhead + value)
  Per consumer per endpoint: 24 keys/minute
  10,000 consumers × 24 endpoints: 240,000 keys
  Total memory: 240,000 × 100 bytes ≈ 24 MB/minute
  Max keys simultaneously: 240,000 × 2 (overlapping windows) = 480,000
  Max memory: 480,000 × 100 bytes ≈ 48 MB

During incident:
  3,000 req/min abusive × 24 endpoints = 72,000 extra keys/minute
  But with retry storm: 8,500 req/s × 24 endpoints × 60s = 12,240,000 keys
  At 100 bytes each: 12,240,000 × 100 = 1.22 GB
  Plus normal: 480,000 × 100 = 48 MB
  Total: ~1.27 GB for rate limit keys
  But Redis was at 7.9 GB — suggesting OTHER workloads shared Redis
```

### Appendix E: Client Retry Best Practices

```java
/**
 * Exponential backoff with jitter implementation
 * 
 * Retry delays:
 * Attempt 1: 1.0s ± 20% = 0.8s - 1.2s
 * Attempt 2: 2.0s ± 20% = 1.6s - 2.4s
 * Attempt 3: 4.0s ± 20% = 3.2s - 4.8s
 * Attempt 4: 8.0s ± 20% = 6.4s - 9.6s
 * Attempt 5: 16.0s ± 20% = 12.8s - 19.2s
 * Max: 60s (capped)
 */
public long calculateRetryDelay(int attempt, Long serverRetryAfter) {
    // Use server-provided retry-after if available
    if (serverRetryAfter != null && serverRetryAfter > 0) {
        return TimeUnit.SECONDS.toMillis(serverRetryAfter);
    }
    
    // Exponential backoff: 1s, 2s, 4s, 8s, 16s
    long delay = (long) (1000 * Math.pow(2, attempt));
    
    // Cap at 60 seconds
    delay = Math.min(delay, 60000);
    
    // Add jitter: ±20%
    double jitter = 0.8 + (Math.random() * 0.4);
    return (long) (delay * jitter);
}
```

### Appendix F: Kong Admin API — Rate Limit Management

```bash
# List rate limiting plugins
curl -s http://localhost:8001/plugins | jq '.data[] | select(.name=="rate-limiting")'

# Get plugin for specific consumer
curl -s http://localhost:8001/consumers/{consumer_id}/plugins | jq '.'

# Create rate limiting plugin
curl -X POST http://localhost:8001/plugins \
  -d "name=rate-limiting" \
  -d "config.minute=100" \
  -d "config.burst=50" \
  -d "config.policy=redis" \
  -d "config.fault_tolerant=true" \
  -d "config.redis_host=redis-ratelimit.production.svc.cluster.local"

# Update plugin
curl -X PATCH http://localhost:8001/plugins/{plugin_id} \
  -d "config.minute=200" \
  -d "config.burst=100"

# Delete plugin
curl -X DELETE http://localhost:8001/plugins/{plugin_id}

# Check plugin health
curl -s http://localhost:8001/plugins/{plugin_id}/health
```

### Appendix G: API Terms of Service — Rate Limit Section

```markdown
## Rate Limiting

All API requests are subject to rate limiting to ensure fair usage.

### Standard Limits
- Free tier: 10 requests per minute
- Basic tier: 60 requests per minute  
- Paid tier: 100 requests per minute
- Enterprise tier: Custom limits

### Burst Allowance
All tiers include a 50% burst capacity above the base limit.
Example: Paid tier (100 req/min) can burst to 150 req/min.

### Rate Limit Headers
Every response includes:
- X-RateLimit-Limit: Your per-minute limit
- X-RateLimit-Remaining: Remaining requests in current window
- X-RateLimit-Reset: Unix timestamp when the window resets
- X-RateLimit-Warning: Warning header when approaching limit
- Retry-After: Seconds to wait before retrying (on 429 only)

### Handling 429 Responses
When you receive a 429 Too Many Requests response:
1. Read the Retry-After header to determine wait time
2. Implement exponential backoff with jitter
3. Base delay: minimum 1 second
4. Maximum retries: 5 per request
5. After 5 retries, fail gracefully
```
