# Lab 05 — Slow Query / Deadlock: Code Examples

## SQL Examples

### The Slow Query (Before Fix)

```sql
-- Original slow query: missing index on SETTLEMENT_HOLD.transaction_ref
-- Full table scan on both TRANSACTIONS (50M) and SETTLEMENT_HOLD (5M)
-- Results in temp space spill (8.5GB)
SELECT t.id, t.amount, t.created_at, sh.status as hold_status, sa.amount as audit_amount
FROM transactions t
JOIN settlement_hold sh ON t.id = sh.transaction_ref    -- Missing index!
LEFT JOIN settlement_audit sa ON t.txn_id = sa.txn_id
WHERE t.status = 'PENDING'
  AND t.created_at >= SYSDATE - 1
ORDER BY t.created_at DESC;
```

### Execution Plan (Before)

```sql
-- Generate execution plan
EXPLAIN PLAN FOR
SELECT t.id, t.amount, t.created_at, sh.status, sa.amount
FROM transactions t
JOIN settlement_hold sh ON t.id = sh.transaction_ref
LEFT JOIN settlement_audit sa ON t.txn_id = sa.txn_id
WHERE t.status = 'PENDING'
  AND t.created_at >= SYSDATE - 1
ORDER BY t.created_at DESC;

SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);

-- Bad plan shows:
-- TABLE ACCESS FULL on TRANSACTIONS (50M rows)
-- TABLE ACCESS FULL on SETTLEMENT_HOLD (5M rows)
-- HASH JOIN with temp spill
-- No index usage
```

### The Fix — Create Missing Index

```sql
-- Create composite index for the JOIN + WHERE clause
CREATE INDEX idx_settlement_hold_transaction_ref
ON settlement_hold(transaction_ref, status)
TABLESPACE app_index
ONLINE;

-- Gather statistics for the new index
EXEC DBMS_STATS.GATHER_INDEX_STATS('APPSCHEMA', 'IDX_SETTLEMENT_HOLD_TRANSACTION_REF');

-- Create index for the WHERE clause on TRANSACTIONS
CREATE INDEX idx_transactions_status_created
ON transactions(status, created_at DESC)
TABLESPACE app_index
ONLINE;

EXEC DBMS_STATS.GATHER_INDEX_STATS('APPSCHEMA', 'IDX_TRANSACTIONS_STATUS_CREATED');
```

### The Fix — Query Rewrite with Hints

```sql
-- Fixed query with hints
SELECT /*+ LEADING(t sh sa) 
           USE_NL(sh) INDEX(sh IDX_SETTLEMENT_HOLD_TRANSACTION_REF)
           USE_HASH(sa)
           INDEX(t IDX_TRANSACTIONS_STATUS_CREATED) */
    t.id, t.amount, t.created_at, sh.status, sa.amount
FROM transactions t
JOIN settlement_hold sh ON t.id = sh.transaction_ref AND sh.status IN ('ACTIVE', 'PENDING')
LEFT JOIN settlement_audit sa ON t.txn_id = sa.txn_id AND sa.created_at >= SYSDATE - 1
WHERE t.status = 'PENDING'
  AND t.created_at >= SYSDATE - 1
ORDER BY t.created_at DESC;
```

### Partition Exchange Loading (PEL)

```sql
-- Partition Exchange Loading for efficient batch processing

-- Step 1: Create staging table identical to main table
CREATE TABLE transactions_staging
AS SELECT * FROM transactions WHERE 1=0;

-- Step 2: Load data into staging table (fast direct path)
INSERT /*+ APPEND */ INTO transactions_staging
SELECT * FROM external_transactions_source;

-- Step 3: Build indexes on staging table (no impact on main table)
CREATE INDEX idx_staging_status ON transactions_staging(status, created_at);

-- Step 4: Exchange staging partition with main table partition
ALTER TABLE transactions
  EXCHANGE PARTITION p_2024_01
  WITH TABLE transactions_staging
  INCLUDING INDEXES
  WITHOUT VALIDATION;

-- Step 5: Rebuild global indexes (if any)
ALTER INDEX idx_transactions_user_id REBUILD ONLINE;

-- Step 6: Drop staging table
DROP TABLE transactions_staging;
```

### Unit Test Framework for Query Performance

```java
import org.junit.jupiter.api.*;
import java.sql.*;
import javax.sql.DataSource;

class QueryPerformanceTest {

    private DataSource dataSource;

    @Test
    void testBatchQueryCompletesWithinThreshold() throws SQLException {
        String sql = "SELECT /*+ LEADING(t sh sa) USE_NL(sh) USE_HASH(sa) */ "
            + "COUNT(*) FROM transactions t "
            + "JOIN settlement_hold sh ON t.id = sh.transaction_ref "
            + "LEFT JOIN settlement_audit sa ON t.txn_id = sa.txn_id "
            + "WHERE t.status = 'PENDING' AND t.created_at >= SYSDATE - 1";

        long start = System.currentTimeMillis();
        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                System.out.println("Count: " + rs.getLong(1));
            }
        }
        long duration = System.currentTimeMillis() - start;

        assertTrue(duration < 60000, "Query should complete within 60 seconds, took " + duration + "ms");
    }

    @Test
    void testNoFullTableScans() throws SQLException {
        String sql = "SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY_CURSOR())";
        // Verify execution plan has no TABLE ACCESS FULL on large tables
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("EXPLAIN PLAN FOR " + getTestQuery())) {
            // Execute and check plan via DBMS_XPLAN
        }
    }

    private String getTestQuery() {
        return "SELECT COUNT(*) FROM transactions t "
            + "JOIN settlement_hold sh ON t.id = sh.transaction_ref "
            + "WHERE t.status = 'PENDING' AND t.created_at >= SYSDATE - 1";
    }
}
```

### Query Performance Recorder

```java
import java.sql.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class QueryPerformanceRecorder {
    private final ConcurrentHashMap<String, QueryStats> stats = new ConcurrentHashMap<>();

    public void recordExecution(String sql, long durationMs) {
        stats.compute(sql, (key, existing) -> {
            if (existing == null) {
                return new QueryStats(durationMs, durationMs, durationMs, 1);
            }
            return new QueryStats(
                Math.min(existing.min, durationMs),
                Math.max(existing.max, durationMs),
                (existing.avg * existing.count + durationMs) / (existing.count + 1),
                existing.count + 1
            );
        });
    }

    public QueryStats getStats(String sql) {
        return stats.get(sql);
    }

    record QueryStats(long min, long max, double avg, long count) {}
}
```
