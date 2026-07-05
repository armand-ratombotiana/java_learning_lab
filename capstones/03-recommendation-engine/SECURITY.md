# Security: Recommendation Engine

## Data Protection
- User interaction data is PII-adjacent — anonymize user IDs in model artifacts
- Item metadata encrypted at rest
- All data in transit encrypted (TLS 1.3)
- Access control: ROLE_USER (own recs), ROLE_ADMIN (all data)

## Model Security
- Model poisoning: validate training data integrity
- Inference API rate limited (50 req/s per user)
- Recommendation results should not leak other users' data

## Privacy
- GDPR right to deletion: remove user interactions from training data
- Differential privacy during training (add noise to gradients)
- No raw PII in model features
