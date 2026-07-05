# Security: Vector Database

## Access Control
- API key authentication per collection
- Role-based access: READ, WRITE, ADMIN
- Tenant isolation via collection namespacing

## Data Protection
- Vectors may contain PII-adjacent information — encrypt at rest
- TLS 1.3 for all API communication
- Metadata field-level access control

## Injection Prevention
- Metadata query injection: validate and parameterize filter expressions
- Vector dimension validation: reject mismatched dimensions
- Size limits on batch operations to prevent DoS

## Monitoring
- Audit log of all write operations
- Rate limiting per API key (1000 req/s default)
- Anomalous query pattern detection (e.g., bulk export)
