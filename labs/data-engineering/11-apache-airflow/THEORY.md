# Apache Airflow Theory

## DAG Structure
Nodes = Tasks (operator instances). Edges = Dependencies (upstream -> downstream). Direction = data/control flow. Acyclic = no cycles (prevents infinite processing). Scheduling via schedule_interval, start_date, catchup.

## Execution Models
SequentialExecutor: one task at a time (dev). LocalExecutor: parallel tasks on one machine (small production). CeleryExecutor: distributed across workers (production). K8sExecutor: each task in container (cloud-native). DebugExecutor: single process (debugging).

## Sensors
ExternalTaskSensor: wait for upstream DAG. S3KeySensor: wait for file in S3. SqlSensor: check SQL query results. TimeSensor: wait until specific time. Sensors can be in poke (polling) or reschedule (deferred) mode.

## XComs
Key-value store for task communication. Push: ti.xcom_push(key, value). Pull: ti.xcom_pull(task_ids, key). Max size 48KB (metadata only). For large data, store reference (S3 path, table name).
