# Mock Interview: Backend Security Deep Dive (Lab 20)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What are the most common security vulnerabilities in REST APIs?

**Candidate:** The OWASP Top 10 for APIs includes:
1. **Broken Object Level Authorization (BOLA):** User A can access User B's data by changing IDs
2. **Broken Authentication:** Weak JWT secrets, missing token validation
3. **Excessive Data Exposure:** Returning full entities instead of DTOs
4. **Lack of Resources & Rate Limiting:** No throttling, enabling DDoS
5. **Broken Function Level Authorization:** Regular user accessing admin endpoints
6. **Mass Assignment:** Binding unexpected fields from request bodies
7. **Security Misconfiguration:** Default credentials, verbose error messages
8. **Injection:** SQL, NoSQL, command injection
9. **Improper Assets Management:** Undocumented APIs still exposed
10. **Insufficient Logging & Monitoring:** Can't detect attacks in progress

**Interviewer:** How do you prevent SQL injection in JPA/Hibernate?

**Candidate:** JPA/Hibernate is inherently resistant to SQL injection when using:
- **JPQL parameter binding:** `WHERE u.name = :name` (not string concatenation)
- **Criteria API:** `cb.equal(root.get("name"), name)`
- **Spring Data JPA derived queries:** `findByName(String name)` — auto-parameterized

However, raw SQL via `@Query(nativeQuery = true)` or `EntityManager.createNativeQuery` is vulnerable if parameters are concatenated. Always use `?` or `: named` parameter binding.

Additional layers: input validation with regex patterns, and a Web Application Firewall (WAF) for defense in depth.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How does Spring Security handle CORS? When would you configure it differently per environment?

**Candidate:** CORS (Cross-Origin Resource Sharing) is configured globally:
```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://app.example.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

Per-environment configuration using profiles:
```yaml
# application-dev.yml
cors:
  allowed-origins: http://localhost:3000, http://localhost:5173

# application-prod.yml
cors:
  allowed-origins: https://app.example.com
```

In production, restrict to specific domains, not wildcards (`*`), especially when `allowCredentials(true)` is used.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a security framework for a multi-tenant SaaS platform that handles PII data.

**Candidate:** 

**Multi-layered security architecture:**

**Layer 1 — Network Security:**
- Private VPC with no public IPs for database/backend
- WAF (CloudFront + AWS WAF or Cloud Armor) for OWASP Top 10
- API Gateway with per-client rate limiting (token bucket in Redis)
- TLS 1.3 for all communications

**Layer 2 — Authentication:**
- OAuth 2.0 + OIDC with Keycloak or Auth0
- MFA for admin users
- JWT with short expiry (15 min) + refresh tokens (7 days) in HTTP-only cookies
- JWT binding: tie token to client IP and user-agent

**Layer 3 — Authorization:**
```java
@PreAuthorize("@securityService.canAccessTenant(#tenantId)")
public CustomerData getCustomer(@PathVariable String tenantId, 
                                @PathVariable Long customerId) {
    return customerService.findById(customerId);
}
```
- Row-level security: every query filters by tenant ID
- VPD (Virtual Private Database) via Hibernate multi-tenancy (Lab 21)
- `@PostFilter` / `@PreFilter` for collection-level filtering

**Layer 4 — Data Security:**
- PII fields encrypted at rest (AES-256-GCM)
- Encryption keys in Key Vault (AWS KMS, Azure Key Vault)
- Audit logging for all PII access (who, what, when, why)
- Data masking: `@JsonView` with masked fields for non-privileged users

**Layer 5 — Rate Limiting:**
```java
rateLimiter.allowRequest(clientId, "api/customers", 1000, Duration.ofMinutes(1));
rateLimiter.allowRequest(ipAddress, null, 10000, Duration.ofMinutes(1)); // per IP
```
- Per-endpoint limits (write endpoints lower than read endpoints)
- Per-user limits
- Burst allowance with token bucket algorithm

**Layer 6 — Audit & Monitoring:**
- All security events logged: login attempts, access denials, role changes
- SIEM integration (Splunk, ELK)
- Anomaly detection: sudden traffic spike, unusual access patterns
- Alerting on: repeated 403s, brute force attempts, unusual data export

**Interviewer:** How would you implement encryption at rest for PII fields with Spring Boot?

**Candidate:** Using JPA attribute converters for transparent encryption:

```java
@Converter
@AutoApply
public class EncryptionConverter implements AttributeConverter<String, String> {
    private final EncryptionService encryption;
    
    @Override
    public String convertToDatabaseColumn(String plaintext) {
        return plaintext == null ? null : encryption.encrypt(plaintext);
    }
    
    @Override
    public String convertToEntityAttribute(String ciphertext) {
        return ciphertext == null ? null : encryption.decrypt(ciphertext);
    }
}

@Entity
public class Customer {
    @Convert(converter = EncryptionConverter.class)
    private String email; // Automatically encrypted in DB, decrypted in memory
    
    @Convert(converter = EncryptionConverter.class)
    private String ssn; // PII field
}
```

Key management: Keys stored in AWS KSM or Azure Key Vault, rotated every 90 days. Old keys retained for data decryption (envelope encryption: DEK encrypted by KEK).

---

## Interviewer Feedback

**Strengths:** Comprehensive security knowledge, practical multi-tenant design, clear encryption implementation  
**Areas to Improve:** Could discuss OAuth2 client credentials flow vs authorization code flow  
**Verdict:** Strong Hire

---

*Lab 20 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
