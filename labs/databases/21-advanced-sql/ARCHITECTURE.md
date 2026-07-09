# Architecture of Advanced SQL

## Overview
Advanced SQL extends standard SQL with analytical processing capabilities, hierarchical queries, and sophisticated optimization mechanisms. This document describes the architecture of the Oracle SQL engine as it processes advanced SQL constructs.

## SQL Statement Processing Pipeline
1. Parsing: SQL text is parsed, syntax and semantics validated
2. Optimization: Cost-based optimizer generates multiple execution plans
3. Row Source Generation: Optimizer plan is converted to row source tree
4. Execution: Row sources execute, producing result sets

## Window Function Architecture
Windows define a moving frame over the result set. The processing order is:
- FROM/JOIN/WHERE/GROUP BY/HAVING (base result set)
- Window functions evaluate over each partition
- ORDER BY applies after window functions

## Recursive CTE Processing
Recursive CTEs require:
- Anchor member: initial result set
- Recursive member: references the CTE itself
- Termination condition: when recursive member returns no rows

## PIVOT/UNPIVOT Mechanics
PIVOT rotates rows into columns by aggregating grouped data. The architecture:
1. Group by the implicit GROUP BY columns (all non-pivot, non-aggregated columns)
2. Apply aggregation for each pivot column value
3. Output pivoted row with column aliases

## Optimizer Architecture
The Cost-Based Optimizer (CBO) evaluates:
- Table statistics (rows, blocks, average row length)
- Column statistics (distinct values, nulls, histogram)
- Index statistics (B-tree depth, clustering factor, leaf blocks)
- System statistics (I/O throughput, CPU speed)

## Partitioning Architecture
Partitions are logical segments that map to physical segments. The partitioning key determines which partition stores each row. Pruning eliminates irrelevant partitions at query time.

## Execution Plan Structure
Plans are trees of operations:
- TABLE ACCESS (FULL, BY INDEX ROWID, BY USER ROWID)
- INDEX operations (UNIQUE SCAN, RANGE SCAN, FULL SCAN, SKIP SCAN, FAST FULL SCAN)
- JOIN operations (NESTED LOOPS, HASH JOIN, SORT MERGE JOIN, CARTESIAN)
- SORT operations (ORDER BY, GROUP BY, AGGREGATE, UNIQUE)
- VIEW operations for inline views and subqueries

## MODEL Clause Architecture
The MODEL clause creates a multi-dimensional array from the result set and applies rules:
1. PARTITION BY defines independent model partitions
2. DIMENSION BY defines the coordinates for each cell
3. MEASURES defines the cell values
4. RULES specify cell assignments using positional and symbolic references

## Adaptive Execution Plans
Starting from Oracle Database 12c, the optimizer can:
- Defer decisions until runtime
- Use statistics feedback to switch join methods
- Adapt parallel distribution methods
- Collect cardinality feedback for subsequent executions