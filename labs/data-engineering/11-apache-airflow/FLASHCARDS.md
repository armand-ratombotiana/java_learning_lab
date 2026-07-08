# Flashcards: Apache Airflow

## Card 1
**Front**: What is an Airflow DAG?
**Back**: Directed Acyclic Graph defining workflow tasks, their dependencies, and scheduling

## Card 2
**Front**: What is the difference between Operators and Sensors?
**Back**: Operators perform actions; Sensors wait for external conditions to be met

## Card 3
**Front**: What is XCom used for?
**Back**: Cross-task communication for small metadata (<48KB): task IDs, statuses, small results

## Card 4
**Front**: What are the executor types?
**Back**: Sequential (dev), Local (parallel single-node), Celery (distributed), K8s (container-based)

## Card 5
**Front**: What does catchup=True do?
**Back**: Creates DAG runs for all intervals between start_date and now that haven't been executed
