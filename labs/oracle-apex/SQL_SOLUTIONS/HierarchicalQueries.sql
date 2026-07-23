-- ============================================================================
-- HierarchicalQueries.sql
-- Oracle CONNECT BY and Recursive CTE (WITH RECURSIVE) queries
-- For Oracle APEX and Oracle Database interviews
-- ============================================================================

-- ============================================================================
-- SECTION 1: EMP Table Organization Chart (CONNECT BY)
-- ============================================================================

-- 1.1 Basic hierarchical query with LEVEL
SELECT LEVEL,
       empno, ename, job, mgr
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
ORDER BY LEVEL, ename;

-- 1.2 Formatted organization chart with indentation
SELECT LEVEL AS depth,
       LPAD(' ', (LEVEL - 1) * 3, ' ') || ename AS org_chart,
       ename AS employee_name,
       job AS position,
       empno AS employee_id,
       mgr AS manager_id
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- 1.3 Organization chart with SYS_CONNECT_BY_PATH
SELECT ename,
       LEVEL AS depth,
       SYS_CONNECT_BY_PATH(ename, ' / ') AS full_path,
       CONNECT_BY_ROOT ename AS root_employee,
       CONNECT_BY_ISLEAF AS is_leaf,
       CONNECT_BY_ISCYCLE AS is_cycle
FROM emp
START WITH mgr IS NULL
CONNECT BY NOCYCLE PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- 1.4 Find all subordinates of a specific manager
SELECT ename, empno, LEVEL,
       LPAD(' ', (LEVEL - 1) * 3, ' ') || ename AS tree
FROM emp
START WITH ename = 'JONES'
CONNECT BY PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- 1.5 Find the management chain for a specific employee (reverse direction)
SELECT ename, empno, LEVEL,
       SYS_CONNECT_BY_PATH(ename, ' -> ') AS reporting_chain
FROM emp
START WITH ename = 'FORD'
CONNECT BY PRIOR mgr = empno
ORDER BY LEVEL DESC;

-- 1.6 Show all managers and their direct reports count
SELECT m.ename AS manager,
       m.empno,
       COUNT(e.empno) AS direct_reports,
       LISTAGG(e.ename, ', ') WITHIN GROUP (ORDER BY e.ename) AS reports
FROM emp m
LEFT JOIN emp e ON m.empno = e.mgr
GROUP BY m.ename, m.empno
HAVING COUNT(e.empno) > 0
ORDER BY direct_reports DESC;

-- ============================================================================
-- SECTION 2: Recursive CTE (WITH RECURSIVE)
-- ============================================================================

-- 2.1 Recursive CTE for organization chart (SQL Standard)
WITH RECURSIVE org_chart AS (
    -- Anchor: top-level managers
    SELECT empno, ename, mgr, 1 AS level,
           ename AS path
    FROM emp
    WHERE mgr IS NULL

    UNION ALL

    -- Recursive: employees reporting to someone
    SELECT e.empno, e.ename, e.mgr, oc.level + 1,
           oc.path || ' > ' || e.ename
    FROM emp e
    JOIN org_chart oc ON e.mgr = oc.empno
)
SEARCH DEPTH FIRST BY ename SET ordering
SELECT level, LPAD(' ', (level - 1) * 3, ' ') || ename AS tree,
       empno, mgr, path
FROM org_chart
ORDER BY ordering;

-- 2.2 Recursive CTE to find all levels of management
WITH RECURSIVE mgr_chain AS (
    SELECT empno, ename, mgr, 1 AS mgr_level
    FROM emp
    WHERE mgr IS NULL

    UNION ALL

    SELECT e.empno, e.ename, e.mgr, mc.mgr_level + 1
    FROM emp e
    JOIN mgr_chain mc ON e.mgr = mc.empno
)
SELECT MAX(mgr_level) AS org_depth, COUNT(*) AS total_employees
FROM mgr_chain;

-- 2.3 Recursive CTE for product categories (e-commerce use case)
WITH RECURSIVE category_tree AS (
    SELECT category_id, parent_id, name, 1 AS level,
           name AS path
    FROM categories
    WHERE parent_id IS NULL

    UNION ALL

    SELECT c.category_id, c.parent_id, c.name, ct.level + 1,
           ct.path || ' > ' || c.name
    FROM categories c
    JOIN category_tree ct ON c.parent_id = ct.category_id
)
SELECT level, LPAD(' ', (level - 1) * 3, ' ') || name AS category,
       path
FROM category_tree
ORDER BY path;

-- ============================================================================
-- SECTION 3: Oracle-Specific Hierarchical Functions
-- ============================================================================

-- 3.1 CONNECT_BY_ROOT: Get top-level ancestor
SELECT ename,
       CONNECT_BY_ROOT ename AS ceo,
       LEVEL AS depth
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr;

-- 3.2 CONNECT_BY_ISLEAF: Identify leaf nodes
SELECT ename, job,
       CASE CONNECT_BY_ISLEAF
           WHEN 1 THEN 'LEAF (No direct reports)'
           ELSE 'MANAGER (Has direct reports)'
       END AS node_type
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- 3.3 ORDER SIBLINGS BY: Order children alphabetically
SELECT ename,
       SYS_CONNECT_BY_PATH(ename, '/') AS path
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
ORDER SIBLINGS BY ename;

-- 3.4 NOCYCLE: Prevent infinite loops
SELECT ename, LEVEL,
       CONNECT_BY_ISCYCLE AS would_create_cycle
FROM emp
START WITH ename = 'KING'
CONNECT BY NOCYCLE PRIOR empno = mgr;

-- 3.5 Hierarchical aggregation (sum salaries per branch)
SELECT ename, sal,
       SUM(sal) OVER (PARTITION BY CONNECT_BY_ROOT empno) AS branch_total_salary
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr;

-- ============================================================================
-- SECTION 4: Real-World Hierarchical Patterns
-- ============================================================================

-- 4.1 Employee leave approval hierarchy
WITH RECURSIVE approval_chain AS (
    SELECT empno, ename, mgr, 1 AS step,
           ename AS approvers
    FROM emp
    WHERE empno = :P_EMPLOYEE_ID

    UNION ALL

    SELECT e.empno, e.ename, e.mgr, ac.step + 1,
           ac.approvers || ' -> ' || e.ename
    FROM emp e
    JOIN approval_chain ac ON e.empno = ac.mgr
)
SELECT step, ename AS approver, approvers
FROM approval_chain
ORDER BY step;

-- 4.2 Bill of Materials (BOM) explosion
WITH RECURSIVE bom_explosion AS (
    SELECT part_id, assembly_id, quantity, 1 AS level,
           part_id AS root_part
    FROM bom
    WHERE assembly_id IS NULL  -- Top-level assemblies

    UNION ALL

    SELECT b.part_id, b.assembly_id, b.quantity, be.level + 1,
           be.root_part
    FROM bom b
    JOIN bom_explosion be ON b.assembly_id = be.part_id
)
SELECT level, LPAD(' ', (level - 1) * 3, ' ') || part_id AS bom,
       quantity
FROM bom_explosion
ORDER BY root_part, level;

-- 4.3 Organizational budget rollup
WITH RECURSIVE dept_budget AS (
    SELECT deptno, dname, budget, 1 AS level
    FROM departments
    WHERE parent_deptno IS NULL

    UNION ALL

    SELECT d.deptno, d.dname, d.budget, db.level + 1
    FROM departments d
    JOIN dept_budget db ON d.parent_deptno = db.deptno
)
SELECT level, LPAD(' ', (level - 1) * 3, ' ') || dname AS dept,
       budget,
       SUM(budget) OVER (PARTITION BY CONNECT_BY_ROOT deptno) AS branch_budget
FROM dept_budget;

-- ============================================================================
-- SECTION 5: Common Hierarchical Interview Problems
-- ============================================================================

-- 5.1 Find all direct and indirect reports of a manager
SELECT ename, empno, LEVEL
FROM emp
START WITH ename = 'KING'
CONNECT BY PRIOR empno = mgr
ORDER BY LEVEL, ename;

-- 5.2 Find the depth of the organization
SELECT MAX(LEVEL) AS organization_depth
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr;

-- 5.3 Find all employees at each level
SELECT LEVEL AS org_level,
       COUNT(*) AS employee_count,
       LISTAGG(ename, ', ') WITHIN GROUP (ORDER BY ename) AS employees
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
GROUP BY LEVEL
ORDER BY LEVEL;

-- 5.4 Find all paths from root to leaf
SELECT SYS_CONNECT_BY_PATH(ename, ' -> ') AS career_path,
       LEVEL AS path_length,
       CONNECT_BY_ISLEAF AS is_leaf
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
ORDER BY path_length, career_path;

-- 5.5 Find the longest reporting chain
SELECT ename, LEVEL AS chain_length,
       SYS_CONNECT_BY_PATH(ename, ' -> ') AS full_chain
FROM emp
START WITH mgr IS NULL
CONNECT BY PRIOR empno = mgr
ORDER BY LEVEL DESC
FETCH FIRST 5 ROWS ONLY;

-- 5.6 Employees whose salary is higher than all their direct reports
SELECT e.ename AS manager, e.sal AS mgr_salary
FROM emp e
WHERE e.empno IN (SELECT DISTINCT mgr FROM emp WHERE mgr IS NOT NULL)
  AND e.sal > ALL (
      SELECT sal FROM emp WHERE mgr = e.empno
  )
ORDER BY e.sal DESC;

-- ============================================================================
-- SECTION 6: Hierarchical Data with DML
-- ============================================================================

-- 6.1 Move an employee to a new manager
UPDATE emp
SET mgr = (SELECT empno FROM emp WHERE ename = 'BLAKE')
WHERE ename = 'FORD';

-- 6.2 Delete an employee and reassign their reports
UPDATE emp
SET mgr = (SELECT mgr FROM emp WHERE ename = 'JONES')
WHERE mgr = (SELECT empno FROM emp WHERE ename = 'JONES');

DELETE FROM emp WHERE ename = 'JONES';

-- 6.3 Validate no cycles in hierarchy
-- Create a temp table to check for cycles
CREATE TABLE emp_cycle_check AS
SELECT empno, ename, mgr
FROM emp;

-- Use CONNECT_BY_ISCYCLE to detect cycles
SELECT ename, LEVEL, CONNECT_BY_ISCYCLE
FROM emp
START WITH mgr IS NULL
CONNECT BY NOCYCLE PRIOR empno = mgr
WHERE CONNECT_BY_ISCYCLE = 1;

-- ============================================================================
-- END OF HIERARCHICAL QUERIES
-- ============================================================================
