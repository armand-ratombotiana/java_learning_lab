# Security: Cache-Oblivious Data Structures

## Security Considerations

While primarily a data structure, security considerations apply in production systems.

## Denial of Service

Hash-based variants are vulnerable to collision attacks degrading performance from O(1) to O(n).

### Mitigation

Use randomized hash functions, implement rate limiting, set size limits, use cryptographic hashes for security-critical applications.

## Data Integrity

Add integrity checks for security-critical contexts.

## Resource Exhaustion

Implement capacity limits and eviction policies.

## Best Practices

Use salted hash functions, validate inputs, set size limits, use synchronization in concurrent contexts, and monitor for anomalous behavior.
