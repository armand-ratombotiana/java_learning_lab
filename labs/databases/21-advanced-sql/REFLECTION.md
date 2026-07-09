# Reflection: Advanced SQL

## Key Takeaways
- Window functions are the most powerful new SQL feature since GROUP BY — they allow analytical queries without self-joins or subqueries
- Recursive CTEs make SQL Turing-complete for graph and hierarchical data traversal
- PIVOT/UNPIVOT, MERGE, and MODEL clause eliminate application-level data transformations
- Understanding the optimizer is essential for production database performance
- SQL Plan Management provides insurance against plan regression

## When to Use Each Feature
| Feature | Best Use | Avoid When |
|---------|----------|------------|
| Window Functions | Analytical queries, pagination, ranking | Simple GROUP BY aggregations |
| Recursive CTEs | Hierarchical, graph, BFS/DFS | Flat data with known depth |
| PIVOT | Report generation, cross-tabulation | High-cardinality pivot columns |
| CONNECT BY | Oracle-specific hierarchical queries | Need cross-database portability |
| MERGE | UPSERT, ETL synchronization | Most rows need pure INSERT |
| MODEL | What-if, inter-row calculations | Simple aggregations suffice |
| MATCH_RECOGNIZE | Sequence pattern matching | Simple filters |
| SPM | Production plan stability | Development environments |

## What I Wish I Knew Earlier
1. Always check the execution plan before optimizing
2. Bind variables are critical for scalability (avoid literal SQL)
3. Statistics refresh after bulk loads prevents optimizer mistakes
4. Partitioning is for manageability AND performance
5. Bitmap indexes lock rows — use in OLTP with caution
6. SQL profiles can fix queries without code changes
7. Recursive CTE depth should be bounded with a MAXRECURSION hint or CYCLE clause

## Common Pitfalls to Always Check
- Are statistics up to date?
- Is the execution plan the one I expect?
- Are there any type conversions causing index suppression?
- Is partition pruning happening?
- Are there proper indexes for all join and filter columns?
- Is the SQL plan baseline enabled?
- Are adaptive plans behaving as expected?

## Assessment Questions
1. What is the difference between RANGE and ROWS window frames?
2. How does MATCH_RECOGNIZE differ from standard SQL window functions?
3. When would MERGE not be the best choice for upsert?
4. What information does the optimizer need for accurate cardinality estimation?
5. How does adaptive plan execution benefit long-running queries?

## Next Steps
After mastering advanced SQL, proceed to:
- PL/SQL Foundations (Lab 22) for stored procedure programming
- PL/SQL Advanced (Lab 23) for performance and package management
- Oracle APEX Academy for low-code application development