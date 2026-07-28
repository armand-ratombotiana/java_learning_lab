# Lab 09: Interview Questions

## FAANG-Level Questions

### Q1: Design a data quality monitoring system for thousands of ML features.
**Answer**: Use a two-layer architecture: (1) Batch validation — Spark/Deequ jobs that run daily on offline data, computing statistics like null rates, min/max, distribution distances (PSI, KS test). (2) Online validation — lightweight checks on each inference request (schema validation, range checks) in the serving layer. Store validation results in a time-series DB and alert on anomalies.

### Q2: How do you handle schema drift in production ML pipelines?
**Answer**: Implement schema evolution policies: (1) backward compatible — new columns added are optional, (2) forward compatible — old models ignore unknown columns. Use Avro/Protobuf schema registry with compatibility checks. Automatically retrain models when schema changes significantly (new features available, old features deprecated).

### Q3: Compare Great Expectations vs Deequ for data validation.
**Answer**: Great Expectations is Python-native with rich expectations library and data docs. Deequ is Spark-native (Scala) with automated constraint suggestion and Apache Spark integration. GE is better for notebook/exploratory workflows; Deequ for large-scale Spark pipelines. For Java ecosystems, Deequ is more natural due to Spark integration.

### Q4: How do you validate data for real-time inference?
**Answer**: Implement a feature validation layer in the model server: (1) schema validation — JSON Schema or Protobuf, (2) range checks — feature min/max from training data, (3) type coercion — safe conversion with error handling, (4) missing value handling — fallback to defaults/median. Return 400 error for invalid inputs with clear error messages.

## LeetCode / NeetCode References
- **Design Log Aggregation System** — Collecting validation results
- **Valid Parentheses (LeetCode 20)** — Schema validation patterns
- **Design Error Monitoring System** — Data quality issue tracking
