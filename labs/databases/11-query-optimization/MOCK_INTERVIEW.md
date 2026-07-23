# Mock Interview: Query Optimization (Lab 11/15)

**Role:** Database Performance Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** How do you read an Oracle execution plan?

**Candidate:** An execution plan shows how Oracle accesses data:
- **Operation:** TABLE ACCESS FULL (full scan), INDEX RANGE SCAN (index lookup), NESTED LOOPS, HASH JOIN
- **Cost:** Relative cost estimate (lower is better)
- **Cardinality:** Estimated rows returned
- **Bytes:** Estimated data volume
- **Time:** Estimated execution time

Key things to look for:
- Full table scans on large tables (missing index)
- Inefficient join order (high-cardinality result set joined first)
- Cartesian joins (missing join condition)
- Predicate information: WHERE clause filtering efficiency

**Interviewer:** What is the AWR report and how do you interpret it?

**Candidate:** AWR (Automatic Workload Repository) captures database performance snapshots. Key sections:
- **Top 5 Timed Events:** Where the database spends most of its time
- **SQL Statistics:** Top SQL by elapsed time, CPU, I/O
- **Instance Efficiency:** Buffer cache hit ratio, library cache hit ratio
- **Wait Events:** What sessions are waiting for (I/O, locks, network)
- **Segment Statistics:** Hot objects (tables, indexes with high I/O)

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** A query runs in 1 second in development but 10 seconds in production. What do you check?

**Candidate:** 
1. **Statistics:** `SELECT table_name, num_rows, last_analyzed FROM all_tables` — stale optimizer statistics cause poor plans
2. **Execution plan comparison:**
   - Dev: INDEX RANGE SCAN + NESTED LOOPS (good for small result set)
   - Prod: FULL TABLE SCAN + HASH JOIN (bad for large result set)
3. **Bind variable peeking:** Query optimized for first bind value, not representative
4. **Parameter differences:**
   - `optimizer_mode` (ALL_ROWS vs FIRST_ROWS)
   - `db_file_multiblock_read_count` (affects full scan efficiency)
   - `workarea_size_policy` (affects sort/hash join memory)

**Fix:** Gather fresh statistics, use SQL profiles or SQL Plan Baselines to stabilize plan.

**Interviewer:** What are the common index types and when should I use each?

**Candidate:** 
- **B-tree:** Default, good for high-cardinality columns, range queries, equality
- **Bitmap:** Low-cardinality columns (gender, status) — efficient for warehousing, bad for OLTP with concurrent updates
- **Function-based:** `CREATE INDEX idx_upper_name ON employees(UPPER(last_name))` — for queries using functions
- **Composite:** Multiple columns — key ordering matters (most selective first)
- **Unique:** Enforces uniqueness, used by primary key
- **Reverse key:** For clustered inserts (reduces index block contention)
- **Invisible:** For testing new indexes without affecting existing plans

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** An OLTP database is experiencing "enq: TX - row lock contention" wait events. 50% of queries are waiting for locks. Diagnose and propose solutions.

**Candidate:** 

**Diagnosis:**
1. **Identify the locked object:** `SELECT * FROM v$locked_object` → identify table and row
2. **Find blocking session:** `SELECT * FROM dba_blockers` → who holds the lock
3. **Find waiting sessions:** `SELECT * FROM dba_waiters` → who's waiting
4. **Check SQL from blocking session:** What transaction is holding the lock for so long?

**Common causes:**
1. **Uncommitted transactions:** A session holds lock without committing
2. **Long-running batch job:** Holding locks on rows while processing
3. **Unindexed foreign keys:** `ON UPDATE CASCADE` without index locks entire parent table
4. **Application logic error:** Transaction opened, lock acquired, bug prevents COMMIT/ROLLBACK

**Solutions:**
1. **Immediate:** Kill blocking session `ALTER SYSTEM KILL SESSION 'sid,serial#'`
2. **Short-term:** Set `COMMIT` more frequently in batch jobs
3. **Long-term:**
   - Add indexes on foreign key columns
   - Use `NOWAIT` or `SKIP LOCKED` for concurrent access to queue tables
   - Review transaction boundaries: keep transactions short
   - Implement optimistic locking in application for low-contention data
   - Configure `db_block_checking` for hot blocks

**Interviewer:** Your team has a query that does a full table scan on a 500M row table every 5 minutes. How many I/O operations does this cause? How can you optimize?

**Candidate:** 

**I/O calculation:**
- Row width: ~200 bytes
- Oracle block size: 8KB
- Blocks per scan: 500M × 200 / 8192 ≈ 12.2M blocks
- Scans per hour: 12 scans
- Total blocks read per hour: ~146M blocks
- At 500 IOPs per disk: 146M / (500 × 3600) ≈ 81 seconds of I/O per hour

**Optimization options:**
1. **Index:** Create composite index covering WHERE + SELECT columns (covering index)
2. **Materialized view:** Pre-compute and refresh periodically
3. **Partitioning:** Partition by date/region — query only relevant partitions
4. **Result cache:** `SELECT /*+ RESULT_CACHE */ ...` — cache result in SGA
5. **Parallel query:** `ALTER SESSION ENABLE PARALLEL QUERY` — distribute scan across CPUs
6. **In-Memory Column Store:** Store table in IM column store for sub-second scans
7. **Review business requirement:** Does it really need to scan every 5 minutes?

---

## Interviewer Feedback

**Strengths:** Deep performance tuning knowledge, practical diagnosis approach, I/O calculation skills  
**Areas to Improve:** Could discuss SQL Monitor for real-time SQL monitoring  
**Verdict:** Strong Hire

---

*Databases Lab 11/15 MOCK_INTERVIEW.md*
