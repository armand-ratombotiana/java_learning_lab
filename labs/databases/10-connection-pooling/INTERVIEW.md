# Interview Questions: Connection Pooling (Oracle Focus)

## Oracle-Specific Questions
- Compare HikariCP with Oracle UCP (Universal Connection Pool) for connection management.
- How does HikariCP's ConcurrentBag implementation work with Oracle's connection model?
- What is the optimal pool size for an Oracle OLTP application? Explain Little's Law in this context.
- How do you configure Oracle connection validation? Explain `SELECT 1 FROM DUAL` vs Oracle's `isValid()`.
- How does Oracle's DRCP (Database Resident Connection Pooling) differ from application-side pooling?
- How do you tune HikariCP for Oracle RAC environments with multiple instances?
- Explain connection leak detection in HikariCP: `leakDetectionThreshold` and how it works with Oracle.
- What happens to pooled connections when Oracle RAC fails over? How does HikariCP handle this?

## Google Cloud / Technical
- Cloud SQL connection pooling best practices
- Cloud SQL proxy vs HikariCP for connection management
- AlloyDB connection pooling compared to Oracle UCP

## Microsoft / Azure
- Azure SQL connection pooling vs Oracle UCP
- Azure Connection Monitor for database pool metrics
- SQL Server connection pooling differences from Oracle

## Amazon / AWS
- RDS Proxy for Oracle connection pooling
- RDS Oracle vs HikariCP pool sizing on AWS
- Aurora connection management vs Oracle UCP

## Apple
- Connection pooling for Apple server-side Java apps with Oracle
- Secure connection management for Apple services

## LeetCode-Style Problems
| Problem | Topic | Difficulty | Pattern |
|---------|-------|-----------|---------|
| Thread Pool | Web Server Threads | Medium | Resource Management |
| Pool Sizing | Little's Law | Medium | Queueing Theory |
| Rate Limiter | Token Bucket | Medium | Algorithm |
| Circuit Breaker | Resilience Pattern | Hard | State Machine |

## Production Scenarios
- Scenario 1: "HikariCP pool exhausted — connections not returned to pool"
- Scenario 2: "Oracle DRCP + HikariCP double pooling causing contention"
- Scenario 3: "RAC failover: HikariCP connections pointing to failed node"
- Scenario 4: "Connection validation 'SELECT 1 FROM DUAL' causing 1M queries/day"

## Interview Patterns & Tips
- Oracle interviews expect deep understanding of connection pooling for high-throughput apps
- Know HikariCP configuration for Oracle-specific scenarios (RAC, Data Guard)
- Pool sizing using Little's Law is a common interview question
- Performance engineering roles: $130K-$195K
- Oracle UCP certification is a plus for enterprise roles
