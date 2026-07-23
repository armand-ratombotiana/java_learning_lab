# Mock Interview: Multi-Tenancy Patterns (Lab 21)

**Role:** Backend Engineer (Senior)  
**Duration:** 45 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** What is multi-tenancy and what are the main strategies?

**Candidate:** Multi-tenancy is a software architecture where a single instance serves multiple tenants (customers/organizations) with data isolation. The three main strategies:

| Strategy | Isolation | Cost | Complexity | Best For |
|----------|-----------|------|------------|----------|
| **Database per tenant** | Strongest | High | Medium | Enterprise, regulated data |
| **Schema per tenant** | Strong | Medium | Low-Medium | Mid-market SaaS |
| **Discriminator column** | Weakest | Low | Low | Small apps, low sensitivity |

**Interviewer:** How does Hibernate support multi-tenancy?

**Candidate:** Hibernate provides `MultiTenancyConnectionProvider` and `CurrentTenantIdentifierResolver`. Spring Boot can configure these beans:
```java
@Bean
public CurrentTenantIdentifierResolver<String> tenantResolver() {
    return new TenantContext(); // reads tenant from ThreadLocal
}

@Bean
public MultiTenantConnectionProvider<String> connectionProvider() {
    return new SchemaPerTenantProvider(dataSource);
}
```

Hibernate intercepts every SQL query and prefixes with the appropriate schema or switches the connection based on the tenant.

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How do you resolve the tenant identifier in a Spring Boot application?

**Candidate:** Common tenant resolution strategies:

1. **JWT claim:** Extract tenant from authenticated user's JWT `iss` or custom claim
2. **Subdomain:** `tenant1.app.example.com` — extract from `Host` header
3. **Header:** `X-Tenant-ID: tenant1` — custom HTTP header
4. **URL path:** `/api/tenant1/users` — path variable
5. **Request parameter:** `?tenantId=tenant1`

Implementation:
```java
@Component
public class TenantInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, 
                            Object handler) {
        String tenantId = request.getHeader("X-Tenant-ID");
        if (tenantId == null) throw new TenantNotFoundException();
        TenantContext.setCurrentTenant(tenantId);
        return true;
    }
    
    @Override
    public void afterCompletion(...) {
        TenantContext.clear(); // Prevent memory leaks
    }
}
```

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a multi-tenant SaaS analytics platform that handles 1000+ tenants, some with 1M records and some with 1B+ records. How do you choose the right tenancy model?

**Candidate:** Hybrid approach — **database per tier, schema per tenant:**

**Tier-based tenancy:**
```
Tier 1 (Free/Starter): Discriminator column (shared table, tenant_id column)
Tier 2 (Business): Schema per tenant (shared database, separate schemas)
Tier 3 (Enterprise): Database per tenant (isolated database)
```

**Promotion path:** When a tenant upgrades from Tier 1 to Tier 2, data is migrated from shared tables to a dedicated schema. From Tier 2 to Tier 3, the schema is migrated to a dedicated database.

**Implementation:**
```java
public interface TenantAware {
    String getTenantId();
    void setTenantId(String tenantId);
}

// Discriminator approach for Tier 1
@Entity
@Table(name = "analytics_events")
public class AnalyticsEvent implements TenantAware {
    @Id private Long id;
    private String tenantId; // filter on all queries
    private String eventData;
}

// Schema per tenant for Tier 2
// Same entity, different schema based on tenant
public class SchemaPerTenantProvider implements MultiTenantConnectionProvider {
    @Override
    public Connection getConnection(String tenantIdentifier) throws SQLException {
        Connection conn = dataSource.getConnection();
        conn.createStatement().execute("SET SCHEMA '" + tenantIdentifier + "'");
        return conn;
    }
}
```

**Scaling considerations:**
- Connection pooling: Separate pool per database (Tier 3) vs shared pool (Tier 1/2)
- Backup strategy: Per-tenant backup for Enterprise, shared backup for lower tiers
- Query performance: Discriminator columns need composite indexes on (tenant_id, query_column)
- Cross-tenant analytics: Separate data warehouse with anonymized data

**Interviewer:** How do you handle cross-tenant operations (admin, billing, reporting)?

**Candidate:** A privileged admin context that can operate across tenants:
```java
@PreAuthorize("hasRole('SYSTEM_ADMIN')")
public List<UsageReport> getAllTenantsUsage() {
    // Temporarily disable tenant filter
    TenantContext.runAsSystem(() -> {
        return usageRepository.findAllUsage();
    });
}
```

Use a `SYSTEM` tenant context that bypasses filtering. For billing, use a separate billing service with its own tenant mapping that aggregates across tenants for invoice generation. Cross-tenant reporting uses a separate data warehouse that aggregates anonymized data.

---

## Interviewer Feedback

**Strengths:** Clear understanding of all three tenancy strategies, practical tier-based hybrid approach  
**Areas to Improve:** Could discuss tenant migration strategies and zero-downtime migration  
**Verdict:** Strong Hire

---

*Lab 21 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
