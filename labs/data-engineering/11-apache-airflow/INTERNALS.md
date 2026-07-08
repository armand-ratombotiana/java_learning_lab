# Apache Airflow Internals

## Scheduler Loop
1. Parse DAG files every 30s. 2. Create DAGRuns for schedules/triggers. 3. Create TaskInstances for ready tasks. 4. Queue to executor. 5. Executor distributes to workers. 6. Workers execute and report. 7. Scheduler evaluates next downstream tasks.

## Task Lifecycle
none -> scheduled -> queued -> running -> success/failed/skipped/up_for_retry. Failed tasks with retries go back to scheduled state. SLA misses recorded but don't stop execution.

## Metadata Database
Key tables: dag (definitions), dag_run (executions), task_instance (task records), xcom (communication), log (execution logs), sla_miss (breach records). PostgreSQL or MySQL are production backends.

## DAG Serialization
DAGs serialized to DB for Webserver display (no DAG file parsing needed). File parsing happens only in Scheduler. This enables independent scaling of Webserver.
