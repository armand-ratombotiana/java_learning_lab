# Security for Real-Time Feature Store

## Access Control
- Feature-level access (some features are PII-sensitive)
- Entity-level access (user A can't query user B's features)
- API authentication (API keys, OAuth)

## Privacy
- PII masking in feature values
- Differential privacy for aggregate features
- Audit logging of all feature retrievals

## Infrastructure
- TLS for serving API
- Network isolation for online store
- Encryption at rest for offline store
