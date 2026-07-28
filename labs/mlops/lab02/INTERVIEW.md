# Lab 02: Interview Questions

## FAANG-Level Questions

### Q1: Design an experiment tracking system for a team of 50 data scientists.
**Answer**: Use MLflow Tracking Server with a PostgreSQL backend for reliability. Implement hierarchical experiments (team/project/run), role-based access control, and automated tagging from CI/CD. Use a tracking client library that auto-logs environment, code version (git commit), and dependencies (conda/pip). Store artifacts in S3/GCS with lifecycle policies.

### Q2: How do you handle experiment reproducibility in MLflow?
**Answer**: Log the full environment (conda.yaml, requirements.txt), source code git commit hash, input dataset hash/version, and random seed. Use MLflow's `set_tags()` to record environment info. For Java, log `System.getProperties()` and classpath as artifacts.

### Q3: What are the limitations of MLflow and how would you address them?
**Answer**: MLflow lacks native hyperparameter search, model lineage tracking across multi-step pipelines, and fine-grained access control. Address by: (1) integrating Optuna/Ray Tune for search, (2) extending with custom lineage tracking using DAG run IDs, (3) wrapping with a proxy server for auth.

### Q4: Compare MLflow vs Weights & Biases vs Neptune.
**Answer**: MLflow is open-source with simple REST API; W&B offers superior visualization and collaboration; Neptune has the best team management. MLflow wins for self-hosted/on-prem; W&B for rapid prototyping; Neptune for enterprise teams.

## LeetCode / NeetCode References
- **Design In-Memory File System (LeetCode 588)** — Store/retrieve experiment artifacts
- **Time-Based Key-Value Store (LeetCode 981)** — Logging metrics over time with steps
- **Design Log Storage System (LeetCode 635)** — Structured logging for experiment runs
