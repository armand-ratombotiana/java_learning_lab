# Math Foundation for Monitoring & Logging

## Statistical Concepts
- **Percentiles**: p50 (median), p95, p99 for latency distributions.
- **Histogram quantile estimation**: `histogram_quantile(0.99, rate(...))`.
- **Rolling windows**: Moving averages for trend detection.

## Time-Series Compression (Gorilla)
- **Double-delta encoding**: Store differences between consecutive timestamps and values.
- **Floating-point XOR compression**: Efficient storage of similar floating-point values.

## Alerting Thresholds
- **Static thresholds**: Simple comparison (CPU > 90%).
- **Dynamic/seasonal thresholds**: Based on historical patterns (e.g., 3 standard deviations from rolling mean).
- **Rate of change**: Derivative-based detection (e.g., `deriv(disk_free[30m]) < 0`).

## Log Analysis Math
- **TF-IDF**: Term frequency-inverse document frequency for log pattern extraction.
- **Levenshtein distance**: Fuzzy matching for log deduplication.
- **Regex/pattern matching**: Log parsing and structured extraction.

## SLO/SLI Calculations
- **Burn rate**: How fast error budget is consumed.
- **Error budget**: 1 - SLO target (e.g., 99.9% SLO = 0.1% error budget).
- **Time to exhaustion**: Error budget remaining / current burn rate.
