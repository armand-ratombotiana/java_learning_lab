# Mock Interview: Backend Performance Optimization (Lab 24)

**Role:** Backend Engineer (Staff)  
**Duration:** 50 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How do you measure performance in a Spring Boot application?

**Candidate:** Multiple levels of measurement:

1. **JMH (Java Microbenchmark Harness):** For method-level microbenchmarks
```java
@Benchmark
@BenchmarkMode(Mode.Throughput)
@Measurement(iterations = 5, time = 1)
public void benchmarkUserLookup() {
    userService.findById(1L);
}
```
2. **Micrometer + Prometheus:** For application-level metrics (request duration, error rate, JVM metrics)
3. **Spring Boot Actuator:** `/actuator/metrics` for HTTP, `@Timed` for service methods
4. **Async Profiler:** CPU and wall-clock profiling for hotspots
5. **GC logging:** `-Xlog:gc*:file=gc.log` for garbage collection analysis

**Interviewer:** What are the most common performance bottlenecks in Spring Boot applications?

**Candidate:** Top bottlenecks in order:
1. **Database queries:** N+1 queries, missing indexes, large result sets
2. **Connection pool exhaustion:** Too many concurrent requests, pool too small
3. **Thread pool saturation:** Blocking I/O on limited threads
4. **Serialization/Deserialization:** Large JSON payloads, slow serializers
5. **Logging:** Synchronous logging under high throughput
6. **Memory leaks:** Not clearing ThreadLocals, growing collections
7. **GC pauses:** Object allocation rate exceeds GC throughput

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you optimize database query performance? Give specific Spring Boot patterns.

**Candidate:** 

1. **Keyset pagination instead of offset:**
```java
// Bad: SELECT * FROM orders ORDER BY id LIMIT 10 OFFSET 100000 (table scan)
// Good: SELECT * FROM orders WHERE id > :lastSeen ORDER BY id LIMIT 10 (index scan)
@Query("SELECT o FROM Order o WHERE o.id > :lastSeen ORDER BY o.id")
List<Order> findNextPage(@Param("lastSeen") Long lastSeen, Pageable pageable);
```

2. **DTO projections instead of entities:**
```java
public interface OrderSummary {
    Long getId();
    String getStatus();
    BigDecimal getTotal();
    @Value("#{target.orderDate}")
    LocalDate getDate();
}
```

3. **@Transactional(readOnly=true) on queries** to route to read replicas and disable dirty checking

4. **JDBC batch for bulk operations:**
```properties
spring.jpa.properties.hibernate.jdbc.batch_size=50
spring.jpa.properties.hibernate.order_inserts=true
```

5. **Connection pool tuning:**
```yaml
spring:
  datasource:
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 5000
      max-lifetime: 1800000
```

**Interviewer:** What profiling tools do you use and how do you interpret results?

**Candidate:** 
- **Async Profiler** (async-profiler): Generate CPU and allocation flame graphs
- **Java Flight Recorder (JFR):** Continuous low-overhead profiling (JDK 21+ built-in)
- **IntelliJ Profiler:** Integrated CPU/memory profiling for development

Interpreting flame graphs:
- Wide bars = methods consuming most CPU
- Look for: unexpected method calls (accidental N+1), deep call stacks suggesting reflection, hot loops
- Allocation flame graphs: look for frequent object creation in tight loops

---

## Round 3: Hard (15-25 minutes)

**Interviewer:** Your Spring Boot application handles 500 requests/second but latency has increased from 50ms to 500ms over the past month. Diagnose and fix.

**Candidate:** Systematic diagnosis:

**Step 1 — Check JVM metrics:**
- CPU: Is it maxed? → Profile to find hotspots
- Heap: Is GC frequent? → `-Xlog:gc*` to check GC pause times
- Threads: Are threads BLOCKED or WAITING? → Thread dump analysis

**Step 2 — Database metrics:**
- Slow query log: `log_min_duration_statement = 100`
- Connection pool: Are all connections in use?
- Lock contention: `pg_locks` (PostgreSQL) or `V$LOCK` (Oracle)

**Step 3 — External dependencies:**
- Circuit breaker states: Are many circuits opening?
- Retry storms: Are services retrying aggressively and compounding load?

**Most likely scenarios:**
1. **Connection pool leak:** Connections not returned to pool → threads queue → latency increases. Fix: check `HikariConfig.setLeakDetectionThreshold(10000)`.
2. **Database index degradation:** New query pattern without proper index. Fix: `EXPLAIN ANALYZE` and add missing index.
3. **Memory leak → GC thrashing:** `jmap -histo:live` to compare object counts week-over-week. Fix: fix reference leak.
4. **Log backpressure:** Async logging queue is full. Fix: increase `queueSize` or reduce log volume.

**Interviewer:** How do you handle connection pooling for high-throughput applications?

**Candidate:** HikariCP tuning:
```yaml
spring.datasource.hikari:
  maximum-pool-size: 20
  minimum-idle: 10
  idle-timeout: 300000
  connection-timeout: 5000
  max-lifetime: 1800000
  pool-name: MainPool
  leak-detection-threshold: 60000
  validation-timeout: 3000
```

**Myth debunk:** `maximum-pool-size` does NOT equal max concurrent queries. Formula: `connections = ((core_count * 2) + effective_spindle_count)`. For modern SSDs with async drivers, even smaller pools (10-20 connections per instance) handle thousands of QPS. More connections = more context switching = slower.

For WebFlux with R2DBC, use even smaller pools (5-10 connections) because the same connection serves many requests asynchronously.

**Interviewer:** How does Java 21's virtual threads impact Spring Boot performance?

**Candidate:** Virtual threads (Project Loom) are a game-changer for Spring Boot performance:

```java
// Spring Boot 3.2+ with virtual threads enabled
spring.threads.virtual.enabled=true
```

**Performance impact:**
- Traditional: 200 threads = ~2MB stack each = ~400MB just for threads
- Virtual threads: Thousands of virtual threads on ~1MB carrier thread pools
- No thread pool sizing needed — virtual threads are lightweight enough to create per-request

**Where virtual threads help most:**
- Blocking I/O (JDBC calls, REST calls, file I/O) — previously blocked platform threads
- High-concurrency web servers (Tomcat with virtual threads handles 10K concurrent requests)

**Where they don't help:**
- CPU-bound operations (need more CPU cores, not more threads)
- Reactive applications (already non-blocking)
- Synchronized blocks or native methods (pinning)

---

## Interviewer Feedback

**Strengths:** Excellent diagnostic approach, practical connection pool tuning, deep virtual threads knowledge  
**Areas to Improve:** Could discuss JMH benchmark warmup phases and fork settings  
**Verdict:** Strong Hire

---

*Lab 24 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
