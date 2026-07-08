# Apache Airflow

## Overview
Apache Airflow is a platform for programmatically authoring, scheduling, and monitoring workflows using directed acyclic graphs (DAGs). This lab covers DAG design, operators, sensors, the TaskFlow API, executors, and XComs for inter-task communication.

## Key Concepts
- **DAG**: Directed Acyclic Graph defining workflow structure with task dependencies
- **Operators**: Atomic units of work (PythonOperator, BashOperator, SparkSubmitOperator)
- **Sensors**: Operators that wait for external conditions (file arrival, time, query result)
- **TaskFlow API**: Decorator-based DAG creation using @dag and @task
- **Executors**: How tasks run: Sequential, Local, Celery, Kubernetes
- **XComs**: Cross-communication metadata store between tasks (max 48KB)

## Learning Objectives
1. Design DAGs with proper task dependencies and scheduling
2. Use TaskFlow API for clean, decorator-based DAGs
3. Configure different executors for production deployments
4. Use XComs for task communication
5. Implement sensors for event-driven workflows
6. Monitor and troubleshoot DAG execution
