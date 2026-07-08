# How Data Quality Engineering Works

1. Data arrives at quality checkpoint (batch or streaming)
2. Schema validated against expected schema from data contract
3. Expectations suite runs configured checks (null, unique, range, format)
4. Results compared against thresholds and SLAs
5. Pass: data flows to next stage with quality metrics recorded
6. Fail: based on severity, either alert or block downstream processing
7. Metrics stored for trending analysis and dashboard
8. Data contracts enforced: schema changes require coordination
