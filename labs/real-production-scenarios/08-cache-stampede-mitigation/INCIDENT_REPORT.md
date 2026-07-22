# Incident Report — Cache Stampede Database Overload

## Incident ID: INC-2026-0725-008

**Date**: July 25, 2026  
**Reported By**: Database Reliability Team (Rachel Green)  
**Status**: Resolved  
**Severity**: SEV1 / P0  

---

## Timeline (All Times UTC)

### Pre-Incident Conditions

| Time | Event |
|------|-------|
| 13:45 | Breaking news event: global technology outage reported — traffic begins increasing to news feed API |
| 13:50 | News feed API request rate: 8,000 req/s (baseline: 5,000 req/s) — within normal range |
| 13:55 | Request rate: 12,000 req/s — cache hit rate at 94% (stable) |
| 14:00 | Request rate: 18,000 req/s — cache hit rate drops to 88% (new content requests not cached yet) |
| 14:01 | Cache entries with TTL=300s set at 09:00 begin expiring (approximately 12,000 entries) |
| 14:02 | Database query rate: 3,000 q/s → 7,000 q/s (cache misses generating DB queries) |
| 14:03 | Database query rate: 12,000 q/s — PostgreSQL query queue depth: 5,000 |
| 14:04 | Database query rate: 18,000 q/s — query queue depth: 25,000 |
| 14:05 | PostgreSQL primary CPU: 100% — query queue depth: 40,000 |
| 14:06 | Connection pool (200 connections) exhausted — new connections rejected |
| 14:07 | Thousands of requests timing out at 30s — retries amplify load |
| 14:08 | Database replicas also overloaded (CPU 95%) — replicas start lagging |
| 14:09 | PagerDuty alert: "Database critical — query queue depth exceeds threshold of 5,000" |
| 14:10 | SRE and DBA teams paged — SEV1 declared |
| 14:12 | Database query rate peaks at 42,000 q/s — primary at 100% capacity |

### Containment

| Time | Event |
|------|-------|
| 14:13 | Cache hit rate: 12% (down from 95%) — virtually all requests hitting database |
| 14:14 | Decision: Disable cache expiry temporarily and serve stale content |
| 14:15 | Redis config updated: `maxmemory-policy noeviction` — prevent further entry eviction |
| 14:16 | Application configuration change: increased cache TTL from 300s to 3600s temporarily |
| 14:17 | Database connection pool increased from 200 to 500 (emergency override) |
| 14:18 | Query queue depth begins declining from peak of 55,000 |
| 14:20 | Database CPU declining — 85% |
| 14:22 | Database CPU: 65% — cache hit rate recovering: 30% |
| 14:25 | Cache hit rate: 60% — database query rate: 8,000 q/s |
| 14:30 | Cache warming script executed — top 10,000 feed keys pre-loaded |
| 14:35 | Database query rate: 4,000 q/s (within normal range) |
| 14:40 | Cache hit rate: 88% — all key metrics recovering |

### Recovery

| Time | Event |
|------|-------|
| 14:45 | Cache hit rate: 93% — back to baseline |
| 14:50 | Database query rate: 3,500 q/s (normal) |
| 14:55 | Database CPU: 45% — all replicas healthy |
| 15:00 | Cache TTL restored to 300s with jitter applied |
| 15:05 | Stale-while-revalidate enabled in application configuration |
| 15:07 | Incident formally resolved |

### Metrics Summary

| Metric | Baseline | Peak During Incident | Post-Recovery |
|--------|----------|---------------------|---------------|
| Cache Hit Rate | 95% | 12% | 93% |
| Database Query Rate | 3,000/s | 42,000/s | 3,500/s |
| Database CPU (Primary) | 45% | 100% | 48% |
| Database Query Queue | 0 | 55,000 | 0 |
| Database Connection Pool | 150/200 | 200/200 | 145/200 |
| Request Error Rate | 0.1% | 62% | 0.2% |
| p95 Latency | 120ms | 28,000ms | 140ms |
| Active Cache Entries | 1,200,000 | 420,000 (evicted) | 1,180,000 |

### Root Cause Summary

The incident was caused by:
1. Uniform cache TTL (300s) for all news feed entries — all entries expired simultaneously
2. A traffic spike from a breaking news event amplified the expired entries issue
3. No stale-while-revalidate pattern — expired entries triggered synchronous regeneration
4. No TTL jitter — entries were all created at similar times, resulting in simultaneous expiry
5. No backpressure mechanism to limit concurrent cache miss regeneration

### Action Items

| # | Action | Owner | Target | Status |
|---|--------|-------|--------|--------|
| 1 | Implement TTL jitter (+/- 10%) for all cache entries | Platform Team | 07/26 | Done |
| 2 | Implement stale-while-revalidate pattern | Platform Team | 07/28 | In Progress |
| 3 | Implement probabilistic early expiration (XFetch) | Platform Team | 08/05 | Planned |
| 4 | Add database connection pool alerts | DBA Team | 07/26 | Done |
| 5 | Implement rate limiting for cache miss regeneration | Platform Team | 08/01 | Planned |
| 6 | Create cache warming automation for known traffic patterns | Data Team | 08/15 | Planned |
| 7 | Conduct load test with cache expiration storm scenario | QA Team | 08/10 | Planned |

## Expanded Incident Analysis

### Cache Infrastructure Metrics

**Redis Cluster Health During Incident**:

| Metric | Pre-Incident | Peak During | Post-Recovery |
|--------|-------------|------------|---------------|
| Hit Rate | 95% | 12% | 93% |
| Miss Rate | 5% | 88% | 7% |
| Evicted Keys/sec | 0 | 12,500 | 0 |
| Expired Keys/sec | 5 | 4,000 | 8 |
| Connected Clients | 150 | 450 | 160 |
| Memory Usage | 6.2GB (62%) | 3.1GB (31%) | 6.1GB (61%) |
| CPU Utilization | 25% | 85% | 28% |
| Network Throughput | 200 Mbps | 1.2 Gbps | 210 Mbps |
| Command Processing Rate | 25,000/s | 120,000/s | 26,000/s |

**Key Observation**: Memory usage actually decreased during the incident (from 62% to 31%) because entries were being evicted faster than they were being regenerated. This is a classic stampede symptom — the cache is being emptied faster than it can be refilled.

### Database Impact Analysis

**PostgreSQL Primary During Incident**:

| Metric | Pre-Incident | Peak During | Post-Recovery |
|--------|-------------|------------|---------------|
| Active Connections | 150/200 | 200/200 | 145/200 |
| Queries/Second | 3,000 | 42,000 | 3,500 |
| CPU Utilization | 45% | 100% | 48% |
| Query Queue Depth | 0 | 55,000 | 0 |
| Disk I/O (read) | 150 MB/s | 800 MB/s | 160 MB/s |
| Disk I/O (write) | 20 MB/s | 50 MB/s | 22 MB/s |
| Replication Lag | 50ms | 5,200ms | 100ms |
| Checkpoint Frequency | every 5 min | every 30 sec | every 5 min |

**Database Query Decomposition (Top Queries During Stampede)**:

| Query Type | % of Total | Average Duration | Wait Time |
|-----------|-----------|-----------------|-----------|
| Feed generation (aggregation) | 65% | 850ms | 12,000ms |
| User preference lookup | 15% | 120ms | 8,000ms |
| Content metadata query | 12% | 95ms | 5,000ms |
| Cache entry check | 5% | 2ms | 200ms |
| Other | 3% | 150ms | 3,000ms |

The feed generation query was the primary bottleneck — it performed a complex 15-table JOIN that took 850ms under normal conditions and up to 5,000ms under load.

### Traffic Analysis

**Request Pattern During Incident**:

The traffic spike was caused by a breaking news event (global technology outage). Traffic characteristics:
- Traffic volume: 3.5x normal (18,000 req/s vs 5,000 req/s baseline)
- Traffic source: primarily mobile app users (75%), web users (25%)
- Geographic distribution: global (all regions equally affected)
- User behavior: frequent refreshes (users checking for updates)

**Cache Entry Age Distribution**:

At the time of the stampede:
- 0-60 seconds old: 180,000 entries (new content)
- 60-180 seconds old: 350,000 entries
- 180-270 seconds old: 340,000 entries
- 270-300 seconds old: 330,000 entries (about to expire)
- The 330,000 entries in the 270-300 second range were created by a batch job at 09:00-09:05
- All 330,000 entries had the same base TTL of 300 seconds
- At 14:00-14:05, all 330,000 entries expired simultaneously

### Post-Incident Analysis

**Why the 80% Warning Alert Was Not Escalated**:
The initial warning alert at 10:00 (database CPU at 80%) was not escalated because:
1. The on-call engineer was handling a separate incident (non-production)
2. Database CPU at 80% was considered "within normal range" for peak traffic
3. No trend-based alerting existed — the alert was threshold-only
4. The alert did not indicate which queries were causing the load

**Why Cache Hit Rate Drop Was Not Detected Earlier**:
The cache hit rate metric was calculated with a 5-minute aggregation window. By the time the alert fired (hit rate < 80%), the stampede had been in progress for 3 minutes. With a 5-minute window, the metric showed a smoothed value that masked the severity until it was too late.

**Key Improvement: Real-Time Cache Hit Rate Monitoring**:
Following this incident, cache hit rate monitoring was changed from 5-minute aggregation to 10-second aggregation with real-time alerting.

### Expanded Technical Details

**Redis Cluster Configuration Details**:
```
Cluster mode: clustered (14 shards, 2 replicas each)
Max memory: 10GB per node
Eviction policy: allkeys-lru (before fix)
Eviction policy: noeviction (after fix)
Timeout: 300ms
TCP keepalive: 300s
Max memory policy before incident: allkeys-lru (allowed eviction of hot keys)
Max memory policy after fix: noeviction (prevent eviction, return errors instead)

The allkeys-lru eviction policy was inappropriate for this workload because:
- Under memory pressure, Redis evicts least-recently-used keys
- During a cache miss storm, keys are not being accessed frequently
- This means hot keys that would normally be protected by LRU are evicted
- Eviction creates more cache misses, making the stampede worse
```

**Connection Pool Configuration Details**:
```
HikariCP Configuration (before fix):
  maximumPoolSize: 200
  minimumIdle: 50
  connectionTimeout: 30000 (30 seconds)
  idleTimeout: 600000
  maxLifetime: 1800000

HikariCP Configuration (after fix):
  maximumPoolSize: 400 (increased emergency capacity)
  minimumIdle: 100
  connectionTimeout: 5000 (reduced from 30s)
  idleTimeout: 600000
  maxLifetime: 1800000

Key changes:
- Increased pool size to handle worst-case cache miss scenarios
- Reduced connection timeout to prevent threads blocking for 30 seconds
```

### Communication Timeline Detail

**10:00 — First Warning Alert**:
- Alert: "Database CPU > 80%"
- Acknowledged by on-call SRE
- Action: Checked database, saw high load but attributed to traffic spike
- No escalation

**10:30 — Second Warning Alert**:
- Alert: "Cache hit rate < 80%"
- Not acknowledged (on-call SRE in meeting)
- No escalation

**11:00 — First Critical Alert**:
- Alert: "Database CPU > 95%"
- Paged SRE secondary (primary not responding)
- Investigation started
- Cache stampede not yet identified

**11:15 — Root Cause Identified**:
- SRE secondary identifies simultaneous cache expiration as root cause
- Begins coordinating emergency response
- Cache warming initiated

**Key Communication Lesson**: The incident was detected 3 hours after the stampede began because the on-call engineer was not actively monitoring alerts. The no-escalation policy for "acknowledged but not resolved" alerts was a critical gap. New policy: any acknowledged alert that is not resolved within 30 minutes auto-escalates.
