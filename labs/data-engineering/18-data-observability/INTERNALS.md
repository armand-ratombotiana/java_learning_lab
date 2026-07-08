# Data Observability Internals

## Soda Architecture
Soda Core: Python library for executing checks. Soda SQL: command-line for SQL-based validation. Soda Cloud: managed dashboard and alerting. Checks: SQL queries returning measurement values. Metrics: numerical results of checks. Alerts: threshold violations.

## Monte Carlo Architecture
Agent: deploys in customer environment (reads metadata, profiles data). Backend: ML-based anomaly detection models. Dashboard: observability UI with health scores. Teams: integration with PagerDuty, Slack, Jira. Lineage: automatic capture from query logs.

## Profiling Pipeline
1. Extract sample (deterministic or random). 2. Compute statistics (summary + distribution). 3. Compare to historical baseline. 4. Store results in metrics database. 5. Update dashboards. 6. Trigger alerts on anomalies. 7. Retain history for trend analysis.

## Lineage Capture
SQL parsing: extract table/column dependencies from query text. Snowflake/Presto query history: analyze execution logs. ETL tool metadata: import lineage from dbt, Airflow, Spark. Manual annotation: document external ingestion. Graph storage: Neo4j, custom DAG store with indexing for impact analysis.
