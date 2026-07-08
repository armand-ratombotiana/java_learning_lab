# Data Observability Theory

## Five Pillars of Data Observability
Freshness: how up-to-date is the data (lag between event and availability). Distribution: are data values within expected statistical ranges. Volume: is all expected data present (row count expectations). Schema: has the structure changed without notice. Lineage: how is data produced and consumed.

## Anomaly Detection Techniques
Static thresholds: row count > 1M. Moving average: compare to 7/30/90 day window. Z-score: |value - mean| / stddev > 3. IQR: value outside Q1-1.5*IQR or Q3+1.5*IQR. Seasonality: compare to same day last week, last month, last year. ML-based: isolation forest, LSTM for time series.

## Data Profiling
Column-level: null count, distinct count, min/max, mean/stddev, value distribution (histogram). Table-level: row count, size, partition count, timestamp range. Cross-column: correlation, functional dependencies, foreign key violations. Profiling runs on schedule and compares to historical baselines.

## Incident Lifecycle
1. Detection: check fails or anomaly triggered. 2. Classification: severity (critical, high, medium, low). 3. Notification: email, Slack, PagerDuty. 4. Investigation: use lineage for root cause. 5. Resolution: fix pipeline, backfill data. 6. Post-mortem: prevent recurrence. 7. Documentation: update runbooks.
