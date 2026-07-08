# Apache Airflow Architecture

## Production Architecture
```
[ELB] -> [Webserver x2] -> [PostgreSQL Primary + Replica]
                              |
[Scheduler x2 (Active/Standby)]
                              |
[Redis/Sentinel] -> [Celery Workers x N]
```

## Deployment Options
Docker Compose (dev), Helm Chart (K8s), AWS MWAA (managed), Google Cloud Composer (managed)
