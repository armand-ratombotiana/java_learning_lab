# Mock Interview: Workflow Orchestration (08-workflow-orchestration)

## Scenario: Design an orchestration platform for 100+ pipelines
Your data team manages 100+ pipelines across 5 teams. Pipelines are a mix of Airflow, custom scripts, and cloud services. You need a unified orchestration platform.

### Time: 45 minutes | Difficulty: Hard

---

## Part 1: Orchestration Selection (15 min)

**Comparison: Airflow vs Dagster vs Prefect**

| Criteria | Airflow | Dagster | Prefect |
|----------|---------|---------|---------|
| Maturity | Most mature, largest community | Growing rapidly | Moderate |
| DAG definition | Python DAG files | Python with @op/@job decorators | Python with @task/@flow decorators |
| Testing | Difficult (need local executor) | Better (asset isolation, CLI testing) | Good (prefect.testing, flow runner) |
| Monitoring | statsd, logs, Celery Flower | Dagit UI, rich lineage | Prefect UI, webhooks, notifications |
| Scaling | Hard at 1000+ DAGs (scheduler bottleneck) | Better (partitioning, multi-process) | Good (auto-scaling workers) |
| Backfill | Built-in via `backfill` CLI | Built-in via partitions | Built-in via flow runs |
| Learning curve | Medium (complex operator API) | Medium-high (new concepts) | Low (intuitive API) |
| CI/CD | GitSync, custom | dagster-cloud, custom | prefect deploy |
| Best for | Traditional scheduling, large DE teams | Data platform teams, asset lineage | ML pipelines, dynamic workflows |

**Recommendation for this use case:**
- If team prefers classic scheduling and has Airflow experience: **Airflow with KubernetesExecutor**
- If team wants software-defined assets and data lineage: **Dagster**
- If team is small and wants fast iteration: **Prefect**

**Decision factors:**
- Existing investment: any existing Airflow DAGs?
- Team skills: Python experience level
- Scale: 100 DAGs is moderate, any orchestrator handles this
- Monitoring needs: Dagster has best lineage, Airflow has best community monitoring

---

## Part 2: DAG Design Pattern (10 min)

**Reusable ETL DAG template:**
```python
from airflow.decorators import dag, task
from datetime import datetime, timedelta

def create_etl_dag(table_name, source_conn, target_conn, schedule, retention_days=7):
    @dag(
        dag_id=f"etl_{table_name}",
        schedule=schedule,
        start_date=datetime(2024, 1, 1),
        catchup=False,
        default_args={
            "retries": 2,
            "retry_delay": timedelta(minutes=5),
            "retry_exponential_backoff": True,
            "execution_timeout": timedelta(hours=4),
            "sla": timedelta(hours=12),
        },
        tags=["etl", table_name],
        max_active_runs=1,
    )
    def generate_dag():
        @task
        def extract(**context):
            # Extract from source_conn
            pass

        @task
        def validate_staging(data):
            # Validate row count, schema, freshness
            pass

        @task
        def transform(data):
            # Apply transformations
            pass

        @task
        def load(data):
            # Load to target_conn
            pass

        @task
        def quality_check(**context):
            # Data quality verification
            pass

        @task
        def cleanup(**context):
            # Clean staging data older than retention_days
            pass

        @task
        def notify(**context):
            # Send success notification
            pass

        data = extract()
        validated = validate_staging(data)
        transformed = transform(validated)
        loaded = load(transformed)
        qc_result = quality_check(loaded)
        cleanup_success = cleanup(loaded)
        notify()

        # Conditional: if quality check fails → alert, don't mark complete
        from airflow.operators.python import BranchPythonOperator
        def check_quality(qc_result):
            if qc_result["passed"]:
                return "cleanup"
            return "notify_failure"

    return generate_dag()

# Generate DAGs
create_etl_dag("customers", "postgres_db", "snowflake", "@daily")
create_etl_dag("orders", "postgres_db", "snowflake", "@hourly")
create_etl_dag("products", "api_source", "snowflake", "@daily")
```

---

## Part 3: Failure Handling & SLAs (10 min)

**Retry strategy:**
```python
default_args = {
    "retries": 3,
    "retry_delay": timedelta(minutes=5),
    "retry_exponential_backoff": True,  # 5, 25, 125 minutes
    "max_retry_delay": timedelta(hours=1),
    "execution_timeout": timedelta(hours=2),
    "sla": timedelta(hours=6),
}
```

**Alerting hierarchy:**
| Severity | Condition | Action | Response Time |
|----------|-----------|--------|---------------|
| P1 (Critical) | DAG fails > 3 retries, SLA miss > 2 hours | PagerDuty phone call | 15 min |
| P2 (High) | DAG fails, but retry succeeds, SLA miss < 1 hour | Slack alert + PagerDuty push | 30 min |
| P3 (Medium) | Task retry, DATA_QUALITY_FAILURE | Slack channel notification | 4 hours |
| P4 (Low) | Schema drift detected, row count variance > 20% | Jira ticket auto-created | 24 hours |

**SLA tracking:**
```python
# Per-table SLA freshness
@sla_miss_dag
def sla_miss_callback(dag, task_list, blocking_task_list, slas, blocking_tis):
    send_slack_alert(f"SLA miss on {dag.dag_id}: {task_list}")
    create_jira_ticket(f"Data freshness SLA missed: {dag.dag_id}")
```

---

## Part 4: CI/CD & Monitoring (10 min)

**CI/CD pipeline for DAG deployment:**
```
Developer push to feature branch
    │
    ├── Pre-commit hooks (lint, validate Python syntax)
    │
    ├── CI (GitHub Actions):
    │   ├── Run unit tests: pytest tests/
    │   ├── Validate DAGs: airflow dags list-import-errors
    │   ├── Integration test: airflow dags test <dag_id> <execution_date>
    │   └── Schema check: verify tables exist in target
    │
    ├── Staging deploy:
    │   ├── Deploy to staging Airflow
    │   ├── Run DAG on sample data
    │   └── Validate output metrics
    │
    └── Production deploy:
        ├── GitSync (automatic DAG sync from main branch)
        ├── Canary: run on 1 day of data, compare to production
        └── 100% rollout
```

**Monitoring dashboard (Datadog / Grafana):**
```python
# Metrics to track
statsd.gauge(f"etl.{table_name}.row_count", row_count)
statsd.gauge(f"etl.{table_name}.duration_seconds", duration)
statsd.gauge(f"etl.{table_name}.error_count", error_count)
statsd.gauge(f"etl.{table_name}.freshness_seconds", freshness_lag)
```

**Dashboard panels:**
1. **Pipeline Health:** % success rate over 7 days
2. **DAG Duration:** P50, P95, P99 execution time per DAG
3. **SLA compliance:** % of DAGs meeting SLA over 30 days
4. **Error breakdown:** by DAG, by task, by error type
5. **Resource utilization:** executor slots, queue depth, DB connections

---

## Follow-up Questions

**Incremental backfill across 1 year:**
```python
# Dynamic backfill using task mapping
@dag(schedule=None, start_date=datetime(2024, 1, 1), catchup=True)
def backfill_customers():
    @task
    def backfill_day(execution_date):
        # Process single day
        pass

    # Create task for each day in range
    dates = [datetime(2024, d, 1) for d in range(1, 13)]  # 12 months
    backfill_day.expand(execution_date=dates)

backfill_customers_dag = backfill_customers()
```

**KubernetesExecutor vs CeleryExecutor:**
| Aspect | Celery | Kubernetes |
|--------|--------|------------|
| Isolation | Worker process (shared host) | Full pod (container) |
| Scaling | Pre-configured workers | Dynamic pod creation |
| Resource limits | OS-level (cgroups) | Container-level (requests/limits) |
| Security | Shared filesystem? Shared DB? | Namespace isolation |
| Complexity | Manage Redis/RabbitMQ + Celery workers | Manage K8s cluster |
| Best for | Stable workloads, smaller teams | 100+ DAGs, multi-tenant, isolation needs |

**Cross-team data contracts:**
- Define SLA expectations per dataset: freshness, completeness, schema
- Implement as dbt tests or Airflow sensors
- Track via data catalog (Dagster assets or DataHub contracts)
- Automated validation in CI/CD before deploying dependent DAGs

