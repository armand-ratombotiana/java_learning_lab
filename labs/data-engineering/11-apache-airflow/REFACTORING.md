# Refactoring Apache Airflow Pipelines

## Traditional to TaskFlow API
Before:
```python
with DAG('etl') as dag:
    op1 = PythonOperator(task_id='extract', python_callable=extract, dag=dag)
```
After:
```python
@dag(schedule='0 6 * * *')
def etl():
    @task
    def extract(): return {'data': '...'}
```

## XComs to Object Store
Before: Push large DataFrames via XCom
After: Save to S3, pass S3 key via XCom
