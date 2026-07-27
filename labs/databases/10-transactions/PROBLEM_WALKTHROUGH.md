# SQL Problem Walkthrough: 10-transactions

## Problem 1: Bank Account Transfer with Isolation (LC SQL 1978 variant) — Oracle

### Interview Scenario
"Oracle interview: Design a transaction-safe bank transfer. Account A has $1000, Account B has $500. Transfer $200 from A to B. Handle the case where two transfers happen simultaneously."

### The Problem
Tables: **Accounts** (account_id NUMBER PK, balance NUMBER). Write a transaction that transfers $200 from account 1 to account 2 while preventing:
1. Lost update (both reads $1000 before updating)
2. Non-repeatable reads
3. Phantom reads (if scanning all accounts)
4. Deadlocks with concurrent transfers

### Step 1: Understand Schema
- accounts: account_id (PK), balance
- Transaction must debit A, credit B atomically
- Balance must never go negative
- Two concurrent transfers between same accounts must produce correct results

Sample data:
| account_id | balance |
|------------|---------|
| 1          | 1000    |
| 2          | 500     |

### Step 2: Think Aloud
ACID properties needed:
- **Atomicity**: Both debit and credit succeed or neither does
- **Consistency**: Sum of balances preserved, no negative balances
- **Isolation**: Concurrent transfers don't interfere
- **Durability**: Committed transfer survives crashes

In Oracle, SERIALIZABLE isolation prevents all anomalies but has higher overhead. READ COMMITTED with SELECT FOR UPDATE provides sufficient protection for this case.

### Step 3: Write the Transaction
```sql
SET TRANSACTION ISOLATION LEVEL READ COMMITTED;

-- Lock both rows in a consistent order to prevent deadlocks
SELECT balance INTO v_balance FROM accounts
 WHERE account_id = 1
   FOR UPDATE;

SELECT balance INTO v_balance FROM accounts
 WHERE account_id = 2
   FOR UPDATE;

-- Check sufficient funds
SELECT balance INTO v_from_balance
  FROM accounts
 WHERE account_id = 1
   FOR UPDATE;

IF v_from_balance >= 200 THEN
  UPDATE accounts
     SET balance = balance - 200
   WHERE account_id = 1;

  UPDATE accounts
     SET balance = balance + 200
   WHERE account_id = 2;
END IF;

COMMIT;
```

PL/SQL procedure version:
```sql
CREATE OR REPLACE PROCEDURE transfer_funds (
  p_from_acct IN accounts.account_id%TYPE,
  p_to_acct   IN accounts.account_id%TYPE,
  p_amount    IN accounts.balance%TYPE
) AS
  v_from_balance accounts.balance%TYPE;
BEGIN
  -- Lock accounts in account_id order to prevent deadlocks
  IF p_from_acct < p_to_acct THEN
    SELECT balance INTO v_from_balance
      FROM accounts
     WHERE account_id = p_from_acct
       FOR UPDATE;
    SELECT balance INTO v_from_balance
      FROM accounts
     WHERE account_id = p_to_acct
       FOR UPDATE;
  ELSE
    SELECT balance INTO v_from_balance
      FROM accounts
     WHERE account_id = p_to_acct
       FOR UPDATE;
    SELECT balance INTO v_from_balance
      FROM accounts
     WHERE account_id = p_from_acct
       FOR UPDATE;
  END IF;

  -- Check balance
  SELECT balance INTO v_from_balance
    FROM accounts
   WHERE account_id = p_from_acct;

  IF v_from_balance < p_amount THEN
    RAISE_APPLICATION_ERROR(-20001, 'Insufficient funds');
  END IF;

  -- Perform transfer
  UPDATE accounts
     SET balance = balance - p_amount
   WHERE account_id = p_from_acct;

  UPDATE accounts
     SET balance = balance + p_amount
   WHERE account_id = p_to_acct;

  COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    RAISE;
END transfer_funds;
/
```

### Step 4: Execution Plan Analysis
```sql
EXPLAIN PLAN FOR
UPDATE accounts SET balance = balance - 200 WHERE account_id = 1;
```

The UPDATE with FOR UPDATE generates:
- INDEX UNIQUE SCAN on PK (find the row)
- TABLE ACCESS BY INDEX ROWID
- Row-level lock (TX enqueue)

Lock analysis with v$lock:
```sql
SELECT sid, type, lmode, request, id1, id2
  FROM v$lock
 WHERE sid = (SELECT sid FROM v$mystat WHERE ROWNUM = 1);
```

### Step 5: Optimize for Concurrency
1. **Lock order**: Always lock lower account_id first to prevent deadlock.

2. **Row-level vs table-level**: Oracle uses row-level locking by default; no need for special syntax.

3. **Isolation level comparison**:
   - READ COMMITTED (default): Non-repeatable reads possible but FOR UPDATE prevents it
   - SERIALIZABLE: Full isolation but higher ORA-08177 (snapshot too old) risk
   - READ ONLY: For reporting consistency

4. **Timeouts and retry logic**:
```sql
-- Set lock timeout (in seconds)
EXECUTE IMMEDIATE 'SET TRANSACTION ISOLATION LEVEL READ COMMITTED';
-- Oracle uses NOWAIT or WAIT n
SELECT balance FROM accounts
 WHERE account_id = 1
   FOR UPDATE WAIT 5;  -- Wait max 5 seconds
```

### Step 6: Test Concurrency

Test scenario: Concurrent transfer from A→B and B→A:
```sql
-- Session 1: Transfer $200 from 1 to 2
-- Session 2: Transfer $100 from 2 to 1

-- With ordered locking (1 then 2), both sessions try to lock account 1 first.
-- Session 1 gets lock on 1. Session 2 waits.
-- Session 1 locks 2 (no contention, Session 2 hasn't locked it yet).
-- Session 1 completes and commits.
-- Session 2 then gets locks on 1 and 2.

-- Result: Both transfers succeed, no deadlock.
```

Edge cases:
- **Insufficient funds**: Rollback, report error
- **Same account transfer**: Guard with IF p_from_acct = p_to_acct THEN RETURN
- **Negative amount**: Validate p_amount > 0
- **NULL values**: Check for NULL before proceeding
- **Deadlock detection**: Oracle detects deadlocks automatically and rolls back one transaction with ORA-00060

```sql
-- Test deadlock detection (bad: no ordered locking)
-- Session 1 locks account 1; Session 2 locks account 2
-- Session 1 tries to lock account 2 (waits)
-- Session 2 tries to lock account 1 (waits)
-- Oracle detects deadlock, kills one session with ORA-00060
```

### Company Evaluation
- **Oracle**: Deep understanding of locking, transactions, FOR UPDATE WAIT/NOWAIT, deadlock detection, redo logs.
- **Amazon**: Discuss DynamoDB transactions with conditional updates and optimistic locking.
- **Google**: Spanner — true external consistency with TrueTime. Discuss 2PC vs Percolator.
- **Microsoft**: T-SQL uses WITH (UPDLOCK, SERIALIZABLE) for pessimistic locking.

---

## Problem 2: Lost Update Prevention with Optimistic Locking (LC SQL 175 variant) — Amazon

### Interview Scenario
"Amazon interview: Two concurrent processes update the same inventory item. Use optimistic locking to prevent lost updates without blocking reads."

### The Problem
Table **Inventory**: product_id (NUMBER PK), quantity (NUMBER), version NUMBER (or last_updated TIMESTAMP). Two warehouse systems simultaneously decrement inventory by different amounts. Prevent the second update from overwriting the first.

### Step 1: Understand Schema
- inventory: product_id, quantity, version
- Optimistic locking: read the current version, update only if version matches

Sample data:
| product_id | quantity | version |
|------------|----------|---------|
| 100        | 50       | 1       |

System A decrements by 5, System B decrements by 10 — both read version = 1.

### Step 2: Think Aloud
Pessimistic vs optimistic:
- **Pessimistic** (FOR UPDATE): Blocks concurrent readers, lower throughput
- **Optimistic** (version check): Higher throughput, but one update fails

For high-concurrency inventory systems, optimistic locking is preferred.

### Step 3: Write the Transaction
```sql
-- Procedure with optimistic locking
CREATE OR REPLACE PROCEDURE decrement_inventory (
  p_product_id IN inventory.product_id%TYPE,
  p_decrement  IN inventory.quantity%TYPE
) AS
  v_old_version inventory.version%TYPE;
  v_new_quantity inventory.quantity%TYPE;
BEGIN
  LOOP
    -- Read current state
    SELECT quantity, version
      INTO v_new_quantity, v_old_version
      FROM inventory
     WHERE product_id = p_product_id
       FOR UPDATE;  -- Still need row lock for the update itself

    -- Check sufficient inventory
    IF v_new_quantity < p_decrement THEN
      RAISE_APPLICATION_ERROR(-20002, 'Insufficient inventory');
    END IF;

    -- Try to update only if version hasn't changed
    UPDATE inventory
       SET quantity = quantity - p_decrement,
           version = version + 1
     WHERE product_id = p_product_id
       AND version = v_old_version;

    -- If no rows updated, someone else changed it — retry
    EXIT WHEN SQL%ROWCOUNT > 0;

    -- Release lock and retry
    COMMIT;  -- or use SAVEPOINT/ROLLBACK TO
  END LOOP;

  COMMIT;
EXCEPTION
  WHEN OTHERS THEN
    ROLLBACK;
    RAISE;
END decrement_inventory;
/
```

Using timestamp-based optimistic locking:
```sql
ALTER TABLE inventory ADD last_updated TIMESTAMP DEFAULT SYSTIMESTAMP;

UPDATE inventory
   SET quantity = quantity - p_decrement,
       last_updated = SYSTIMESTAMP
 WHERE product_id = p_product_id
   AND last_updated = v_old_timestamp;
```

### Step 4: Execution Plan
```sql
EXPLAIN PLAN FOR
UPDATE inventory
   SET quantity = quantity - 5,
       version = version + 1
 WHERE product_id = 100
   AND version = 1;
```

Index scan on PK (product_id), then filter on version. If version doesn't match, zero rows updated.

### Step 5: Optimize for High Concurrency

1. **Retry logic** with exponential backoff:
```sql
-- Inside the loop
DBMS_LOCK.SLEEP(MIN(v_retry_count * 0.1, 2));  -- Max 2 second wait
v_retry_count := v_retry_count + 1;
IF v_retry_count > 10 THEN
  RAISE_APPLICATION_ERROR(-20003, 'Max retries exceeded');
END IF;
```

2. **Partitioning** by product_id range to reduce lock contention:
```sql
CREATE TABLE inventory_part
  PARTITION BY RANGE (product_id)
  (PARTITION p1 VALUES LESS THAN (10000),
   PARTITION p2 VALUES LESS THAN (20000));
```

3. **In-memory quantity** with periodic flush to database (cache-aside pattern).

### Step 6: Test Concurrent Access

```sql
-- Session 1: DECREMENT_INVENTORY(100, 5);
-- Session 2: DECREMENT_INVENTORY(100, 10);

-- Both read version = 1
-- Session 1 updates: quantity = 45, version = 2 (rowcount = 1, exit)
-- Session 2 updates: WHERE product_id = 100 AND version = 1 (rowcount = 0, retry)
-- Session 2 re-reads: quantity = 45, version = 2
-- Session 2 updates: quantity = 35, version = 3 (rowcount = 1, exit)
-- Final: quantity = 35, version = 3
```

Edge cases:
- **Zero inventory**: Error raised on first check
- **Version mismatch every time**: Possible under high contention — need max retries
- **Bulk decrements**: Use batch update instead of row-by-row
- **Long-running transactions**: Set COMMIT more frequently for batch processing

### Company Evaluation
- **Amazon**: Optimistic locking is the standard at Amazon (DynamoDB conditional updates). Discuss leaderless replication conflicts.
- **Oracle**: FOR UPDATE still needed for the UPDATE itself. Discuss ORA-08177.
- **Google**: Spanner uses commit timestamps for optimistic locking.

---

## Problem 3: Snapshot Isolation and Read Consistency (LC SQL 601 variant) — Microsoft

### Interview Scenario
"Microsoft interview: A reporting query runs for 10 minutes while updates are happening. You need a consistent snapshot of the data as of the time the query started. Explain how Oracle achieves this and write the query."

### The Problem
Tables: **Orders** (1B rows), **Order_Items** (5B rows). A report sums order totals by region. While the report runs, new orders are being inserted. The report must see a consistent view.

### Step 1: Understand Schema
- Orders: id, region, order_date, total
- Order_Items: id, order_id, product_id, quantity, price
- Report runs for 10 minutes on 1B+ rows

### Step 2: Think Aloud
Oracle provides **MVCC (Multiversion Concurrency Control)**:
- READ COMMITTED: Each statement sees data as of statement start
- SERIALIZABLE: All statements see data as of transaction start
- READ ONLY: Transaction sees data as of transaction start, no DML allowed

For a long-running report, use READ ONLY or SERIALIZABLE to get a consistent snapshot.

### Step 3: Write the Transaction
```sql
-- Approach 1: READ ONLY transaction (best for reporting)
SET TRANSACTION READ ONLY
  NAME 'region_sales_report';

SELECT r.region_name,
       COUNT(DISTINCT o.id) AS order_count,
       SUM(oi.quantity * oi.price) AS total_sales,
       AVG(oi.quantity * oi.price) AS avg_order_value
  FROM orders o
  JOIN order_items oi ON o.id = oi.order_id
  JOIN regions r ON o.region_id = r.id
 WHERE o.order_date >= ADD_MONTHS(SYSDATE, -12)
 GROUP BY r.region_name
 ORDER BY total_sales DESC;

COMMIT;  -- Releases snapshot
```

```sql
-- Approach 2: SERIALIZABLE (if you need to do DML based on report results)
SET TRANSACTION ISOLATION LEVEL SERIALIZABLE;

-- Reports queries here...

-- Then DML based on consistent data
UPDATE region_targets t
   SET t.actual_sales = (
     SELECT SUM(oi.quantity * oi.price)
       FROM orders o
       JOIN order_items oi ON o.id = oi.order_id
      WHERE o.region_id = t.region_id
        AND o.order_date >= ADD_MONTHS(SYSDATE, -12)
   );

COMMIT;
```

### Step 4: Execution Plan and Undo Analysis
```sql
EXPLAIN PLAN FOR
SELECT r.region_name,
       COUNT(DISTINCT o.id) AS order_count,
       SUM(oi.quantity * oi.price) AS total_sales
  FROM orders o
  JOIN order_items oi ON o.id = oi.order_id
  JOIN regions r ON o.region_id = r.id
 WHERE o.order_date >= ADD_MONTHS(SYSDATE, -12)
 GROUP BY r.region_name;
```

The query reads the current snapshot. Oracle uses UNDO segments to provide read consistency:
- When the query started, Oracle records a System Change Number (SCN)
- As the query reads blocks, it checks if the block's SCN is newer than the query SCN
- If newer, Oracle reconstructs the older version from UNDO

```sql
-- Monitor UNDO usage during the report
SELECT tablespace_name, SUM(undo_blocks) AS undo_blocks,
       SUM(undoblks * (SELECT VALUE FROM v$parameter WHERE name = 'db_block_size')) / 1024/1024 AS undo_mb
  FROM v$undostat;
```

### Step 5: Optimize for Read Consistency

1. **Set appropriate UNDO_RETENTION**:
```sql
ALTER SYSTEM SET UNDO_RETENTION = 1800;  -- 30 minutes
```

2. **Use flashback queries** if the report can run against a past snapshot:
```sql
-- Flashback to 1 hour ago (avoids contention entirely)
SELECT r.region_name,
       SUM(oi.quantity * oi.price) AS total_sales
  FROM orders AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' HOUR) o
  JOIN order_items AS OF TIMESTAMP (SYSTIMESTAMP - INTERVAL '1' HOUR) oi
    ON o.id = oi.order_id
  JOIN regions r ON o.region_id = r.id
 WHERE o.order_date >= ADD_MONTHS(SYSDATE, -12)
 GROUP BY r.region_name;
```

3. **Materialized view** with refresh after business hours:
```sql
CREATE MATERIALIZED VIEW region_sales_mv
REFRESH COMPLETE ON DEMAND
AS
SELECT r.region_name, o.order_date,
       SUM(oi.quantity * oi.price) AS total_sales
  FROM orders o
  JOIN order_items oi ON o.id = oi.order_id
  JOIN regions r ON o.region_id = r.id
 GROUP BY r.region_name, o.order_date;
```

### Step 6: Test

```sql
-- Simulate concurrent DML while report runs
-- Session 1 (report):
SET TRANSACTION READ ONLY;
SELECT COUNT(*) FROM orders;  -- Returns 1,000,000,000

-- Session 2 (insert):
INSERT INTO orders VALUES (1000000001, ...);
COMMIT;  -- Committed after report started

-- Session 1 (report continues):
SELECT COUNT(*) FROM orders;  -- Still returns 1,000,000,000 (consistent)
```

Edge cases:
- **ORA-01555: Snapshot too old**: UNDO segment was overwritten before the query finished. Increase UNDO_RETENTION.
- **Long-running vs high-DML**: Higher UNDO_RETENTION or use materialized views.
- **Distributed transactions**: Read consistency across databases requires distributed transaction coordination.

```sql
-- Monitor ORA-01555
SELECT reason, object_owner, object_name
  FROM dba_outstanding_alerts
 WHERE reason LIKE '%01555%';
```

### Company Evaluation
- **Microsoft**: T-SQL uses SNAPSHOT ISOLATION (ALTER DATABASE SET ALLOW_SNAPSHOT_ISOLATION ON) vs Oracle's READ ONLY.
- **Oracle**: MVCC is built-in — no special setup. UNDO management is critical.
- **Amazon**: Redshift uses MVCC for reads. Aurora's storage layer handles consistency at the log level.
- **Google**: Spanner's TrueTime provides external consistency across data centers.
