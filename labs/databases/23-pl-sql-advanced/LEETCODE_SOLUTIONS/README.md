# LEETCODE_SOLUTIONS — PL/SQL Advanced

## Advanced PL/SQL Solutions

| LeetCode Problem | Advanced PL/SQL Technique | Why |
|-----------------|---------------------------|-----|
| 569 Median Salary | PIPELINED function | Process rows on demand |
| 571 Find Median | Collection + statistical functions | Complex aggregation |
| 579 Cumulative Salary | RESULT_CACHE + recursion | Frequently accessed data |
| 618 Students Report | Dynamic PIVOT with SQL generation | Column count varies |
| 1097 Game Play V | Autonomous transaction for logging | Non-blocking audit |

### Pipelined Function: LeetCode 569 Median
```sql
CREATE OR REPLACE PACKAGE median_pkg AS
    TYPE salary_rec IS RECORD (company VARCHAR2(100), median_sal NUMBER);
    TYPE salary_tab IS TABLE OF salary_rec;
    FUNCTION get_median_salaries RETURN salary_tab PIPELINED;
END median_pkg;
/

CREATE OR REPLACE PACKAGE BODY median_pkg AS
    FUNCTION get_median_salaries RETURN salary_tab PIPELINED IS
        CURSOR comp_cur IS SELECT DISTINCT company FROM Employee;
    BEGIN
        FOR comp IN comp_cur LOOP
            FOR rec IN (
                SELECT company, PERCENTILE_CONT(0.5) WITHIN GROUP (ORDER BY salary) AS median
                FROM Employee WHERE company = comp.company
                GROUP BY company
            ) LOOP
                PIPE ROW(rec);
            END LOOP;
        END LOOP;
        RETURN;
    END;
END median_pkg;
/

-- SELECT * FROM TABLE(median_pkg.get_median_salaries);
```

### Result Cache for Frequent LeetCode Queries
```sql
CREATE OR REPLACE FUNCTION get_dept_top_salaries(p_dept_id NUMBER)
RETURN SYS_REFCURSOR RESULT_CACHE RELIES_ON (Employee) AS
    v_cursor SYS_REFCURSOR;
BEGIN
    OPEN v_cursor FOR
        SELECT * FROM (
            SELECT name, salary,
                   DENSE_RANK() OVER (ORDER BY salary DESC) AS rank
            FROM Employee WHERE department_id = p_dept_id
        ) WHERE rank <= 3;
    RETURN v_cursor;
END get_dept_top_salaries;
/
```
