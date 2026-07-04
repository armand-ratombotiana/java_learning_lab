# Consistency Models: Security

## Security Implications

### Strong Consistency
- Access control decisions are immediately visible
- Audit logs are trustworthy
- Token revocation takes effect immediately

### Weak Consistency
- Stale ACLs may allow unauthorized access
- Audit trails may have gaps
- Revoked tokens may still work temporarily

## Best Practices
1. Use strong consistency for security-critical operations
2. Implement defense-in-depth (validate at multiple layers)
3. Monitor consistency violations as security events
4. Use monotonic reads for security token verification
