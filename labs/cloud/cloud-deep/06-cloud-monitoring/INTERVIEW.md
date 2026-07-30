# Interview Questions — Cloud Monitoring

## Beginner

Q: What are the three pillars of observability?
A: Metrics, logs, and traces.

Q: What is the difference between a counter and a gauge?
A: Counter is cumulative (only increases/resets); gauge represents a current value that can go up or down.

## Intermediate

Q: How does distributed tracing propagate context across services?
A: Trace context (trace ID, span ID) is propagated via HTTP headers (traceparent/tracestate), message headers, or gRPC metadata.

Q: How do you design effective alert rules?
A: Define SLI-based thresholds, use multi-condition rules, include burn-rate alerts, avoid alert fatigue with proper severity levels.

## Advanced

Q: Design a monitoring system for 1000 microservices.
A: Hierarchical service graph, metrics aggregation pipeline, sampling-based tracing, centralized logging with correlation IDs, topologically-aware dashboards.

Q: How would you detect and surface anomalies in near real-time?
A: Use streaming window algorithms (z-score, EWMA, Holt-Winters) on metric streams, compare to baseline, surface via alert manager.
