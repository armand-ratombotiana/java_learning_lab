# Step-by-Step: Working with Data Observability

1. Install Soda Core: pip install soda-core-snowflake (or your warehouse)
2. Create configuration: YAML file with connection details
3. Write checks: YAML checks for freshness, volume, schema, custom SQL
4. Run scan: soda scan -d my_datasource checks.yml
5. Review output: check results in terminal output
6. Set up automated scans in Airflow or cron
7. Integrate with Slack for alerts on failed checks
8. Build observability dashboard (Grafana, Superset)
9. Configure anomaly detection with moving averages
10. Set up data profiling pipeline and trend monitoring
