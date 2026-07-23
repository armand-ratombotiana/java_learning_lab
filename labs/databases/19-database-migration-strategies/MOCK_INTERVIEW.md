# Mock Interview: Database Migration Strategies (Lab 19)

**Role:** Database Migration Engineer (Senior)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the main strategies for migrating databases between systems?

**Candidate:** Five main strategies:
1. **Big Bang (Offline):** Stop source, migrate everything, switch to target. Simple but downtime proportional to data volume.
2. **Trickle (Online):** Continuous sync between source and target, then quick cutover. Near-zero downtime but complex.
3. **Phased (Database per service):** Migrate one service/database at a time. Used in microservices migration.
4. **Dual-write:** Write to both databases simultaneously during migration. Most complex but zero risk of data loss.
5. **Replication-based:** Use CDC (Change Data Capture) to replicate source to target. Minimal downtime.

**Interviewer:** Compare Database Migration Service (DMS) with manual migration approaches.

**Candidate:** 
- **AWS DMS:** Managed, supports heterogeneous (Oracle to Aurora, SQL Server to PostgreSQL). Handles ongoing replication. Cost: hourly + data transfer.
- **Oracle GoldenGate:** Real-time, bidirectional, heterogeneous. Very reliable but expensive and complex.
- **Manual (Data Pump + scripts):** Full control, free, but high effort for ongoing sync. Best for one-time migrations with downtime.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you plan a migration from Oracle to PostgreSQL?

**Candidate:** 

**Assessment phase:**
1. **Schema compatibility:** Check unsupported Oracle features (CONNECT BY, hierarchical queries, flashback, packages)
2. **Query compatibility:** Rewrite PL/SQL to PL/pgSQL
3. **Data type mapping:**
   - `NUMBER` → `NUMERIC` or `INTEGER`
   - `VARCHAR2` → `VARCHAR`
   - `DATE` → `TIMESTAMP` (PostgreSQL DATE has no time component)
   - `CLOB` → `TEXT`
4. **Feature gaps:**
   - Oracle sequences → PostgreSQL `SERIAL`/`IDENTITY`/sequence
   - Oracle materialized views → PostgreSQL materialized views (REFRESH MATERIALIZED VIEW)
   - Oracle VPD → PostgreSQL Row-Level Security

**Migration phase:**
1. **Schema migration:** Use ora2pg or pgloader for automated conversion
2. **Data migration:** Dump via `expdp`, transform, load via `pg_bulkload`
3. **Application changes:** Rewrite SQL for PostgreSQL compatibility
4. **Testing:** Run both systems in parallel, compare results
5. **Cutover:** Quick switch after validating data integrity

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a zero-downtime migration from Oracle 11g to Oracle 19c on OCI.

**Candidate:** 

**Strategy: Oracle GoldenGate replication + phased cutover:**

```
Phase 1 (Weeks 1-4): Setup
  Oracle 11g (on-prem) ─── GoldenGate Extract ──→ GoldenGate Replicat ──→ Oracle 19c (OCI)
     │                     │
     │             ┌───────┴───────┐
     │             │  Initial load │
     │             │  (Data Pump)  │
     │             └───────────────┘
     │
Phase 2 (Weeks 5-6): Ongoing sync
  GoldenGate captures changes from 11g redo logs
  Replicates to 19c in near-real-time
  Benchmark query performance on 19c

Phase 3 (Cutover): < 5 minutes
  1. Stop application writes
  2. GoldenGate catches up (seconds)
  3. Verify data parity (row counts, checksums)
  4. Switch DNS/connection strings to 19c
  5. Start application on 19c
  6. GoldenGate reverse sync (19c → 11g) for rollback capability

Phase 4 (Post-migration): Weeks 7-8
  Monitor 19c performance
  Decommission 11g after validation period
  GoldenGate removed
```

**Key considerations:**
- **Rollback plan:** GoldenGate reverse sync allows instant rollback within 24 hours
- **Performance comparison:** Run same workload on both systems during Phase 2
- **Backward compatibility:** 19c is backward compatible with 11g for SQL syntax
- **Testing:** All stored procedures, schedules, and reports validated before cutover

---

## Interviewer Feedback

**Strengths:** Excellent migration strategy knowledge, practical GoldenGate design, thorough cutover planning  
**Areas to Improve:** Could discuss AWS DMS for Oracle to Aurora migration  
**Verdict:** Strong Hire

---

*Databases Lab 19 MOCK_INTERVIEW.md*
