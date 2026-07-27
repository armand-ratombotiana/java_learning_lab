# SQL Problem Walkthrough: 17-oracle-architecture

## Problem 1: Tuning SQL Based on Wait Events (Oracle DBA) -- Oracle

### Interview Scenario
"Oracle interview: Users report that the database is slow. Using AWR and ASH reports, you identify that 'log file sync' and 'buffer busy waits' are the top wait events. Diagnose and fix the underlying issues."

### The Problem
You have an OLTP database. AWR shows:
- log file sync: 40% of DB time
- buffer busy waits: 15% of DB time
- CPU time: 20%
- Other: 25%

The application does frequent INSERT/UPDATE/DELETE on a few hot tables. Identify the root causes and propose specific SQL and configuration changes to reduce these waits.

### Step 1: Understand Oracle Architecture
Key components involved:
- **Redo Log Buffer**: In-memory buffer for redo entries
- **LGWR (Log Writer)**: Writes redo log buffer to online redo log files
- **Commit processing**: COMMIT triggers LGWR to flush buffer to disk
- **Buffer Cache**: In-memory cache for data blocks
- **DBWR (Database Writer)**: Writes dirty buffers to data files

Wait events explained:
- **log file sync**: A session waiting for COMMIT to complete -- LGWR is writing to redo logs
- **buffer busy waits**: Multiple sessions trying to access the same block in buffer cache (hot block contention)

### Step 2: Think Aloud
log file sync:
- Frequent commits (commit-after-every-row pattern)
- Slow redo log I/O (redo logs on slow disk)
- Redo log buffer too small
- LGWR not keeping up (batch commit optimization needed)

buffer busy waits:
- Hot blocks -- many sessions accessing the same block
- Sequence-based PKs causing right-hand-side index contention
- Freelist contention (multiple sessions inserting into same block)

### Step 3: Diagnose with SQL
```sql
-- Check redo log file placement and size
SELECT group#, member, type, status, bytes/1024/1024 AS size_mb
  FROM v$logfile
  JOIN v$log USING (group#)
 ORDER BY group#;

-- Check redo log buffer size
SELECT name, value/1024/1024 AS size_mb
  FROM v$parameter
 WHERE name = 'log_buffer';

-- Find hot blocks (buffer busy waits)
SELECT o.object_name,
       bh.block_class,
       COUNT(*) AS buffer_busy_count
  FROM v$segment_statistics ss
  JOIN dba_objects o ON ss.obj# = o.object_id
  JOIN v$bh bh ON bh.objd = ss.obj#
 WHERE ss.statistic_name = 'buffer busy waits'
   AND ss.value > 0
 GROUP BY o.object_name, bh.block_class
 ORDER BY COUNT(*) DESC
 FETCH FIRST 10 ROWS ONLY;
```

### Step 4: Write Fixes

Fix 1 -- Reduce commit frequency (application change):
```sql
-- Problem: commit after every row
FOR rec IN cursor LOOP
  INSERT INTO orders VALUES (...);
  COMMIT;  -- BAD: triggers log file sync per row
END LOOP;

-- Fix: batch commit every N rows
v_counter := 0;
FOR rec IN cursor LOOP
  INSERT INTO orders VALUES (...);
  v_counter := v_counter + 1;
  IF MOD(v_counter, 1000) = 0 THEN
    COMMIT;
  END IF;
END LOOP;
COMMIT;
```

Fix 2 -- Use NOLOGGING for non-critical operations:
```sql
ALTER TABLE staging_table NOLOGGING;
INSERT /*+ APPEND */ INTO staging_table SELECT * FROM source_table;
```

Fix 3 -- Reduce index contention:
```sql
-- Reverse key index for sequence-based PK
CREATE INDEX orders_pk_rev ON orders(REVERSE(order_id));

-- Hash partition index
CREATE TABLE orders (
  order_id NUMBER,
  ...
) PARTITION BY HASH (order_id) PARTITIONS 8;
```

Fix 4 -- Increase redo log buffer and log file size:
```sql
ALTER SYSTEM SET log_buffer = 64M SCOPE = SPFILE;
-- Requires restart

-- Add larger redo log groups
ALTER DATABASE ADD LOGFILE GROUP 4
  'C:\ORADATA\ORCL\redo04.log' SIZE 2G;
```

Fix 5 -- Use commit batch optimization:
```sql
ALTER SYSTEM SET commit_write = 'BATCH,NOWAIT' SCOPE = BOTH;
-- Allows commits to return before LGWR finishes writing
```

### Step 5: Analysis

```sql
-- Check segment statistics for buffer busy waits
SELECT owner, segment_name, segment_type,
       SUM(buffer_busy_waits) AS total_waits
  FROM DBA_SEGMENTS s
  JOIN V$SEGMENT_STATISTICS ss ON (s.owner = ss.owner AND s.segment_name = ss.object_name)
 WHERE statistic_name = 'buffer busy waits'
 GROUP BY owner, segment_name, segment_type
 ORDER BY total_waits DESC;
```

### Step 6: Test and Verify
```sql
-- Before fix: measure log file sync wait
SELECT event, total_waits, time_waited_micro/1000000 AS time_sec
  FROM v$system_event
 WHERE event = 'log file sync';

-- After fix: recheck
SELECT event, total_waits, time_waited_micro/1000000 AS time_sec
  FROM v$system_event
 WHERE event = 'log file sync';
```

Edge cases:
- **RAC environment**: log file sync includes inter-node coordination
- **Flash storage**: log file sync still matters (latency matters more than throughput)
- **DG environment**: log file sync waits for remote standby acknowledgment (SYNC transport)

### Company Evaluation
- **Oracle**: Deep architecture knowledge. AWR/ASH analysis, redo mechanics, buffer cache internals.
- **Amazon**: Aurora separates compute/storage -- log is written to storage layer, reducing log file sync.
- **Google**: Spanner uses Paxos-based commit -- different architecture, same latency concerns.

---

## Problem 2: Row Chaining and Migration (Performance Tuning) -- Oracle

### Interview Scenario
"Oracle interview: A frequently accessed table with 500 columns has performance problems. Queries are doing excessive logical I/O even for single-row access. Diagnose row chaining/migration and fix it."

### The Problem
Table **WIDE_TABLE** has 500 columns, each VARCHAR2(200). Average row length is 12,000 bytes. Oracle block size is 8KB. Rows are chained across multiple blocks. Queries for one row often read 5-10 blocks.

### Step 1: Understand Architecture
- Oracle block size: 8KB (8192 bytes after overhead)
- Max row size: 12,000 bytes >> 8KB block
- Row chaining: Row stored across multiple blocks
- Row migration: Row updated and moved to new block

Diagnostics:
```sql
-- Check for chained rows
ANALYZE TABLE wide_table LIST CHAINED ROWS INTO chained_rows;
SELECT COUNT(*) AS chained_row_count
  FROM chained_rows
 WHERE table_name = 'WIDE_TABLE';
```

### Step 2: Think Aloud
When a row exceeds the block size, Oracle splits it across blocks. This causes multi-block reads for single-row access, excessive buffer cache usage, and increased logical I/O.

Solutions:
1. Use larger block size (16KB or 32KB for this tablespace)
2. Compress columns (basic or advanced compression)
3. Vertical partitioning (split into multiple tables)
4. Use CLOB/BLOB for very large data

### Step 3: Write Fixes

Fix 1 -- Recreate with larger block size:
```sql
CREATE TABLESPACE ts_large_block
  DATAFILE 'C:\ORADATA\ORCL\ts_large_block01.dbf'
  SIZE 10G
  BLOCKSIZE 16384;

ALTER TABLE wide_table MOVE TABLESPACE ts_large_block;
```

Fix 2 -- Vertical partitioning:
```sql
CREATE TABLE wide_table_main AS
  SELECT id, name, status, created_date
    FROM wide_table
   WHERE 1 = 0;

CREATE TABLE wide_table_extended AS
  SELECT id, col1, col2, col3, ..., col500
    FROM wide_table
   WHERE 1 = 0;

CREATE OR REPLACE VIEW wide_table_v AS
  SELECT m.*, e.col1, e.col2, ..., e.col500
    FROM wide_table_main m
    LEFT JOIN wide_table_extended e ON m.id = e.id;
```

Fix 3 -- Use Advanced Compression:
```sql
ALTER TABLE wide_table COMPRESS FOR OLTP;
```

Fix 4 -- Reorganize to eliminate existing chaining:
```sql
ALTER TABLE wide_table MOVE;
ALTER INDEX wide_table_pk REBUILD;
```

### Step 4: Execution Plan Impact
Before fix:
```
TABLE ACCESS BY INDEX ROWID (single row)
  INDEX UNIQUE SCAN
  TABLE ACCESS: 5-10 buffer gets per row
```

After fix:
```
TABLE ACCESS BY INDEX ROWID (single row)
  INDEX UNIQUE SCAN
  TABLE ACCESS: 1 buffer get per row
```

### Step 5: Optimize Further
Materialized views for frequently accessed column subsets:
```sql
CREATE MATERIALIZED VIEW wide_table_fast
REFRESH FAST ON COMMIT
AS
SELECT id, name, status, created_date
  FROM wide_table;
```

### Step 6: Test
```sql
SET AUTOTRACE TRACEONLY;
SELECT * FROM wide_table WHERE id = 123456;
-- Note consistent_gets before and after fix
```

Edge cases:
- **PCTFREE too high**: Wastes space. Set PCTFREE 5.
- **Migrated rows from UPDATE**: Use ALTER TABLE ... MOVE to fix.
- **LOB columns**: Stored separately (LOB segment) -- not the same as row chaining.

### Company Evaluation
- **Oracle**: Block structure, PCTFREE, row chaining vs migration.
- **Amazon**: DynamoDB item size limit is 400KB -- similar large item penalties.
- **Google**: Spanner has 10,240 byte limit per column value; large rows use interleaved tables.

---

## Problem 3: Redo Log Buffer and LGWR Tuning -- Amazon (RDS Oracle)

### Interview Scenario
"Amazon RDS Oracle interview: Customers running OLTP workloads on RDS are experiencing high 'log file sync' waits. As a database specialist, recommend configuration changes and application best practices."

### The Problem
A customer's application performs 10,000 transactions per second. Each transaction inserts one row and commits. The redo log buffer is 8MB, log files are 500MB each, and log file sync waits average 15ms per commit. Total DB time lost to log file sync is 150 seconds per second -- the system is overloaded.

### Step 1: Understand Architecture
Redo flow:
1. Session generates redo in PGA
2. Session copies redo to SGA redo log buffer
3. On COMMIT, LGWR writes redo buffer to online redo log files
4. LGWR signals the session that commit is complete

At 10K commits/sec with 15ms per sync, LGWR spends 150 seconds per second of wall time.

### Step 2: Think Aloud
Key insight: LGWR is single-threaded.

Solutions:
1. **Batch commits** -- reduce commit frequency (most effective)
2. **Increase redo log size** -- LGWR writes larger I/Os less frequently
3. **Faster storage** -- SSD with <1ms write latency
4. **Use COMMIT WRITE BATCH NOWAIT** -- reduce durability
5. **Group commits** -- LGWR naturally batches commits within same ms

### Step 3: Write Fixes

Application-level fix (most impactful):
```sql
-- Instead of one commit per row:
FOR i IN 1..10000 LOOP
  INSERT INTO orders VALUES (...);
  COMMIT;  -- 10,000 commits
END LOOP;

-- Batch commits:
FOR i IN 1..10000 LOOP
  INSERT INTO orders VALUES (...);
  IF MOD(i, 500) = 0 THEN
    COMMIT;
  END IF;
END LOOP;
COMMIT;
```

Database-level fixes:
```sql
ALTER SYSTEM SET log_buffer = 256M SCOPE = SPFILE;
ALTER DATABASE ADD LOGFILE GROUP 4
  'C:\ORADATA\ORCL\redo04.log' SIZE 4G;
ALTER SYSTEM SET commit_write = 'BATCH,NOWAIT' SCOPE = BOTH;
```

Storage optimization:
```sql
-- Check redo log I/O latency
SELECT name, latency_per_fsync, single_blk_wr_time
  FROM v$iostat_detail
 WHERE filetype_name = 'Redo Log'
   AND latency_per_fsync IS NOT NULL;
```

### Step 4: Analysis SQL
```sql
-- Calculate potential benefit of batching
SELECT 10000 AS current_commits_per_sec,
       15 AS avg_log_sync_ms,
       (10000 * 15) / 1000 AS log_sync_seconds_per_second,
       CASE
         WHEN (10000 * 15) > 1000 THEN 'OVERLOADED'
         ELSE 'ACCEPTABLE'
       END AS status
  FROM dual;

-- Group commit analysis
SELECT name, value
  FROM v$sysstat
 WHERE name IN ('redo synch time', 'redo synch writes', 'user commits');
```

### Step 5: Advanced Tuning
PL/SQL bulk operations reduce redo:
```sql
DECLARE
  TYPE t_order_tab IS TABLE OF orders%ROWTYPE;
  v_orders t_order_tab := t_order_tab();
BEGIN
  SELECT * BULK COLLECT INTO v_orders FROM new_orders;
  FORALL i IN 1..v_orders.COUNT
    INSERT INTO orders VALUES v_orders(i);
  COMMIT;
END;
/
```

Minimize redo generation for non-critical tables:
```sql
ALTER TABLE staging NOLOGGING;
INSERT /*+ APPEND */ INTO staging SELECT * FROM source;
```

### Step 6: Test
```sql
-- Measure before changes
SELECT event, total_waits, time_waited_micro/1000000 AS wait_seconds
  FROM v$system_event
 WHERE event = 'log file sync';

-- After changes, compare waits
SELECT event, total_waits, time_waited_micro/1000000 AS wait_seconds
  FROM v$system_event
 WHERE event = 'log file sync';
```

Edge cases:
- **Data Guard sync**: SYNC transport doubles wait. Consider ASYNC for non-critical standbys.
- **RAC**: Each instance has its own redo thread. Interconnect latency adds to sync time.
- **RDS limitations**: Cannot add redo log files or change log_buffer in smaller instance classes.

### Company Evaluation
- **Oracle**: Redo architecture -- log buffer, LGWR, group commit, COMMIT WRITE.
- **Amazon**: RDS-optimized storage (gp3/io2), parameter group tuning, Aurora log-as-database.
- **Google**: Spanner uses Paxos-based commit -- true external consistency, no traditional redo log.
