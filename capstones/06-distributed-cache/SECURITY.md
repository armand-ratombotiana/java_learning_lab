# Security: Distributed Cache

## Access Control
- Password-based authentication for client connections
- Allow/deny lists for client IP ranges
- Role-based commands: admin (FLUSH, SHUTDOWN) vs user (GET, SET)

## Network Security
- TLS 1.3 for client-server and inter-node communication
- mTLS between cluster nodes for mutual authentication
- Firewall rules: only expose client port, not gossip port

## Data Protection
- Encryption at rest for persisted data (RDB/AOF)
- No sensitive data in key names (avoid PII in keys)
- Key prefix isolation for multi-tenant deployments

## DoS Prevention
- Max connection limit per client
- Max memory per tenant
- Command rate limiting
- Slow command detection (e.g., large MGET, KEYS)
