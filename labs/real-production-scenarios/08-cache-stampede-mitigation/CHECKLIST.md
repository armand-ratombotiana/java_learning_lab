# Incident Response Runbook Checklist — Cache Stampede / Database Overload

## Incident Type: SEV1/P0 — Cache Stampede / Database Overload

### Classification
- [ ] Incident severity: SEV1 (Critical) / P0 (Immediate)
- [ ] Incident type: Cache Stampede / Thundering Herd / Database Overload
- [ ] Affected cache cluster: ________________________________
- [ ] Affected database: ________________________________
- [ ] Affected service(s): ________________________________
- [ ] Current cache hit rate: ____ %
- [ ] Current database query rate: ____ q/s
- [ ] Current incident commander: ________________________________

---

## Phase 1: Detection and Triage (0–5 minutes)

### Immediate Detection
- [ ] Confirm alert: cache hit rate drop, database query spike, or connection pool exhaustion
- [ ] Check Redis hit rate: `redis-cli info stats | Select-String "keyspace_hits|keyspace_misses"`
- [ ] Check database query queue: `SELECT count(*) FROM pg_stat_activity WHERE state = 'active'`
- [ ] Check database connection pool: `az monitor metrics list --resource {db-id} --metric "connections_active"`
- [ ] Determine if this is cache-related or database-only

### Initial Assessment
- [ ] Is cache hit rate below 80%? 50%?
- [ ] Are cache entries expiring simultaneously?
- [ ] Is database query rate > 3x normal?
- [ ] Is there a recent traffic spike?
- [ ] Is this a known traffic pattern (news event, scheduled job)?

### Communication
- [ ] Declare SEV1 incident in #incidents channel
- [ ] Open bridge with SRE team, DBA team, application team
- [ ] Notify incident commander
- [ ] Post initial status: "Cache stampede detected — database under load"

---

## Phase 2: Containment (5–20 minutes)

### Database Protection
- [ ] Temporarily increase connection pool size: `ALTER SYSTEM SET max_connections = 500; SELECT pg_reload_conf();`
- [ ] Kill long-running queries: `SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE state = 'active' AND query_start < now() - interval '30 seconds'`
- [ ] Enable query queue monitoring
- [ ] Consider failing fast (return errors) instead of queuing

### Cache Emergency Actions
- [ ] Disable cache eviction: `redis-cli CONFIG SET maxmemory-policy noeviction`
- [ ] Increase default TTL to 3600s temporarily
- [ ] Enable stale-while-revalidate if available
- [ ] Activate rate limiter for cache miss regeneration
- [ ] Warm popular cache keys manually

### Application Actions
- [ ] Enable SWR in application configuration
- [ ] Activate backpressure (rate limit cache misses)
- [ ] Consider serving degraded content (static feed, cached-only)
- [ ] Disable retries if retry storm detected

---

## Phase 3: Recovery (20–60 minutes)

### Database Recovery
- [ ] Monitor database query rate declining
- [ ] Check database CPU utilization returning to normal
- [ ] Verify replication lag is recovering
- [ ] Monitor connection pool utilization

### Cache Recovery
- [ ] Monitor cache hit rate recovering
- [ ] Verify cache warming is populating hot keys
- [ ] Check TTL distribution for uniformity
- [ ] Verify no new cache stampede forming

### Service Verification
- [ ] Verify error rate declining
- [ ] Check p95 latency returning to normal
- [ ] Verify all service instances healthy
- [ ] Run end-to-end test of news feed API

---

## Phase 4: Root Cause Investigation (60–180 minutes)

### TTL Analysis
- [ ] Check TTL configuration for all cache key spaces
- [ ] Verify TTL jitter is enabled and configured correctly
- [ ] Check TTL distribution: `redis-cli --eval check_ttl_distribution.lua`
- [ ] Identify if TTL was uniform (all entries same TTL)
- [ ] Check when entries were created (batch creation times)

### Cache Architecture Analysis
- [ ] Is SWR implemented?
- [ ] Is request coalescing implemented?
- [ ] Is XFetch / probabilistic early expiration implemented?
- [ ] Is cache miss regeneration rate-limited?
- [ ] Is there a cache warming mechanism?

### Database Analysis
- [ ] Check database capacity vs peak query rate
- [ ] Review slow query log
- [ ] Check index usage for cache miss queries
- [ ] Review connection pool configuration

### Traffic Analysis
- [ ] Was there a traffic spike? What caused it?
- [ ] Is the traffic pattern predictable?
- [ ] Should cache warming be triggered by this pattern?

---

## Phase 5: Fix and Prevention (180–360 minutes)

### Immediate Fixes
- [ ] Enable TTL jitter (+/- 10%) in application configuration
- [ ] Implement SWR pattern in all cache read paths
- [ ] Add request coalescing (Redis-based mutex)
- [ ] Add rate limiter for cache miss regeneration

### Short-Term Prevention
- [ ] Implement XFetch algorithm
- [ ] Create cache warming automation
- [ ] Add TTL distribution monitoring
- [ ] Add cache stampede alert rules

### Long-Term Prevention
- [ ] Review all caching patterns across all services
- [ ] Add caching standards to architecture guidelines
- [ ] Add cache stampede scenarios to load testing
- [ ] Implement chaos testing for cache infrastructure

---

## Phase 6: Post-Incident (After Resolution)

### Documentation
- [ ] Complete INCIDENT_REPORT.md with full timeline
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Update runbooks with cache stampede procedures
- [ ] Document TTL jitter and SWR configuration

### Action Items
- [ ] Create JIRA tickets for all remediation actions
- [ ] Assign owners and target dates
- [ ] Schedule cache architecture review
- [ ] Plan caching best practices training

### Prevention
- [ ] Add cache configuration to deployment pipeline gate
- [ ] Enforce TTL jitter in code review
- [ ] Add cache stampede test to load test suite
- [ ] Schedule quarterly caching infrastructure review

---

## Emergency Commands

```powershell
# Redis — Check hit rate
redis-cli -h {redis-host} -p 6379 INFO stats

# Redis — Disable eviction
redis-cli CONFIG SET maxmemory-policy noeviction

# Redis — Check TTL for specific key
redis-cli TTL feed:user:001

# Redis — Check all keys with TTL
redis-cli --scan --pattern "feed:*" | % { redis-cli TTL $_ }

# PostgreSQL — Check active queries
SELECT pid, query, state, query_start FROM pg_stat_activity WHERE state = 'active' ORDER BY query_start;

# PostgreSQL — Kill queries
SELECT pg_terminate_backend(pid) FROM pg_stat_activity WHERE query_start < now() - interval '30s';

# PostgreSQL — Increase connections
ALTER SYSTEM SET max_connections = 500; SELECT pg_reload_conf();

# Application — Enable SWR
curl -X POST http://feed-service:8080/actuator/config refresh -d '{"cache.swr.enabled": true}'

# Application — Increase TTL temporarily
curl -X POST http://feed-service:8080/actuator/config refresh -d '{"cache.ttl.base": 3600}'

# Cache warming
curl -X POST http://feed-service:8080/admin/cache/warm
```

---

## Emergency Contacts

| Role | Name | Phone | Slack |
|------|------|-------|-------|
| SRE Primary | Mike Davis | +1-555-0300 | @mike.davis |
| DBA Lead | Rachel Green | +1-555-0301 | @rachel.green |
| Platform Lead | Kevin Brown | +1-555-0302 | @kevin.brown |
| Application Lead | Sarah Johnson | +1-555-0303 | @sarah.johnson |
| Incident Commander | David Park | +1-555-0304 | @david.park |

---

## Checklist Version
- Version: 1.0
- Last Updated: 2026-07-26
- Approved By: DBA Lead
- Next Review Date: 2026-10-26
