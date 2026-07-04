# Architecture

## Production Setup
```
[Webserver] [Scheduler]
     |           |
[PostgreSQL] [Redis/Celery]
              |
         [Workers]
```

## Components
- Webserver: UI
- Scheduler: Triggers DAGs
- Executor: Runs tasks (Celery, K8s)
- Metadata DB: PostgreSQL/MySQL
