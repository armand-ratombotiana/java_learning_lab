# Interview Cheatsheet: Slow Query / Deadlock

## Key Diagnostic Commands
- MySQL: `SHOW FULL PROCESSLIST` — running queries
- MySQL: `EXPLAIN ANALYZE <query>` — query plan
- MySQL: `SHOW ENGINE INNODB STATUS` — deadlock info
- PostgreSQL: `pg_stat_activity` — active queries
- PostgreSQL: `pg_locks` — lock information
- AWR (Oracle): compare snapshots for plan regression
- `pt-query-digest` / `pt-deadlock-logger` — Percona Toolkit

## Common Metrics to Check
- Slow query count (long_query_time threshold)
- Lock wait time
- Row lock count / table lock count
- Query execution plan (full table scan? index used?)
- Index hit ratio
- Temp table creation count

## Typical Root Causes
- Missing index causing full table scan
- N+1 query pattern (ORM lazy loading)
- Stale optimizer statistics
- Plan regression (optimizer chose bad plan)
- Row lock escalation → table lock
- Unindexed foreign key
- Large IN() clause with suboptimal plan

## Interview Question Patterns
- "How do you find and fix N+1 queries?"
- "What causes a database deadlock and how do you resolve it?"
- "How do you optimize a slow query with EXPLAIN?"
- "Design a batch processing system that avoids locks"

## STAR Story Template
**S**: Monthly report generation went from 2 hours to 12+ hours
**T**: Identify and fix the performance regression
**A**: Found N+1 query pattern in report, optimized with batch JOIN+IN, added index
**R**: Report completed in 15 minutes, added query monitoring
