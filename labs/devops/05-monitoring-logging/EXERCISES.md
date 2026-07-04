# Monitoring & Logging Exercises

## Exercise 1: Prometheus Scrape
Deploy Prometheus. Configure it to scrape itself (localhost:9090/metrics). Verify targets are up.

## Exercise 2: Grafana Dashboard
Add Prometheus as data source in Grafana. Create a dashboard showing Prometheus's own memory usage.

## Exercise 3: Custom Metrics
Create a Python/Node.js app exposing /metrics with a custom counter. Scrape it with Prometheus.

## Exercise 4: PromQL Queries
Write queries for: average CPU across all instances, request rate per second, p99 latency.

## Exercise 5: Alerting Rules
Create an alert that fires when a target is down for 5 minutes. Test by stopping a target.

## Exercise 6: ELK Stack
Deploy Elasticsearch, Logstash, and Kibana using Docker Compose. Verify connectivity.

## Exercise 7: Log Shipping
Configure Filebeat to ship Nginx access logs to Logstash, which parses and sends to Elasticsearch. Visualize in Kibana.

## Exercise 8: Structured Logging
Instrument a sample app with structured JSON logging. Parse with Logstash grok filter.

## Exercise 9: SLO Monitoring
Set up SLI metrics (latency, error rate). Define SLO alert based on burn rate.
