# Internals: Query Optimization

## Internal Architecture

### Query Plan Analyzer Internals
Parses EXPLAIN output to analyze query execution strategies and identify optimization opportunities.

**Parsing:**
- Regex-based extraction of cost, row estimates, scan types
- Pattern matching for table scans vs index scans
- Warning generation for common anti-patterns

### Index Advisor Internals
Analyzes table schemas and query patterns to recommend optimal indexes.

**Selectivity Calculation:**
```java
selectivity = distinctValues / tableRows
isHighSelectivity = selectivity > 0.1
```

**Recommendation Logic:**
- High selectivity + unindexed -> STRONG recommendation
- Low cardinality (<100 distinct) -> Questionable value
- Already indexed -> Skip

### Query Execution Flow
1. SQL parsing and normalization
2. Query plan generation by optimizer
3. Plan analysis for optimization opportunities
4. Index recommendation based on table stats
5. EXPLAIN ANALYZE for actual vs estimated costs

### Cost-Based Optimization
The optimizer evaluates multiple query plans and selects the one with lowest estimated cost:
```java
totalCost = I/O cost + CPU cost + network cost
```

### Materialized View Management
Materialized views pre-compute and store query results for fast access:
- Refresh strategies: ON COMMIT, ON DEMAND, SCHEDULED
- Incremental vs full refresh
- Storage overhead vs query performance trade-off

### Monitoring
- Query execution time (P50/P99/P999)
- Index usage statistics
- Table scan frequency
- Cache hit ratio
- Plan cache effectiveness
