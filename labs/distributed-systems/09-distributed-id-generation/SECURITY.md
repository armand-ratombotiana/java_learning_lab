# Distributed ID Generation: Security

## Security Concerns
- Sequential IDs reveal growth rate and volume
- UUID v1 includes MAC address (privacy risk)
- Predictable IDs enable IDOR attacks
- Worker IDs may leak infrastructure information

## Best Practices
1. **Use unpredictable IDs** for public-facing resources
2. **Never expose MAC** (use random node ID)
3. **Add random bits** to prevent enumeration
4. **Validate access** independently of IDs (don't rely on ID obscurity)

```java
public class SecureIdGenerator {
    private final SnowflakeGenerator generator;
    
    public String generatePublicId() {
        long id = generator.nextId();
        // Mix bits to prevent time-based enumeration
        long mixed = mixBits(id);
        return Base62.encode(mixed);
    }
    
    private long mixBits(long id) {
        // XOR with random salt to obscure timestamp
        return id ^ SALT;
    }
}
```
