# Lab 03: Interview Questions

## FAANG-Level Questions

### Q1: Design a model registry that supports thousands of models across multiple teams.
**Answer**: Use a relational DB (PostgreSQL) for metadata with object storage (S3) for model binaries. Implement hierarchical namespaces (team/project/model). Use microservices architecture: registry API service, versioning service, lineage service. Cache active production models in Redis. Implement webhook notifications for model transitions.

### Q2: How do you handle rollbacks in model registry?
**Answer**: Maintain immutable version history. Rollback is achieved by re-tagging a previous version to "Production" and archiving the current one. Automatically run validation on the rollback target to ensure it's compatible with current data schema.

### Q3: Champion/Challenger pattern — explain and implement.
**Answer**: Champion is the current production model; challenger is a candidate with better offline metrics. Route a percentage of traffic to challenger (shadow or canary). If challenger outperforms champion over a window, promote challenger to champion. In Java: maintain two model references and a routing rule (e.g., 95% champion, 5% challenger).

### Q4: How do you ensure model lineage for audit compliance?
**Answer**: Store for each model version: git commit, dataset version hash, hyperparameters, training script path, evaluation metrics, CI/CD run ID, timestamp, and user who performed the promotion. Use an immutable event log for all stage transitions. Regular reconciliation audits.

## LeetCode / NeetCode References
- **LRU Cache (LeetCode 146)** — Caching active model versions
- **Design Version Control System** — Model version diff and history
- **Design File System (LeetCode 588)** — Hierarchical model organization
