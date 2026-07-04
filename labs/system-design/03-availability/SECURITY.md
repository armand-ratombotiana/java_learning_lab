# Availability - SECURITY

## Availability as a Security Concern

### Denial of Service
- Availability is the "A" in the CIA triad (Confidentiality, Integrity, Availability)
- DDoS attacks target availability directly
- Ransomware encrypts data and holds availability hostage

## DDoS Protection

### Network Level
```yaml
# AWS Shield Advanced
# Cloudflare Magic Transit
# Azure DDoS Protection Standard
```

### Application Level
```java
@RateLimiter(name = "apiRateLimiter")
@GetMapping("/api/orders")
public List<Order> getOrders() { /* ... */ }

// Rate limit config
resilience4j.ratelimiter:
  instances:
    apiRateLimiter:
      limit-for-period: 100
      limit-refresh-period: 1s
      timeout-duration: 500ms
```

### Infrastructure Level
- Auto-scaling to absorb traffic
- WAF to filter malicious requests
- CDN to cache and absorb

## Disaster Recovery Security

### Backup Security
```yaml
# Encrypted backups
# Offsite storage
# Access control for restore
backup:
  encryption: AES-256
  storage: s3://backups (encrypted, versioned)
  access: read-only for backup tool, write for restore only
```

### Failover Security
- Standby must have same security controls as primary
- Certificate rotation must work across failover
- Audit logs must be centralized (survive instance loss)

## Deployment Security

### Immutable Infrastructure
```
// No SSH access to instances
// Config changes require redeployment
// Canary deployments for safe rollouts
```

### Zero-Trust for Failover
```
// mTLS between all services
// Even standby must authenticate
// No trusted network assumption
```

## Incident Response Security

### During Outages
- Attackers often strike during recovery (chaos)
- Use break-glass procedures with audit trail
- Post-incident review for security lessons
- Rotate credentials exposed during incident
