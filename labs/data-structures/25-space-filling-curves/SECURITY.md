# Security: Space-Filling Curves

## Security Considerations

While Space-Filling Curves is primarily a data structure and not a security primitive, several security considerations apply when using it in production systems.

## Input Validation

Ensure that all inputs are validated before processing. Malicious inputs could trigger worst-case behavior or cause excessive resource consumption.

## Denial of Service

Hash-based variants can be vulnerable to hash collision attacks where an attacker deliberately crafts inputs that all hash to the same bucket, degrading performance from O(1) to O(n).

### Mitigation

- Use randomized hash functions with per-instance seeds
- Implement load shedding and request rate limiting
- Set maximum size limits on the structure
- Use cryptographic hash functions for security-critical applications

## Data Integrity

For structures used in security-critical contexts, consider adding integrity checks to detect corruption or tampering.

## Resource Exhaustion

Unbounded growth of the data structure can lead to memory exhaustion. Implement capacity limits and eviction policies where appropriate.

## Thread Safety

In concurrent environments, ensure proper synchronization. Race conditions can lead to corrupted internal state and security vulnerabilities.

## Best Practices

1. Use salted hash functions to prevent collision attacks
2. Validate all external inputs
3. Set appropriate size limits
4. Use synchronization in concurrent contexts
5. Log and monitor for anomalous behavior
