# Query Optimization — Interview Questions

## Beginner
1. What is the difference between `EXPLAIN` and `EXPLAIN ANALYZE`?
2. What is a sequential scan and when does the planner choose one?
3. How does a Hash Join work internally?

## Intermediate
4. Explain the cost parameters `seq_page_cost` and `random_page_cost`. Why does random_page_cost default to 4?
5. What causes a planner to choose a Nested Loop over a Hash Join?
6. How do outdated statistics affect query plans?

## Advanced
7. Describe how a Bitmap Heap Scan combines multiple index scans. When would it be better than a plain Index Scan?
8. What is a "Moiré pattern" in join ordering and how does CBO handle it?
9. How would you debug a query that suddenly becomes slow after a VACUUM?

## System Design
10. Design a system that auto-tunes query performance using plan feedback (e.g., learn from past cardinality mis-estimates).