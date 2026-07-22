# Monitoring and Alerting Guide — Cache Stampede Detection and Prevention

## Lab 08: Cache Stampede Mitigation

This guide documents the monitoring and alerting configuration required to detect cache stampede events, database overload, and caching infrastructure health in real time.

---

## 1. Cache Infrastructure Metrics

### Redis Key Metrics

| Metric | Redis Command | Description |
|--------|---------------|-------------|
| Hit Rate | `INFO stats: keyspace_hits / (keyspace_hits + keyspace_misses)` | Percentage of cache hits |
| Miss Rate | `INFO stats: keyspace_misses / keyspace_hits + keyspace_misses` | Percentage of cache misses |
| Evicted Keys | `INFO stats: evicted_keys` | Keys evicted due to memory pressure |
| Expired Keys | `INFO stats: expired_keys` | Keys expired due to TTL |
| Connected Clients | `INFO clients: connected_clients` | Current client connections |
| Memory Usage | `INFO memory: used_memory / maxmemory` | Memory utilization |
| Hit Rate per Key Space | `INFO keyspace: keys=... expires=...` | Keys and TTLs per database |
| Slow Log | `SLOWLOG GET 10` | Slow Redis commands |
| Replication Lag | `INFO replication: master_repl_offset - slave_repl_offset` | Replica delay |

### Metrics Collection with Micrometer

```java
package com.example.cache.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Gauge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import javax.annotation.PostConstruct;

@Component
public class RedisMetricsExporter {

    private static final Logger log = LoggerFactory.getLogger(RedisMetricsExporter.class);

    private final RedisTemplate<String, String> redisTemplate;
    private final RedisConnectionFactory connectionFactory;
    private final MeterRegistry meterRegistry;

    public RedisMetricsExporter(
            RedisTemplate<String, String> redisTemplate,
            RedisConnectionFactory connectionFactory,
            MeterRegistry meterRegistry
    ) {
        this.redisTemplate = redisTemplate;
        this.connectionFactory = connectionFactory;
        this.meterRegistry = meterRegistry;
    }

    @PostConstruct
    public void registerMetrics() {
        Gauge.builder("cache.hit.rate", this, RedisMetricsExporter::getHitRate)
            .description("Redis cache hit rate")
            .register(meterRegistry);

        Gauge.builder("cache.miss.rate", this, RedisMetricsExporter::getMissRate)
            .description("Redis cache miss rate")
            .register(meterRegistry);

        Gauge.builder("cache.connected.clients", this, RedisMetricsExporter::getConnectedClients)
            .description("Connected Redis clients")
            .register(meterRegistry);

        Gauge.builder("cache.memory.usage", this, RedisMetricsExporter::getMemoryUsage)
            .description("Redis memory usage ratio")
            .register(meterRegistry);

        Gauge.builder("cache.evicted.keys.total", this, RedisMetricsExporter::getEvictedKeys)
            .description("Total evicted keys")
            .register(meterRegistry);
    }

    private double getHitRate() {
        try {
            Properties info = redisTemplate.getRequiredConnectionFactory()
                .getConnection().info("stats");
            long hits = Long.parseLong(info.getProperty("keyspace_hits"));
            long misses = Long.parseLong(info.getProperty("keyspace_misses"));
            long total = hits + misses;
            return total > 0 ? (double) hits / total : 1.0;
        } catch (Exception e) {
            log.warn("Failed to get hit rate", e);
            return -1;
        }
    }

    private double getMissRate() {
        double hitRate = getHitRate();
        return hitRate >= 0 ? 1.0 - hitRate : -1;
    }

    private double getConnectedClients() {
        try {
            Properties info = redisTemplate.getRequiredConnectionFactory()
                .getConnection().info("clients");
            return Double.parseDouble(info.getProperty("connected_clients"));
        } catch (Exception e) {
            log.warn("Failed to get connected clients", e);
            return -1;
        }
    }

    private double getMemoryUsage() {
        try {
            Properties info = redisTemplate.getRequiredConnectionFactory()
                .getConnection().info("memory");
            long used = Long.parseLong(info.getProperty("used_memory"));
            long max = Long.parseLong(info.getProperty("maxmemory"));
            return max > 0 ? (double) used / max : -1;
        } catch (Exception e) {
            log.warn("Failed to get memory usage", e);
            return -1;
        }
    }

    private double getEvictedKeys() {
        try {
            Properties info = redisTemplate.getRequiredConnectionFactory()
                .getConnection().info("stats");
            return Double.parseDouble(info.getProperty("evicted_keys"));
        } catch (Exception e) {
            log.warn("Failed to get evicted keys", e);
            return -1;
        }
    }
}
```

---

## 2. Database Metrics

### PostgreSQL Key Thresholds

| Metric | Query | Warning | Critical | Action |
|--------|-------|---------|----------|--------|
| Connections | `SELECT count(*) FROM pg_stat_activity` | > 150 | > 190 | Scale pool or throttle |
| Query Queue | `SELECT count(*) FROM pg_stat_activity WHERE state = 'active'` | > 100 | > 180 | Investigate slow queries |
| CPU Utilization | CloudWatch / OS metric | > 70% | > 90% | Scale up or optimize |
| Replication Lag | `SELECT pg_wal_lsn_diff()` | > 100MB | > 1GB | Check replica health |
| Cache Hit Ratio | `SELECT sum(blks_hit) / sum(blks_read + blks_hit)` | < 95% | < 90% | Tune shared_buffers |

### Application-Level Database Metrics

```java
package com.example.cache.monitoring;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.Counter;
import org.springframework.stereotype.Component;
import java.util.concurrent.TimeUnit;

@Component
public class CacheMissDbMetrics {

    private final MeterRegistry meterRegistry;
    private final Timer queryTimer;
    private final Counter cacheMissCounter;
    private final Counter cacheHitCounter;
    private final Counter swrServeCounter;
    private final Counter xFetchTriggerCounter;

    public CacheMissDbMetrics(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;

        this.queryTimer = Timer.builder("cache.miss.query.duration")
            .description("Database query duration on cache miss")
            .register(meterRegistry);

        this.cacheMissCounter = Counter.builder("cache.miss.total")
            .description("Total cache misses")
            .register(meterRegistry);

        this.cacheHitCounter = Counter.builder("cache.hit.total")
            .description("Total cache hits")
            .register(meterRegistry);

        this.swrServeCounter = Counter.builder("cache.swr.serve.total")
            .description("Times stale data was served during SWR")
            .register(meterRegistry);

        this.xFetchTriggerCounter = Counter.builder("cache.xfetch.trigger.total")
            .description("Times XFetch triggered early refresh")
            .register(meterRegistry);
    }

    public void recordCacheMiss() {
        cacheMissCounter.increment();
    }

    public void recordCacheHit() {
        cacheHitCounter.increment();
    }

    public void recordQueryDuration(long durationMs) {
        queryTimer.record(durationMs, TimeUnit.MILLISECONDS);
    }

    public void recordSwrServe() {
        swrServeCounter.increment();
    }

    public void recordXFetchTrigger() {
        xFetchTriggerCounter.increment();
    }

    public double getHitRate() {
        double misses = cacheMissCounter.count();
        double hits = cacheHitCounter.count();
        double total = misses + hits;
        return total > 0 ? hits / total : 1.0;
    }
}
```

---

## 3. Alert Rules

### Prometheus Alert Rules for Cache Stampede

```yaml
groups:
  - name: cache-stampede-alerts
    rules:
      - alert: CacheHitRateCritical
        expr: rate(cache_hit_total[1m]) / (rate(cache_hit_total[1m]) + rate(cache_miss_total[1m])) < 0.5
        for: 1m
        labels:
          severity: critical
          incident_type: cache_stampede
        annotations:
          summary: "Cache hit rate below 50% — possible stampede"
          description: "Cache hit rate dropped to {{ $value | humanizePercentage }}"

      - alert: DatabaseQueryRateSpike
        expr: rate(cache_miss_query_duration_seconds_count[1m]) > 10000
        for: 1m
        labels:
          severity: critical
        annotations:
          summary: "Database query rate from cache misses > 10,000/s"

      - alert: DatabaseConnectionPoolExhaustion
        expr: hikaricp_connections_active / hikaricp_connections_max > 0.8
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Database connection pool > 80% utilized"

      - alert: DatabaseConnectionPoolCritical
        expr: hikaricp_connections_active / hikaricp_connections_max > 0.95
        for: 30s
        labels:
          severity: critical
        annotations:
          summary: "Database connection pool > 95% utilized"

      - alert: HighCacheMissRate
        expr: rate(cache_miss_total[1m]) > 500
        for: 1m
        labels:
          severity: warning
        annotations:
          summary: "Cache miss rate > 500/s — rate limiter may activate"
```

### Application-Specific Alerts

| Alert Condition | Severity | Response |
|----------------|----------|----------|
| Cache miss rate > 1,000/s for 30s | Critical | Activate rate limiter, check database |
| Cache hit rate < 80% for 2 min | Warning | Investigate TTL configuration |
| XFetch triggers > 1,000/min | Info | Early refresh working as expected |
| SWR stale serves > 10,000/min | Warning | Check backend query performance |
| TTL distribution < 5% variation | Warning | Jitter may be misconfigured |

---

## 4. Grafana Dashboard Layout

### Cache Health Dashboard

**Row 1 — Overview**
- Cache hit rate gauge (green > 95%, yellow > 80%, red < 80%)
- Current cache miss rate (requests/second)
- Database query rate (queries/second)
- Active cache entries count

**Row 2 — Performance**
- Cache hit/miss rate (time series, 6h window)
- Database query latency (p50/p95/p99) by cache miss
- Redis memory utilization (percentage)
- Redis eviction rate (keys/second)

**Row 3 — Stampede Indicators**
- TTL distribution histogram (should show uniform spread, not spike)
- Concurrent requests per key (top 10 keys)
- SWR stale serve rate (serves/second)
- XFetch trigger rate (triggers/second)

**Row 4 — Database Health**
- Connection pool utilization (active/idle/waiting)
- Active queries (by state)
- Replication lag
- Slow queries (count per minute)

---

## 5. Log Analysis Queries

### ELK/Splunk Queries for Stampede Detection

**Find cache stampede events:**
```
source="feed-service" AND "cache miss" AND rate > 1000
| timechart count by key limit=20
| where count > 1000
```

**Identify TTL synchronization:**
```
source="redis" AND "expired" AND "feed:*"
| stats count by _time
| where count > 100
```

**Trace stampede propagation:**
```
source="feed-service" AND ("database query" OR "cache miss" OR "timeout")
| transaction maxspan=30s by trace_id maxevents=10
| where eventcount > 5
| stats count by source
```

### Application Insights Query

```kusto
// Detect cache stampede pattern
traces
| where timestamp > ago(1h)
| where message contains "cache miss"
| summarize MissCount = count() by bin(timestamp, 10s)
| where MissCount > 500
| project timestamp, MissCount, Alert = "High cache miss rate"
```

---

## 6. TTL Distribution Monitoring

```java
package com.example.cache.monitoring;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class TtlDistributionMonitor {

    private static final Logger log = LoggerFactory.getLogger(TtlDistributionMonitor.class);

    private final RedisTemplate<String, String> redisTemplate;
    private static final int SAMPLE_SIZE = 1000;
    private static final double UNIFORMITY_THRESHOLD = 0.15;

    public TtlDistributionMonitor(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    @Scheduled(fixedRate = 300000) // every 5 minutes
    public void checkTtlDistribution() {
        Set<String> keys = redisTemplate.keys("feed:*");
        if (keys.isEmpty()) {
            return;
        }

        List<Long> ttls = keys.stream()
            .limit(SAMPLE_SIZE)
            .map(key -> redisTemplate.getExpire(key, TimeUnit.SECONDS))
            .filter(Objects::nonNull)
            .collect(Collectors.toList());

        if (ttls.isEmpty()) {
            return;
        }

        double mean = ttls.stream().mapToLong(Long::longValue).average().orElse(0);
        double stdDev = Math.sqrt(ttls.stream()
            .mapToDouble(t -> Math.pow(t - mean, 2))
            .average()
            .orElse(0));

        double cv = mean > 0 ? stdDev / mean : 0;

        log.info("TTL Distribution: mean={}s, stdDev={}s, CV={}", mean, stdDev, cv);

        if (cv < UNIFORMITY_THRESHOLD) {
            log.warn("TTL distribution CV={} is below threshold={} — possible TTL synchronization",
                cv, UNIFORMITY_THRESHOLD);
        }
    }
}
```

---

## 7. Alert Response Runbook

### Alert: Cache Hit Rate Below 50%
1. Check Redis INFO for hit rate, evictions, and memory
2. Check database query rate and connection pool
3. Check for recent TTL changes in configuration
4. Verify TTL jitter is enabled
5. Verify SWR is working (check X-Cache-Stale headers)
6. If stampede confirmed: activate emergency cache warming

### Alert: Database Query Rate Spike
1. Check if this correlates with cache miss spike
2. Check database CPU and connection pool
3. If cache-related: verify rate limiter is active
4. Consider temporarily increasing TTL
5. Consider serving stale content

### Alert: Connection Pool Exhaustion
1. Kill long-running queries
2. Increase pool size temporarily (emergency)
3. Activate backpressure in application
4. Consider read replica for cache miss queries

---

## 8. Monitoring Configuration Checklist

- [ ] Redis hit/miss rate metrics exported to Prometheus
- [ ] Database query rate and pool utilization monitored
- [ ] TTL distribution check (5-minute interval)
- [ ] SWR stale serve rate tracked
- [ ] XFetch trigger rate tracked
- [ ] Cache miss rate limiter metrics exported
- [ ] Alert for cache hit rate < 50%
- [ ] Alert for database query rate spike
- [ ] Alert for connection pool > 80%
- [ ] Alert for TTL distribution CV < 0.15
- [ ] Grafana dashboard with all panels
- [ ] ELK/Splunk queries for stampede analysis

---

## References
- Meta USENIX NSDI 2013: "Scaling Memcache at Facebook"
- Redis Documentation: "INFO command metrics"
- Prometheus Documentation: "Alerting rules for caching"
- Grafana Blog: "Monitoring Redis with Prometheus"
- Google SRE Book — Chapter 10: "Practical Alerting"
