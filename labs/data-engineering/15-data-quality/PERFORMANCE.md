# Performance Optimization for Data Quality Engineering

## Sampling for Large Datasets
Use sample(0.01) for quick checks; full scan only for critical paths. Approximate distinct counts for uniqueness checks.

## Incremental Checks
Only check new/changed data since last validation. Use watermark or batch_id filters to limit scan scope.

## Parallel Rule Execution
Run independent expectations in parallel. Use Spark for distributed quality checks.

## Monitoring Overhead
Minimize performance impact: async quality checks, tiered validation (fast checks first, heavy checks async)
