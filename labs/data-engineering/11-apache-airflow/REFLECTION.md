# Reflection: Apache Airflow

- Airflow's DAG-as-code model enables version control, testing, and code review of workflows
- Operator ecosystem with 1000+ providers makes it extensible to virtually any external system
- XCom limitations (48KB) enforce good practices — pass references, not data
- Scheduler scaling is the most common production bottleneck
- TaskFlow API is significantly cleaner than traditional DAG construction
