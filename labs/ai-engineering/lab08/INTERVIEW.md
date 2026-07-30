# Lab 08: AI Observability — Interview Q&A

## FAANG-Level Questions

### Q1: Design an observability system for a multi-model LLM serving platform.

**A:** Every request gets a trace ID that flows through API gateway → load balancer → model replica. Collect: (1) token counts per request (input/output); (2) latency breakdown (queue time, inference time, post-processing); (3) model name and version; (4) user/customer ID for cost attribution; (5) response quality scores. Export to a time-series database. Build dashboards for cost, latency, and error rate. Alert on p99 latency spikes and cost anomalies.

### Q2: How do you detect data drift in production?

**A:** Compare the distribution of production inputs to a reference distribution (training data). Use statistical tests: KL divergence, PSI, or chi-squared test for categorical features; Kolmogorov-Smirnov test for continuous features. Set thresholds per feature. Alert when drift exceeds threshold. Track drift over time in a dashboard. When drift is detected, trigger model retraining or data investigation.

### Q3: How would you attribute LLM costs to different teams/products?

**A:** Tag every request with metadata: product ID, team name, feature name, user tier. Track tokens per tag. Compute cost using per-model pricing. Build a cost allocation report that sums costs by team/product. Implement budget alerts per team. Use chargebacks to incentivize efficient prompt design.

### Q4: What metrics would you monitor for a real-time AI system with SLAs?

**A:** Service-level metrics: (1) latency p50/p95/p99 vs. SLA target; (2) throughput (requests/sec); (3) error rate (4xx, 5xx, model errors); (4) saturation (queue depth, GPU utilization). Business-level: (5) cost per request; (6) user satisfaction score; (7) task completion rate. Health checks every 10s with synthetic probes.

### Q5: How do you handle the trade-off between detailed observability and performance overhead?

**A:** Use sampling — log 100% of errors and a configurable percentage of successful requests (e.g., 1%). Use async metric emission to avoid blocking the critical path. Batch metric writes. For high-cardinality dimensions, aggregate client-side before writing. Use lightweight statistical profiling (e.g., HdrHistogram) instead of recording every individual latency value.