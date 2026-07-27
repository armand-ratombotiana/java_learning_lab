# Mock Interview: Data Quality (09-data-quality)

## Scenario: Build a data quality framework
Your company's dashboards have had 3 data quality incidents in the past month (duplicate orders, missing revenue data, stale inventory). The CEO demands a data quality framework.

### Time: 45 minutes | Difficulty: Medium

---

## Part 1: Quality Dimensions (10 min)

**Six dimensions of data quality:**

| Dimension | Definition | Metrics | Example |
|-----------|------------|---------|---------|
| **Freshness** | How up-to-date is the data? | Time since last update, max(event_timestamp) | SLA: data < 4 hours old |
| **Completeness** | Are all records present? | Row count vs expected, % NULL in critical columns | Orders table: expected 500K rows/day ±10% |
| **Accuracy** | Does data reflect reality? | Reconciliation with source, business rule validation | Revenue totals match payment system |
| **Consistency** | Is data consistent across systems? | Cross-table referential integrity, format consistency | Customer IDs match across fact and dim tables |
| **Uniqueness** | Are there duplicates? | Duplicate rate on natural keys | Order IDs should be unique |
| **Validity** | Does data conform to schema? | Type validation, enum values, range checks | Status must be 'active'/'inactive'/'closed' |

**Business-level SLAs:**
```json
{
  "table": "fact_orders",
  "freshness_sla_hours": 4,
  "completeness": {
    "min_row_count": 450000,
    "max_row_count_deviation_pct": 10,
    "critical_columns": ["order_id", "revenue", "customer_id"],
    "null_threshold_pct": 1
  },
  "accuracy": {
    "reconciliation_query": "SELECT SUM(revenue) FROM fact_orders vs payment_system"
  },
  "uniqueness": {
    "duplicate_threshold_pct": 0.01
  },
  "schedule": "daily at 8 AM",
  "steward": "data-engineering@company.com"
}
```

---

## Part 2: Monitoring Architecture (15 min)

**Data quality monitoring pipeline:**

```
Data Sources (batch/streaming)
    │
    ├── Data Quality Runner (dbt test / Great Expectations / Deequ)
    │     ├── Run after each pipeline stage (bronze, silver, gold)
    │     ├── Configurable checks per table
    │     └── Output: pass/fail, metrics values
    │
    ├── Metrics Store (Postgres / Snowflake / InfluxDB)
    │     ├── Timeseries of all metrics
    │     ├── dbt run_results + test_results
    │     └── Custom quality metrics
    │
    ├── Anomaly Detection Service
    │     ├── Detect row count drops > 2 standard deviations
    │     ├── Detect freshness lag increase
    │     └── Detect schema drift (new/dropped columns)
    │
    ├── Alerting & Incident Management
    │     ├── PagerDuty (P1/P2) - pipeline blocking
    │     ├── Slack (P3) - non-blocking warnings
    │     └── Jira (P4) - tracked improvements
    │
    ├── Data Quality Dashboard (Grafana / Metabase)
    │     ├── DQ score per table (0-100)
    │     ├── Trend over 30/90 days
    │     └── Drill-down to specific checks
    │
    └── Data Quality Report (Weekly / Monthly)
        ├── Executive summary: % of tables passing all checks
        ├── Top 5 quality issues
        └── Improvement actions
```

**dbt test implementation:**
```yaml
# schema.yml
version: 2

models:
  - name: fact_orders
    description: "Order fact table"
    columns:
      - name: order_id
        tests:
          - unique
          - not_null
      - name: revenue
        tests:
          - not_null
          - dbt_utils.accepted_range:
              min_value: 0
              max_value: 100000
      - name: order_date
        tests:
          - not_null
          - dbt_utils.expression_is_true:
              expression: ">= '2020-01-01'"
    tests:
      - dbt_utils.expression_is_true:
          expression: "COUNT(*) > 0"
      - dbt_utils.recency:
          datepart: hour
          field: order_timestamp
          interval: 6
      - dbt_utils.equal_rowcount:
          compare_model: ref('stg_orders')
```

---

## Part 3: Alerting & Remediation (10 min)

**Alert severity matrix:**

| Severity | Example | Response | Action |
|----------|---------|----------|--------|
| P1 | Revenue $0 for 30 min, table empty after pipeline | PagerDuty phone call, 15 min response | Stop downstream, investigate immediately |
| P2 | Row count 30% below expected, freshness > 6 hours | Slack alert + PagerDuty push, 30 min response | Block pipeline, analyze cause |
| P3 | Duplicate rate > 0.1%, NULL ratio > 5% | Slack channel notification, 4 hour response | Auto-fix if possible, log otherwise |
| P4 | Schema drift detected (new column), minor metric deviation | Jira ticket, next sprint | Document, update checks |

**Auto-remediation strategies:**
```python
# Auto-remediation runbook
remdiation_rules = {
    "duplicate_records": {
        "detect": "Row count > 2 stddev above average, dedup check fails",
        "auto_fix": "Run dedup SQL: DELETE using ROW_NUMBER() OVER (PARTITION BY id ORDER BY ts DESC)",
        "notify": "Flag as P3, notify steward",
        "rollback": "Keep original data in backup table"
    },
    "schema_drift": {
        "detect": "ALTER TABLE target ... ADD COLUMN detected in source",
        "auto_fix": "Auto-add nullable column to target, log change",
        "notify": "Create Jira for steward to review",
        "rollback": "Revert column addition if downstream fails"
    },
    "pipeline_failure": {
        "detect": "Task fails > 3 retries",
        "auto_fix": "Trigger backfill for failed partition",
        "notify": "PagerDuty P1",
        "rollback": "Dual run: old process + new process"
    }
}
```

---

## Part 4: Governance & Trends (10 min)

**Data SLAs governance framework:**

| Tier | Description | Freshness | Completeness | Support | Example |
|------|-------------|-----------|-------------|---------|---------|
| Critical | Executive dashboards, financial reporting | < 2 hours | 99.99% | 24/7 | fact_orders, dim_customer |
| Important | Operational dashboards, ML features | < 6 hours | 99.9% | Business hours | fact_sessions, fact_inventory |
| Standard | Ad-hoc analytics, long-term trends | < 24 hours | 99% | Next business day | historical_users, raw_logs |
| Best-effort | Experimental, research | No SLA | 95% | Best effort | exploratory_datasets |

**Tracking data quality trends:**
```sql
-- Data quality score over time
SELECT measurement_date,
  ROUND(AVG(CASE WHEN status = 'pass' THEN 100.0 ELSE 0.0 END), 2) AS avg_score
FROM dbt_test_results
WHERE measurement_date >= DATEADD('month', -3, CURRENT_DATE)
  AND table_schema = 'analytics'
GROUP BY measurement_date
ORDER BY measurement_date;
```

**Weekly quality report:**
1. Overall DQ score: 94% (target: 97%)
2. Top 3 improvements from last week: inventory accuracy up 5%, no P1 incidents
3. Top 3 issues: orders table NULL rate (8%), product dimension duplicates (0.5%), freshness miss on sessions (2h lag)
4. Actions: fix orders transformation, add dedup check for products, optimize sessions pipeline

---

## Follow-up Questions

**Streaming vs batch quality:**
| Aspect | Batch | Streaming |
|--------|-------|-----------|
| Check timing | After pipeline run | Continuous |
| Metrics | Row count, schema, freshness | Message rate, schema, latency |
| Alerting | Slack/Jira after window | PagerDuty immediate |
| Remediation | Backfill or re-run | Skip bad messages, DLQ |
| Sliding window check | N/A | Row count over tumbling window (e.g., 5 min) |

**Great Expectations suite example:**
```python
import great_expectations as gx

context = gx.get_context()
datasource = context.sources.add_snowflake_connection("snowflake")
batch_request = datasource.get_asset("fact_orders").build_batch_request()

expectations = [
    gx.expectations.ExpectColumnValuesToBeUnique(column="order_id"),
    gx.expectations.ExpectColumnValuesToNotBeNull(column="revenue"),
    gx.expectations.ExpectColumnMeanToBeBetween(column="revenue", min_value=10, max_value=500),
    gx.expectations.ExpectTableRowCountToBeBetween(min_value=400000, max_value=600000),
    gx.expectations.ExpectColumnValueLengthsToBeBetween(column="order_id", min_value=1, max_value=36),
]

checkpoint = context.add_or_update_checkpoint(
    name="orders_quality",
    validations=[{"expectation_suite_name": "orders_suite"}],
)
checkpoint.run()
```

**Data quality culture:**
- Quality metrics in every pipeline (not optional)
- Blameless post-mortems for quality incidents
- Quarterly data quality review with stakeholders
- Quality score as part of team performance metrics

