# Security: Search Engine

## Access Control
- Document-level security: filter search results based on user permissions
- Field-level security: restrict access to sensitive fields (e.g., salary)
- Index-level access: separate indices for different tenants

## Injection Prevention
- Query parser must handle special characters safely
- No raw query string execution (always parse through query parser)
- Limit query complexity (max boolean clauses, max query depth)

## Data Protection
- Index files may contain sensitive data — encrypt at rest
- TLS 1.3 for search API
- Avoid logging full document content (strip sensitive fields)

## DoS Prevention
- Max query size (max bytes, max terms)
- Timeout per query (circuit breaker)
- Rate limiting per API key/user
- Max result window (cannot request page 10000)
