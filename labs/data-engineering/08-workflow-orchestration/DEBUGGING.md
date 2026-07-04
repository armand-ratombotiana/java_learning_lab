# Debugging

## DAG Not Appearing
```bash
aiflow dags list-import-errors
python -c "import ast; ast.parse(open('dag.py').read())"
```

## Task Stuck
```bash
aiflow jobs check
celery -A airflow.executors.celery_executor inspect active
```

## Test Single Task
```bash
aiflow tasks test my_dag my_task 2024-01-01
```
