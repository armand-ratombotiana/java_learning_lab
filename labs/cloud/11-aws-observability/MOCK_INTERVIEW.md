# Mock Interview — AWS Observability

## Format
- **Duration**: 45 minutes
- **Type**: Technical + Scenario
- **Difficulty**: Professional

## Warm-Up (5 min)

Q1: What are the three pillars of observability? Explain the difference between monitoring and observability.

Q2: What is the difference between CloudWatch, X-Ray, and CloudTrail?

## Technical Questions (20 min)

### Question 1: Distributed Tracing (10 min)
A request across your microservices is taking 5 seconds. You need to identify the bottleneck. Architecture: API Gateway → Lambda → SQS → Lambda → RDS.

**Question**: How do you implement distributed tracing with X-Ray? What specific metrics would you look at to find the bottleneck? How do you instrument a Java Lambda function?

### Question 2: Alerting Strategy (10 min)
Design an alerting strategy for a critical e-commerce service. Requirements:
- Detect anomalies before they affect customers
- Reduce alert fatigue (currently 200 alerts/day, 95% ignored)
- Page on-call for critical issues (P1, P2)
- Business-level SLOs (99.9% uptime, P95 < 500ms)

**Design**: Which metrics, what thresholds, composite alarms, SLO tracking.

## Behavioral Question (10 min)

**Question**: Tell me about a time you improved observability in a system you were responsible for. How did you measure the improvement?

## System Design Whiteboard (10 min)

**Problem**: Design an observability stack for a Kubernetes cluster running 50 microservices:
- Metrics (Prometheus, CloudWatch, or AMP)
- Logging (CloudWatch Logs, Fluent Bit, OpenSearch)
- Tracing (X-Ray, OpenTelemetry)
- Dashboards (Grafana, CloudWatch Dashboards)
- Alerting (Alertmanager, SNS)
- Cost < $2000/month for the observability infrastructure

## Evaluation Criteria

| Area | Excellent | Good | Needs Improvement |
|------|-----------|------|-------------------|
| X-Ray | Annotations, segments, subsegments, sampling | Basic traces | No tracing knowledge |
| CloudWatch | Logs Insights, Contributor Insights, metrics math | Basic metrics | Simple CPU/mem only |
| Alerting | SLO-based, multi-dimensional, composite, anomaly | Static thresholds | Single threshold |
| Tracing | Distributed context, correlation IDs, latency breakdown | Basic latency | No distributed tracing |
| Observability | Three pillars, open standards (OTel) | Basic monitoring | Monitoring only |

## Sample Solution Outline

### X-Ray Distributed Tracing
- Enable X-Ray SDK in Java Lambda: add `aws-xray-recorder-sdk-core` dependency
- Use X-Ray segments/subsegments: wrap SQS send, RDS query, external API calls
- X-Ray sampling: 10% default sampling, adjust for high-traffic endpoints
- Key metrics: Average response time by service, fault rate, throttle count
- Annotations: add `[Service: "OrderService"]`, `[Environment: "prod"]` for filtering
- Service graph: visualize end-to-end topology and latency distribution

### Alerting Strategy
- Define SLOs: 99.9% uptime = 8.76 hours downtime/year
- Error budget: 10% of SLO targets → 30m downtime/month
- Burn rate alerts: page if error budget consumed at 2x rate for 1h, 10x for 6m
- Multi-dimensional: aggregate vs per-route, per-deployment
- Composite alarms: combine high latency AND high error rate
- Use CloudWatch Anomaly Detection for seasonal metrics (traffic by hour)
- Reduce alert fatigue:
  - P1: Service down, SLO breach critical → page immediately
  - P2: Latency > SLO, error rate > 1% → page within 5 min
  - P3: CPU > 80%, disk > 85% → ticket (no page)
  - P4: Info, minor warnings → email digest

### K8s Observability Stack
- **Metrics**: Amazon Managed Service for Prometheus (AMP) + AWS Distro for OpenTelemetry
  - Prometheus Operator to scrape pods
  - kube-state-metrics + node-exporter
  - Alertmanager rules for K8s-specific alerts
- **Logging**: Fluent Bit DaemonSet → CloudWatch Logs
  - Structured JSON logs from applications
  - Log Insights queries for troubleshooting
- **Tracing**: OpenTelemetry Collector → X-Ray or AMP
  - Auto-instrumentation for Java with OpenTelemetry Java Agent
- **Dashboards**: Amazon Managed Grafana with AMP, CloudWatch, and X-Ray data sources
  - RED metrics (Rate, Errors, Duration) per service
  - USE method (Utilization, Saturation, Errors) per node
- **Alerting**: Alertmanager → SNS → PagerDuty/Slack/Email
