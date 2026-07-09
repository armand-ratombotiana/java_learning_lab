# Visual Guide to Advanced SQL

## Window Function Data Flow
```
Result Set (ordered by PARTITION BY, ORDER BY)
+------+--------+---------+
| dept |  name  | salary  |
+------+--------+---------+
| IT   | Alice  |  5000   |  ← Window: {Alice}
| IT   | Bob    |  4500   |  ← Window: {Alice, Bob}
| IT   | Carol  |  4000   |  ← Window: {Alice, Bob, Carol}
| HR   | David  |  5500   |  ← Window: {David} (new partition)
| HR   | Eve    |  4800   |  ← Window: {David, Eve}
+------+--------+---------+

ROW_NUMBER() OVER (PARTITION BY dept ORDER BY salary DESC)
IT:  Alice=1, Bob=2, Carol=3
HR:  David=1, Eve=2
```

## Recursive CTE Execution Flow
```
ANCHOR: SELECT root rows
   ↓
RECURSIVE PASS 1: join base with anchor
   ↓  Stores results as "previous"
RECURSIVE PASS 2: join base with previous pass
   ↓
RECURSIVE PASS 3: join base with pass 2
   ↓  (no more rows)
   ↓
UNION ALL all passes
   ↓
FINAL SELECT
```

## PIVOT Visualization
```
Source (normalized):
Dept | Job      | Salary
IT   | IT_PROG  | 5000
IT   | IT_PROG  | 4500
IT   | SA_MAN   | 6000
HR   | IT_PROG  | 4800
HR   | FI_ACCOUNT| 3500

After PIVOT (SUM(salary) FOR job_id IN (...)):
Dept | IT_PROG | SA_MAN | FI_ACCOUNT
IT   | 9500    | 6000   | NULL
HR   | 4800    | NULL   | 3500
```

## MERGE Flow
```
SOURCE                  TARGET
+---+------+     +---+------+
|101| Alice|     |101| Bob  |  → UPDATE: salary=5000
|102| Carol|     |103| Eve  |  → INSERT: (102, Carol, 4000)
+---+------+     +---+------+
```
MERGE matches on emp_id=101 (UPDATE), emp_id=102 not found (INSERT), emp_id=103 not in source (no action unless DELETE clause).

## Execution Plan Tree
```
SELECT STATEMENT
  ↓
HASH JOIN
 ↙        ↘
TABLE      TABLE
ACCESS     ACCESS
(FULL)     (BY INDEX ROWID)
  ↓         ↓
EMPLOYEES  INDEX
           (RANGE SCAN)
             ↓
           DEPT_ID_IDX
```

## B-tree vs Bitmap Index
```
B-tree:
[Root] → [Branch] → [Leaf: (10, row1), (10, row2), (20, row3)]
B-tree is tall, narrow, good for high cardinality.

Bitmap:
DEPT_IT: [1,1,0,0,0,1,0]  (rows 1,2,6 are IT)
DEPT_HR: [0,0,1,1,1,0,0]
Bitmap is flat, wide, good for low cardinality.
```

## Partition Pruning
```
Query: SELECT * FROM sales WHERE sale_date = DATE '2024-03-15'
                                                          ↓
Partition Map: p2024Q1(Jan-Mar) | p2024Q2(Apr-Jun) | p2024Q3 | p2024Q4
                   ↑ ACCESS         ↑ SKIP            ↑ SKIP    ↑ SKIP
```

## MODEL Clause Grid
```
PARTITION region='West'
          | Jan | Feb | Mar |
  Sales   | 100 | 150 | 200 |
  CumSales| 100 | 250 | 450 |  ← Rule: cum[CV] = sales[CV] + NVL(cum[CV-1],0)
```

## SQL Plan Baseline Flow
```
SQL Statement
  ↓
Check DBA_SQL_PLAN_BASELINES for exact (or similar) SQL
  ↓
If found: Use only accepted plans
  ↓
If not found: Generate new plan → add as non-accepted
  ↓
New plan is evolved: run and compare performance
  ↓
If better or same: mark as accepted
  ↓
If worse: keep as non-accepted (not used)
```