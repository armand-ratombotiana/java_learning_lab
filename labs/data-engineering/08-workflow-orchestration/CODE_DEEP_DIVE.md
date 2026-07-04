# Code Deep Dive: Airflow DAG

## Complete DAG
```python
from airflow import DAG
from airflow.providers.apache.spark.operators.spark_submit import SparkSubmitOperator
from airflow.operators.python import PythonOperator
from datetime import datetime, timedelta

dag = DAG('data_pipeline', schedule_interval='0 2 * * *',
    start_date=datetime(2024, 1, 1), catchup=True)

extract = SparkSubmitOperator(task_id='extract',
    application='/jobs/extract.py', executor_memory='8g', dag=dag)

validate = PythonOperator(task_id='validate',
    python_callable=run_checks, dag=dag)

load = SparkSubmitOperator(task_id='load',
    application='/jobs/load.py', dag=dag)

extract >> validate >> load
```
