# Lab 15: Interview Questions

## FAANG-Level Questions

### Q1: Design an end-to-end ML platform that serves 100+ models with 99.9% availability.
**Answer**: Start with requirements: model diversity (TF, PyTorch, sklearn), latency (<100ms P99), throughput (10K QPS), multi-region HA. Architecture: (1) Model Registry — stores all versions with metadata, (2) Model Router — Istio-based traffic splitting for canary/shadow, (3) Serving mesh — K8s with sidecar for metrics, (4) Feature Store — Redis cluster for online, S3/Parquet for offline, (5) Monitoring — Prometheus + Grafana + Evidently for drift, (6) CI/CD — GitHub Actions + ArgoCD for GitOps deployment.

### Q2: How do you design for ML platform reliability and failure modes?
**Answer**: Implement: (1) Circuit breakers — if model server is slow, fall back to simple heuristic, (2) Bulkheading — separate thread pools per model, (3) Graceful degradation — degrade to cached predictions if model unavailable, (4) Health checks — liveness/readiness probes, (5) Rate limiting — prevent cascading failures, (6) Chaos engineering — regularly test failure scenarios.

### Q3: How do you handle data skew and traffic spikes in ML serving?
**Answer**: (1) Auto-scaling — HPA based on request queue depth and CPU, (2) Predictive scaling — learn traffic patterns and scale ahead, (3) Request queuing — buffer spikes with Kafka/RabbitMQ between API gateway and model servers, (4) Load shedding — prioritize premium customers during overload, (5) Feature cache warming — pre-cache features for predicted hotspots.

### Q4: Design a multi-region ML serving architecture.
**Answer**: Active-active with DNS-based routing (Route53/Cloud DNS). Each region has independent model servers + feature store replicas. Global model registry with cross-region replication (S3 CRR). Use read-replicas for feature store in each region. For model updates: deploy to one region, validate, then roll out globally via CI/CD. Handle regional failures with automatic failover.

## LeetCode / NeetCode References
- **Design a Distributed System** — ML platform as distributed system
- **Design a Rate Limiter (LeetCode 359)** — Protecting model serving endpoints
- **Design a Recommendation System** — End-to-end ML system design
- **Design YouTube or Netflix** — Large-scale ML infrastructure patterns
