# Lab 10: AI Deployment & CI/CD — Interview Q&A

## FAANG-Level Questions

### Q1: Design a deployment strategy for a critical AI model serving real-time predictions.

**A:** Use a three-phase approach. Phase 1: shadow deployment — run new model in parallel with production, log predictions but don't serve them. Phase 2: canary — route 5% traffic, monitor for 24 hours, check latency, accuracy, and drift. Phase 3: blue-green — switch to 100% with instant rollback capability. Automated rollback triggers if any metric exceeds threshold.

### Q2: How do you handle model versioning and rollback in a microservice architecture?

**A:** Each model version is a separate artifact in a registry with metadata (training date, dataset hash, evaluation metrics). The model-serving service loads a specific version via configuration. Rollback updates the config to point to the previous version. Use a service mesh for traffic routing between versions during canary testing.

### Q3: What tests should be in an ML CI/CD pipeline?

**A:** (1) Data validation — schema checks, distribution statistics; (2) model validation — accuracy, precision, recall on holdout set; (3) integration tests — end-to-end inference with sample inputs; (4) performance benchmarks — latency and throughput thresholds; (5) bias/fairness tests — demographic parity, equal opportunity; (6) safety tests — toxicity, injection resistance.

### Q4: Compare canary vs. blue-green deployment for AI systems.

**A:** Blue-green: simpler, instant rollback, but expensive (2x infrastructure) and no gradual validation. Canary: gradual exposure catches real-world issues, limits blast radius, but takes longer and requires sophisticated monitoring. Use blue-green for model updates with low risk; use canary for significant model changes or new architectures.

### Q5: How do you automate rollback decisions based on model quality metrics?

**A:** Define metric thresholds for each quality dimension (accuracy, latency, drift). During canary, compute metrics continuously. If any metric exceeds threshold for N consecutive observations, trigger automatic rollback. Log the decision and alert the team. Store metrics for post-mortem analysis. Implement a cooldown period before attempting re-deployment.