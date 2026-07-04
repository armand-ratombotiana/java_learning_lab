# Math Foundation

## Scheduling
`Next = schedule_interval.ceil(start_date)`
`Backfill = Historical runs between start_date and now`

## DAG Complexity
`Complexity = O(N + E)` for topological sort
`Critical Path = Longest path (minimum execution time)`

## SLA Metrics
`Success Rate = Succeeded / (Succeeded + Failed)`
`Duration P50/P95/P99 = Percentile(durations)`
