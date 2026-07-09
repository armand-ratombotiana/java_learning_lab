# Quiz: Advanced SQL

## Questions (30)

### Part 1: Window Functions (5 questions)
1. Which window function returns a unique sequential integer within a partition, with no ties?
2. How does RANK differ from DENSE_RANK?
3. What does NTILE(4) do when applied to 100 rows?
4. What is the default window frame for ORDER BY in OVER clause?
5. What does LAG(salary, 2, 0) mean in a window function?

### Part 2: Recursive CTEs (3 questions)
6. What is the purpose of the anchor member in a recursive CTE?
7. Why should UNION ALL be preferred over UNION in recursive CTEs?
8. How do you prevent infinite loops in recursive CTEs?

### Part 3: PIVOT/MERGE (3 questions)
9. What happens to null values in the pivot column?
10. What does ORA-30926 mean in a MERGE statement?
11. When would UNPIVOT be useful?

### Part 4: Hierarchical Queries (2 questions)
12. What does PRIOR do in CONNECT BY?
13. How does ORDER SIBLINGS BY differ from regular ORDER BY?

### Part 5: Optimizer (4 questions)
14. What does EXPLAIN PLAN display?
15. What is cardinality estimation and why is it important?
16. What is the difference between cost-based and rule-based optimization?
17. How does the optimizer use histograms?

### Part 6: Partitioning/Indexing (3 questions)
18. What is partition pruning?
19. When would a bitmap index be preferred over a B-tree index?
20. What is a function-based index?

## Answers
1. ROW_NUMBER() — always gives unique sequential numbers
2. RANK skips positions after ties (1,1,3,4); DENSE_RANK has no gaps (1,1,2,3)
3. NTILE(4) divides 100 rows into 4 quartiles of 25 rows each
4. RANGE BETWEEN UNBOUNDED PRECEDING AND CURRENT ROW
5. Returns salary from 2 rows before current row; if null, returns 0
6. The base case — the starting set for the recursion
7. UNION ALL avoids the duplicate elimination overhead of UNION
8. Use the CYCLE clause or limit recursion depth in the recursive member
9. NULL values in pivot column are excluded by default
10. Duplicate rows in source matching same target row
11. When you need to normalize denormalized column data to rows
12. PRIOR specifies the parent side of the parent-child relation
13. ORDER SIBLINGS BY orders within the same level; ORDER BY sorts the whole set
14. The execution plan showing access paths and join methods
15. Estimating number of rows each operation will return; affects join order/method
16. RBO uses hardcoded rules; CBO uses statistics to estimate costs
17. Histograms model data distribution for columns with skewed values
18. The optimizer eliminates irrelevant partitions based on query predicates
19. Data warehousing with low-cardinality columns and read-mostly access
20. Index on an expression/function, used when the function appears in WHERE