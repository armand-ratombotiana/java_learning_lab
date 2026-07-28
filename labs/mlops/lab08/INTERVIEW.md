# Lab 08: Interview Questions

## FAANG-Level Questions

### Q1: Design a real-time model monitoring system for 1000+ deployed models.
**Answer**: Use a streaming architecture: predictions → Kafka → Flink/Spark streaming for real-time drift computation. Store reference distributions in a feature store. Compute PSI/KL divergence every hour per model. Use Prometheus for metric exposition and Grafana for dashboards. Alert via PagerDuty/Slack when drift exceeds thresholds. Store all monitoring data in a time-series database for historical analysis.

### Q2: How do you detect concept drift without ground truth labels?
**Answer**: Use proxy metrics: (1) Prediction distribution shift — PSI on model outputs, (2) Feature importance drift — track top-K feature values, (3) Uncertainty estimation — increase in model entropy/uncertainty, (4) Business metrics — CTR, conversion rate, revenue changes. These serve as early warning before ground truth is available.

### Q3: Explain PSI, KL divergence, and JS divergence. When would you use each?
**Answer**: PSI is the industry standard for credit risk monitoring — symmetric and bounded. KL divergence is asymmetric but has strong information-theoretic foundations. JS divergence is symmetric and bounded [0,1], making it more interpretable. Use PSI for regulatory compliance, JS for general drift detection, KL for understanding specific feature contributions to drift.

### Q4: How do you set drift thresholds for automated retraining?
**Answer**: Threshold should be statistically grounded: compute PSI on a validation set during training to establish baseline. Set alert threshold at 95th percentile of validation PSI. Use multiple severity levels: warning (PSI > 0.1), critical (PSI > 0.25). Automate retraining when critical drift is detected, with human-in-loop approval for production deployment.

## LeetCode / NeetCode References
- **Design Metrics Collection System** — Sliding window aggregation
- **Design Monitoring Dashboard** — Time-series visualization
- **Moving Average from Data Stream (LeetCode 346)** — Sliding window statistics
