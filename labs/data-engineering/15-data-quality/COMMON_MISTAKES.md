# Common Mistakes with Data Quality Engineering

1. End-Only Checks: Checking quality only at final destination; check at every pipeline stage
2. Empty DataFrame Handling: Quality checks on empty data produce division-by-zero or false positives
3. Hardcoded Thresholds: Thresholds should be configurable per dataset and adjustable over time
4. No Trend Monitoring: Single run pass/fail ignores degradation trends; track quality over time
5. Over-alerting: Alerting on every failure causes alert fatigue; aggregate and escalate by severity
