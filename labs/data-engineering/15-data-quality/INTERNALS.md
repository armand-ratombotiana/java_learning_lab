# Data Quality Engineering Internals

## Great Expectations Architecture
Expectation: atomic rule (expect_column_values_to_not_be_null). Suite: collection of expectations. Checkpoint: configuration for evaluation. Data Docs: rendered HTML reports. Store: backend for expectation/config/validation artifacts (filesystem, S3, GCS).

## Quality Check Pipeline
1. Extract data sample or full dataset. 2. Run expectation suite. 3. Generate validation results. 4. Compare against thresholds. 5. Pass/fail decision. 6. Trigger actions (alert, block downstream, notify). 7. Update Data Docs. 8. Send metrics to monitoring system.

## Schema Drift Detection
Incoming schema → Compare with expected schema (stored in schema registry). Detect: new columns, missing columns, type changes, nullability changes. Classify: additive (safe), breaking (dangerous). Respond: auto-evolve for additive, alert for breaking, block pipeline for destructive.

## Quality Metric Store
Store quality results over time for trending. Common backends: PostgreSQL, BigQuery, S3. Metrics schema: dataset, expectation, pass_count, fail_count, run_timestamp, run_id. Trend queries: quality score over last 30 days, failure rate by expectation.
