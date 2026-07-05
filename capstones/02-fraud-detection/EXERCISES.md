# Exercises: Fraud Detection

## Beginner
1. Add a new rule: reject transactions from high-risk countries (ISO code list)
2. Implement a simple count-based velocity rule (max 3 tx/min)
3. Create a REST endpoint to view recent fraud decisions

## Intermediate
4. Implement the feedback pipeline: consume labeled transactions, store to PostgreSQL
5. Add a model registry with version tracking and rollback support
6. Implement A/B testing: route 10% of traffic to new model version
7. Write a Kafka Streams topology that joins transactions with user profiles

## Advanced
8. Build a real-time fraud dashboard using WebSockets (live score stream)
9. Implement explainable AI: compute SHAP values for each fraud decision
10. Add graph-based fraud detection (link analysis between accounts/devices)
11. Implement adversarial input detection and sanitization
