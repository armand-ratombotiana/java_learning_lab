# Mock Interview: Anomaly Detection for Credit Card Transactions

**Topic:** Design an anomaly detection system for credit card transactions

## System Design

### Q1: What are the requirements?

**Answer:**
- **Real-time detection:** Flag transactions in < 100ms
- **High imbalance:** ~0.01% fraudulent transactions
- **Evolving patterns:** Fraudsters adapt
- **Regulatory:** Explainability for flagged transactions
- **Scale:** Millions of transactions/day
- **Cost-sensitive:** False negatives (missed fraud) cost more than false positives

### Q2: Design the architecture.

```
Streaming Pipeline:
  Transaction Event → Feature Engineering → Model Ensemble → Decision → Alert

Offline Pipeline:
  Historical Data → Feature Store → Model Training → Candidate Models → Evaluation → Deploy

Components:
  - Feature Store: Aggregates per-user features (velocity, location, amount statistics)
  - Model Ensemble: Combines multiple detectors
  - Drift Monitor: Tracks feature/model distribution shifts
  - Human-in-loop: Review flagged transactions for feedback
```

### Q3: What features would you use?

**Answer:**
- **Transaction features:** Amount, merchant category, time of day, day of week
- **User features:** Average transaction amount, frequency, typical merchant types
- **Velocity features:** Number of transactions in last 1h/6h/24h, sum of amounts
- **Geographic features:** Distance from last transaction, country mismatch
- **Device/IP features:** Device fingerprint, IP reputation, VPN detection
- **Behavioral:** Session duration, typing speed, navigation patterns (if web)

### Q4: What models would you use?

**Answer:**
Unsupervised + Supervised ensemble approach:

| Model | Type | Role |
|-------|------|------|
| **Isolation Forest** | Unsupervised | Baseline — isolates anomalies by random partitioning |
| **Autoencoder** | Unsupervised | Reconstruction error → anomaly score |
| **XGBoost** | Supervised | Learns known fraud patterns |
| **Graph Neural Network** | Semi-supervised | Captures merchant-cardholder relationship networks |
| **Online Learning (Hedgeback/AGGM)** | Streaming | Adapts to concept drift |

**Ensemble:** Weighted combination of model scores. Weights adapt based on recent performance.

### Q5: How do you evaluate and handle class imbalance?

**Answer:**
- **Metrics:** Precision-Recall curve, AUC-PR (not ROC — too optimistic with high imbalance), F-beta (weighted for recall), False Positive Rate at high recall thresholds
- **Sampling:** SMOTE, ADASYN, or near-miss for training; avoid in production
- **Cost-sensitive learning:** Weight false negatives higher in loss function
- **Threshold tuning:** Use validation set to find optimal threshold for business cost

### Q6: How do you handle concept drift and feedback loops?

**Answer:**
- **Drift detection:** Monitor feature means/std, prediction distribution, model accuracy on human-reviewed samples
- **Retraining schedule:** Hourly/daily incremental updates + weekly full retraining
- **Online learning models:** Keep streaming models updated with each verified transaction
- **Champion/challenger:** A/B test new models against current production
- **Feedback loop:** Customer disputes, manual reviews → labeled data → next training cycle

## Advanced

- **Adversarial robustness:** Fraudsters probe to learn detection rules — need adversarial training
- **Explainability:** SHAP values for flagged transactions (regulatory requirement)
- **Privacy:** Homomorphic encryption or federated learning for cross-bank fraud detection
- **Graph analysis:** Detect fraud rings via community detection on transaction graph
