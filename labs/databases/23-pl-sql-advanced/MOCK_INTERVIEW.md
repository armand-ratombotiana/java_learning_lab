# Mock Interview: Advanced PL/SQL (Lab 23)

**Role:** Senior PL/SQL Developer / Database Architect  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is the difference between a procedure, function, and package in PL/SQL?

**Candidate:**
| Feature | Procedure | Function | Package |
|---------|-----------|----------|---------|
| Returns | No (output via OUT params) | Yes (single value) | Group of related objects |
| Used in SQL | No | Yes (if pure) | References to functions/procedures |
| Parameters | IN, OUT, IN OUT | IN (default), IN OUT | Depends on members |
| Example | `UPDATE salary(...)` | `GET_SALARY(...) RETURN NUMBER` | `emp_pkg.get_salary(...)` |

**Interviewer:** Explain the different types of triggers in Oracle.

**Candidate:** Triggers are classified by:
- **Timing:** BEFORE, AFTER, INSTEAD OF
- **Event:** INSERT, UPDATE, DELETE, DDL, LOGON, LOGOFF
- **Level:** Row-level (FOR EACH ROW) or Statement-level
- **Scope:** Schema-level or Database-level

```sql
-- Row-level: Fires once per affected row, can reference :OLD and :NEW
CREATE TRIGGER trg_salary_audit 
BEFORE UPDATE OF salary ON employees
FOR EACH ROW
BEGIN
    INSERT INTO salary_audit(employee_id, old_salary, new_salary, changed_by, changed_at)
    VALUES (:OLD.employee_id, :OLD.salary, :NEW.salary, USER, SYSTIMESTAMP);
END;

-- Statement-level: Fires once per statement regardless of rows affected
CREATE TRIGGER trg_block_salary_update
BEFORE UPDATE OF salary ON employees
BEGIN
    IF TO_CHAR(SYSDATE, 'DAY') IN ('SATURDAY', 'SUNDAY') THEN
        RAISE_APPLICATION_ERROR(-20001, 'Cannot update salaries on weekends');
    END IF;
END;
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you improve PL/SQL performance with bulk operations?

**Candidate:** Use `BULK COLLECT` and `FORALL` to reduce context switches between PL/SQL and SQL:

**Without bulk (slow — N context switches):**
```sql
FOR rec IN (SELECT * FROM large_table) LOOP
    UPDATE another_table SET status = 'PROCESSED' WHERE id = rec.id;
END LOOP; -- 100K context switches
```

**With bulk (fast — reduced context switches):**
```sql
DECLARE
    TYPE id_tab IS TABLE OF large_table.id%TYPE;
    v_ids id_tab;
BEGIN
    -- Bulk collect from query (one context switch)
    SELECT id BULK COLLECT INTO v_ids FROM large_table WHERE status = 'PENDING';
    
    -- FORALL sends all DMLs in one context switch
    FORALL i IN 1..v_ids.COUNT
        UPDATE another_table SET status = 'PROCESSED' WHERE id = v_ids(i);
    
    DBMS_OUTPUT.PUT_LINE('Processed ' || SQL%ROWCOUNT || ' rows');
EXCEPTION
    WHEN OTHERS THEN
        -- FORALL has SAVE EXCEPTIONS clause for partial failures
        FOR i IN 1..SQL%BULK_EXCEPTIONS.COUNT LOOP
            DBMS_OUTPUT.PUT_LINE('Error at index ' || 
                SQL%BULK_EXCEPTIONS(i).ERROR_INDEX || ': ' ||
                SQLERRM(-SQL%BULK_EXCEPTIONS(i).ERROR_CODE));
        END LOOP;
END;
```

Performance improvement: Typically 10-100x faster for large datasets.

**Interviewer:** How do you create and use pipelined table functions?

**Candidate:** Pipelined table functions return rows incrementally (like streaming), useful for transforming data in ETL:
```sql
-- Define row type
CREATE TYPE transaction_row AS OBJECT(
    txn_date DATE, amount NUMBER, category VARCHAR2(50)
);
CREATE TYPE transaction_tab AS TABLE OF transaction_row;

-- Pipelined function
CREATE OR REPLACE FUNCTION categorize_transactions(p_start DATE, p_end DATE)
RETURN transaction_tab PIPELINED AS
BEGIN
    FOR rec IN (SELECT * FROM raw_transactions WHERE txn_date BETWEEN p_start AND p_end) LOOP
        -- Transform and yield each row
        PIPE ROW(transaction_row(
            rec.txn_date,
            rec.amount,
            CASE WHEN rec.amount > 1000 THEN 'HIGH'
                 WHEN rec.amount > 100 THEN 'MEDIUM'
                 ELSE 'LOW'
            END
        ));
    END LOOP;
    RETURN;
END;

-- Usage in SQL (rows available immediately, no waiting for full result)
SELECT * FROM TABLE(categorize_transactions(DATE '2024-01-01', DATE '2024-01-31'))
WHERE category = 'HIGH';
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a PL/SQL-based ETL process that moves 10M records from staging to production nightly, with incremental loads, error handling, and performance optimization.

**Candidate:** 

**Package design:**
```sql
CREATE OR REPLACE PACKAGE etl_process AS
    PROCEDURE run_full_load(p_batch_id NUMBER);
    PROCEDURE run_incremental_load(p_batch_id NUMBER);
    PROCEDURE validate_data(p_batch_id NUMBER);
    FUNCTION get_batch_status(p_batch_id NUMBER) RETURN VARCHAR2;
    PROCEDURE purge_old_data(p_retention_days NUMBER);
END etl_process;
/

CREATE OR REPLACE PACKAGE BODY etl_process AS

    -- Private bulk load procedure
    PROCEDURE bulk_load(p_batch_id NUMBER) IS
        TYPE order_rec_t IS RECORD(
            order_id      staging_orders.order_id%TYPE,
            customer_id   staging_orders.customer_id%TYPE,
            amount        staging_orders.amount%TYPE,
            order_date    staging_orders.order_date%TYPE,
            status        staging_orders.status%TYPE
        );
        TYPE order_tab IS TABLE OF order_rec_t;
        v_orders order_tab;
        
        CURSOR c_staging IS
            SELECT order_id, customer_id, amount, order_date, status
            FROM staging_orders
            WHERE batch_id = p_batch_id AND processed_flag = 'N';
    BEGIN
        LOOP
            -- Bulk collect in chunks of 5000
            FETCH c_staging BULK COLLECT INTO v_orders LIMIT 5000;
            EXIT WHEN v_orders.COUNT = 0;
            
            -- FORALL with MERGE (upsert)
            FORALL i IN 1..v_orders.COUNT SAVE EXCEPTIONS
                MERGE INTO production_orders t
                USING (SELECT v_orders(i).order_id AS order_id FROM DUAL) s
                ON (t.order_id = s.order_id)
                WHEN MATCHED THEN
                    UPDATE SET amount = v_orders(i).amount, 
                               status = v_orders(i).status,
                               last_updated = SYSTIMESTAMP
                WHEN NOT MATCHED THEN
                    INSERT (order_id, customer_id, amount, order_date, status)
                    VALUES (v_orders(i).order_id, v_orders(i).customer_id,
                            v_orders(i).amount, v_orders(i).order_date,
                            v_orders(i).status);
            
            -- Mark staging records as processed
            FORALL i IN 1..v_orders.COUNT
                UPDATE staging_orders 
                SET processed_flag = 'Y',
                    processed_at = SYSTIMESTAMP
                WHERE order_id = v_orders(i).order_id;
            
            COMMIT;
        END LOOP;
        CLOSE c_staging;
        
        UPDATE batch_control SET records_processed = records_processed + v_orders.COUNT;
    EXCEPTION
        WHEN OTHERS THEN
            -- Log error and continue with next batch
            INSERT INTO etl_errors(batch_id, error_msg, error_time)
            VALUES (p_batch_id, SQLERRM, SYSTIMESTAMP);
            ROLLBACK;
    END;

    -- Public: Full load (truncate and reload)
    PROCEDURE run_full_load(p_batch_id NUMBER) IS
    BEGIN
        UPDATE batch_control SET status = 'RUNNING' WHERE batch_id = p_batch_id;
        EXECUTE IMMEDIATE 'TRUNCATE TABLE production_orders';
        bulk_load(p_batch_id);
        UPDATE batch_control SET status = 'COMPLETED', completed_at = SYSTIMESTAMP 
        WHERE batch_id = p_batch_id;
    END;
    
    -- Public: Incremental load
    PROCEDURE run_incremental_load(p_batch_id NUMBER) IS
    BEGIN
        bulk_load(p_batch_id);
    END;
    
    -- Public: Data validation after load
    PROCEDURE validate_data(p_batch_id NUMBER) IS
        v_errors NUMBER;
    BEGIN
        -- Check row count match
        SELECT COUNT(*) - (SELECT COUNT(*) FROM production_orders) 
        INTO v_errors FROM staging_orders WHERE batch_id = p_batch_id;
        
        -- Check orphaned foreign keys
        SELECT COUNT(*) INTO v_errors FROM production_orders po
        WHERE NOT EXISTS (SELECT 1 FROM customers c WHERE c.id = po.customer_id);
        
        IF v_errors > 0 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Data validation failed with ' || v_errors || ' errors');
        END IF;
    END;
    
END etl_process;
/
```

**Performance characteristics:**
- Chunk size of 5000 balances memory usage and context switch savings
- `FORALL` with `SAVE EXCEPTIONS` continues processing even if some rows fail
- `MERGE` handles both inserts and updates (UPSERT) in one statement
- Autonomous transaction for error logging (won't roll back on failure)
- Parallel execution: can be parallelized by partition (run multiple batch IDs concurrently)

---

## Interviewer Feedback

**Strengths:** Excellent advanced PL/SQL design, practical bulk operations, robust ETL architecture  
**Areas to Improve:** Could discuss DBMS_PARALLEL_EXECUTE for chunk-based parallel processing  
**Verdict:** Strong Hire (Architect level)

---

*Databases Lab 23 MOCK_INTERVIEW.md*
