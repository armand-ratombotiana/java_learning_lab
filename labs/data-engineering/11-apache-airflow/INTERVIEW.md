# Interview Questions: Apache Airflow

### Core
**Q**: How does Airflow handle task retries?
**A**: Configure retries and retry_delay on operators; supports exponential backoff; task marked as up_for_retry

### DAG Design
**Q**: What is the difference between catchup and backfill?
**A**: catchup: auto-runs missed intervals on scheduler start; backfill: explicit historical run via CLI

### Dependencies
**Q**: How to handle dependencies between different DAGs?
**A**: ExternalTaskSensor (wait for upstream DAG), TriggerDagRunOperator (trigger downstream), or Dataset-driven scheduling

### Production
**Q**: How do you scale Airflow for production?
**A**: CeleryExecutor with Redis/RabbitMQ broker; PostgreSQL backend; multiple workers; separate webserver and scheduler
