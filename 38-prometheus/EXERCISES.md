# Exercises - Prometheus

## Exercise 1: Basic Metrics
Implement application metrics:

1. Add counter for API request count
2. Add gauge for active connections
3. Add histogram for request latency
4. View metrics at `/actuator/prometheus`

## Exercise 2: Custom Metrics
Create business-specific metrics:

1. Track order processing time with histogram
2. Monitor cache hit ratio with gauge
3. Count successful/failed operations
4. Add labels for dimension breakdown

## Exercise 3: Business Metrics
Monitor application business KPIs:

1. Track user registration rate (counter)
2. Monitor active sessions (gauge)
3. Measure transaction volume per minute
4. Create metrics for business events

## Exercise 4: Query Language (PromQL)
Explore PromQL for analysis:

1. Calculate request rate with `rate()`
2. Use `histogram_quantile()` for latency percentiles
3. Aggregate by label with `sum by()`
4. Use `topk()` to find top consumers

## Exercise 5: Alerting Rules
Create alert rules for issues:

1. Define alert for high error rate (>1%)
2. Create alert for latency spike (P99 > 500ms)
3. Alert on service down (up == 0)
4. Configure recording rules for complex queries

## Bonus Challenge
Build a dashboard that shows: request rate, error rate, latency percentiles (P50, P95, P99), and JVM memory usage on the same Grafana panel.