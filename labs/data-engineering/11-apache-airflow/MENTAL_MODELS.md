# Mental Models for Apache Airflow

## 1. The Recipe Book
DAG is a recipe. Tasks = steps. Dependencies = order of steps. Sensors = waiting for ingredients to arrive before proceeding.

## 2. The Factory Floor
Tasks are machines, scheduler is the foreman, executor is the power source, workers are operators. Materials (data) flow between machines.

## 3. The Project Plan
A DAG is a Gantt chart of your data pipeline. Each task has a duration, dependencies, and resource requirements. The scheduler executes the plan.
