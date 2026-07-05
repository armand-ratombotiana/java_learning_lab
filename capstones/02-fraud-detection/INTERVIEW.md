# Interview: Fraud Detection

## Common Questions

### Q: Design a real-time fraud detection system for a payment processor.
Use events (Kafka), feature extraction, rule engine (low latency), async ML scoring, decision fusion, and feedback loop. Scale with Kafka partitions per user ID hash.

### Q: How do you handle concept drift in fraud models?
Monitor model accuracy over sliding windows. Trigger retraining when F1 drops below threshold. Use ensemble of models with different training windows (1d, 7d, 30d).

### Q: How do you balance false positives vs false negatives?
Set score thresholds based on cost analysis: avg transaction value * fraud rate vs customer support cost + churn risk. Tune threshold to optimize total cost.

### Q: What metrics matter most in fraud detection?
Precision (minimize false positives), Recall (minimize missed fraud), F1, P99 latency, fraud capture rate, percentage of transactions requiring manual review.

### Q: How do you handle adversarial attacks on the ML model?
Adversarial training, input sanitization, detect adversarial patterns (e.g., small perturbations), ensemble models, and human-in-the-loop for borderline cases.
