# API Security — Study Guide

## Core Concepts

### Authentication
- **API Keys**: simple but must be hashed at rest, rotated regularly
- **JWT**: stateless, self-contained, validate signature and claims
- **mTLS**: mutual certificate authentication for machine-to-machine
- **OAuth2 Client Credentials**: standard for service-to-service

### Rate Limiting
- **Token Bucket**: tokens refill at constant rate; burst allowed up to bucket size
- **Sliding Window**: count requests in last N seconds; more accurate than fixed window
- **Per-user vs global**: per-user for fairness, global for overall protection

### OWASP API Top 10 (2023)
1. Broken Object Level Authorization
2. Broken Authentication
3. Broken Object Property Level Authorization
4. Unrestricted Resource Consumption
5. Broken Function Level Authorization
6. Unrestricted Access to Sensitive Business Flows
7. Server Side Request Forgery
8. Security Misconfiguration
9. Improper Inventory Management
10. Unsafe Consumption of APIs

## Implementation Checklist
1. Always validate Content-Type and Accept headers
2. Rate limit by user/API key, not just IP
3. Validate input schema before processing
4. Never expose internal data structures in responses
5. Return generic error messages (don't leak details)

## Common Pitfalls
- Returning stack traces or database errors in responses
- Not rate-limiting authentication endpoints (brute force)
- Exposing internal IDs (use UUID instead of auto-increment)
- CORS misconfiguration allowing arbitrary origins
