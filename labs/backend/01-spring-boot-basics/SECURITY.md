# Security

## Securing Spring Boot Applications

### Sensitive Properties
```properties
# ❌ Never hardcode secrets in application.properties
spring.datasource.password=letmein

# ✅ Use environment variables or external secret store
spring.datasource.password=${DB_PASSWORD}
```

### Actuator Security
```properties
# Restrict actuator endpoints
management.endpoints.web.exposure.include=health,info
management.endpoint.shutdown.enabled=false

# Secure actuator with authentication
management.endpoints.web.base-path=/internal/actuator
```

### Production Checklist
- [ ] Change default actuator paths
- [ ] Use secrets management (Vault, AWS Secrets Manager)
- [ ] Enable HTTPS
- [ ] Remove debug endpoints in production
- [ ] Run with least privileges
- [ ] Keep Spring Boot and dependencies updated (CVE patches)
- [ ] Use SBOM (Software Bill of Materials) generation

### Disable Auto-Configuration for Security
```java
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
```
