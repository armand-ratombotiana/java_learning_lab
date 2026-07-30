# 01 - Query Optimization

## Topics Covered
- Query Plans (EXPLAIN, EXPLAIN ANALYZE)
- Index Analysis (sequential vs index scans, Bitmap Heap Scan)
- Join Algorithms (Nested Loop, Hash Join, Merge Join)
- Cost-Based Optimization (CBO)
- Statistics, selectivity estimation, cardinality estimates

## Goal
Understand how a relational database transforms SQL into an execution plan, how costs are estimated, and how to read plans to diagnose performance issues.

## Exercises

1. Generate an EXPLAIN plan for a multi-table join and identify each node type.
2. Create a query that forces a Nested Loop join and rewrite it to trigger a Hash Join.
3. Analyze a slow query using EXPLAIN (ANALYZE, BUFFERS, FORMAT JSON) and propose index changes.
4. Simulate outdated statistics (disable autovacuum, insert data, run query) and observe plan degradation.