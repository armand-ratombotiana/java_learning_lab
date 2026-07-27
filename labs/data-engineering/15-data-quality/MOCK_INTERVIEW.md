# Mock Interview: Data Quality (15-data-quality)

## Scenario: Implement data quality for ML feature pipelines
Your ML team depends on features computed by your pipelines. Data quality issues have caused model degradation twice this quarter (missing features, drifted distributions).

### Time: 45 minutes | Difficulty: Medium-Hard

---

## Part 1: Feature Validation Strategy (15 min)

**Quality checks for ML features:**

| Check Type | Description | Example | Threshold |
|-----------|-------------|---------|-----------|
| **Completeness** | % of records with feature value | null_rate for 'customer_lifetime_value' | < 5% null |
| **Range** | Feature values within expected bounds | transaction_amount: 0 - 100000 | 99.9% within range |
| **Distribution** | Statistical distribution hasn't shifted | KL divergence from training distribution | < 0.1 |
| **Cardinality** | Categorical feature cardinality stable | device_type has 3 expected values | < 5 new values |
| **Correlation** | Feature correlations with target stable | purchase_probability ~ transaction_count | < 0.2 change |
| **Freshness** | Features computed on time | latest feature_timestamp | < 6 hours lag |
| **Schema** | Feature types and shapes match | embedding dimension is 128 | Exact match |

**Feature validation pipeline:**
```python
from datetime import datetime, timedelta
from scipy.stats import ks_2samp, entropy
import numpy as np

class FeatureQualityChecker:
    def __init__(self, training_stats):
        self.training_stats = training_stats  # Store training distribution stats

    def validate_features(self, df, feature_name):
        checks = {}

        # 1. Completeness check
        null_rate = df[feature_name].isnull().mean()
        checks['completeness'] = null_rate < 0.05

        # 2. Range check
        if np.issubdtype(df[feature_name].dtype, np.number):
            q1, q3 = df[feature_name].quantile([0.01, 0.99])
            checks['range'] = True  # Default pass
            checks['outlier_rate'] = ((df[feature_name] < q1) | (df[feature_name] > q3)).mean()

        # 3. Distribution shift (KS test)
        if feature_name in self.training_stats:
            train_dist = self.training_stats[feature_name]
            current_dist = df[feature_name].dropna().values
            if len(current_dist) > 100 and len(train_dist) > 100:
                ks_stat, p_value = ks_2samp(current_dist, train_dist)
                checks['distribution_shift'] = ks_stat
                checks['distribution_stable'] = ks_stat < 0.1

        # 4. Freshness
        if 'timestamp' in df.columns:
            max_ts = df['timestamp'].max()
            checks['freshness_hours'] = (datetime.now() - max_ts).total_seconds() / 3600
            checks['fresh_ok'] = checks['freshness_hours'] < 6

        return checks
```

---

## Part 2: Pipeline Architecture (10 min)

**Feature pipeline with quality gates:**

```
Feature Pipeline:
Source Data → Feature Computation → Quality Gate → Feature Store

Quality Gate:
1. Schema validation: columns match expected
2. Completeness: null rates within thresholds
3. Distribution: KS test against training distribution
4. Cardinality: categorical values match expected set
5. Correlation: feature-target correlation stable

If quality gate fails:
├── WARN: send alert, still write features (non-critical)
└── BLOCK: stop pipeline, notify ML team (critical features)
```

**Implementation with quality gates:**
```python
from pyspark.sql import SparkSession, DataFrame
from pyspark.sql.functions import col, isnan, when, count, isnull

def compute_feature_pipeline():
    # Step 1: Read source data
    source_df = spark.table("silver.orders")

    # Step 2: Feature computation
    features_df = compute_features(source_df)

    # Step 3: Quality check
    qc_results = run_quality_checks(features_df)
    quality_score = calculate_overall_score(qc_results)

    if quality_score < 0.8:
        # Critical failure - block pipeline
        send_alert(f"Feature quality score {quality_score} below threshold 0.8")
        raise Exception("Pipeline blocked: feature quality check failed")
    elif quality_score < 0.95:
        # Warning - notify but continue
        send_slack_warning(f"Feature quality score {quality_score} below optimal")

    # Step 4: Write to feature store
    features_df.write.format("delta") \
        .mode("append") \
        .save("s3://feature-store/customer_features")

def run_quality_checks(df: DataFrame) -> dict:
    checks = {}

    # Row count check
    row_count = df.count()
    checks['row_count'] = {
        'value': row_count,
        'expected_min': 100000,
        'expected_max': 200000,
        'passed': 100000 <= row_count <= 200000
    }

    # Null rate per column
    null_rates = df.select([
        (count(when(isnull(c) | isnan(c), c)) / count("*")).alias(c)
        for c in df.columns
    ]).collect()[0].asDict()
    checks['null_rates'] = null_rates

    return checks
```

---

## Part 3: Anomaly Detection (10 min)

**Statistical drift detection:**

```python
from datetime import datetime, timedelta
import numpy as np
from scipy.stats import wasserstein_distance

class FeatureDriftDetector:
    def __init__(self, window_days=7):
        self.window_days = window_days
        self.baseline_stats = {}  # Load from training

    def detect_drift(self, feature_name: str, current_values: np.array) -> dict:
        baseline = self.baseline_stats.get(feature_name)

        # Method 1: Population Stability Index (PSI)
        psi = self._calculate_psi(baseline['histogram'], current_values)

        # Method 2: Wasserstein distance
        wasserstein_dist = wasserstein_distance(baseline['distribution'], current_values)

        # Method 3: Mean/Std deviation shift
        mean_shift = abs(np.mean(current_values) - baseline['mean']) / baseline['std']
        std_shift = abs(np.std(current_values) - baseline['std']) / baseline['std']

        alerts = []
        if psi > 0.25:
            alerts.append(f"PSI {psi:.3f} > 0.25: significant drift in {feature_name}")
        if wasserstein_dist > baseline['threshold']:
            alerts.append(f"Wasserstein {wasserstein_dist:.3f} above threshold for {feature_name}")
        if mean_shift > 2:
            alerts.append(f"Mean shifted {mean_shift:.2f} std for {feature_name}")

        return {'drift_detected': len(alerts) > 0, 'alerts': alerts, 'psi': psi}

    def _calculate_psi(self, expected_hist, actual_values):
        actual_hist, _ = np.histogram(actual_values, bins=10, range=(0, 1))
        actual_pct = actual_hist / actual_hist.sum()
        expected_pct = expected_hist / expected_hist.sum()
        # Avoid division by zero
        actual_pct = np.clip(actual_pct, 0.001, 1)
        expected_pct = np.clip(expected_pct, 0.001, 1)
        psi = np.sum((actual_pct - expected_pct) * np.log(actual_pct / expected_pct))
        return psi
```

**Alerting workflow:**

| Drift Level | PSI | Wasserstein | Alert | Action |
|------------|-----|-------------|-------|--------|
| Low | 0.1 - 0.2 | < threshold | Slack | Monitor, log |
| Medium | 0.2 - 0.3 | > threshold | Slack + Jira | Review, consider retraining |
| High | > 0.3 | > 2x threshold | PagerDuty | Block pipeline, investigate |

---

## Part 4: Backfill & SLAs (10 min)

**Backfill 3 months of buggy features:**

```python
def backfill_features(start_date, end_date):
    """
    Strategy: Parallel backfill by week with validation gates
    """

    # Step 1: Identify affected features and date range
    affected_features = ["customer_lifetime_value", "purchase_frequency"]
    weeks = generate_weekly_ranges(start_date, end_date)

    # Step 2: Backfill in parallel (using Spark)
    for week_start, week_end in weeks:
        spark.sql(f"""
            INSERT INTO feature_store.customer_features
            SELECT * FROM (
                SELECT customer_id,
                       compute_clv(orders) AS customer_lifetime_value,
                       compute_purchase_freq(orders) AS purchase_frequency,
                       CURRENT_TIMESTAMP() AS computed_at
                FROM silver.orders
                WHERE order_date BETWEEN '{week_start}' AND '{week_end}'
                GROUP BY customer_id
            )
        """)

    # Step 3: Validate backfilled data
    for feature in affected_features:
        qc_result = run_quality_check_on_feature(feature, start_date, end_date)
        assert qc_result['quality_score'] > 0.95, f"Backfill quality failed for {feature}"

    # Step 4: Notify ML team
    send_notification(f"Feature backfill complete for {start_date} to {end_date}")
```

**ML feature SLAs:**
```yaml
feature_product_recommendations:
  freshness_sla: "features must be < 4 hours old at model inference time"
  completeness_sla: "> 99% of product_ids must have computed features"
  availability_sla: "feature store available 99.9% during inference hours (6AM - 10PM)"
  backfill_sla: "corrected features available within 24 hours of bug discovery"
  monitoring:
    - metric: "call_model_inference_score"
      threshold: "> 0.85"  # Model quality score
      alert: "PagerDuty if below threshold"
    - metric: "feature_lag_hours"
      threshold: "< 4"
      alert: "Slack if > 4 hours"
```

---

## Follow-up Questions

**Online vs offline feature quality:**
| Aspect | Offline Feature Store | Online Feature Store |
|--------|----------------------|---------------------|
| Computation | Batch (Spark, daily/hourly) | Streaming (Flink, real-time) |
| Validation | Row-level, distribution checks | Record-level, latency checks |
| Storage | Parquet/Delta on S3 | KV store (Redis, DynamoDB) |
| Consistency | Eventual (batch window) | Eventual (streaming lag) |
| Quality gate | Blocking (stop pipeline) | Non-blocking (flag bad records) |

**Deequ (Spark) vs Great Expectations vs dbt:**
| Tool | Best For | Real-time? | ML Features? |
|------|----------|------------|-------------|
| Deequ | Large-scale Spark data validation | No | Yes (distribution checks) |
| Great Expectations | DataFrame-level checks, profiling | No | Yes (expectation suites) |
| dbt tests | SQL-based warehouse quality | No | Limited |
| Whylogs | ML feature monitoring | Yes | Yes (profiling, drift) |

**Feature drift monitoring dashboard:**
1. Per-feature KS statistic over time (training = 0 baseline)
2. Null rate trend per feature (daily)
3. Feature value range (min/max) per day
4. Categorical feature cardinality (new categories detected)
5. Feature freshness: time since last successful computation
6. Overall feature quality score (composite of all checks)

