# Interview Questions: Advanced SQL

## Beginner Level
1. What is a window function and how is it different from GROUP BY?
2. Explain the difference between ROW_NUMBER, RANK, and DENSE_RANK.
3. What is a CTE and when would you use it?
4. Explain the MERGE statement in Oracle.
5. What is an execution plan and how do you generate one?

## Intermediate Level
6. How would you find the top-N rows per department using window functions?
7. Explain the difference between ROWS and RANGE in window frame clauses.
8. Write a recursive CTE to find all direct and indirect reports for a given manager.
9. What is the PIVOT operation and when would you use it over CASE expressions?
10. Explain the CONNECT BY clause with PRIOR keyword.

## Advanced Level
11. How does Oracle's cost-based optimizer estimate cardinality?
12. Explain the Oracle MODEL clause and provide a use case.
13. What is MATCH_RECOGNIZE and how does it differ from LEAD/LAG?
14. What are the different types of partitioning and when do you use each?
15. Explain SQL Plan Management and how it prevents regression.

## Expert Level
16. How does the Oracle optimizer choose between nested loops and hash join?
17. What is an adaptive execution plan and how is it enabled?
18. Explain how Oracle handles recursive CTE execution internally.
19. Describe the difference between global and local partitioned indexes.
20. How do you evolve SQL plan baselines?

## Hands-On Questions
21. Write a query to calculate moving average of stock prices for the last 7 days.
22. Write a query using MATCH_RECOGNIZE to detect consecutive profit growth.
23. Tune a query showing FULL TABLE SCAN on a large table.
24. Design partition strategy for a 10B row order table with date-range access.
25. Create a SPM baseline and test plan evolution.

## Oracle-Specific
26. What Oracle-specific SQL features exist?
27. What is the difference between SPM and SQL Profiles?
28. When would you use function-based index vs regular B-tree?
29. Explain Oracle's histogram types and when they are used.
30. How does Oracle handle automatic SQL tuning and what is SQL Tuning Advisor?