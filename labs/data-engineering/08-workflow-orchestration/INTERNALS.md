# Orchestration Internals

## Scheduler Loop
1. Parse DAG files (every 30s)
2. Create DAG runs (schedule, catchup)
3. Create Task Instances
4. Queue tasks to Executor
5. Process results
6. Evaluate next dependencies

## Task Lifecycle
```
none -> scheduled -> queued -> running -> success/failed/up_for_retry
```
