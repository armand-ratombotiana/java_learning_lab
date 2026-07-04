# Step-by-Step

1. **Install**: pip install apache-airflow
2. **Init**: airflow db init
3. **Create DAG**: Python file in dags/
4. **Start**: airflow webserver + airflow scheduler
5. **Monitor**: http://localhost:8080
6. **Integrate**: SparkSubmitOperator for Java/Spark jobs
