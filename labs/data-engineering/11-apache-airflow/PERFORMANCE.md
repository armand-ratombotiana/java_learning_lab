# Performance Optimization for Apache Airflow

## Scheduler Tuning
Increase parsing_processes (default 2). Set min_file_process_interval to 30-60s. Adjust dag_dir_list_interval. Use DAG serialization.

## Database Cleanup
Periodically clean task_instance, xcom, and log tables. VACUUM / ANALYZE PostgreSQL. Use separate DB for Airflow.

## Deferrable Operators
Use mode='reschedule' for long-running sensors to free worker slots. Write custom deferrable operators for custom waits.

## Pools and Concurrency
Set pool_slots per task. Configure max_active_tasks and max_active_runs per DAG. Use priority_weight for queue ordering.
