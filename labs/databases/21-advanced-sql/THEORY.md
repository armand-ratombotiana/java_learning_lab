# Theoretical Foundations of Advanced SQL

## Relational Algebra and Window Functions
Window functions extend relational algebra with the concept of an ordered partition. The OVER clause defines the windows frame. The theoretical model: for each row, compute a function over a subset of rows related to the current row.

## ROW_NUMBER Theory
ROW_NUMBER() assigns a unique sequential integer within a partition. The ranking has no ties — each row gets a unique number even if ORDER BY values are equal. The theoretical model resembles a sequentially numbered set.

## RANK vs DENSE_RANK Theory
RANK gives the same value to tied rows but leaves gaps (1,1,3,4...). DENSE_RANK gives the same value to tied rows with no gaps (1,1,2,3...). This reflects the distinction between Olympic-style ranking (RANK) and sequential position (DENSE_RANK).

## NTILE Theory
NTILE(n) divides an ordered partition into n buckets as evenly as possible. The theoretical model: if there are M rows and N buckets, the first M MOD N buckets get CEIL(M/N) rows, remaining get FLOOR(M/N) rows.

## Recursive CTE Theory
A recursive CTE is a form of structural recursion. It computes the transitive closure of a relation. The anchor member is the base of the recursion; the recursive member applies a transformation function F. Formally: let R0 = anchor, Ri = F(R_{i-1}), result = union of all Ri.

## PIVOT Theory
PIVOT implements a relational division operation. It takes a normalized relation and produces a cross-tabulation. Mathematically, it converts the set of {row_key, pivot_column, value} triples into {row_key, col1, col2, ...} where colN is the aggregated value for that pivot column value.

## Data Skew and Optimizer Theory
Data skew affects cardinality estimates. The optimizer uses histograms (frequency, height-balanced, hybrid, top-N) to model column value distributions. Without histograms, the optimizer assumes uniform distribution, leading to poor cardinality estimates for skewed data.

## Partition Pruning Theory
Partition pruning transforms a query into one that only accesses relevant partitions. The optimizer analyzes WHERE clauses to eliminate partitions. For range partitioning, partition elimination is based on value ranges. For list partitioning, it is based on IN-lists and equality predicates.

## Join Order and Cost
Join order optimization is NP-complete in the general case. The optimizer uses dynamic programming for up to ~12 tables, then switches to greedy algorithms. The cost is based on: (cardinality of input streams) x (cost per row operation) + (I/O cost).

## Cardinality Estimation
Cardinality estimation uses:
- Column statistics (NDV, nulls, min/max, histograms)
- Join selectivity formulas
- Dynamic sampling for complex queries
- Adaptive statistics for common operations

## SQL Plan Management Theory
SPM captures SQL execution plans and evolves them over time. A SQL handle maps a SQL ID to a set of accepted plans. The optimizer uses only plans in the SPM baseline. Plan evolution validates new plans by comparing performance metrics.

## Adaptive Execution Theory
Adaptive plans allow the optimizer to postpone decisions until execution time. Join methods can switch at runtime based on actual cardinalities. Bitmap pruning can decide at runtime which partition to access. This closes the gap between optimizer estimates and actual execution characteristics.

## Materialized View and Query Rewrite
Materialized views pre-compute expensive joins and aggregations. Query rewrite transparently substitutes a query against the base tables with a query against the materialized view. The rewrite engine checks:
- Compatibility (view contains all necessary columns)
- Data freshness (freshness relative to query requirements)
- Cost benefit (estimated benefit of using the MV vs base tables)