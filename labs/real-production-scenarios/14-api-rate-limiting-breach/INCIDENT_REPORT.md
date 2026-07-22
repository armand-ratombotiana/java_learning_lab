# Incident Report: API Rate Limiting Breach

## Incident ID: INC-2026-0815-RL
## Date: August 15, 2026
## Severity: SEV1 (Critical)

## Timeline (All times UTC)

### 08:30 — Misconfigured Client Integration
- Enterprise client "MegaRetail Inc." deploys new integration with AcmeCorp API
- Integration has a bug: sends requests in a tight loop instead of paced batch
- Request rate: 1,500 req/min (15x the paid tier limit of 100 req/min)
- Client API key: `sk_live_megaretail_2026_08`

### 08:42 — Rate Limiter Activates
- MegaRetail's usage exceeds 100 req/min threshold at 08:42:13
- Kong rate limiter returns first HTTP 429 responses
- MegaRetail's retry logic has NO exponential backoff — retries immediately
- Retry rate: 1,500 req/min (same as original — all rejected)
- Rate limiter starts processing 3,000 req/min for this client (original + retries)

### 08:45 — Cascade Begins
- Rate limiter processing overhead increases Kong CPU from 45% to 72%
- Redis INCR commands: 50/sec → 500/sec for MegaRetail's keys
- Redis CPU increases from 20% to 55%
- No impact on other clients yet

### 08:47 — Redis Memory Pressure
- Rate limiter creates per-endpoint, per-minute keys for MegaRetail
- Key count: 24 endpoints × 60 seconds × 1 client = 1,440 keys/minute
- Redis memory grows by 200 MB in 2 minutes
- Other clients' rate limit keys compete for Redis memory

### 08:48 — First Collateral Blocking
- Redis eviction policy (allkeys-lru) begins evicting older keys
- Other clients' rate limit counters are evicted from Redis
- When a counter is evicted, Kong treats it as "no prior usage" → allows requests
- BUT: immediately creates a new counter and increments past limit → blocks client
- Result: clients oscillate between allowed and blocked within seconds

### 08:49 — PagerDuty Alert
- Prometheus alert `KongRateLimitExceeded` fires:
  ```yaml
  alert: KongRateLimitExceeded
  expr: kong_http_requests_total{status="429"} / kong_http_requests_total > 0.05
  ```
- 5% threshold exceeded (actual: 23% and climbing)
- On-call SRE acknowledges

### 08:52 — Incident Declared
- 429 rate climbs to 47% of all responses
- PagerDuty escalation to secondary on-call
- Slack #inc-rate-limit- cascade created
- Support team reports 200+ tickets in 7 minutes

### 08:58 — Investigation Begins
- SRE checks Kong rate limiter status:
  ```bash
  curl -s http://localhost:8001/plugins | jq '.data[] | select(.name=="rate-limiting")'
  ```
- Configuration:
  ```json
  {
    "name": "rate-limiting",
    "config": {
      "minute": 100,
      "policy": "redis",
      "redis_host": "redis-ratelimit.production.svc.cluster.local",
      "fault_tolerant": false,
      "hide_client_headers": false
    }
  }
  ```

### 09:05 — Redis Analysis
- SRE checks Redis memory:
  ```bash
  redis-cli -h redis-ratelimit.production INFO memory
  ```
- Memory: 7.2 GB / 8 GB (89.5% used)
- Evicted keys: 12,847 in last 60 seconds
- Key count: 2.1 million rate limit keys

### 09:12 — Abusive Client Identified
- SRE queries Kong logs for top 429 consumers:
  ```sql
  SELECT client_id, COUNT(*) as requests
  FROM kong_logs
  WHERE response_code = 429
  GROUP BY client_id
  ORDER BY requests DESC
  LIMIT 10;
  ```
- Top result: `sk_live_megaretail_2026_08` — 847,000 requests in 25 minutes
- Contact enterprise support to reach MegaRetail

### 09:18 — Temporary Mitigation
- SRE temporarily increases MegaRetail's rate limit to 2,000 req/min
- Kong admin API:
  ```bash
  curl -X PATCH http://localhost:8001/consumers/sk_live_megaretail_2026_08/plugins/rate-limiting \
    -d "config.minute=2000"
  ```
- MegaRetail's 429s stop (now within limit)
- BUT: Redis memory pressure continues (other clients still affected)

### 09:25 — Second Issue: Redis Eviction
- Even with MegaRetail fixed, other clients still getting 429
- SRE identifies Redis eviction as root cause for collateral blocking
- Temporary fix: increase Redis maxmemory to 12 GB:
  ```bash
  redis-cli -h redis-ratelimit.production CONFIG SET maxmemory 12gb
  ```
- Memory pressure reduces; evictions stop in 30 seconds

### 09:30 — Redis Configuration Fix
- Change Redis eviction policy from allkeys-lru to noeviction:
  ```bash
  redis-cli -h redis-ratelimit.production CONFIG SET maxmemory-policy noeviction
  ```
- This prevents key eviction; INCR commands will fail instead of evicting
- Kong configured with fault_tolerant: true to handle Redis failures gracefully

### 09:42 — Burst Allowance Configuration
- SRE increases rate limiter parameters:
  ```json
  {
    "config": {
      "minute": 100,
      "burst": 50,
      "policy": "redis",
      "redis_host": "redis-ratelimit.production.svc.cluster.local",
      "fault_tolerant": true
    }
  }
  ```
- Token bucket now: 150 tokens, refill 100/minute
- Allows momentary bursts up to 150 req/min

### 09:50 — Graduated Limiting Implemented
- Three-tier rate limiting added:
  1. 80 req/min: Warning header returned (no blocking)
  2. 100 req/min: Soft limit (requests delayed by 100ms)
  3. 150 req/min: Hard limit (HTTP 429 with Retry-After)

### 10:15 — Client Retry Behavior Fixed
- Enterprise support contacts MegaRetail
- MegaRetail deploys fix: exponential backoff with jitter
- Retry pattern: 1s, 2s, 4s, 8s, 16s, 30s, 60s (with ±20% jitter)

### 10:30 — Recovery
- 429 rate drops from 87% to 2%
- Redis memory stable at 6.5 GB / 12 GB
- Kong CPU back to 55%
- All enterprise clients confirmed unblocked

### 11:00 — Incident Resolved
- All metrics within normal range
- Post-mortem scheduled
- Client notified with incident report

## Detailed Event Analysis

### Kong Rate Limiter Plugin States

```
08:45: Kong rate limiter state:
  Plugin: rate-limiting (consumer: sk_live_megaretail_2026_08)
  Config: minute=100, policy=redis, fault_tolerant=false
  Current count: 1,402 (limit: 100)
  Status: BLOCKING 1,302 of 1,402 requests (92% block rate)

08:48: Kong rate limiter state (collateral blocking):
  Plugin: rate-limiting (consumers: 8,000+)
  Config: minute=100, policy=redis, fault_tolerant=false
  Redis evictions: 847 keys in last 60 seconds
  Status: Random blocking of legitimate clients (evicted keys = no counter)
  
09:18: Kong rate limiter state (after MegaRetail fix):
  Plugin: rate-limiting (consumer: sk_live_megaretail_2026_08)
  Config: minute=2000, policy=redis, fault_tolerant=false
  Current count: 1,847 (limit: 2,000)
  Status: ACCEPTING — retries no longer blocked
  
09:30: Kong rate limiter state (after Redis fix):
  Plugin: rate-limiting (all consumers)
  Config: minute=100, policy=redis, fault_tolerant=true
  Redis: maxmemory=12GB, policy=noeviction
  Status: Evictions stopped, but many counters lost
```

### Redis Database Analysis

```
Redis INFO at 09:05:
  # Memory
  used_memory: 7,730,145,280 (7.2 GB)
  used_memory_rss: 8,521,388,032 (7.9 GB)
  used_memory_peak: 7,901,234,567 (7.4 GB)
  maxmemory: 8,589,934,592 (8 GB)
  maxmemory_policy: allkeys-lru
  mem_fragmentation_ratio: 1.10
  
  # Stats
  evicted_keys: 12,847 (in last 60 seconds)
  expired_keys: 192,341 (in last 60 seconds)
  keyspace_hits: 847,123 (since last flush)
  keyspace_misses: 94,567 (since last flush — increasing due to evictions)
  
  # Keyspace
  db0: keys=2,147,582, expires=1,982,344, avg_ttl=58.3s
  
  # CPU
  used_cpu_sys: 847.23 seconds
  used_cpu_user: 2,341.89 seconds
  used_cpu_sys_children: 12.34 seconds
  used_cpu_user_children: 45.67 seconds
```

### Kong Proxy Analysis

```
Kong status at 09:00:
  Server:
    total_requests: 12,847,234
    requests_per_second: 14,234
    
  Database:
    reachable: true
    
  Rate Limiting:
    429 responses: 11,178,294 (87% of all traffic)
    200 responses: 1,668,940 (13%)
    Rate limited consumers: 3,427
  
  Connections:
    active: 847
    reading: 23
    writing: 456  
    waiting: 368
    accepted: 15,234,567
    handled: 15,234,567
```

### Client Impact Distribution

```
Top 10 clients by 429 count:

1. sk_live_megaretail_2026_08: 847,293 429s (24.6% of all 429s)
2. sk_live_retailcloud_2026_01: 123,847 429s (3.6%)
3. sk_live_payments_pro_2026_03: 98,234 429s (2.8%)
4. sk_live_ecomplus_2026_02: 87,234 429s (2.5%)
5. sk_live_shopify_migration: 76,123 429s (2.2%)
6. sk_live_bigbox_retail: 65,982 429s (1.9%)
7. sk_live_omnichannel_io: 54,321 429s (1.6%)
8. sk_live_paymentgateway_inc: 43,210 429s (1.3%)
9. sk_live_logistics_pro: 32,109 429s (0.9%)
10. sk_live_inventory_cloud: 21,098 429s (0.6%)

Top client (MegaRetail) caused 24.6% of ALL 429s.
Remaining 9 clients: 17.4% combined.
Other 3,417 clients: 58% of 429s (collateral damage).
```

### Rate Limit Configuration Fix History

```
09:18 — Temporary fix: MegaRetail limit increased to 2,000 req/min
  curl -X PATCH http://localhost:8001/consumers/sk_live_megaretail_2026_08/plugins/rate-limiting \
    -d "config.minute=2000"
  Result: MegaRetail unblocked, but collateral damage continues

09:25 — Redis maxmemory increased from 8GB to 12GB
  redis-cli CONFIG SET maxmemory 12gb
  Result: Evictions stopped, Redis stabilized

09:30 — Redis eviction policy changed to noeviction
  redis-cli CONFIG SET maxmemory-policy noeviction
  Result: No more key eviction

09:42 — Burst allowance added to rate limiter
  curl -X PATCH ... -d "config.minute=100&config.burst=50"
  Result: Clients can burst to 150 req/min before blocking

09:50 — Graduated limiting implemented
  Implementation: Three-tier system (warn → soft → hard)
  Result: Clients receive warning at 80, delay at 100, block at 150

10:15 — Fault tolerance enabled
  curl -X PATCH ... -d "config.fault_tolerant=true"
  Result: Redis failures don't cascade to Kong
```

## Post-Incident Actions

1. Implement graduated rate limiting (warn → soft → hard)
2. Add burst allowance to all rate limit configurations (50% of limit)
3. Fix Redis eviction policy (noeviction + fault_tolerant)
4. Set up dedicated Redis cluster for rate limiting
5. Implement retry-after header for all 429 responses
6. Create client quota increase workflow
7. Add rate limiter performance monitoring (Redis CPU, keys, evictions)
8. Update enterprise support runbook for rate limit incidents
9. Add client retry behavior requirements to API terms of service
10. Implement Redis connection pooling limits per Kong plugin instance
