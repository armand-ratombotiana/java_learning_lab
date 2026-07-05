# Security: Fraud Detection

## Data Protection
- PII (name, email, SSN) encrypted at rest (AES-256-GCM)
- PAN/CVV never stored; tokenized via vault
- All data encrypted in transit (TLS 1.3)
- Access control: ROLE_ADMIN, ROLE_ANALYST, ROLE_AUDITOR

## Model Security
- Model artifacts signed and checksum-verified before loading
- Adversarial attack detection: flag inputs designed to fool the model
- Training data sanitization: remove poisoned samples

## API Security
- API keys for service-to-service communication
- Rate limiting (100 req/s per service)
- Request logging with PII masking

## Monitoring
- Alert on unusual model degradation
- Audit trail of all fraud decisions
- Anomalous access patterns (e.g., bulk export of decisions)
