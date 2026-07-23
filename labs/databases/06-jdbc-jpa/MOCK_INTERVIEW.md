# Mock Interview: JDBC & JPA (Lab 06)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is JDBC and how does it work?

**Candidate:** JDBC (Java Database Connectivity) is a Java API for connecting to relational databases. The flow:
1. Load driver: `Class.forName("oracle.jdbc.OracleDriver")`
2. Get connection: `DriverManager.getConnection(url, user, pass)`
3. Create statement: `connection.prepareStatement(sql)`
4. Execute query: `statement.executeQuery()` or `executeUpdate()`
5. Process results: Iterate through `ResultSet`
6. Close resources: ResultSet, Statement, Connection (in finally block or try-with-resources)

Connection pooling (HikariCP) is critical for production — creating connections is expensive. Pool manages a set of reusable connections.

**Interviewer:** Compare JDBC, JPA, and Spring Data JPA.

**Candidate:**
| Layer | Abstraction Level | Code Needed | Caching | Learning Curve |
|-------|------------------|-------------|---------|----------------|
| JDBC | Low (SQL strings) | High (mapping, resource mgmt) | None | Low |
| JPA | Medium (entities, relationships) | Medium (entities, repositories) | L1/L2 cache | Medium |
| Spring Data JPA | High (repository interfaces) | Low (interfaces) | + Spring cache | Low (for simple cases) |

Spring Data JPA eliminates boilerplate: `interface UserRepository extends JpaRepository<User, Long>` automatically provides CRUD methods.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Explain connection pooling. How does HikariCP work?

**Candidate:** Connection pooling maintains a pool of database connections that can be reused:
1. Application requests connection from pool
2. Pool provides an idle connection (or creates new one up to max)
3. Application uses connection
4. Application returns connection to pool (calling `close()` actually returns to pool)
5. Pool validates connection health (test query, aliveness check)

**HikariCP optimizations:**
- Fast connection state validation (JDBC4 Connection.isValid())
- Optimized pool size calculation (smaller pools often faster due to less context switching)
- Concurrent bag data structure for lock-free connection management
- Leak detection threshold

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Your application's database connection pool is exhausted. Connections spike to 100% usage every hour. Diagnose and fix.

**Candidate:** 

**Diagnosis:**
1. **Enable pool monitoring:** `HikariConfig.setMetricsTrackerFactory()`, expose `/actuator/health`
2. **Analyze pattern:** If spikes are hourly, look for scheduled jobs (cron, batch processing)
3. **Check for leaks:** Enable leak detection: `leakDetectionThreshold=10000` (10 seconds)
4. **Review timeout settings:** `connectionTimeout=5000` — if connections don't return in 5 seconds, new ones are created but never closed

**Common causes:**
1. **Missing `close()` in exception paths:** Connection not returned to pool on error
2. **Transaction too long:** `@Transactional` method doing slow external API call
3. **Connection starvation:** Pool too small for peak load
4. **Dead connections:** Network issue, database restart — pool keeps stale connections

**Fix:**
```yaml
spring.datasource.hikari:
  maximum-pool-size: 20
  minimum-idle: 5
  connection-timeout: 5000
  max-lifetime: 1800000  # 30 min
  idle-timeout: 600000    # 10 min
  leak-detection-threshold: 10000
  validation-timeout: 3000
```

Also add `spring.datasource.hikari.isolate-internal-queries=true` and set `connection-test-query: SELECT 1 FROM DUAL`.

---

## Interviewer Feedback

**Strengths:** Clear JDBC/JPA understanding, practical connection pooling diagnosis  
**Areas to Improve:** Could discuss R2DBC for reactive database access  
**Verdict:** Hire

---

*Databases Lab 06 MOCK_INTERVIEW.md*
