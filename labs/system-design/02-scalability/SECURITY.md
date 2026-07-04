# Scalability - SECURITY

## Security Challenges at Scale

### Distributed Auth
Centralized authentication becomes a bottleneck. Solutions:
- **JWT**: Stateless tokens, no session lookup
- **OAuth2 + API Gateway**: Validate at edge, propagate internally
- **mTLS**: Service-to-service mutual TLS

### Rate Limiting at Scale
```java
// Distributed rate limiting with Redis
@Aspect
@Component
public class RateLimitAspect {
    private final RedisTemplate<String, String> redis;

    @Around("@annotation(rateLimit)")
    public Object rateLimit(ProceedingJoinPoint pjp, RateLimit rateLimit) {
        String key = "ratelimit:" + getClientIp();
        Long count = redis.opsForValue().increment(key);
        if (count == 1) redis.expire(key, rateLimit.window(), TimeUnit.SECONDS);
        if (count > rateLimit.limit()) throw new RateLimitException();
        return pjp.proceed();
    }
}
```

### DDoS Protection
- **CDN + WAF**: Absorb and filter at edge
- **Auto-scaling**: Absorb traffic spikes
- **Anycast DNS**: Distribute across regions
- **Load shedding**: Drop non-critical traffic under extreme load

### Secrets Management for Auto-Scaling
```yaml
# Use Vault or AWS Secrets Manager
# Don't bake secrets into AMIs/containers
secrets:
  db_password: ${vault:secret/database/password}
  api_key: ${aws:secretsmanager:prod/api-key}
```

### Security at Each Scale Layer

| Layer | Security Measure |
|-------|-----------------|
| Edge | WAF, DDoS protection, Rate limiting |
| Load Balancer | TLS termination, IP whitelisting |
| Application | JWT validation, RBAC |
| Cache | Encryption at rest, access control |
| Database | Network isolation, encryption, audit logs |
