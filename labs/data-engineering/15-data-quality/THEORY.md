# Data Quality Engineering Theory

## Six Quality Dimensions
Accuracy: data reflects real-world values (e.g., zip code matches city). Completeness: all required fields have values (no unexpected nulls). Consistency: data doesn't contradict itself across systems. Timeliness: data is current enough for its use case. Validity: data conforms to format (email format, date range). Uniqueness: no unexpected duplicates in key columns.

## Great Expectations Framework
Expectation Suites contain expectations (one rule per check). DataContext manages project config. Checkpoints evaluate expectations against data and produce validation results. Data Docs render results as human-readable reports. Actions: Slack, email, update Data Docs on failure.

## dbt Tests
Built-in: unique (no duplicates), not_null (no nulls), accepted_values (values in set), relationships (FK integrity). Custom tests: SQL query returning failing rows. Severity: warn (pass pipeline) or error (fail pipeline). Freshness tests: are tables updated within SLA window.

## Data Contracts
Producer-owned, consumer-reviewed. Components: schema with types, ownership, SLAs (freshness, completeness threshold), change management (adding column = 7 day notice, breaking change = 30 day notice), quality metrics, notification channels (Slack, email, PagerDuty).
