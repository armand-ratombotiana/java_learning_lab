# Lab 01: Interview Questions

## FAANG-Level Questions

### Q1: Design an ML pipeline orchestrator that supports retries, alerting, and dynamic task generation.
**Answer**: Use a DAG-based architecture where each task extends a base `PipelineTask` with `retry_count`, `timeout`, and `alert_channels`. The DAG scheduler performs topological sorting and uses a thread pool executor. Dynamic tasks can be generated using a `TaskFactory` that reads config from YAML or a DB. Alerting integrates with PagerDuty/Slack via webhooks.

### Q2: How would you handle task failures in a long-running ML pipeline?
**Answer**: Implement exponential backoff retries for transient failures (e.g., network timeouts, resource contention). For deterministic failures (e.g., data schema mismatch), fail fast and alert immediately. Use checkpointing at each task boundary so pipelines can resume from the last successful task.

### Q3: Compare Airflow, Prefect, and Dagster for ML orchestration.
**Answer**: Airflow is mature with rich operator ecosystem but has static DAGs and no built-in data lineage. Prefect offers dynamic DAGs, automatic retries, and cloud-native features. Dagster focuses on data assets and lineage, making it strong for data quality. For ML, Prefect's dynamic nature and caching are advantageous.

### Q4: How do you ensure idempotency in ML pipelines?
**Answer**: Use deterministic task IDs based on input hashes, write-once output storage (S3 with versioning), and timestamp-based partitioning. Upsert patterns in feature stores prevent duplicate records. MLflow run IDs ensure experiment runs are idempotent.

## LeetCode / NeetCode References
- **Course Schedule (LeetCode 207)** — Topological sort for DAG dependency resolution
- **Course Schedule II (LeetCode 210)** — Returning the order of tasks
- **Alien Dictionary (LeetCode 269)** — Building a DAG from ordering constraints
- **Task Scheduler (LeetCode 621)** — Task scheduling with cooldown periods
