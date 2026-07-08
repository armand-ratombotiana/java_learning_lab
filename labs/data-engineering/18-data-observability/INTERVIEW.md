# Interview Questions: Data Observability

### Observability
**Q**: What are the 5 pillars of data observability?
**A**: Freshness (how recent?), Distribution (how similar to expected?), Volume (how complete?), Schema (how structured?), Lineage (how derived?)

### Anomaly Detection
**Q**: How do you detect data volume anomalies?
**A**: Compare to historical window (7/30/90 day moving average). Z-score or IQR-based outlier detection. Adjust for seasonality (day-of-week, month-end).

### Lineage
**Q**: Why is data lineage important for observability?
**A**: Impact analysis: if source table changes, which dashboards break? Root cause: if dashboard is wrong, which upstream stage caused it? Governance: track data flow for compliance.

### Soda vs Monte Carlo
**Q**: Compare Soda and Monte Carlo.
**A**: Soda: open-source, SQL-based checks, self-hosted, flexible. Monte Carlo: SaaS, ML-based anomaly detection, automatic lineage, managed incident workflows. Soda for control and cost, Monte Carlo for automated detection.
