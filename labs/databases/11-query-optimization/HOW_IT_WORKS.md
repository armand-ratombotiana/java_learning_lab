# How Query Optimization Works

## Database Optimizer Phases
1. **Parsing**: SQL text → parse tree
2. **Rewrite**: View expansion, constant folding, predicate pushdown
3. **Planning**: Generate candidate execution plans
4. **Cost Estimation**: Estimate cost for each plan
5. **Selection**: Choose lowest-cost plan
6. **Execution**: Run the chosen plan

## Cost Estimation
```
Cost = seq_page_cost × pages_scanned
     + random_page_cost × index_pages_visited
     + cpu_tuple_cost × tuples_processed
     + cpu_operator_cost × operator_invocations
```

## Index Selection
The optimizer decides between:
- **Seq Scan**: Full table read (good for >10% of rows)
- **Index Scan**: Index lookup + heap fetch (good for <5% of rows)
- **Bitmap Scan**: Bitmap of matching heap pages (good for 5-10% of rows)
- **Index Only Scan**: All data in index (best for covering indexes)

## Statistics
The optimizer uses table statistics (pg_class, pg_statistic):
- Row count, page count
- Null fraction
- Most common values (MCV)
- Histogram bounds
- Correlation between physical and logical order
