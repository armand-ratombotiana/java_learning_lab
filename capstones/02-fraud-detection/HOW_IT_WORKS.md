# How It Works: Fraud Detection

1. Transaction event arrives via Kafka topic `raw.transactions`
2. Feature extraction: compute amount z-score, velocity windows, geo-distance, device entropy
3. Stage 1 — Rule Engine: evaluate rules in order (amount cap, velocity, blacklist, geo-anomaly)
4. If any rule returns REJECT (score > 90), transaction is blocked immediately
5. Stage 2 — ML Model: Isolation Forest predicts anomaly score asynchronously
6. Score fusion: weighted combination of rule score + ML score
7. Decision: APPROVE, REVIEW, or REJECT based on fused score thresholds
8. Decision published to `fraud.decisions` topic
9. Feedback loop: labeled transactions (confirmed fraud / false positive) used for model retraining
