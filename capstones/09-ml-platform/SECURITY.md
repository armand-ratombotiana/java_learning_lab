# Security: ML Platform

## Access Control
- Feature store access control: READ, WRITE per feature group
- Model registry access: VIEWER, CONTRIBUTOR, ADMIN roles
- API key or OAuth2 for prediction API

## Data Protection
- Features may contain PII — encrypt sensitive feature groups at rest
- Model artifacts may leak training data — restrict download access
- TLS 1.3 for all API communication

## Model Security
- Model poisoning: validate training data integrity
- Adversarial attacks: input sanitization for prediction API
- Model theft: rate limit prediction API, restrict batch size

## Compliance
- Audit logging of model deployments and predictions
- Training data lineage tracking for regulatory compliance
- Model explainability records for regulated industries
