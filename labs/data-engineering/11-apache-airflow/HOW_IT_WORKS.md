# How Apache Airflow Works

1. Scheduler continuously scans DAG files in dags/ directory
2. For each DAG, checks if schedule condition is met
3. Creates DAG Run and Task Instances for ready tasks
4. Queues tasks to configured Executor
5. Executor distributes tasks to workers (local processes or remote)
6. Workers execute task code and report status
7. Scheduler marks downstream tasks as ready when dependencies succeed
8. Web server reads metadata DB for UI display
9. On failure, tasks are retried per configuration
10. Completed runs are retained per configuration
