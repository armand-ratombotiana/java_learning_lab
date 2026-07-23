# Lab 04 — Database Connection Pool Exhaustion: Interview Questions

**Q1: What is connection pool exhaustion and how does it manifest?**

**Answer:** Connection pool exhaustion occurs when all connections in a pool are checked out and no idle connections remain. New threads requesting connections block for up to connectionTimeout (default 30s) then throw SQLException: "Connection is not available, request timed out after Nms." Manifestation: application hangs, database-dependent features fail, error logs show connection timeout messages.

**Q2: How does HikariCP detect leaked connections?**

**Answer:** HikariCP has a leakDetectionThreshold parameter (in milliseconds). When a connection is checked out from the pool for longer than this threshold, HikariCP logs a warning with a stack trace showing where the connection was acquired. Default is 0 (disabled). For production, set to 3-5 seconds. The key: the threshold must be shorter than typical query duration + expected processing time.

**Q3: Explain the HikariCP pool sizing formula.**

**Answer:** The recommended formula: connections = (core_count * 2) + effective_spindle_count. For SSD-backed databases, spindle_count ≈ 1. So for a 4-core instance: (4 * 2) + 1 = 9 connections. The reasoning: database connections are multiplexed over a fixed number of DB worker threads. More connections than optimal causes context switching overhead. The formula assumes optimal query performance (< 5ms). If queries are slow, pool capacity is effectively reduced.

**Q4: What's the difference between a connection leak and a slow query in the context of pool exhaustion?**

**Answer:** Connection leak: connection is checked out but never returned (missing close() call). Pool active count stays high permanently. Slow query: connection is returned but held for a long time before return. Pool active stays high for the query duration. Leaks eventually exhaust the pool permanently. Slow queries exhaust the pool temporarily but recover after queries complete. Log analysis: leaks show stack traces at acquire point; slow queries show SQL execution time.

**Q5: How would you diagnose a production connection pool exhaustion?**

**Answer:** 1) Check HikariCP metrics: pool.ActiveConnections, pool.PendingThreads, pool.ConnectionTimeoutCount. 2) If Active ≈ Total and Pending > 0, pool is exhausted. 3) Check pool.LeakDetectionThreshold logs for leaked connection stack traces. 4) If no leaks, check MySQL SHOW FULL PROCESSLIST for long-running queries. 5) Check SHOW ENGINE INNODB STATUS for locks. 6) Check for recent deployment that might have introduced a missing close() call.

**Q6: What is try-with-resources and how does it prevent connection leaks?**

**Answer:** try-with-resources (Java 7+) automatically calls close() on AutoCloseable resources when the try block exits, both normally and on exception. For Connection: try (Connection conn = ds.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) { ... } — all three resources are closed in reverse order, even if an exception occurs. This eliminates the most common connection leak pattern: missing finally block.

**Q7: Calculate the optimal pool size for: 8 vCPU instances, 40 app instances, RDS max_connections=2000, average query 10ms, peak TPS 1000 per instance.**

**Answer:** HikariCP formula: (8 * 2) + 1 = 17 per instance. 40 instances * 17 = 680 total (under 2000 limit). But verify with workload: 1000 TPS * 10ms avg query = 10 concurrent connections needed per instance. 17 provides headroom. If queries spike to 50ms, need 50 connections. So: base on peak query time, not average. Formula is starting point — monitor and adjust.

**Q8: You see 200 active connections, 87 pending threads, and leak detection threshold is 30 minutes. What's wrong?**

**Answer:** The leak detection threshold (30 min) is way too high — it takes 30 minutes to detect a leak. The pool exhausts in 4 minutes at 3-5 leaks/minute. So leaks are detected 26 minutes AFTER the pool is exhausted. Fix: reduce leakDetectionThreshold to 3-5 seconds. Also: investigate why connections are not being returned.

**Q9: Design a connection pool health check that runs in production.**

**Answer:** 1) Metric collection: export active/idle/pending/timeout counts via JMX/Micrometer every 10 seconds. 2) Dashboard: Grafana showing utilization %, pending thread count, timeout rate. 3) Alerts: active > 80% of max for 5 min (warning), pending > 0 for 1 min (critical), timeout count > 0 (critical). 4) Auto-diagnosis: if pool exhausted, capture stack traces of threads waiting for connections, capture current SQL from database. 5) Auto-mitigation: kill oldest queries in DB, increase pool size temporarily, restart connection pool.

**Q10: How does database connection pooling work with serverless/containerized environments?**

**Answer:** In containerized environments (Kubernetes), each pod runs its own connection pool. Pool sizing must account for: number of pods (scales up/down), total database max_connections, connection lifetime (pods restart periodically). Key strategies: use external connection pool (PgBouncer, RDS Proxy) for serverless, minimize idle connections, use connection validation on checkout to handle pod restarts gracefully, monitor pod-to-database connection ratio.

**Q11: Tell me about a time you resolved a connection pool exhaustion incident. (STAR)**

**Answer:** Situation: E-commerce order processing halted — all database operations failing with "Connection is not available, request timed out after 30000ms." Task: As on-call engineer, I needed to restore service. Action: I checked HikariCP metrics — 198 active/200 total, 87 pending threads. I checked recent deployments and found a new transaction handler missing a finally block. I restarted the connection pool as immediate mitigation (connections returned). Then I deployed a fix adding try-with-resources. I also found a slow query that was aggravating the issue and added an index. Result: Service restored in 10 minutes. Pool utilization dropped from 99% to 25%. No recurrence.

**Q12: What static analysis rules can prevent connection leaks?**

**Answer:** 1) ErrorProne check: "Connection" type variables must appear in try-with-resources. 2) SpotBugs rule: "Method may fail to close database resource." 3) Custom Checkstyle: ensure every getConnection() call is inside try-with-resources. 4) ArchUnit test: verify that no method in the persistence layer returns a Connection object. 5) Integration test: invoke all database access methods with failing scenarios and verify connections are returned to the pool.

**Q13: How do you configure HikariCP for a production microservice?**

**Answer:** Properties: poolName=OrderService, maxPoolSize=20, minIdle=5, connectionTimeout=5000ms, idleTimeout=300000ms, maxLifetime=600000ms, leakDetectionThreshold=5000ms, validationTimeout=1000ms, registerMbeans=true. recommendation: always set leakDetectionThreshold to 5s, connectionTimeout to 5s, and enable JMX metrics. For read-heavy services, consider increasing maxPoolSize. For write-heavy, keep smaller to avoid DB contention.

**Q14: How would you load test a connection pool setup?**

**Answer:** 1) Create test that simulates peak traffic volume (TPS = expected peak * 1.5). 2) Monitor pool metrics during test: active connections, pending threads, timeout count, connection acquire time. 3) Verify: active stays < 80% of max, pending stays 0, timeout count stays 0, P99 acquire time < 5ms. 4) Run for 1+ hours to catch slow leaks. 5) Test failure scenarios: database restart, network partition, slow query injection.

**Q15: Compare database connection pool exhaustion to thread pool starvation. How are they similar? How do they differ?**

**Answer:** Similarities: both cause application hangs, both result from requests queuing, both require monitoring of utilization %. Differences: connection pool exhaustion affects database operations only (other features work). Thread pool starvation affects all work done by that thread pool. Connection pool exhaustion can be mitigated by killing DB queries. Thread pool starvation requires restarting or scaling the application. Connection leaks are permanent (until eviction), thread leaks are transient (threads eventually die).
