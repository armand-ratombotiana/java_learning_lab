# Caching - SECURITY

## Sensitive Data in Cache

### Never Cache Sensitive Information
```java
// WRONG: Caching passwords or PII
@Cacheable("users")
public User getUser(String id) {
    return userRepository.findById(id);  // includes password hash!
}

// RIGHT: Cache only non-sensitive fields
@Cacheable("userProfiles")
public UserProfile getUserProfile(String id) {
    User user = userRepository.findById(id);
    return new UserProfile(user.getName(), user.getEmail());  // no passwords
}
```

## Cache Poisoning

### Attack Vector
Attacker writes malicious data to cache by exploiting miss-load flow.

### Prevention
```java
// Validate data before caching
public Product getProduct(String id) {
    return cache.get(id, k -> {
        Product p = repository.findById(k).orElseThrow();
        validateProduct(p);  // prevent cache poisoning
        return p;
    });
}
```

## Cache Snooping

### Shared Cache Risk
In multi-tenant systems, one tenant's data must not be accessible to another.

### Tenant Isolation
```java
public class TenantCacheResolver implements CacheResolver {
    @Override
    public Collection<? extends Cache> resolveCaches(CacheOperationInvocationContext<?> context) {
        String tenantId = TenantContext.getCurrentTenant();
        return List.of(cacheManager.getCache(tenantId + "_products"));
    }
}
```

## Cache Security Configuration

### Redis Security
```yaml
# Require authentication
spring.redis.password: ${REDIS_PASSWORD}

# TLS encryption
spring.redis.ssl: true

# Network isolation (security group)
# Only allow application servers to connect
```

### Encryption at Rest
```yaml
# Redis Enterprise or ElastiCache encryption
encryption-at-rest: true
encryption-in-transit: true
```

## Cache Stampede as DoS Vector
Attackers can send many unique requests to intentionally cause cache misses, flooding the database. Mitigate with rate limiting and caching negative results.

## Access Control
```java
@PreAuthorize("hasPermission(#id, 'Product', 'READ')")
@Cacheable(value = "products", key = "#id")
public Product getProduct(String id) {
    return repository.findById(id).orElseThrow();
}
```
