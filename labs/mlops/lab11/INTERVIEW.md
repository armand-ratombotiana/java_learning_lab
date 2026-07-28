# Lab 11: Interview Questions

## FAANG-Level Questions

### Q1: Design a model governance framework for a financial institution.
**Answer**: Three-tier framework: (1) Model inventory — all models registered with metadata, owner, risk tier, (2) Model risk management — validation process: conceptual soundness, outcomes analysis, ongoing monitoring, (3) Compliance reporting — automated reports for regulators including model performance, fairness metrics, audit trail. Use immutable storage (blockchain or append-only DB) for audit logs.

### Q2: How do you detect and mitigate bias in ML models?
**Answer**: Detection: compute demographic parity, equal opportunity, and disparate impact across protected groups. Use adversarial debiasing, reweighting training samples, or post-processing threshold adjustment. Monitor bias metrics in production alongside performance metrics. Conduct regular fairness audits with diverse teams.

### Q3: What information should a model card contain?
**Answer**: (1) Model details — name, version, type, framework, training date, (2) Intended use — primary use case, target population, (3) Factors — demographic groups, use conditions, (4) Metrics — overall and per-group performance, (5) Evaluation data — datasets used, size, splits, (6) Ethical considerations — limitations, biases identified, (7) Caveats — known failure modes, robustness concerns.

### Q4: How do you implement audit trails for ML model decisions?
**Answer**: Log model predictions along with: model version, input features, prediction, confidence, user ID, timestamp, and decision ID. Store in append-only log (e.g., Kafka topic with infinite retention, Amazon QLDB, or ledger DB). Hash-chaining for tamper evidence. Provide API for regulators to query decisions by user or time range.

## LeetCode / NeetCode References
- **Design Log Aggregation System** — Audit trail collection
- **Design a Permission System** — Model access control
- **Design Blockchain-based Ledger** — Immutable audit logs
