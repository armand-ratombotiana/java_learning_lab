# Reflection: Fraud Detection

## What I Learned
- Real-time ML inference in Java with ONNX Runtime
- Stream processing patterns for time-windowed aggregations
- Feature engineering for behavioral anomaly detection
- Trade-offs between rule latency and ML accuracy

## Challenges
- Tuning Isolation Forest threshold for acceptable false positive rate
- Debugging feature staleness in Redis causing missed fraud
- Managing model versioning and rollback during production incidents

## What I'd Do Differently
- Build the feature extraction pipeline first and test separately from ML
- Implement more comprehensive monitoring on feature distributions
- Start with a simpler model (logistic regression) before Isolation Forest
- Add automated canary testing for new model versions
