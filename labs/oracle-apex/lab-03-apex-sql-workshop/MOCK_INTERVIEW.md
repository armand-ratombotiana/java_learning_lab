# Mock Interview: APEX SQL Workshop (Lab 03)

**Role:** Oracle APEX Developer (Junior/Mid)  
**Duration:** 35 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What tools does APEX SQL Workshop provide for database management?

**Candidate:** SQL Workshop provides browser-based database management tools:
1. **Object Browser:** Visual tool to browse, create, and modify database objects (tables, views, indexes, triggers, packages, sequences)
2. **SQL Commands:** Execute SQL and PL/SQL statements, view results, download query output
3. **SQL Scripts:** Create, run, and manage multi-statement SQL scripts (for DDL, DML, data migration)
4. **Data Workshop:** Load data from CSV/XLSX files, generate sample data, export data to CSV/JSON/XML
5. **Utilities:** Table counts, object reports, query builder (wysiwyg SQL creation)

**Interviewer:** How do you load data from a CSV file into an APEX application?

**Candidate:** Through Data Workshop:
1. Navigate to SQL Workshop → Data Workshop → Data Load
2. Select the target schema and table (or create a new one)
3. Upload the CSV file (drag-and-drop or file picker)
4. Map CSV columns to table columns
5. Preview the data and confirm loading
6. APEX generates and runs INSERT statements to load the data

For large datasets (>10K rows), use SQL*Loader or external tables instead. For repeated data loads, automate with a PL/SQL procedure using `APEX_DATA_PARSER`:
```sql
INSERT INTO employees (employee_id, first_name, last_name, email, hire_date)
SELECT col001, col002, col003, col004, TO_DATE(col005, 'YYYY-MM-DD')
FROM TABLE(APEX_DATA_PARSER.parse(
    p_content => :P1_CSV_CONTENT,
    p_file_name => 'employees.csv',
    p_skip_rows => 1
));
```

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** Write a SQL query to find the top 5 highest-paid employees in each department.

**Candidate:**
```sql
SELECT department_id, employee_id, first_name, last_name, salary, rank
FROM (
    SELECT department_id, employee_id, first_name, last_name, salary,
           DENSE_RANK() OVER (PARTITION BY department_id ORDER BY salary DESC) AS rank
    FROM employees
    WHERE department_id IS NOT NULL
)
WHERE rank <= 5
ORDER BY department_id, rank;
```

This uses the `DENSE_RANK()` window function. If there are ties (same salary), employees share the same rank. If you want exactly 5 rows per department (no ties), use `ROW_NUMBER()` instead of `DENSE_RANK()`.

**Interviewer:** How do you debug a slow-performing query in APEX SQL Workshop?

**Candidate:** 
1. Use **SQL Commands** with `SET TIMING ON` to see execution time
2. Use **Explain Plan**: `EXPLAIN PLAN FOR <query>; SELECT * FROM TABLE(DBMS_XPLAN.DISPLAY);`
3. Check for full table scans (FTS) in the execution plan — indicates missing indexes
4. Use **SQL Tuning Advisor**: `EXECUTE DBMS_SQLTUNE.REPORT_TUNING_TASK(...);`
5. Use **Active Session History (ASH)** for real-time performance monitoring
6. Check bind variable peeking issues — if query performance varies with different inputs

Common issues: missing indexes, non-selective filters, cartesian joins, data type conversion preventing index usage, AND NULL vs NOT NULL handling.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Create a data loading process that handles 100K records from an external source daily. Include error handling, deduplication, and audit logging. Design both the SQL and the APEX implementation.

**Candidate:** 

**Step 1 — Staging table:**
```sql
CREATE TABLE staging_orders (
    batch_id NUMBER,
    order_ref VARCHAR2(50),
    customer_id NUMBER,
    order_date DATE,
    total_amount NUMBER,
    status VARCHAR2(20),
    processed_flag VARCHAR2(1) DEFAULT 'N',
    error_message VARCHAR2(4000),
    created_at TIMESTAMP DEFAULT SYSTIMESTAMP
);
```

**Step 2 — Merge logic:**
```sql
CREATE OR REPLACE PROCEDURE process_staging_data(p_batch_id NUMBER) AS
    v_inserted NUMBER := 0;
    v_updated NUMBER := 0;
    v_errors NUMBER := 0;
BEGIN
    -- Deduplication: MERGE (UPSERT)
    MERGE INTO orders o
    USING (
        SELECT s.order_ref, s.customer_id, s.order_date, s.total_amount, s.status
        FROM staging_orders s
        WHERE s.batch_id = p_batch_id AND s.processed_flag = 'N'
    ) src
    ON (o.order_ref = src.order_ref)
    WHEN MATCHED THEN
        UPDATE SET o.total_amount = src.total_amount,
                   o.status = src.status,
                   o.last_updated = SYSDATE
    WHEN NOT MATCHED THEN
        INSERT (order_ref, customer_id, order_date, total_amount, status)
        VALUES (src.order_ref, src.customer_id, src.order_date, src.total_amount, src.status);
    
    -- Mark as processed
    UPDATE staging_orders
    SET processed_flag = 'Y',
        error_message = CASE 
            WHEN order_ref IS NULL OR customer_id IS NULL THEN 'Missing required fields'
            ELSE NULL
        END
    WHERE batch_id = p_batch_id;
    
    -- Audit
    INSERT INTO data_load_audit(batch_id, records_processed, errors, completed_at)
    VALUES (p_batch_id, SQL%ROWCOUNT, v_errors, SYSDATE);
END;
```

**Step 3 — APEX implementation:**
Create a schedule via **APEX Scheduler**:
- `DBMS_SCHEDULER.create_job(...)` for nightly execution
- APEX page with a "Run Now" button and batch status display
- Progress bar showing processed/total records

**Interviewer:** You need to provide a REST API that returns data loaded through this process. How would you expose it via ORDS?

**Candidate:**
```sql
-- Enable REST for a view
BEGIN
    ORDS.ENABLE_OBJECT(
        p_enabled => TRUE,
        p_schema => 'APP_SCHEMA',
        p_object => 'order_summary_view',
        p_object_type => 'VIEW',
        p_object_alias => 'orders'
    );
    
    -- Define a template for single order lookup
    ORDS.DEFINE_TEMPLATE(
        p_module_name => 'data-loading',
        p_pattern => 'orders/:ref'
    );
    
    ORDS.DEFINE_HANDLER(
        p_module_name => 'data-loading',
        p_pattern => 'orders/:ref',
        p_method => 'GET',
        p_source_type => ORDS.SOURCE_TYPE_QUERY,
        p_source => 'SELECT * FROM order_summary_view WHERE order_ref = :ref'
    );
END;
```

This exposes: `GET /ords/apex/data-loading/orders/{ref}` returning JSON. APEX applications can consume this via Web Source Modules.

---

## Interviewer Feedback

**Strengths:** Strong SQL skills, practical batch processing design, good ORDS knowledge  
**Areas to Improve:** Could discuss APEX_DATA_PARSER for modern CSV parsing  
**Verdict:** Strong Hire

---

*Lab 03 MOCK_INTERVIEW.md — Part of Oracle APEX Academy Interview Preparation*
