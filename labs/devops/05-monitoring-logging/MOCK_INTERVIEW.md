# Monitoring & Logging MOCK_INTERVIEW.md

## Scenario 1: Alert Fatigue
Your team receives 200+ alerts per night. Most are noise. Engineers are ignoring critical alerts.

**Questions**:
1. How would you reduce alert volume?
2. What's the difference between an alert and a notification?
3. How do you design an alert to be actionable?
4. Explain alert fatigue and how to measure it.

**Expected approach**: Alert audit — classify by severity, delete unused rules, group correlated alerts, increase thresholds, add delays (for flapping), set up escalation policies. Alert vs notification: alerts require action, notifications are informational. Actionable: clear severity, runbook link, relevant context.

## Scenario 2: Incident Response
A critical service is down. You're the first on-call engineer.

**Questions**:
1. Walk through your incident response steps.
2. What dashboards/logs do you check first?
3. When do you escalate? To whom?
4. How do you communicate during an incident?

**Expected approach**: 1) Acknowledge incident. 2) Determine severity. 3) Check Grafana dashboards (4 golden signals). 4) Check logs (Loki, ELK). 5) Check recent changes/deployments. 6) Mitigate (rollback, scale, redirect). 7) Communicate (Slack, status page). 8) Postmortem. Escalate if: SEV1, unknown area, needs authorization.

## Scenario 3: Designing Monitoring for Microservices
You have 50 microservices. You need to design an observability strategy.

**Questions**:
1. How would you structure metrics collection?
2. What metrics matter for each service?
3. How do you implement distributed tracing?
4. How do you correlate metrics, logs, and traces?

**Expected approach**: RED metrics (Rate, Errors, Duration) per service. Prometheus with service discovery. Structured logging with correlation IDs. Distributed tracing (OpenTelemetry). Grafana Loki for logs, Tempo for traces. Use trace/span IDs in logs for correlation.

## Scenario 4: SLO Implementation
Your team has never defined SLOs. You need to introduce the practice.

**Questions**:
1. How do you define SLIs for a web service?
2. How do you set SLO targets?
3. How does an error budget work?
4. How do you alert based on SLO burn rate?

**Expected approach**: SLIs: latency (p99 < 200ms), availability (success rate > 99.9%), throughput (requests/sec). SLO: 99.9% over 30 days. Error budget: 43 minutes of downtime/month. Burn rate alerts: 1h, 6h, 3d windows. Multi-window, multi-burn-rate alerts.

## Scenario 5: Log Management at Scale
Your application produces 10TB of logs per day. Logs are expensive to store.

**Questions**:
1. How would you reduce log volume and cost?
2. What's structured logging and why does it matter?
3. How do you design log retention policies?
4. How would you set up log-based metrics?

**Expected approach**: Log levels (ERROR/WARN always, DEBUG lower), sampling high-volume endpoints, exclusion filters for health checks, retention tiers (hot 7d, warm 30d, cold 1y). Structured JSON logs with fields for easy querying. Log-based metrics via Promtail pipeline stages or Elastic Watcher.

## Key Monitoring Interview Questions
1. Explain the four golden signals of monitoring.
2. What's the USE method? When do you use it?
3. What's the RED method? When do you use it?
4. Explain the difference between white-box and black-box monitoring.
5. How does Prometheus pull model work?
6. What's the difference between a counter, gauge, histogram, and summary?
7. Explain PromQL: rate, irate, increase, histogram_quantile.
8. How does distributed tracing work? What's a span?
9. Explain sampling strategies (head-based vs tail-based).
10. How does Grafana Loki differ from Elasticsearch for logs?

## Whiteboard Challenge
Design a monitoring and observability stack for a Kubernetes-based microservices platform with 50+ services, 1000+ pods, and multi-region deployment.

## Follow-up
1. How would you alert on missing metrics/data?
2. How would you implement chaos engineering monitoring?
3. How would you handle monitoring for ephemeral environments?