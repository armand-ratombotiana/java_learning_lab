# Debugging Apache Airflow

## DAG Not Appearing
airflow dags list-import-errors shows import errors; validate Python syntax with ast.parse

## Task Failure
airflow tasks test my_dag my_task 2024-01-01 runs single task in isolation; check log files

## Scheduler Issues
airflow jobs check --job-type SchedulerJob verifies scheduler health; check parsing_processes config

## Performance
Config tuning: min_file_process_interval, parsing_processes, dag_dir_list_interval
