# Solution: Fixing Slow Query and Optimizing Batch Processing

**Incident**: INC-2024-0810-BATCH
**Fix Version**: Batch Job v2.3.1 + Database Schema Patch
**Last Updated**: August 12, 2024

## Overview

The fix addresses the batch job performance at three levels:

1. **Primary Fix**: Create composite index on the JOIN column
2. **Query Fix**: Rewrite the batch query with optimizer hints for efficient join order
3. **Architecture Fix**: Implement partition exchange loading (PEL) for future batch operations

## Fix 1: Create Composite Index

The immediate fix is creating an index on the `SETTLEMENT_HOLD.transaction_ref` column. A composite index that also covers other frequently queried columns provides additional benefit.

### Index Creation

```sql
-- Step 1: Create the missing index on the JOIN column
-- This is the PRIMARY fix — without this, any join involving
-- SETTLEMENT_HOLD.transaction_ref will be a full table scan

CREATE INDEX idx_sh_transaction_ref
ON settlement_hold (transaction_ref)
TABLESPACE users
STORAGE (INITIAL 64M NEXT 32M PCTINCREASE 0)
NOLOGGING
ONLINE;
-- ONLINE allows DML operations during index creation
-- NOLOGGING reduces redo generation

-- Index created in 15 minutes for 5M rows
-- Size: ~120MB

-- Step 2: Update statistics for the new index
EXEC DBMS_STATS.GATHER_INDEX_STATS('APPSCHEMA', 'IDX_SH_TRANSACTION_REF');


-- Step 3: Alternative — composite index covering additional query predicates
-- If queries frequently filter by both status and transaction_ref:
CREATE INDEX idx_sh_status_transaction_ref
ON settlement_hold (status, transaction_ref)
TABLESPACE users
NOLOGGING
ONLINE;

-- Step 4: Verify the index
SELECT index_name, index_type, uniqueness, status
FROM user_indexes
WHERE table_name = 'SETTLEMENT_HOLD';

-- Output:
-- INDEX_NAME                  INDEX_TYPE  UNIQUENESS  STATUS
-- --------------------------- ----------- ----------- --------
-- PK_SETTLEMENT_HOLD          NORMAL      UNIQUE      VALID
-- IDX_SH_TRANSACTION_REF      NORMAL      NONUNIQUE   VALID      ← NEW!
```

### Verify Index Usage

```sql
-- Check if the optimizer uses the new index
EXPLAIN PLAN FOR
SELECT t.transaction_id, sh.hold_reason
FROM transactions t
LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref
WHERE t.status = 'PENDING';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Expected plan (before):
-- TABLE ACCESS FULL: SETTLEMENT_HOLD (bad)

-- Expected plan (after):
-- TABLE ACCESS BY INDEX ROWID: SETTLEMENT_HOLD
--   INDEX RANGE SCAN: IDX_SH_TRANSACTION_REF (good!)
```

## Fix 2: Query Rewrite with Optimizer Hints

The original query allowed the optimizer to choose poor join order and join methods. The rewritten query uses hints to force efficient join order and index usage.

### Original Query (Slow)

```sql
-- Original INSERT-SELECT (slow, 8+ hours)
INSERT INTO settlement_summary (
    business_date, transaction_id, amount, hold_reason, audit_status
)
SELECT
    TRUNC(SYSDATE) AS business_date,
    t.transaction_id,
    t.amount,
    sh.hold_reason,
    sa.audit_status
FROM transactions t
JOIN settlement_audit sa ON t.txn_id = sa.txn_id
LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref
WHERE t.status = 'PENDING';
```

### Rewritten Query with Hints

```sql
-- Rewritten INSERT-SELECT with optimizer hints
-- Hints to force efficient execution:
--   LEADING(t sh sa) — force join order: TRANSACTIONS first, then SETTLEMENT_HOLD, then SETTLEMENT_AUDIT
--   USE_NL(sh) — use nested loop for joining SETTLEMENT_HOLD (index-driven)
--   USE_HASH(sa) — use hash join for SETTLEMENT_AUDIT (large table)
--   INDEX(sh IDX_SH_TRANSACTION_REF) — force index usage on SETTLEMENT_HOLD

INSERT /*+
    LEADING(t sh sa)
    USE_NL(sh)
    INDEX(sh IDX_SH_TRANSACTION_REF)
    USE_HASH(sa)
    INDEX(t IDX_TRANSACTIONS_STATUS)
    QB_NAME(main_query)
*/
INTO settlement_summary (
    business_date, transaction_id, amount, hold_reason, audit_status
)
SELECT
    TRUNC(SYSDATE) AS business_date,
    t.transaction_id,
    t.amount,
    sh.hold_reason,
    sa.audit_status
FROM transactions t
JOIN settlement_audit sa ON t.txn_id = sa.txn_id
LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref
WHERE t.status = 'PENDING';
```

### Execution Plan (After Fix)

```
SQL> SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR(null, null, 'ALLSTATS LAST'));

Plan hash value: 1234567890

-----------------------------------------------------------------------------------------------
| Id | Operation                     | Name                  | Rows  | Cost | Time     | Starts|
-----------------------------------------------------------------------------------------------
|  0 | INSERT STATEMENT              | SETTLEMENT_SUMMARY    |       | 2847 |          |     1 |
|  1 |  LOAD TABLE CONVENTIONAL      | SETTLEMENT_SUMMARY    |       | 2847 |          |     1 |
|  2 |   HASH JOIN                   |                       | 418K  | 2842 | 00:17:42 |     1 |
|  3 |    NESTED LOOPS LEFT          |                       | 418K  | 2241 | 00:14:00 |     1 |
|  4 |     TABLE ACCESS BY INDEX ROWID| TRANSACTIONS          | 418K  | 1836 | 00:09:30 |     1 |
|  5 |      INDEX RANGE SCAN         | IDX_TRANSACTIONS_STATUS| 418K  | 1254 | 00:05:00 |     1 |
|  6 |     TABLE ACCESS BY INDEX ROWID| SETTLEMENT_HOLD       |    1  |    1 | 00:00:01 |  418K |
|  7 |      INDEX RANGE SCAN         | IDX_SH_TRANSACTION_REF|    1  |    1 | 00:00:01 |  418K |
|  8 |    TABLE ACCESS FULL          | SETTLEMENT_AUDIT      |  22M  |  394 | 00:03:00 |     1 |
-----------------------------------------------------------------------------------------------

Key differences:
- No full table scans on TRANSACTIONS (now uses index range scan)
- No full table scan on SETTLEMENT_HOLD (now uses index range scan)
- Temp space: 0 bytes (previously 8.5GB)
- Elapsed: 17 minutes (previously 8h 15min)
```

### Alternative: SQL Profile via SQL Tuning Advisor

```sql
-- Use Oracle SQL Tuning Advisor for automated tuning
DECLARE
    task_name VARCHAR2(100);
BEGIN
    -- Create tuning task for the problematic SQL
    task_name := DBMS_SQLTUNE.CREATE_TUNING_TASK(
        sql_id      => '6g8k2x3a1b4c',
        scope       => DBMS_SQLTUNE.SCOPE_COMPREHENSIVE,
        time_limit  => 300,
        task_name   => 'batch_settlement_tune'
    );

    -- Execute the tuning task
    DBMS_SQLTUNE.EXECUTE_TUNING_TASK(task_name);

    -- View recommendations
    DBMS_OUTPUT.PUT_LINE('View recommendations with:');
    DBMS_OUTPUT.PUT_LINE('SELECT DBMS_SQLTUNE.REPORT_TUNING_TASK(''' || task_name || ''') FROM dual;');
END;
/

-- View SQL Tuning Advisor report
SELECT DBMS_SQLTUNE.REPORT_TUNING_TASK('batch_settlement_tune') FROM dual;
```

## Fix 3: Partition Exchange Loading

For long-term batch processing efficiency, implement partition exchange loading (PEL). This loads data into a separate staging table and then exchanges the partition with the main table — a metadata-only operation that avoids bulk DELETE/INSERT.

### Staging Table Setup

```sql
-- Step 1: Create staging table with same structure as main table
CREATE TABLE settlement_summary_staging (
    business_date     DATE NOT NULL,
    transaction_id    VARCHAR2(50) NOT NULL,
    amount            NUMBER(18,2),
    hold_reason       VARCHAR2(200),
    audit_status      VARCHAR2(20),
    created_at        DATE DEFAULT SYSDATE
)
TABLESPACE staging_data
NOLOGGING
PARALLEL 4;

-- Indexes on staging (for efficient query during load)
CREATE INDEX idx_ss_staging_business_date
ON settlement_summary_staging (business_date);

-- Step 2: Partition the main table by business_date
ALTER TABLE settlement_summary
MODIFY
    PARTITION BY RANGE (business_date)
    (
        PARTITION p_2024_08_01 VALUES LESS THAN (TO_DATE('2024-08-02', 'YYYY-MM-DD')),
        PARTITION p_2024_08_02 VALUES LESS THAN (TO_DATE('2024-08-03', 'YYYY-MM-DD')),
        PARTITION p_2024_08_03 VALUES LESS THAN (TO_DATE('2024-08-04', 'YYYY-MM-DD')),
        PARTITION p_2024_08_04 VALUES LESS THAN (TO_DATE('2024-08-05', 'YYYY-MM-DD')),
        PARTITION p_future VALUES LESS THAN (MAXVALUE)
    );

-- Step 3: Partition Exchange Load procedure
CREATE OR REPLACE PROCEDURE load_settlement_summary_pel (
    p_business_date DATE
) AS
    partition_name VARCHAR2(50);
BEGIN
    -- Determine the partition name for this business date
    partition_name := 'P_' || TO_CHAR(p_business_date, 'YYYY_MM_DD');

    -- Truncate staging table
    EXECUTE IMMEDIATE 'TRUNCATE TABLE settlement_summary_staging';

    -- Load data into staging table (the batch query)
    INSERT /*+ APPEND PARALLEL(4) */
    INTO settlement_summary_staging
    SELECT
        p_business_date,
        t.transaction_id,
        t.amount,
        sh.hold_reason,
        sa.audit_status,
        SYSDATE
    FROM transactions t
    JOIN settlement_audit sa ON t.txn_id = sa.txn_id
    LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref
    WHERE t.status = 'PENDING';

    COMMIT;

    -- Gather statistics on staging table
    DBMS_STATS.GATHER_TABLE_STATS('APPSCHEMA', 'SETTLEMENT_SUMMARY_STAGING');

    -- Exchange staging partition with main table
    EXECUTE IMMEDIATE
        'ALTER TABLE settlement_summary ' ||
        'EXCHANGE PARTITION ' || partition_name || ' ' ||
        'WITH TABLE settlement_summary_staging ' ||
        'INCLUDING INDEXES ' ||
        'WITHOUT VALIDATION';  -- WITHOUT VALIDATION for faster operation

    COMMIT;

    DBMS_OUTPUT.PUT_LINE('Partition ' || partition_name || ' loaded successfully.');
END load_settlement_summary_pel;
/
```

### Java Code for Batch Job

```java
package com.finance.batch;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.logging.Logger;
import javax.sql.DataSource;

/**
 * End-of-Day settlement batch job with optimized query and PEL support.
 */
public class EndOfDaySettlementJob {

    private static final Logger LOG =
            Logger.getLogger(EndOfDaySettlementJob.class.getName());

    private final DataSource dataSource;
    private final boolean usePartitionExchange;

    public EndOfDaySettlementJob(DataSource dataSource, boolean usePartitionExchange) {
        this.dataSource = dataSource;
        this.usePartitionExchange = usePartitionExchange;
    }

    /**
     * Executes the end-of-day settlement processing.
     * Uses either direct INSERT or Partition Exchange Loading.
     */
    public void execute(LocalDate businessDate) {
        long startTime = System.currentTimeMillis();

        if (usePartitionExchange) {
            executePEL(businessDate);
        } else {
            executeDirectInsert(businessDate);
        }

        long duration = System.currentTimeMillis() - startTime;
        LOG.info("EOD settlement completed in " + (duration / 1000) + " seconds");
    }

    /**
     * Direct INSERT with optimizer hints (the rewritten query).
     */
    private void executeDirectInsert(LocalDate businessDate) {
        String sql =
            "INSERT /*+ LEADING(t sh sa) USE_NL(sh) INDEX(sh IDX_SH_TRANSACTION_REF) " +
            "USE_HASH(sa) */ " +
            "INTO settlement_summary (business_date, transaction_id, amount, " +
            "    hold_reason, audit_status, created_at) " +
            "SELECT ? AS business_date, t.transaction_id, " +
            "       t.amount, sh.hold_reason, sa.audit_status, SYSDATE " +
            "FROM transactions t " +
            "JOIN settlement_audit sa ON t.txn_id = sa.txn_id " +
            "LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref " +
            "WHERE t.status = 'PENDING'";

        try (Connection conn = dataSource.getConnection();
             java.sql.PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(businessDate));
            int updated = stmt.executeUpdate();
            conn.commit();

            LOG.info("Direct INSERT completed: " + updated + " rows inserted");

        } catch (SQLException e) {
            throw new BatchJobException("Direct INSERT failed", e);
        }
    }

    /**
     * Partition Exchange Loading (PEL) for efficient batch processing.
     * Calls the stored procedure that loads into staging and exchanges partition.
     */
    private void executePEL(LocalDate businessDate) {
        String sql = "{CALL load_settlement_summary_pel(?)}";

        try (Connection conn = dataSource.getConnection();
             CallableStatement stmt = conn.prepareCall(sql)) {

            stmt.setDate(1, java.sql.Date.valueOf(businessDate));
            stmt.execute();

            LOG.info("PEL completed for business date: " + businessDate);

        } catch (SQLException e) {
            throw new BatchJobException("PEL failed for date: " + businessDate, e);
        }
    }
}
```

## Performance Comparison

```
METRIC                  | BEFORE (No Index) | AFTER (Index + Hints) | IMPROVEMENT
────────────────────────┼───────────────────┼───────────────────────┼─────────────
Elapsed Time            | 8h 15min          | 17 min                | 29x faster
CPU Time                | 1,203s            | 284s                  | 4x less CPU
Temp Space Used         | 8.5 GB            | 0 MB                  | Eliminated
Disk Reads              | 12.8M             | 248K                  | 51x fewer
Logical Reads           | 18.4M             | 512K                  | 35x fewer
Rows Inserted           | 418K              | 418K                  | Same (correct)
Full Table Scans        | 3                 | 1 (SETTLEMENT_AUDIT)  | 2 eliminated
```

## Verification

```sql
-- Step 1: Verify execution plan uses index
EXPLAIN PLAN FOR
SELECT t.transaction_id, sh.hold_reason
FROM transactions t
LEFT JOIN settlement_hold sh ON t.transaction_id = sh.transaction_ref
WHERE t.status = 'PENDING';

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Step 2: Check index usage in real-time
SELECT index_name, table_name, used
FROM v$object_usage;

-- Step 3: Measure actual query performance
SET TIMING ON;
-- Execute the rewritten query
INSERT /*+ LEADING(t sh sa) USE_NL(sh) INDEX(sh IDX_SH_TRANSACTION_REF) USE_HASH(sa) */
INTO settlement_summary (...)
SELECT ...;
-- Record elapsed time

-- Step 4: Compare with SQL Monitor report
SELECT
    sql_id,
    elapsed_time / 1000000 AS elapsed_seconds,
    cpu_time / 1000000 AS cpu_seconds,
    disk_reads,
    buffer_gets,
    temp_space_used_mb
FROM v$sql_monitor
WHERE sql_id = '&your_sql_id';
```

## References

- Oracle: "DBMS_XPLAN" — Oracle PL/SQL Packages and Types Reference
- Oracle: "Using Optimizer Hints" — Oracle SQL Tuning Guide
- Oracle: "Partition Exchange Loading" — Oracle VLDB and Partitioning Guide
- Use The Index, Luke: "Indexing for JOIN Performance" — https://use-the-index-luke.com/sql/join
- Oracle: "SQL Tuning Advisor" — Oracle Database Performance Tuning Guide
- Oracle: "AWR Reports" — Oracle Performance Guide

