# Refactoring Orchestration

## Before: Monolithic DAG
```python
# 500-line DAG with everything
dag = DAG('everything')
task1 = ... task2 = ... # 50 more tasks
```

## After: Modular DAGs
Use SubDagOperators or separate DAG files per domain.

## Before: Duplicated Code
Same validation in 5 DAGs.

## After: Shared Plugins
```python
# airflow/plugins/custom_operators.py
class QualityCheckOperator(BaseOperator):
    def execute(self, context):
        # Shared validation logic
        pass
```
