# Step-by-Step: Working with Apache Airflow

1. Install Airflow: pip install apache-airflow
2. Initialize database: airflow db init
3. Create admin user: airflow users create --username admin --password admin --role Admin
4. Write DAG: Create Python file in dags/ directory with DAG definition
5. Start services: airflow webserver -p 8080 (UI) and airflow scheduler (task scheduling)
6. Trigger DAG via Web UI or CLI: airflow dags trigger etl_pipeline
7. Monitor execution via Tree, Graph, Gantt, Code, and Task Duration views
8. Integrate Java/Spark: use SparkSubmitOperator with connection config
