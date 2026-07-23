# Mock Interview: Connection Pooling (Lab 10)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 30 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is connection pooling and why is it important?

**Candidate:** Connection pooling maintains a cache of database connections that can be reused across requests. Importance:
1. **Connection creation is expensive:** TCP handshake, authentication, session setup (10-100ms)
2. **Resource management:** Databases can handle limited connections (e.g., Oracle ~500-1000)
3. **Performance:** Pooled connections are ready-to-use (sub-millisecond acquisition)
4. **Throttling:** Pool prevents application from overwhelming the database

**Interviewer:** What is HikariCP and why is it the default in Spring Boot?

**Candidate:** HikariCP is a high-performance JDBC connection pool. It's the Spring Boot default because:
- **Fast:** Optimized bytecode, minimal object allocation, lock-free collection (`ConcurrentBag`)
- **Small:** ~130KB JAR (vs ~1MB for commons-dbcp2)
- **Reliable:** Extensive test suite, battle-tested in production
- **Feature-rich:** Leak detection, JMX metrics, health checks, configurable validation

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you calculate the optimal connection pool size?

**Candidate:** The formula: `connections = ((core_count * 2) + effective_spindle_count)`

For modern systems:
- **OLTP workload:** 10-20 connections per application instance
- **With R2DBC:** Even fewer (10 connections handle thousands of requests)
- **CPU-bound workload:** More connections (up to 100)
- **I/O-bound workload with JPA:** Fewer connections (10-30)

Myth: More connections = better throughput. In reality, each connection consumes ~1MB memory + server resources. Too many connections cause contention, context switching, and slower performance.

**Interviewer:** What happens when the connection pool is exhausted?

**Candidate:** When pool is exhausted:
1. Requesting thread waits (blocked) for `connectionTimeout` (default 30s in HikariCP)
2. If timeout expires, `SQLException` with "Connection is not available" message
3. Application layer should handle this with retry or circuit breaker

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your production application shows intermittent "Connection is not available" errors during peak load. Pool size is 50, max connections = 200. Diagnose and fix.

**Candidate:** 

**Diagnosis:**
1. **Check pool metrics:** Is pool exhausted at peak? Enable HikariCP metrics (Micrometer + Prometheus).
2. **Check database:** Are connections actually being used? `SELECT count(*) FROM v$session` — if database has connections idle, the pool is holding them without using them.
3. **Check connection duration:** Are connections held for too long? Add `leakDetectionThreshold=30000`.
4. **Check transaction boundaries:** `@Transactional` on service methods that include slow external API calls.

**Root causes:**
- **Long transactions:** `@Transactional` method calling external REST API with 5s timeout → ties up connection for 5+ seconds
- **Not closing resources:** Missing `finally` block for connection close in raw JDBC
- **Slow queries:** A query taking 30 seconds holds the connection
- **Pool too small:** 50 connections × 5s average query time = ~10 TPS capacity

**Fix:**
```yaml
spring.datasource.hikari:
  maximum-pool-size: 30  # Decrease! Smaller pool = faster when correctly sized
  minimum-idle: 10
  max-lifetime: 1800000
  leak-detection-threshold: 30000  # 30 seconds
  connection-timeout: 10000
```

Also: optimize slow queries, add circuit breakers for external calls, ensure `@Transactional` isn't wrapping slow operations.

---

## Interviewer Feedback

**Strengths:** Good pool sizing understanding, practical troubleshooting, clear diagnosis approach  
**Areas to Improve:** Could discuss database-side connection limits and how to set pool sizes accordingly  
**Verdict:** Hire

---

*Databases Lab 10 MOCK_INTERVIEW.md*
