# SQL Problem Walkthrough: 12-plsql-fundamentals

## Problem 1: Find and Fix Invalid Objects (Oracle Internal) — Oracle

### Interview Scenario
"Oracle interview: As a DBA/developer, you need to find all invalid views, functions, procedures, and packages in the schema and recompile them. Write a PL/SQL block to do this."

### The Problem
Use the Oracle data dictionary (USER_OBJECTS or ALL_OBJECTS) to find all invalid objects in the current schema. Recompile each one in the correct order (views first, then standalone procedures/functions, then packages). Log the success or failure of each recompilation and return a summary.

### Step 1: Understand Schema
- ALL_OBJECTS: object_name, object_type, status, owner
- Object types: VIEW, PROCEDURE, FUNCTION, PACKAGE, PACKAGE BODY
- Invalid objects have status = 'INVALID'
- Dependency order matters: compile views before procedures that reference them
- Package body needs package spec compiled first

### Step 2: Think Aloud
Use a cursor to iterate over invalid objects. Use EXECUTE IMMEDIATE to run ALTER ... COMPILE. Use a savepoint per compilation so an error doesn't abort the entire block. Log results. Use deterministic order or UTL_RECOMP.

### Step 3: Write the PL/SQL Block
```sql
CREATE OR REPLACE PROCEDURE recompile_invalid_objects AS
  TYPE t_obj_rec IS RECORD (
    object_name ALL_OBJECTS.object_name%TYPE,
    object_type ALL_OBJECTS.object_type%TYPE
  );
  CURSOR c_invalid IS
    SELECT object_name, object_type
      FROM all_objects
     WHERE owner = USER
       AND status = 'INVALID'
       AND object_type IN ('VIEW', 'PROCEDURE', 'FUNCTION', 'PACKAGE', 'PACKAGE BODY')
     ORDER BY
       CASE object_type
         WHEN 'VIEW'          THEN 1
         WHEN 'PROCEDURE'     THEN 2
         WHEN 'FUNCTION'      THEN 2
         WHEN 'PACKAGE'       THEN 3
         WHEN 'PACKAGE BODY'  THEN 4
       END;

  v_sql      VARCHAR2(200);
  v_count    NUMBER := 0;
  v_success  NUMBER := 0;
  v_fail     NUMBER := 0;
BEGIN
  FOR rec IN c_invalid LOOP
    v_sql := 'ALTER ' || rec.object_type || ' "' || USER || '"."' || rec.object_name || '" COMPILE';
    v_count := v_count + 1;

    SAVEPOINT compile_sp;

    BEGIN
      EXECUTE IMMEDIATE v_sql;
      v_success := v_success + 1;
      DBMS_OUTPUT.PUT_LINE('COMPILED: ' || rec.object_type || ' ' || rec.object_name);
    EXCEPTION
      WHEN OTHERS THEN
        ROLLBACK TO compile_sp;
        v_fail := v_fail + 1;
        DBMS_OUTPUT.PUT_LINE('FAILED:   ' || rec.object_type || ' ' || rec.object_name ||
                             ' - ' || SQLERRM);
    END;
  END LOOP;

  DBMS_OUTPUT.PUT_LINE('---');
  DBMS_OUTPUT.PUT_LINE('Total: ' || v_count || ', Success: ' || v_success || ', Failed: ' || v_fail);
END recompile_invalid_objects;
/
```

Alternative using UTL_RECOMP (Oracle built-in):
```sql
-- For schema-level recompile
BEGIN
  UTL_RECOMP.RECOMP_SERIAL('HR');
END;
/

-- For database-wide parallel recompile (DBA only)
BEGIN
  UTL_RECOMP.RECOMP_PARALLEL(4);
END;
/
```

### Step 4: Execution Plan
PL/SQL doesn't have an execution plan like SQL. Instead, understand the execution flow:
1. Cursor OPEN -- query against ALL_OBJECTS (full scan of the dictionary views)
2. FOR LOOP -- fetch each row
3. EXECUTE IMMEDIATE -- parse and run ALTER COMPILE (recursive SQL)
4. COMMIT/ROLLBACK to savepoint -- only the current recompilation attempt

Dictionary query performance:
- ALL_OBJECTS is a view over SYS.OBJ$ and SYS.USER$ -- indexed by owner and status
- No full table scan expected for single-owner filter

### Step 5: Optimize
1. **Use UTL_RECOMP** instead of custom code -- Oracle handles dependency order optimally:
```sql
BEGIN
  UTL_RECOMP.RECOMP_PARALLEL(
    threads    => 4,
    schema     => 'HR',
    flags      => UTL_RECOMP.RECOMP_NON_SYS_ONLY
  );
END;
/
```

2. **Cache plan for recompile** -- use DBMS_SHARED_POOL.KEEP for critical objects.

3. **Parallel recompile** for large schemas:
   - UTL_RECOMP.RECOMP_PARALLEL uses multiple threads and respects dependencies
   - For custom code, use DBMS_SCHEDULER to run multiple jobs with ordered dependency

### Step 6: Test
Edge cases:
- **No invalid objects**: Cursor returns nothing, messages say Total: 0
- **Circular dependencies**: Compilation loop -- one object depends on another that depends back. Oracle handles this; the first attempt may fail, second pass may succeed.
- **Missing privileges**: ALTER ANY PROCEDURE or object ownership needed
- **Packages**: Package body depends on package spec. Order ensures spec is compiled first.

```sql
-- Force some objects to invalid to test
ALTER PROCEDURE my_proc COMPILE;
-- Will succeed -- no change

-- Test with error logging
EXEC recompile_invalid_objects;
```

### Company Evaluation
- **Oracle**: Core DBA skill. Understanding dependency management and UTL_RECOMP.
- **Amazon**: In RDS, use UTL_RECOMP or automated maintenance windows.
- **Microsoft**: T-SQL uses sp_refreshsqlmodule for views, ALTER ... WITH CHECK for procedures.

---

## Problem 2: Generate Department Payroll Report (LC SQL 184 variant) -- Amazon

### Interview Scenario
"Amazon interview: Write a PL/SQL procedure that generates a payroll report. For each department, list employees with their salary and the department average salary. Highlight employees earning above average."

### The Problem
Tables: **departments** (id, name), **employees** (id, name, salary, dept_id). Write a PL/SQL procedure that prints a formatted report: for each department, show each employee's name, salary, and whether they are above, below, or at the department average salary.

### Step 1: Understand Schema
- departments: id, name
- employees: id, name, salary, dept_id (FK to departments.id)
- Need department average for comparison
- Format with headers, separators, and summary rows

### Step 2: Think Aloud
Three approaches:
1. **Two-step**: First compute averages per department, then read and compare
2. **Analytic AVG**: Compute AVG(salary) OVER (PARTITION BY dept_id) inline
3. **BULK COLLECT**: For performance at scale

Approach 2 is cleanest -- use a cursor with analytic function.

### Step 3: Write the PL/SQL Procedure
```sql
CREATE OR REPLACE PROCEDURE payroll_report AS
  CURSOR c_dept_report IS
    SELECT d.name AS dept_name,
           e.name AS emp_name,
           e.salary,
           ROUND(AVG(e.salary) OVER (PARTITION BY e.dept_id), 2) AS dept_avg_sal,
           CASE
             WHEN e.salary > AVG(e.salary) OVER (PARTITION BY e.dept_id) THEN 'ABOVE'
             WHEN e.salary < AVG(e.salary) OVER (PARTITION BY e.dept_id) THEN 'BELOW'
             ELSE 'AVERAGE'
           END AS status
      FROM employees e
      JOIN departments d ON e.dept_id = d.id
     ORDER BY d.name, e.salary DESC;

  v_prev_dept     VARCHAR2(100) := '';
  v_dept_emp_ct   NUMBER := 0;
  v_dept_sal_sum  NUMBER := 0;
BEGIN
  DBMS_OUTPUT.PUT_LINE(RPAD('=', 80, '='));
  DBMS_OUTPUT.PUT_LINE('PAYROLL REPORT');
  DBMS_OUTPUT.PUT_LINE(RPAD('=', 80, '='));

  FOR rec IN c_dept_report LOOP
    IF rec.dept_name != v_prev_dept AND v_prev_dept IS NOT NULL THEN
      DBMS_OUTPUT.PUT_LINE(RPAD('-', 60, '-'));
    END IF;

    IF rec.dept_name != v_prev_dept THEN
      DBMS_OUTPUT.PUT_LINE(CHR(10) || 'DEPARTMENT: ' || rec.dept_name);
      DBMS_OUTPUT.PUT_LINE(RPAD('-', 60, '-'));
      DBMS_OUTPUT.PUT_LINE(
        RPAD('Employee', 20) ||
        RPAD('Salary', 12) ||
        RPAD('Dept Avg', 12) ||
        'Status'
      );
      DBMS_OUTPUT.PUT_LINE(RPAD('-', 60, '-'));
      v_prev_dept := rec.dept_name;
      v_dept_emp_ct := 0;
      v_dept_sal_sum := 0;
    END IF;

    DBMS_OUTPUT.PUT_LINE(
      RPAD(rec.emp_name, 20) ||
      LPAD(TO_CHAR(rec.salary, '$99,999'), 12) ||
      LPAD(TO_CHAR(rec.dept_avg_sal, '$99,999.99'), 12) ||
      ' ' || rec.status
    );

    v_dept_emp_ct := v_dept_emp_ct + 1;
    v_dept_sal_sum := v_dept_sal_sum + rec.salary;
  END LOOP;

  DBMS_OUTPUT.PUT_LINE(CHR(10) || RPAD('=', 80, '='));
  DBMS_OUTPUT.PUT_LINE('END OF REPORT');
  DBMS_OUTPUT.PUT_LINE(RPAD('=', 80, '='));
END payroll_report;
/
```

### Step 4: Execution Plan
```sql
EXPLAIN PLAN FOR
SELECT d.name, e.name, e.salary,
       AVG(e.salary) OVER (PARTITION BY e.dept_id) AS dept_avg
  FROM employees e
  JOIN departments d ON e.dept_id = d.id
 ORDER BY d.name, e.salary DESC;
```

The cursor query:
1. Full table scan on employees
2. HASH JOIN to departments
3. WINDOW SORT for AVG OVER (PARTITION BY)
4. SORT ORDER BY for the outer ORDER BY

### Step 5: Optimize
1. **Use BULK COLLECT** for large employee counts:
```sql
CREATE OR REPLACE PROCEDURE payroll_report_bulk AS
  TYPE t_emp_rec IS RECORD (
    dept_name  departments.name%TYPE,
    emp_name   employees.name%TYPE,
    salary     employees.salary%TYPE,
    dept_avg   NUMBER
  );
  TYPE t_emp_tab IS TABLE OF t_emp_rec;
  v_employees t_emp_tab;
BEGIN
  SELECT d.name, e.name, e.salary,
         ROUND(AVG(e.salary) OVER (PARTITION BY e.dept_id), 2)
    BULK COLLECT INTO v_employees
    FROM employees e
    JOIN departments d ON e.dept_id = d.id
   ORDER BY d.name, e.salary DESC;

  FOR i IN 1 .. v_employees.COUNT LOOP
    DBMS_OUTPUT.PUT_LINE(v_employees(i).emp_name || ': ' || v_employees(i).salary);
  END LOOP;
END;
/
```

2. **Materialized view** for pre-computed department averages:
```sql
CREATE MATERIALIZED VIEW dept_avg_sal_mv
REFRESH COMPLETE ON DEMAND
AS
SELECT dept_id, ROUND(AVG(salary), 2) AS avg_salary
  FROM employees
 GROUP BY dept_id;
```

### Step 6: Test
Edge cases:
- **Department with no employees**: Not displayed (JOIN excludes it)
- **Employee with NULL salary**: AVG ignores NULL; comparison will be NULL -> 'BELOW' incorrectly
- **Ties at average**: Exact match -> 'AVERAGE'
- **Single employee department**: Employee is always at average

```sql
-- Test with edge cases
INSERT INTO employees VALUES (100, 'CEO', NULL, 1);
INSERT INTO employees VALUES (101, 'Intern', 0, 1);

EXEC payroll_report;
```

### Company Evaluation
- **Amazon**: Focus on bulk operations for large reports.
- **Oracle**: Cursor FOR LOOP vs BULK COLLECT performance. DBMS_OUTPUT limitations.
- **Microsoft**: T-SQL -- can use PRINT or build a result set.

---

## Problem 3: Tree Structure Query -- Org Chart (LC SQL 608 variant) -- Google

### Interview Scenario
"Google interview: Write a PL/SQL function that takes an employee ID and returns the full org chain from CEO to that employee. Use hierarchical query and recursion."

### The Problem
Table **employees**: id (NUMBER PK), name (VARCHAR2), manager_id (NUMBER, nullable). Write a function that accepts an employee_id and returns a VARCHAR2 containing the org chain path. If the employee doesn't exist, return NULL.

### Step 1: Understand Schema
- Self-referencing: manager_id -> id
- CEO has manager_id IS NULL
- Oracle supports hierarchical queries with CONNECT BY

Sample data:
| id | name     | manager_id |
|----|----------|------------|
| 1  | CEO      | NULL       |
| 2  | VP Eng   | 1          |
| 3  | Director | 2          |
| 4  | Employee | 3          |

Function(4) -> "CEO > VP Eng > Director > Employee"

### Step 2: Think Aloud
Oracle has two approaches:
1. **CONNECT BY PRIOR**: Classical Oracle hierarchical query
2. **Recursive WITH** (recursive CTE): SQL standard, available 11gR2+

### Step 3: Write the Function
```sql
CREATE OR REPLACE FUNCTION get_org_chain (p_emp_id IN employees.id%TYPE)
  RETURN VARCHAR2
IS
  v_chain VARCHAR2(4000);
BEGIN
  SELECT SYS_CONNECT_BY_PATH(name, ' > ')
    INTO v_chain
    FROM employees
   WHERE id = p_emp_id
   START WITH id = p_emp_id
   CONNECT BY PRIOR manager_id = id;

  v_chain := LTRIM(v_chain, ' > ');
  RETURN v_chain;
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN NULL;
END get_org_chain;
/
```

Recursive CTE version:
```sql
CREATE OR REPLACE FUNCTION get_org_chain_recursive (p_emp_id IN employees.id%TYPE)
  RETURN VARCHAR2
IS
  v_chain VARCHAR2(4000);
BEGIN
  WITH org_chain (id, name, manager_id, chain) AS (
    SELECT id, name, manager_id, name AS chain
      FROM employees
     WHERE id = p_emp_id
    UNION ALL
    SELECT e.id, e.name, e.manager_id,
           e.name || ' > ' || oc.chain
      FROM employees e
      JOIN org_chain oc ON e.id = oc.manager_id
  )
  SELECT chain
    INTO v_chain
    FROM org_chain
   WHERE manager_id IS NULL;

  RETURN v_chain;
EXCEPTION
  WHEN NO_DATA_FOUND THEN
    RETURN NULL;
END get_org_chain_recursive;
/
```

### Step 4: Execution Plan
CONNECT BY WITH FILTERING (11gR2+) uses a smarter algorithm -- it processes the start row, then probes the index on manager_id to find parent rows. Each level is one index range scan, making it O(depth) operations.

### Step 5: Optimize
```sql
CREATE INDEX emp_mgr_id_idx ON employees(manager_id);
```

For deep hierarchies:
```sql
SELECT SYS_CONNECT_BY_PATH(name, ' > ') AS chain
  FROM employees
 START WITH id = p_emp_id
 CONNECT BY NOCYCLE PRIOR manager_id = id;
```

Use ORDER SIBLINGS BY for tree ordering:
```sql
SELECT LPAD(' ', 2 * (LEVEL - 1)) || name AS org_tree
  FROM employees
 START WITH manager_id IS NULL
 CONNECT BY PRIOR id = manager_id
 ORDER SIBLINGS BY name;
```

### Step 6: Test
Edge cases:
- **CEO (manager_id IS NULL)**: Returns just "CEO"
- **Invalid employee_id**: Returns NULL
- **Circular reference**: CONNECT BY NOCYCLE prevents ORA-01436

```sql
SELECT get_org_chain(4) AS chain FROM dual;
-- CEO > VP Eng > Director > Employee

SELECT get_org_chain(1) AS chain FROM dual;
-- CEO

SELECT get_org_chain(999) AS chain FROM dual;
-- NULL
```

### Company Evaluation
- **Google**: Recursive CTEs. Discuss breadth-first vs depth-first traversal.
- **Oracle**: CONNECT BY is idiomatic. NOCYCLE, ORDER SIBLINGS BY, LEVEL.
- **Microsoft**: T-SQL uses recursive CTEs exclusively.
- **Amazon**: At scale, org hierarchies use adjacency lists or materialized paths.
