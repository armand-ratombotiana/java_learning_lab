# Mock Interview: Anomaly Detection

**Topic:** Design anomaly detection for production ML systems

## Core Questions

### Q1: What types of anomalies exist?

**Answer:**
- **Point anomalies:** Single instance deviating from norm (e.g., fraudulent transaction)
- **Contextual anomalies:** Anomalous in specific context (e.g., 30°C in winter but not summer)
- **Collective anomalies:** Set of points anomalous together (e.g., DDoS attack traffic pattern)

### Q2: Compare anomaly detection approaches.

**Answer:**
| Method | Type | Pros | Cons |
|--------|------|------|------|
| **Isolation Forest** | Unsupervised | Fast, scalable, interpretable | Assumes anomaly isolation is easier |
| **LOF** | Unsupervised | Local density estimation | $O(n^2)$ expensive |
| **One-Class SVM** | Unsupervised | Handles non-linear boundaries | Sensitive to $\nu$ parameter |
| **Autoencoder** | Unsupervised | Flexible, handles high-D | Needs tuning, threshold selection |
| **Gaussian Mixture Model** | Unsupervised | Probabilistic | Assumes Gaussian components |
| **XGBoost + SMOTE** | Supervised | Best accuracy | Needs labeled anomalies |
| **Online detectors** | Streaming | Adapts to drift | More complex |

### Q3: Design a production anomaly detection pipeline.

**Answer:**
```
Stream Pipeline:
  Raw Data → Feature Extraction → Skew/Null Checks → Model Scoring
    → Threshold → Alert (Slack/PagerDuty) → Dashboard

Offline Pipeline:
  Historical Data → Labeling (if any) → Feature Engineering
    → Train Multiple Models → Select Best → Deploy

Components:
  - Statistical monitors: Z-score, MAD, IQR on key metrics
  - ML models: Isolation Forest + Autoencoder (ensemble)
  - Rule engine: Known failure patterns (hard-coded)
  - Drift detection: KS test, PSI on feature distributions
  - Feedback loop: Incidents → labels → retraining
```

### Q4: How do you evaluate anomaly detection?

**Answer:**
- **Precision @ k:** Of top-k alerts, how many are real anomalies
- **Recall @ k:** What fraction of real anomalies are caught in top-k
- **F-beta:** Weight Fβ higher for recall (missing anomalies is costly)
- **Time-to-detection:** Mean time from anomaly occurrence to detection
- **False positive rate:** Per day/week — affects trust in system
- **AUC-PR:** Better than ROC for imbalanced anomaly detection
- **Business metrics:** $ saved, incidents mitigated

**Challenge:** Ground truth labeling is expensive. Use:
- Simulation with injected anomalies
- Labeled historical incidents
- Human-in-the-loop partial verification

### Q5: How do you select anomaly thresholds?

**Answer:**
- **Percentile-based:** Flag top 1%/5% of anomaly scores
- **Statistical:** Mean + $k \times$ std of scores
- **Peak-over-threshold (POT):** Model extreme values with GPD (extreme value theory)
- **Business cost threshold:** Minimize cost(false_negative) + cost(false_positive)
- **Adaptive:** Adjust threshold based on recent false positive rate
- **Unsupervised:** Use validation set with injected anomalies to calibrate

## Advanced

- **Ensemble for anomaly detection:** Combine scores from multiple detectors (Weighted average, min, max, median)
- **Streaming anomaly detection:** Reservoir sampling, sliding window statistics, online isolation forest
- **Adversarial anomalies:** Assume attacker adapts to detection rules — need robust models
- **Explainability for anomalies:** SHAP to explain why a point is anomalous (feature contributions)
- **Multi-modal anomaly detection:** Text + images + structured data together
