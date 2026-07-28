# Lab 07: Interview Questions

## FAANG-Level Questions

### Q1: Design a CI/CD pipeline for an ML system with data dependencies.
**Answer**: Use a two-trigger system: (1) code changes trigger training pipeline, (2) data changes (new data arrival, schema change) trigger data validation + retraining. Use DVC for data versioning in git. The pipeline stages: commit → data pull → data validation → feature engineering → training → evaluation → registry → deploy. Use GitHub Actions for code, and a scheduled job + webhook for data triggers.

### Q2: How do you handle model deployment rollbacks in CI/CD?
**Answer**: Every deployment is versioned. The CD pipeline stores the previous model version and can revert via MLflow registry stage transition. In K8s, use `kubectl rollout undo deployment/<name>`. In CI/CD, add a "Rollback" pipeline job that reverts to the last successful production model.

### Q3: How do you prevent bad models from reaching production?
**Answer**: Implement multiple gates: (1) data validation gate — check feature schemas and distributions, (2) model evaluation gate — champion vs challenger comparison with statistical significance test, (3) shadow deployment — run new model in shadow mode for N hours, (4) manual approval for production promotion.

### Q4: Compare GitHub Actions vs Jenkins for ML pipelines.
**Answer**: GitHub Actions is simpler, cloud-native, with great ecosystem for small-medium teams. Jenkins offers more flexibility, custom plugins, and enterprise features (RBAC, audit). For ML: GitHub Actions is sufficient for CI; Jenkins is better for complex CD pipelines with approvals, especially in regulated industries.

## LeetCode / NeetCode References
- **Design a Pipeline System** — Stage dependencies, parallel execution
- **Design Version Control System** — Model versioning patterns
- **Design a Configuration Management System** — Pipeline configuration as code
