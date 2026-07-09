# Internals of Advanced SQL

## Window Function Internal Execution
Oracle implements window functions in two steps:
1. Sort operation (SORT ORDER BY) that orders rows by PARTITION BY + ORDER BY columns
2. Window buffer operator (WINDOW) that computes function values across the ordered partition

The window buffer maintains a sliding window of rows. For ROWS frames, it tracks row counts. For RANGE frames, it tracks value ranges.

## Recursive CTE Internals
Oracle processes recursive CTEs iteratively:
1. Execute anchor member, materialize result as "current set"
2. Execute recursive member using current set
3. Store results in a new intermediate set
4. If new set is empty, terminate. Else: rename new set as current set, go to 2
5. Union all intermediate results

Optimization: Oracle can use iterative execution (repeated SQL execution) or pipelined execution depending on the query.

## PIVOT Internals
Oracle processes PIVOT as:
1. Identify implicit GROUP BY columns (all non-agg, non-pivot columns)
2. For each pivot column value in the IN clause, create a CASE expression
3. Wrap all CASE expressions with the specified aggregate
4. Execute as a standard GROUP BY query

## MERGE Internals
MERGE performs:
1. Join source and target on the ON condition
2. For MATCHED rows, execute UPDATE (and optional DELETE)
3. For NOT MATCHED rows, execute INSERT
4. Error handling: row-level error logging with LOG ERRORS clause

## CONNECT BY Internals
Oracle uses two variants:
- Top-down CONNECT BY: rows flow from parent to children
- Bottom-up CONNECT BY (PRIOR on the "many" side): rows flow from children to parents

The internal operator builds a hierarchical spool using a stack-based traversal.

## MODEL Clause Internals
The MODEL clause creates:
1. An in-memory multi-dimensional array partitioned by the PARTITION BY columns
2. Rules execute in order (AUTOMATIC ORDER or SEQUENTIAL ORDER)
3. Each rule computes new measure values using array cell references
4. UPDATE rules replace cells, UPSERT rules add new cells

## MATCH_RECOGNIZE Internals
Oracle implements PATTERN matching using:
1. Convert pattern regex to a deterministic finite automaton (DFA)
2. Run the DFA over ordered rows within each partition
3. Define conditions (DEFINE) test row attributes
4. Output one row per match or one row per row with match information

## Materialized View Refresh Internals
- COMPLETE: truncate MV, re-execute query
- FAST: apply materialized view logs (changes only)
- FORCE: try FAST, fall back to COMPLETE

## Query Rewrite Internals
Query rewrite uses:
1. Text match: exact SQL text comparison
2. Partial rewrite: MV contains superset of query columns
3. Aggregate rewrite: MV has GROUP BY that is coarser than query's GROUP BY
4. Join rewrite: MV joins tables that query also joins

## Adaptive Plan Internals
Oracle 12c+ adaptive plans use "sub-plans" — the optimizer generates multiple sub-plans and chooses at runtime. The decision is based on cardinality feedback: the actual number of rows flowing between operations.

## SQL Plan Baseline Internals
SPM stores plan baselines in SYS.SQL$TEXT (SQL text) and SYS.SQL$PLAN (plan data). When new plan is generated:
1. Check if SQL text matches a baseline
2. If no matching plan in baseline, compare cost
3. If similar cost, add as "non-accepted" plan
4. Evolve non-accepted plans by executing them and comparing performance

## SQL Profile Internals
A SQL profile stores additional statistics and optimizer settings for a specific SQL statement. It is stored in DBA_SQL_PROFILES and applied automatically. Profiles can fix cardinality estimates, provide missing statistics, or set optimizer parameters.