# Mock Interview: PostgreSQL (Lab 03)

**Role:** Database Engineer (Mid-Level)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Compare PostgreSQL with Oracle Database.

**Candidate:**

| Feature | PostgreSQL | Oracle |
|---------|-----------|--------|
| Licensing | Open Source (PostgreSQL license) | Commercial |
| ACID compliance | Full (MVCC) | Full (MVCC) |
| Replication | Streaming, logical | Data Guard, GoldenGate |
| Partitioning | Declarative (10+) | Range, list, hash, composite |
| JSON support | JSONB (binary, indexable) | JSON, JSON-relational duality (23c) |
| Extensions | Rich (PostGIS, pg_partman) | Built-in features |
| Performance | Excellent (configurable) | Excellent (many tuning options) |
| Oracle compatibility | Compatible (basic SQL) | Native |

**Interviewer:** Explain PostgreSQL MVCC (Multiversion Concurrency Control).

**Candidate:** PostgreSQL implements MVCC through tuple-level versioning:
- Each row version (tuple) has `xmin` (creating transaction) and `xmax` (deleting/expiring transaction)
- Readers see a snapshot of the database as of the start of their query
- Writers don't block readers (and vice versa) — readers read the last committed version
- Old row versions remain in the table until VACUUM removes them
- No undo tablespace needed (unlike Oracle) — dead tuples are stored in the table itself

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does PostgreSQL handle replication?

**Candidate:** PostgreSQL supports:
1. **Streaming replication (physical):** Primary ships WAL (Write-Ahead Log) segments to standby servers. Standby applies changes in recovery mode. Can be synchronous (no data loss) or asynchronous.
2. **Logical replication:** Publisher sends logical changes (INSERT, UPDATE, DELETE) to subscribers running on any PostgreSQL version. Used for selective replication, upgrades with zero downtime.
3. **Built-in tools:** `pg_basebackup` for initial sync, `recovery.conf` for standby configuration.

**Interviewer:** Explain PostgreSQL indexing types.

**Candidate:**
- **B-tree (default):** Balanced tree for equality and range queries
- **Hash:** Equality lookups only (faster than B-tree for exact matches)
- **GiST:** Generalized Search Tree for full-text search, geometric data, range types
- **GIN:** Generalized Inverted Index for array values, JSONB, full-text search
- **BRIN:** Block Range Index for large sorted tables (much smaller than B-tree)
- **SP-GiST:** Space-partitioned GiST for non-balanced data structures

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a PostgreSQL architecture for a SaaS application handling 50K writes/second and 200K reads/second.

**Candidate:** 

**Architecture:**
```
Application → PgBouncer (Connection Pool) 
                      │
          ┌───────────┴───────────┐
          │                       │
    Write Master (primary)    Read Replicas (x3)
    │                           │
    └── Synchronous ───────────┘
         WAL Shipping
    
    Patroni (High Availability)
    └── etcd (Consensus store)
```

**Configuration:**
```ini
# Primary: Optimized for writes
max_connections = 100
shared_buffers = 32GB
effective_cache_size = 96GB
work_mem = 64MB
maintenance_work_mem = 2GB
wal_level = logical
max_wal_senders = 5
synchronous_commit = on

# Replicas: Optimized for reads
max_standby_streaming_delay = 30s
hot_standby_feedback = on
```

**Key strategies:**
- **Write scaling:** Partition tables by tenant_id or date. Use BRIN indexes for time-series data.
- **Read scaling:** Load balance reads across replicas (pgpool-II or application-level routing).
- **Connection pooling:** PgBouncer with transaction-level pooling (5000 app connections → 100 DB connections).
- **Caching:** pg_stat_statements for query monitoring. Application-level Redis cache for hot data.
- **Auto-failover:** Patroni monitors primary health, auto-promotes replica on failure.

---

## Interviewer Feedback

**Strengths:** Good PostgreSQL-Oracle comparison, strong replication knowledge, practical scaling design  
**Areas to Improve:** Could discuss PostgreSQL 17 new features  
**Verdict:** Hire

---

*Databases Lab 03 MOCK_INTERVIEW.md*
