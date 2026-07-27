# Mock Interview: Data Observability (18-data-observability)

## Scenario: Implement data observability
Your company has frequent data incidents (3 P1s last month: stale dashboards, duplicate orders, missing revenue). No systematic observability exists.

### Time: 45 minutes | Difficulty: Medium

---

## Part 1: Observability Pillars & Metrics (15 min)

**The 5 pillars of data observability:**

| Pillar | Description | Metrics | Example Alert |
|--------|-------------|---------|---------------|
| **Freshness** | Is data up-to-date? | Time since last row was added/updated | "orders table not updated in 6 hours" |
| **Volume** | Is data quantity expected? | Row count, table size trend | "orders table row count dropped 50%" |
| **Schema** | Is data structure correct? | Column count, data types, null rates | "New column 'user_agent' added to customers" |
| **Quality** | Is data accurate? | Duplicate rates, referential integrity, business rules | "12% duplicate order_id found" |
| **Lineage** | What's the data pipeline? | End-to-end dependency graph | "sales_dashboard depends on stale orders table" |

**Metrics collection system:**
```python
from datetime import datetime, timedelta
import snowflake.connector
import boto3
import json
from datadog import statsd

class DataObservabilityCollector:
    def __init__(self):
        self.snowflake = snowflake.connector.connect(...)

    def collect_freshness(self):
        """Collect freshness for all critical tables"""
        query = """
            SELECT TABLE_CATALOG || '.' || TABLE_SCHEMA || '.' || TABLE_NAME AS table_name,
                   MAX(LAST_ALTERED) AS last_altered,
                   DATEDIFF('second', MAX(LAST_ALTERED), CURRENT_TIMESTAMP()) AS freshness_seconds
            FROM INFORMATION_SCHEMA.TABLES
            WHERE TABLE_SCHEMA IN ('ANALYTICS', 'CURATED')
            GROUP BY TABLE_NAME
        """
        results = self.snowflake.execute(query)
        for row in results:
            # Emit metric to Datadog/Prometheus
            statsd.gauge(f"observability.freshness.{row.table_name}", row.freshness_seconds)
            # Check SLA
            if row.freshness_seconds > 4 * 3600:  # 4 hours
                self.alert(f"Freshness SLA miss: {row.table_name}", "P2")

    def collect_volume(self):
        """Track daily row count per table"""
        today = datetime.now().date()
        yesterday = today - timedelta(days=1)

        query = f"""
            SELECT TABLE_NAME, ROW_COUNT
            FROM {self.database}.INFORMATION_SCHEMA.TABLES
        """
        results = self.snowflake.execute(query)
        for row in results:
            statsd.gauge(f"observability.volume.{row.TABLE_NAME}", row.ROW_COUNT)
            # Store in metrics DB for trend analysis
            self.store_metric("volume", row.TABLE_NAME, today, row.ROW_COUNT)

    def detect_anomalies(self, table_name):
        """Detect volume anomalies using 7-day rolling average"""
        recent_volumes = self.get_recent_volumes(table_name, days=7)
        avg_volume = sum(recent_volumes) / len(recent_volumes)
        current_volume = self.get_current_volume(table_name)

        deviation = abs(current_volume - avg_volume) / avg_volume
        if deviation > 0.3:  # 30% deviation
            self.alert(
                f"Volume anomaly: {table_name} {deviation*100:.0f}% "
                f"from 7-day average ({current_volume} vs avg {avg_volume:.0f})",
                "P1" if deviation > 0.5 else "P2"
            )
```

---

## Part 2: Anomaly Detection (10 min)

**Multi-method anomaly detection:**
```python
import numpy as np
from scipy import stats
from sklearn.ensemble import IsolationForest

class AnomalyDetector:
    def __init__(self):
        self.models = {}

    def detect_row_count_anomaly(self, table_name, current_count):
        """Statistical methods for row count"""

        # Method 1: Z-score (for normal distribution)
        history = self.get_historical_counts(table_name, days=30)
        z_score = (current_count - np.mean(history)) / np.std(history)
        if abs(z_score) > 3:
            return True, f"Z-score anomaly: {z_score:.2f}"

        # Method 2: IQR (for non-normal distribution)
        q1, q3 = np.percentile(history, [25, 75])
        iqr = q3 - q1
        if current_count < q1 - 1.5 * iqr or current_count > q3 + 1.5 * iqr:
            return True, f"IQR anomaly: count {current_count} outside [{q1-1.5*iqr}, {q3+1.5*iqr}]"

        # Method 3: Day-of-week adjustment
        dow_adjustments = self.compute_dow_adjustments(table_name)
        expected = current_count * dow_adjustments.get(datetime.now().weekday(), 1)
        if abs(current_count - expected) / expected > 0.3:
            return True, f"Day-of-week adjustment anomaly"

        return False, "Normal"

    def detect_schema_drift(self, table_name, actual_schema):
        """Detect schema changes"""
        expected_schema = self.get_expected_schema(table_name)

        new_columns = set(actual_schema.keys()) - set(expected_schema.keys())
        missing_columns = set(expected_schema.keys()) - set(actual_schema.keys())
        changed_types = {
            col for col in actual_schema
            if col in expected_schema and actual_schema[col] != expected_schema[col]
        }

        alerts = []
        if new_columns:
            alerts.append(f"New columns detected: {new_columns}")
        if missing_columns:
            alerts.append(f"Columns missing: {missing_columns}")
        if changed_types:
            alerts.append(f"Type changes: {changed_types}")

        return alerts
```

---

## Part 3: Incident Response (10 min)

**Incident response workflow:**

```
Anomaly Detected
    │
    ├── Auto-classify severity:
    │   ├── P1: Data critical for business operations
    │   ├── P2: Important but workaround available
    │   ├── P3: Minor, non-blocking
    │   └── P4: Informational
    │
    ├── Notify via:
    │   ├── P1: PagerDuty phone call + Slack
    │   ├── P2: Slack alert + PagerDuty push
    │   ├── P3: Slack channel
    │   └── P4: Jira ticket
    │
    ├── Responder acknowledges (SLA: 15 min for P1)
    │
    ├── Investigate:
    │   ├── Check lineage: upstream dependencies
    │   ├── Check pipeline logs: Airflow, Spark
    │   ├── Check source systems
    │   ├── Compare with data quality metrics
    │   └── Root cause analysis
    │
    ├── Remediate:
    │   ├── Rollback pipeline version
    │   ├── Trigger backfill
    │   ├── Fix source system issue
    │   └── Manual correction if needed
    │
    └── Post-mortem:
        ├── Blameless analysis
        ├── Root cause documented
        ├── Preventive measures added
        └── Monitor for recurrence
```

**Incident severity matrix:**
| Severity | Impact | Response SLA | Communication | Example |
|----------|--------|-------------|---------------|---------|
| P1 | Business-critical decisions blocked | 15 min | PagerDuty + Slack + email | Revenue dashboard shows $0 |
| P2 | Important workflow impacted | 30 min | Slack + email | Orders table 2 hours stale |
| P3 | Minor inconvenience | 4 hours | Slack - team channel | Product dimension duplicate rate > 1% |
| P4 | Informational | Next sprint | Jira ticket | Schema drift detected (new column added) |

---

## Part 4: SLA Tracking & Root Cause (10 min)

**SLA tracking dashboard:**
```sql
-- Data freshness SLA query
SELECT table_name,
  CASE
    WHEN freshness_seconds < 3600 THEN 'healthy'
    WHEN freshness_seconds < 14400 THEN 'warning'
    ELSE 'critical'
  END AS freshness_status,
  freshness_seconds,
  sla_threshold_seconds,
  ROUND(100.0 * AVG(CASE WHEN freshness_seconds < sla_threshold_seconds THEN 1 ELSE 0 END)
        OVER (PARTITION BY table_name ORDER BY measurement_time ROWS 6 PRECEDING), 2) AS sla_compliance_7d
FROM data_observability.freshness_metrics
WHERE measurement_time >= DATEADD('day', -7, CURRENT_TIMESTAMP)
ORDER BY freshness_status, sla_compliance_7d;
```

**Root cause analysis flow:**
```python
def root_cause_analysis(dashboard_name, timestamp):
    """
    Given a dashboard with wrong data, find where the error originated.
    """
    # Step 1: Get dashboard source lineage
    lineage = catalog_api.get_lineage(dashboard_name, direction="upstream")
    # Returns: dashboard → LookML view → dbt model → Snowflake table → source

    # Step 2: Check each node in lineage
    for node in lineage:
        node_health = observability_api.get_node_health(node, timestamp)
        if node_health['status'] != 'healthy':
            return {
                'root_cause': node,
                'evidence': node_health['anomalies'],
                'impacted_downstream': lineage.get_downstream(node),
                'fix': node_health['suggested_fix']
            }

    # Step 3: Check BI tool
    bi_health = bi_tool_api.get_dashboard_health(dashboard_name)
    if bi_health['has_errors']:
        return {
            'root_cause': f"BI Tool: {dashboard_name}",
            'evidence': bi_health['error_log'],
            'fix': bi_health['suggested_fix']
        }

    return {'root_cause': 'unknown', 'action': 'Manual investigation required'}
```

**Data health scorecard:**
```yaml
data_health:
  overall_score: 87/100
  breakdown:
    freshness: 92  # % of tables meeting freshness SLA
    volume: 85     # % of tables with expected volume
    schema: 95     # % of tables without schema drift incidents
    quality: 78    # % of dbt quality tests passing
    lineage: 90    # % of critical datasets with complete lineage
    
  top_issues:
    - table: analytics.fact_orders
      issue: "Row count 23% below 7-day average"
      severity: P2
      duration: "2 hours"
    - table: curated.dim_customer
      issue: "Freshness SLA miss (6.5 hours > 4h SLA)"
      severity: P2
      duration: "1.5 hours"
    - table: analytics.daily_revenue
      issue: "Duplicate records detected (0.5% > 0.01% threshold)"
      severity: P3
      
  trend:
    30_day_trend: "Improving (+5%)"
    7_day_trend: "Stable"
    incidents_reduced: "-40% this quarter"
```

---

## Follow-up Questions

**Monte Carlo vs Sifflet vs Bigeye vs Datadog:**
| Tool | Best For | Key Feature | Pricing |
|------|----------|-------------|---------|
| Monte Carlo | End-to-end data observability | Auto-identifies root cause | Enterprise |
| Sifflet | Data quality + catalog | Rich anomaly detection | Mid-market |
| Bigeye | Large-scale metric monitoring | SQL-based metric definitions | Enterprise |
| Datadog | APM + data observability | Unified with application monitoring | Per host |

**SLAs as code:**
```yaml
# data_sla.yaml - Define SLAs in version-controlled YAML
slas:
  - table: analytics.fact_orders
    freshness: "4 hours"
    volume_deviation: "30% from 7d avg"
    schema_drift: "notify"
    quality: "> 99% pass rate"
    severity: P1
    escalation: "de-oncall@company.com"

  - table: analytics.dim_customer
    freshness: "24 hours"
    volume_deviation: "50%"
    schema_drift: "auto-evolve"
    quality: "> 95% pass rate"
    severity: P2
    escalation: "analytics-steward@company.com"
```

