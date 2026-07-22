# Incident Response Runbook Checklist: API Rate Limiting Breach

## Incident ID: ____________________
## Date: ____________________
## Responder: ____________________

## Severity Classification

- [ ] **P0**: > 25% of clients blocked, revenue-impacting, enterprise SLA breach
- [ ] **P1**: > 5% of clients blocked, significant partner impact
- [ ] **P2**: Single client blocking, non-critical
- [ ] **P3**: Rate limit configuration issue, no client impact

## Immediate Response (First 5 Minutes)

### Detection
- [ ] Confirm 429 rate from Kong metrics:
  ```bash
  curl -s http://localhost:8001/status | jq '.server'
  ```
- [ ] Check Redis rate limiter health:
  ```bash
  redis-cli -h redis-ratelimit PING
  redis-cli -h redis-ratelimit INFO memory | grep -E "used_memory|maxmemory|evicted"
  ```
- [ ] Identify top consumers getting 429:
  ```sql
  SELECT consumer, COUNT(*) as requests
  FROM kong_logs WHERE status = 429
  GROUP BY consumer ORDER BY requests DESC LIMIT 10;
  ```
- [ ] Check Redis eviction rate:
  ```bash
  redis-cli -h redis-ratelimit INFO stats | grep evicted_keys
  ```

### Declaration
- [ ] Declare incident in PagerDuty if > 5% 429 rate
- [ ] Create Slack channel: #inc-ratelimit
- [ ] Post initial situation report:
  ```
  INC-XXXX | SEV[X] | Rate Limiting Breach
  429 rate: [X]% of all requests
  Affected clients: [X]
  Redis memory: [X]/[X] GB
  Eviction rate: [X]/sec
  Started at: [time]
  ```
- [ ] Notify API Platform team
- [ ] Notify Enterprise Support (if clients affected)

## Assessment (5-15 Minutes)

### Rate Limiter Diagnostics
- [ ] Check Kong rate limiter plugin config:
  ```bash
  curl -s http://localhost:8001/plugins | jq '.data[] | select(.name=="rate-limiting")'
  ```
- [ ] Verify burst configuration
- [ ] Verify graduated limiting (warn/soft/hard)
- [ ] Verify fault_tolerant setting
- [ ] Check Redis connection from Kong:
  ```bash
  curl -s http://localhost:8001/plugins | jq '.data[] | select(.name=="rate-limiting") | .config.redis_host'
  ```

### Redis Diagnostics
- [ ] Check Redis memory usage:
  ```bash
  redis-cli -h redis-ratelimit INFO memory
  ```
- [ ] Check eviction policy:
  ```bash
  redis-cli -h redis-ratelimit CONFIG GET maxmemory-policy
  ```
- [ ] Check key count:
  ```bash
  redis-cli -h redis-ratelimit DBSIZE
  ```
- [ ] Check TTL distribution:
  ```bash
  redis-cli -h redis-ratelimit --bigkeys
  ```

### Client Identification
- [ ] Identify top 429 consumers from Kong logs
- [ ] Check client usage patterns (sustained vs burst)
- [ ] Check retry behavior of affected clients
- [ ] Contact enterprise clients if needed

## Rapid Remediation (5-20 Minutes)

### Path A: Redis Memory Pressure
- [ ] Increase Redis maxmemory:
  ```bash
  redis-cli -h redis-ratelimit CONFIG SET maxmemory 12gb
  ```
- [ ] Or scale Redis cluster (add nodes)
- [ ] Change eviction policy:
  ```bash
  redis-cli -h redis-ratelimit CONFIG SET maxmemory-policy noeviction
  ```
- [ ] Set fault_tolerant: true in Kong
- [ ] Monitor eviction rate

### Path B: Abusive Client
- [ ] Identify abusive client API key
- [ ] Increase their limit temporarily:
  ```bash
  curl -X PATCH http://localhost:8001/consumers/<key>/plugins/rate-limiting \
    -d "config.minute=2000"
  ```
- [ ] Contact client to fix retry behavior
- [ ] Monitor for other affected clients

### Path C: Configuration Fix
- [ ] Add burst allowance:
  ```json
  {"config": {"burst": 50}}
  ```
- [ ] Add graduated limiting:
  ```json
  {"config": {"warn_threshold": 80}}
  ```
- [ ] Set fault_tolerant: true
- [ ] Enable rate limit headers

### Path D: Full Capacity
- [ ] Scale Kong nodes:
  ```bash
  kubectl scale deployment kong --replicas=24
  ```
- [ ] Increase Redis connection pool
- [ ] Enable request queuing in Kong
- [ ] Consider failing open (allow all requests)

## Investigation (20-60 Minutes)

### Deep Dive — Root Cause
- [ ] Is the issue a single abusive client?
- [ ] Is it a Redis capacity problem?
- [ ] Is it a configuration problem (no burst, no graduated)?
- [ ] Is it a client retry storm?
- [ ] Is it a DDoS attack?

### Deep Dive — Redis Analysis
- [ ] Review Redis slowlog:
  ```bash
  redis-cli -h redis-ratelimit SLOWLOG GET 100
  ```
- [ ] Analyze key distribution by client
- [ ] Check Redis CPU usage
- [ ] Review network bandwidth

### Deep Dive — Client Behavior
- [ ] Analyze retry patterns of top 429 consumers
- [ ] Check for exponential backoff compliance
- [ ] Check for Retry-After header consumption
- [ ] Review client SLAs and terms

## Recovery Verification

- [ ] 429 rate below 1% of total requests:
  ```bash
  curl -s http://localhost:8001/status | jq '.server.rate_limiting'
  ```
- [ ] Redis eviction rate = 0/sec
- [ ] Redis memory < 80% usage
- [ ] Kong CPU < 70%
- [ ] All enterprise clients unblocked
- [ ] Rate limit headers present on responses:
  ```bash
  curl -sI https://api.acmecorp.com/v2/test | grep -i "x-ratelimit"
  ```
- [ ] Retry-After header present on 429:
  ```bash
  curl -sD - https://api.acmecorp.com/v2/test | grep -i "retry-after"
  ```
- [ ] Monitoring alerts resolved

## Post-Incident Actions

### Immediate Fixes
- [ ] Add burst allowance to rate limiter
- [ ] Configure graduated limiting (warn at 80%, soft at 100%, hard at 150%)
- [ ] Set Redis eviction policy to noeviction
- [ ] Set fault_tolerant: true
- [ ] Add Retry-After header to 429 responses

### Client Communication
- [ ] Notify affected clients with incident report
- [ ] Provide retry best practices documentation
- [ ] Review client retry logic
- [ ] Create quota increase workflow

### Configuration Standardization
- [ ] Create rate limiter configuration template
- [ ] Add burst parameter to all limit configurations
- [ ] Standardize graduated limiting thresholds
- [ ] Document required Retry-After header

## Long-Term Preventive Actions

### Architecture
- [ ] Implement client isolation (dedicated Redis per tier)
- [ ] Add circuit breaker for rate limiter dependencies
- [ ] Implement request queuing with backpressure
- [ ] Add client-side rate limiting enforcement
- [ ] Implement quota increase automation

### Monitoring
- [ ] Add Redis eviction alert
- [ ] Add per-client 429 rate alert
- [ ] Add retry storm detection
- [ ] Create rate limiter capacity dashboard
- [ ] Add synthetic client with known retry behavior

## Escalation Contacts

| Role | Name | Phone | Email |
|------|------|-------|-------|
| API Platform Lead | | | |
| Enterprise Support | | | |
| SRE Lead | | | |
| Redis Admin | | | |
| Client Integration Lead | | | |
| VP Engineering | | | |

## Lessons Learned

### What went well:
- _________________________________________________________________

### What went wrong:
- _________________________________________________________________

### What we will improve:
- _________________________________________________________________

## Sign-Off

- [ ] Incident report completed: ____________________
- [ ] Root cause analysis completed: ____________________
- [ ] All action items assigned: ____________________
- [ ] Post-mortem scheduled: ____________________
- [ ] Final approval by SRE Director: ____________________

---

*This checklist references Kong API Gateway documentation, GitHub API rate limiting, Google Cloud Armor, and Amazon API Gateway best practices.*
