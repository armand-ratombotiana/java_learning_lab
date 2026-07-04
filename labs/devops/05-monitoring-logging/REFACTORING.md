# Monitoring & Logging Refactoring

## Before (Unstructured Logging)
```python
# Python app
print(f"User {user_id} logged in at {datetime.now()}")
print(f"Error: {str(e)}")
```

## After (Structured JSON Logging)
```python
import structlog
logger = structlog.get_logger()

logger.info("user.login", user_id=user_id, ip=request.ip)
logger.error("db.connection_failed", error=str(e), timeout=config.DB_TIMEOUT)
```

## Before (Prometheus Config — Hardcoded Targets)
```yaml
scrape_configs:
  - job_name: 'web'
    static_configs:
      - targets: ['web1:8080', 'web2:8080']
```

## After (Service Discovery)
```yaml
scrape_configs:
  - job_name: 'kubernetes-pods'
    kubernetes_sd_configs:
      - role: pod
    relabel_configs:
      - source_labels: [__meta_kubernetes_pod_annotation_prometheus_io_scrape]
        action: keep
        regex: true
```

## Gains
- Structured logs: machine-parseable, searchable in ELK
- Service discovery: auto-scaling targets without config updates
- Label-based filtering: better PromQL queries
- Dynamic and scalable
