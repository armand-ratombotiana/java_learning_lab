# Distributed Caching: Security

## Security Concerns
- Cache contains sensitive data (PII, sessions, tokens)
- Unauthorized cache access
- Cache poisoning (inject malicious data)
- Side-channel attacks via cache timing

## Best Practices
1. **Encrypt cache data at rest** (Redis AOF with encryption)
2. **Use TLS for cache connections**
3. **Authentication** (Redis requirepass, ACL)
4. **Avoid caching sensitive data** (or encrypt values)
5. **TTL for sensitive data** (limit exposure window)

## Java Example
```java
public class SecureCache {
    private final Cache<String, byte[]> cache;
    private final Cipher cipher;
    
    public void putSecure(String key, Object value) {
        byte[] encrypted = encrypt(serialize(value));
        cache.put(key, encrypted);
    }
    
    public <T> T getSecure(String key, Class<T> type) {
        byte[] encrypted = cache.get(key);
        if (encrypted == null) return null;
        return deserialize(decrypt(encrypted), type);
    }
}
```
