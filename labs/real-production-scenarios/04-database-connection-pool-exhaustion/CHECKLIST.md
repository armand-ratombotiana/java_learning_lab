# Incident Response Runbook: Connection Pool Exhaustion

**Incident Type**: Database Connection Pool Exhaustion
**Severity**: P0-P1
**Response Time**: < 2 minutes for initial triage

## 1. DETECTION AND TRIAGE

### 1.1 Verify the Alert
- [ ] Confirm alert: "PoolExhaustion", "ConnectionTimeout", or "RDSConnectionHigh"
- [ ] Check HikariCP metrics: Active, Idle, Pending, Total
- [ ] Check application logs: "Connection is not available" or timeout errors
- [ ] Check RDS metrics: DatabaseConnections, CPUUtilization
- [ ] Check if application is still serving requests
- [ ] Check incident history: has this happened before?

### 1.2 Assess Impact
- [ ] Which application instances are affected?
- [ ] What percentage of requests are failing?
- [ ] Are downstream services affected?
- [ ] Are users impacted? (payment failures, order failures)
- [ ] Notify on-call incident manager
- [ ] Declare incident severity level (P0/P1)

### 1.3 Initial Mitigation
- [ ] If all connections exhausted:
  - [ ] Option 1: Roll back recent deployment if connection leak is suspected
  - [ ] Option 2: Restart application instances (releases all connections)
  - [ ] Option 3: Kill long-running queries on RDS: `SHOW FULL PROCESSLIST; KILL <id>;`
- [ ] If partial exhaustion:
  - [ ] Identify the slow query and kill it
  - [ ] Increase pool size as temporary measure (if RDS can handle it)
  - [ ] Add temporary index if missing index is identified

## 2. DATA COLLECTION

### 2.1 Collect HikariCP Metrics
- [ ] Check pool active/idle/pending counts
- [ ] Check leak detection logs: `grep "leak detection" /var/log/app/*.log`
- [ ] Check connection timeout count
- [ ] Check connection acquisition time (P50, P99)

### 2.2 Collect Database Metrics
- [ ] RDS active connections: `SHOW STATUS LIKE 'Threads_connected'`
- [ ] Running queries: `SHOW FULL PROCESSLIST`
- [ ] Long-running transactions: `SELECT * FROM information_schema.innodb_trx`
- [ ] Slow query log: check for recently logged queries
- [ ] RDS Performance Insights (if enabled)

### 2.3 Collect Application State
- [ ] Thread dump: `jstack <pid>` (find threads stuck in JDBC calls)
- [ ] Application logs for recent errors
- [ ] Recent deployment changes (last 24 hours)
- [ ] Recent code changes (last week)

### 2.4 Identify the Leak Source
- [ ] Check HikariCP leak detection stack trace (shows where connection was acquired)
- [ ] Search for `dataSource.getConnection()` calls without try-with-resources
- [ ] Search for Connection variables without finally block
- [ ] Check if new code paths were added recently

## 3. ROOT CAUSE ANALYSIS

### 3.1 Connection Leak Analysis
- [ ] Is pool.active == pool.total? → Pool fully utilized
- [ ] Are connections steadily increasing? → Connection leak
- [ ] Do leak detection logs show stack traces? → Identify the leaking code
- [ ] Are connections returning normally? → Check normal operations

### 3.2 Slow Query Analysis
- [ ] Check slow query log for queries > 1 second
- [ ] Run EXPLAIN on identified slow queries
- [ ] Check for missing indexes
- [ ] Check for full table scans (Extra: "Using where" without "Using index")
- [ ] Check for filesort (Extra: "Using filesort")

### 3.3 Code Review
- [ ] Review recent commits (last 24-48 hours)
- [ ] Check all new DataSource.getConnection() calls
- [ ] Verify try-with-resources or finally blocks
- [ ] Check for exception paths that skip close()

## 4. FIX AND VERIFICATION

### 4.1 Apply Fix
#### Connection Leak Fix
- [ ] Add try-with-resources to all Connection usage
- [ ] Add finally blocks where try-with-resources cannot be used
- [ ] Ensure ALL exception paths close connections

#### Slow Query Fix
- [ ] Add missing index (composite index matching WHERE + ORDER BY)
- [ ] Optimize query: remove SELECT *, add LIMIT, use covering indexes
- [ ] Review ORM configuration for N+1 or fetch strategy issues

#### Configuration Fix
- [ ] Set leakDetectionThreshold to 5000ms (5 seconds)
- [ ] Set connectionTimeout to 5000ms (5 seconds)
- [ ] Verify pool sizing matches formula

### 4.2 Verify Fix
- [ ] Deploy to canary instance
- [ ] Verify pool active connections remain at expected levels
- [ ] Simulate connection leak scenario — should not exhaust pool
- [ ] Run slow query — should use index and complete in < 10ms
- [ ] Run load test to verify pool stability

### 4.3 Deploy to Production
- [ ] Canary (10% traffic, 30 minutes)
- [ ] Regional (50% traffic, 2 hours)
- [ ] Full rollout (100%, 4 hours monitoring)

## 5. PREVENTIVE MEASURES

### 5.1 Monitoring
- [ ] Add HikariCP pool utilization alerts
- [ ] Add leak detection alerts
- [ ] Add slow query monitoring and alerts
- [ ] Add RDS connection count dashboard

### 5.2 Code Quality
- [ ] Add Checkstyle/ErrorProne rule for Connection leak detection
- [ ] Add try-with-resources to code style guide
- [ ] Add connection pool tests to CI/CD pipeline
- [ ] Add slow query detection to CI/CD

### 5.3 Database
- [ ] Set up regular slow query review
- [ ] Set up index maintenance schedule
- [ ] Add query performance tests

## 6. POSTMORTEM

- [ ] Complete INCIDENT_REPORT.md
- [ ] Complete ROOT_CAUSE.md with 5 Whys
- [ ] Complete PREVENTION.md
- [ ] Share findings with engineering teams
- [ ] File follow-up tickets

## Key Metrics Reference

| Metric | Healthy | Warning | Critical | Action |
|--------|---------|---------|----------|--------|
| Pool Active % | < 50% | 80% | 95% | Investigate leak |
| Pending Threads | 0 | > 0 | > 10 | Pool exhausted soon |
| Connection Acq Time | < 5ms | 100ms | 1000ms | Pool pressure |
| Leak Detection | 0 per hour | 1 per hour | 1 per 5min | Connection leak |
| Slow Queries | < 5/hour | 10/hour | > 10/5min | Missing index |
| RDS Connections | < 50% max | 80% max | 95% max | RDS capacity |

## Tools Reference

| Tool | Command | Purpose |
|------|---------|---------|
| jstack | `jstack <pid>` | Find threads stuck in JDBC |
| MySQL CLI | `SHOW FULL PROCESSLIST` | Check running queries |
| pt-query-digest | Percona toolkit | Analyze slow query log |
| HikariCP JMX | `jconsole <pid>` | Pool metrics in real-time |
| RDS Console | AWS Console | RDS metrics |
| MySQL CLI | `KILL QUERY <id>` | Kill problem query |
| HikariCP log | `grep "leak detection"` | Find leaked connections |

