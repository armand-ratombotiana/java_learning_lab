# Security: Banking Platform

## Authentication & Authorization
- OAuth2 / JWT tokens validated at Gateway Service
- Role-based access: USER, ADMIN, AUDITOR
- Service-to-service mTLS
- API keys for external integrations

## Data Protection
- PII encrypted at rest (AES-256)
- PAN/PCI data tokenized via vault service
- TLS 1.3 in transit
- Audit logging of all data access

## Fraud Prevention
- Idempotency keys prevent replay attacks
- Rate limiting per user/account/IP
- Suspicious activity monitoring
- Geo-velocity checks

## Compliance
- GDPR: Right to deletion, data portability
- PSD2: Strong customer authentication (SCA)
- SOX: Audit trail integrity
- PCI-DSS: Cardholder data protection
