# Prevention: API Rate Limiting Failures

## Strategic Prevention Framework

Based on patterns from GitHub API rate limiting, Google Cloud Armor, Amazon API Gateway, and Google SRE principles for handling overload.

## Layer 1: Graduated Rate Limiting

### Three-Tier System

```
Level 1: Warning (80% of limit)
  → HTTP 200 with X-RateLimit-Warning header
  → Response includes time until reset
  → No action taken

Level 2: Soft Limit (100% of limit)
  → HTTP 200 with 100ms artificial delay
  → X-RateLimit-Delay header indicates delay applied
  → Client experiences slower responses

Level 3: Hard Limit (150% of limit with burst)
  → HTTP 429 Too Many Requests
  → Retry-After header with recommended wait time
  → Client must back off
```

### Kong Configuration

```json
{
  "name": "rate-limiting",
  "config": {
    "minute": 100,
    "burst": 50,
    "policy": "local",
    "fault_tolerant": true,
    "hide_client_headers": false,
    "warn_threshold": 0.8
  }
}
```

## Layer 2: Token Bucket Tuning

### Recommended Parameters by Tier

| Tier | Sustained Rate | Burst Capacity | Refill Window | Use Case |
|------|---------------|----------------|---------------|----------|
| Free | 10 req/min | 10 req | 1 min | Trial users |
| Basic | 60 req/min | 90 req | 1 min | Individuals |
| Paid | 100 req/min | 150 req | 1 min | Businesses |
| Enterprise | 1,000 req/min | 2,000 req | 1 min | High-volume |
| Custom | Negotiated | 2x sustained | 1 min | Strategic |

### Burst Calculation Formula

```java
public class BurstCalculator {
    public static long calculateBurstCapacity(long sustainedRate,
                                              double burstMultiplier) {
        return (long) (sustainedRate * burstMultiplier);
    }

    public static long calculateRefillRate(long sustainedRate,
                                           long windowSeconds) {
        return sustainedRate / windowSeconds;
    }

    public static void main(String[] args) {
        long rate = 100; // req/min
        double multiplier = 1.5; // 50% burst
        long burst = calculateBurstCapacity(rate, multiplier);
        long refillPerSecond = calculateRefillRate(rate, 60);
        System.out.printf("Rate: %d/min, Burst: %d, Refill: %d/sec%n",
            rate, burst, refillPerSecond);
    }
}
```

## Layer 3: Redis Configuration for Rate Limiting

### Dedicated Redis Cluster

```yaml
# redis-ratelimit.yml
apiVersion: v1
kind: ConfigMap
metadata:
  name: redis-ratelimit-config
data:
  redis.conf: |
    maxmemory 16gb
    maxmemory-policy noeviction
    timeout 30
    tcp-keepalive 300
    maxclients 10000
    appendonly no
    save ""
---
apiVersion: apps/v1
kind: StatefulSet
metadata:
  name: redis-ratelimit
spec:
  replicas: 6
  selector:
    matchLabels:
      app: redis-ratelimit
  template:
    metadata:
      labels:
        app: redis-ratelimit
    spec:
      containers:
        - name: redis
          image: redis:7-alpine
          command: ["redis-server", "/etc/redis/redis.conf"]
          ports:
            - containerPort: 6379
          resources:
            requests:
              memory: "8Gi"
              cpu: "2"
            limits:
              memory: "16Gi"
              cpu: "4"
```

### Key Design Principles

1. **Dedicated Redis instance**: Rate limiting should use a separate Redis from caching/sessions
2. **No eviction policy**: Use `noeviction` to prevent data loss; handle Redis errors gracefully
3. **Short TTL**: Rate limit keys should have TTL matching the window (60 seconds max)
4. **Key namespacing**: Use `ratelimit:{client_id}:{endpoint}:{window}` format
5. **Fault tolerance**: API gateway should allow requests when Redis is unavailable (fail open for availability)

## Layer 4: Client Retry Requirements

### API Terms of Service

```
Required retry behavior for API clients:

1. When receiving HTTP 429:
   - MUST respect Retry-After header if present
   - MUST use exponential backoff with jitter
   - Base delay: minimum 1 second
   - Max delay: 60 seconds
   - Jitter: ±20% of calculated delay

2. Exponential backoff formula:
   delay = min(base * 2^attempt, max_delay) * (0.8 + random(0, 0.4))

3. Retry limits:
   - Maximum 5 retries per request
   - After 5 retries, fail gracefully
   - Report failure to application monitoring
```

### Retry Validation in CI/CD

```yaml
# CI check for retry behavior
- name: Validate retry logic
  run: |
    # Check for exponential backoff implementation
    grep -q "exponential\|backoff\|retryAfter\|Retry-After" src/main/java/ && \
      echo "Retry logic found" || \
      echo "WARNING: No retry logic detected"
    
    # Check for 429 handling
    grep -q "429\|TOO_MANY_REQUESTS" src/main/java/ && \
      echo "429 handling found" || \
      echo "WARNING: HTTP 429 not handled"
```

## Layer 5: Quota Increase Workflow

```yaml
# API quota increase automation
quota_increase:
  workflow:
    - trigger: "Client requests quota increase via dashboard"
    - step: "Validate client tier and usage history"
    - step: "Calculate new limit based on 7-day usage pattern"
    - step: "Apply new limit via Kong Admin API"
    - step: "Notify client with confirmation"
    - step: "Update monitoring thresholds"
  
  auto_approve_rules:
    - current_usage < 70% of current limit for 7 days
    - no 429 violations in last 30 days
    - paid tier customer
  
  escalation:
    - manual_approval_required: usage > 70% or violated terms
    - approver: API Platform Team Lead
    - sla: 4 hours for paid tier
```

## Layer 6: Monitoring Configuration

```yaml
prometheus:
  rules:
    - alert: RateLimitExceededHigh
      expr: rate(kong_http_requests_total{status="429"}[5m]) > 0.01
      labels:
        severity: warning
      annotations:
        summary: "Rate limit exceeded for > 1% of requests"

    - alert: RateLimitExceededCritical
      expr: rate(kong_http_requests_total{status="429"}[5m]) > 0.05
      labels:
        severity: critical
      annotations:
        summary: "Rate limit exceeded for > 5% of requests"

    - alert: RedisEvictionRate
      expr: rate(redis_db_keys_evicted{db="ratelimit"}[5m]) > 0
      labels:
        severity: critical
      annotations:
        summary: "Redis evictions occurring in rate limiter"
```

## Key Prevention Metrics

| Control | Metric | Target | Owner |
|---------|--------|--------|-------|
| Burst configuration | % of rate limiters with burst | 100% | API Platform |
| Graduated limiting | % of limiters with warn/soft/hard | 100% | API Platform |
| Retry-After header | % of 429s with Retry-After | 100% | API Platform |
| Redis eviction | Eviction rate for rate limiter | 0/sec | SRE |
| Fault tolerance | % of plugins with fault_tolerant | 100% | SRE |
| Client retry compliance | % of clients with proper backoff | 100% | Enterprise Support |
| 429 rate | % of traffic returning 429 | < 0.1% | API Platform |

## References

- GitHub API Rate Limiting: https://docs.github.com/en/rest/overview/resources-in-the-rest-api
- Kong Rate Limiting Plugin: https://docs.konghq.com/hub/kong-inc/rate-limiting/
- Google Cloud Armor Rate Limiting: https://cloud.google.com/armor/docs/rate-limiting-overview
- Amazon API Gateway Throttling: https://docs.aws.amazon.com/apigateway/latest/developerguide/api-gateway-request-throttling.html
- Google SRE — Handling Overload: https://sre.google/sre-book/handling-overload/
