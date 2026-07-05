# Reflection: ML Platform

## What I Learned
- Full ML lifecycle from data ingestion to production monitoring
- Feature store architecture for online/offline consistency
- Model registry and deployment pipeline design
- A/B testing and canary deployment patterns
- Drift detection and automated remediation

## Challenges
- Ensuring feature consistency between training and serving
- Building a model registry that doesn't become a bottleneck
- Implementing reliable A/B testing with statistical significance
- Debugging silent model degradation in production

## What I'd Do Differently
- Start with the feature store (most important infrastructure piece)
- Build monitoring from day one (not as an afterthought)
- Use a simpler deployment strategy initially (blue-green) before canary
- Add more comprehensive integration tests for the training pipeline
- Implement feature validation earlier (catching anomalies before they reach the model)
