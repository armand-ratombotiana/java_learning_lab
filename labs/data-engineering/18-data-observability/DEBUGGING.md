# Debugging Data Observability

## Freshness Alert False Positive
Check if system clock is correct; pipeline may have legitimate delay (upstream issue)

## Volume Anomaly False Positive
Check for double-counting; upstream deduplication may have changed; holiday/weekend pattern

## Distribution Drift
Drift may be legitimate (business change, new segment); verify before escalating

## Soda Scan Failure
Check connection credentials, warehouse availability, SQL syntax in checks
