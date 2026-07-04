# Interview Questions: 01-authentication-basics

## Junior Developer Questions

### Q1: What is Spring Security?
**A:** A powerful and customizable authentication and access-control framework for Java applications.

### Q2: Difference between authentication and authorization?
**A:** Authentication verifies who the user is (identity), authorization determines what resources they can access (permissions).

### Q3: How does BCrypt work?
**A:** A password hashing function that incorporates a salt to protect against rainbow table attacks. It's intentionally slow to prevent brute-force attacks.

### Q4: What is a SecurityFilterChain?
**A:** An ordered list of servlet filters that Spring Security uses to process HTTP requests.

### Q5: What does @EnableWebSecurity do?
**A:** Enables Spring Security's web security support and integrates it with Spring MVC.

## Mid-Level Developer Questions

### Q6: Explain the Spring Security authentication architecture.
**A:** SecurityFilterChain intercepts requests -> AuthenticationManager processes auth -> ProviderManager delegates to AuthenticationProviders -> AuthenticationProvider validates credentials -> UserDetailsService loads user info -> SecurityContext holds the result.

### Q7: How to implement remember-me authentication?
**A:** Configure remember-me in HttpSecurity, choose token repository, set token validity period. RememberMeAuthenticationFilter handles automatic login from the cookie.

### Q8: What is session fixation and how to prevent it?
**A:** An attack where an attacker sets a user's session ID to a known value. Prevent by using sessionFixation().migrateSession() or sessionFixation().newSession().

### Q9: How to configure CORS in Spring Security?
**A:** Create CorsConfigurationSource bean defining allowed origins, methods, and headers, then apply with http.cors().

### Q10: Explain method-level security annotations.
**A:** @Secured, @PreAuthorize, @PostAuthorize, @PreFilter, @PostFilter. @PreAuthorize is most flexible, supporting SpEL expressions.

## Senior Developer Questions

### Q11: Design a multi-tenancy authentication system.
**A:** Tenant-aware UserDetailsService, multi-tenant token storage, tenant isolation in security context, custom AuthenticationProvider for tenant resolution.

### Q12: How to scale Spring Security in microservices?
**A:** Stateless JWT authentication, API Gateway handles initial auth, token relay with RestTemplate interceptor, shared authorization service, distributed session management with Redis.

### Q13: How to implement a custom authentication provider?
**A:** Implement AuthenticationProvider interface, override authenticate() with custom logic, override supports(), register the provider.

### Q14: Design a zero-trust security architecture.
**A:** Every request authenticated regardless of origin, mutual TLS for service communication, fine-grained authorization, continuous verification, no implicit trust based on network location.

## System Design Questions

### Q15: Design an authentication system for 10M users.
**A:** Read replicas for user data, Redis caching, CDN, rate limiting, horizontal scaling with stateless JWT, async email service, monitoring.

### Q16: How to implement SSO across multiple applications?
**A:** Use OAuth2 authorization server (Keycloak), configure each app as OAuth2 client, centralized session management, cross-application logout.
