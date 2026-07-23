# LEETCODE_SOLUTIONS — PL/SQL Foundations

## Procedural SQL Solutions

| LeetCode Problem | PL/SQL Approach | Key Feature |
|-----------------|-----------------|-------------|
| 176 Second Highest | Function returning salary | `CREATE FUNCTION` |
| 177 Nth Highest | Dynamic SQL with bind | `EXECUTE IMMEDIATE` |
| 180 Consecutive Numbers | Cursor + loop | `FETCH ... BULK COLLECT INTO` |
| 196 Delete Duplicates | Cursor + DELETE | `FORALL` for bulk delete |
| 597 Accept Rate | Function returning ratio | `RETURN` statement |

### PL/SQL Implementation: LeetCode 176
```sql
CREATE OR REPLACE FUNCTION second_highest_salary RETURN NUMBER IS
    v_salary Employee.salary%TYPE;
BEGIN
    SELECT DISTINCT salary INTO v_salary
    FROM (SELECT salary FROM Employee
          ORDER BY salary DESC
          OFFSET 1 ROW FETCH NEXT 1 ROW ONLY);
    RETURN v_salary;
EXCEPTION
    WHEN NO_DATA_FOUND THEN RETURN NULL;
END second_highest_salary;
/

-- Call: SELECT second_highest_salary FROM DUAL;
```

### PL/SQL for LeetCode Batch Processing
```sql
-- Bulk collect for large datasets (LeetCode with big data)
DECLARE
    TYPE emp_salary_tab IS TABLE OF Employee.salary%TYPE;
    v_salaries emp_salary_tab;
BEGIN
    SELECT salary BULK COLLECT INTO v_salaries FROM Employee
    WHERE salary > 3000;
    -- Process salaries in bulk
    FORALL i IN 1..v_salaries.COUNT
        INSERT INTO high_earners(salary) VALUES(v_salaries(i));
    COMMIT;
END;
/
```
