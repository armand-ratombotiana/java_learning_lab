# Mock Interview — Spring Security Advanced

## Interviewer: Senior Software Engineer — Security (45 min)

**Q1: How do you configure a Spring Boot application as an OAuth 2.0 resource server?**

Candidate: Modern approach uses Spring Security 6+ with OAuth2 Resource Server auto-configuration. In application.yml: spring.security.oauth2.resourceserver.jwt.issuer-uri: https://auth.example.com/realms/myrealm. Spring Security fetches the provider's configuration from {issuer-uri}/.well-known/openid-configuration, discovers the JWKS endpoint, and caches the public keys. For the Java configuration:
```java
@Configuration @EnableWebSecurity
public class SecurityConfig {
    @Bean SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt
                .decoder(jwtDecoder())
                .jwtAuthenticationConverter(jwtAuthConverter())
            ));
        return http.build();
    }
    @Bean JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
            .withJwkSetUri("https://auth.example.com/realms/myrealm/protocol/openid-connect/certs")
            .jwsAlgorithm(SignatureAlgorithm.RS256)
            .build();
    }
}
```
For NimbusJwtDecoder, configure the cache duration for JWK set (default 5 minutes, can be adjusted). Handle JWK set retrieval failures with a CircuitBreaker pattern. Configure clock skew (30 seconds recommended). For testing, use a local JWKS endpoint or mock the JwtDecoder.

**Q2: How do you implement custom authorization rules beyond role checks?**

Candidate: Implement GrantedAuthoritiesMapper to extract custom claims from JWT tokens into Spring Security authorities. Example: extract Keycloak realm roles: jwt.getClaimAsStringList("realm_access"). For complex rules, implement PermissionEvaluator:
```java
@Component public class DocumentPermissionEvaluator implements PermissionEvaluator {
    @Override public boolean hasPermission(Authentication auth, Object target, Object perm) {
        if (!(auth.getPrincipal() instanceof UserPrincipal user)) return false;
        Document doc = (Document) target;
        return switch ((String) perm) {
            case "READ" -> doc.isPublic() || doc.getOwnerId().equals(user.getId());
            case "WRITE" -> doc.getOwnerId().equals(user.getId()) || user.hasRole("EDITOR");
            case "DELETE" -> user.hasRole("ADMIN");
            default -> false;
        };
    }
}
```
Register in MethodSecurityConfig: @Bean PermissionEvaluator documentPermissionEvaluator(). Use via: @PreAuthorize("hasPermission(#documentId, 'Document', 'READ')"). For domain-driven authorization, consider Spring Security ACL (legacy) or use a dedicated authorization service with Open Policy Agent (OPA) for complex policies.

**Q3: How do you secure a WebFlux (reactive) application with Spring Security?**

Candidate: Use @EnableWebFluxSecurity instead of @EnableWebSecurity. Configure SecurityWebFilterChain:
```java
@Configuration @EnableWebFluxSecurity
public class ReactiveSecurityConfig {
    @Bean SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.authorizeExchange(ex -> ex
            .pathMatchers("/api/auth/**").permitAll()
            .anyExchange().authenticated()
        ).oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtDecoder(reactiveJwtDecoder()))
        );
        return http.build();
    }
    @Bean ReactiveJwtDecoder reactiveJwtDecoder() {
        return NimbusReactiveJwtDecoder
            .withJwkSetUri("https://auth.example.com/.well-known/jwks.json")
            .build();
    }
}
```
Use reactive SecurityContextHolder: Mono.just(ctx).map(SecurityContext::getAuthentication). Then retrieve in controllers via @AuthenticationPrincipal Mono. Reactive security is non-blocking end-to-end — avoid calling blocking APIs (like JDBC) in the security context.

**Q4: How do you test Spring Security configuration with JWT authentication?**

Candidate: Use @WebMvcTest for controller tests. For JWT-based APIs, create a test factory method that generates valid JWTs for tests. Use @WithMockJwt annotation:
```java
@Retention(RUNTIME) @WithSecurityContext(factory = WithMockJwtSecurityContextFactory.class)
public @interface WithMockJwt {
    String[] roles() default {"USER"};
    String subject() default "user123";
}
```
In the factory, create a JwtAuthenticationToken with test claims. Test scenarios: (1) Missing token returns 401. (2) Invalid token returns 401. (3) Expired token returns 401. (4) Valid token with insufficient role returns 403. (5) Valid token with required role returns 200. For integration tests, spin up a test auth server or use wiremock to mock the JWKS endpoint. Use @SpringBootTest with AutoConfigureMockMvc for end-to-end testing.

**Q5: How would you implement multi-tenancy in Spring Security where tenant is identified from different sources?**

Candidate: Multi-tenancy has multiple patterns. Tenant from subdomain: org1.app.com, org2.app.com. Extract subdomain via request filter, set TenantContext (ThreadLocal). Tenant from header: X-Tenant-Id header passed by the API gateway. Implement a OncePerRequestFilter that reads the header and populates TenantContext. Tenant from JWT claim: Include tenant_id in the JWT, extract in JwtAuthenticationConverter. Tenant from URL path: /api/{tenant}/resource. Implementation: (1) Create TenantContext with ThreadLocal<TenantId>. (2) Implement filter that sets TenantContext before security processing. (3) Extend JwtAuthenticationConverter to populate tenant from JWT claim. (4) For authorization, include tenant in access decisions: @PreAuthorize("@tenantAccess.check(#tenantId)"). (5) For database isolation, use separate databases per tenant (Saas), schema per tenant, or tenant-scoped queries. Use Hibernate multi-tenancy with SCHEMA or DATABASE approach. Ensure tenant ID propagation in async contexts — use ContextSnapshot or manual propagation.
