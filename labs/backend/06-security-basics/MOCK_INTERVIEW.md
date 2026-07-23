# Mock Interview: Security Basics (Lab 06)

**Role:** Backend Engineer (Mid-Level)  
**Duration:** 40 minutes  
**Difficulty Progression:** Easy → Medium → Hard

---

## Round 1: Easy (5-10 minutes)

**Interviewer:** Explain the difference between authentication and authorization.

**Candidate:** Authentication verifies **who** the user is (identity). Authorization determines **what** they can do (permissions). In Spring Security:
- Authentication is handled by `AuthenticationManager` → `AuthenticationProvider` chain
- Authorization is handled by `AccessDecisionManager` and voted by voters
- Common pattern: authentication via JWT/OAuth2, then role-based or scope-based authorization

**Interviewer:** How does Spring Security's filter chain work?

**Candidate:** Spring Security inserts a chain of servlet filters between the client and the application. The `DelegatingFilterProxy` registers a single filter in the servlet container that delegates to Spring-managed `SecurityFilterChain` beans. Each chain contains ordered filters like:
1. `SecurityContextPersistenceFilter` — restores `SecurityContext` from session/token
2. `UsernamePasswordAuthenticationFilter` — processes login form submissions
3. `ExceptionTranslationFilter` — handles access denied exceptions
4. `FilterSecurityInterceptor` — enforces access rules before controller execution

---

## Round 2: Medium (10-15 minutes)

**Interviewer:** How would you implement JWT-based authentication in Spring Boot?

**Candidate:** JWT authentication flow:
1. User sends credentials to `/api/auth/login`
2. Server validates credentials, generates JWT with claims (userId, roles, expiry)
3. Client stores JWT (typically in HTTP-only cookie or Authorization header)
4. Client sends JWT in `Authorization: Bearer <token>` header on subsequent requests
5. A custom `OncePerRequestFilter` extracts JWT, validates signature, creates `UsernamePasswordAuthenticationToken`
6. Token is set in `SecurityContextHolder`

Implementation:
```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                   HttpServletResponse response, FilterChain chain) {
        String token = extractToken(request);
        if (token != null && jwtService.validateToken(token)) {
            Authentication auth = jwtService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(auth);
        }
        chain.doFilter(request, response);
    }
}
```

Key considerations: token refresh, blacklisting for logout, short expiry (~15 min for access, ~7 days for refresh).

**Interviewer:** How do you protect against CSRF attacks in a REST API?

**Candidate:** For browser-based state-changing requests, CSRF tokens prevent cross-origin attacks. Spring Security enables CSRF by default for any request modifying state (`POST`, `PUT`, `DELETE`, `PATCH`). However, for stateless REST APIs using JWT/Bearer tokens (where the browser doesn't automatically attach credentials), CSRF protection is typically disabled because there's no session cookie to exploit. For session-based auth with Thymeleaf, CSRF protection remains critical and Thymeleaf auto-includes CSRF tokens in forms.

---

## Round 3: Hard (15-20 minutes)

**Interviewer:** Design a multi-factor authentication (MFA) system for an admin portal using Spring Security.

**Candidate:** Architecture:
1. **First factor (password):** Standard `UsernamePasswordAuthenticationFilter` validates credentials
2. **After password success, before full access:** Custom filter checks if MFA is required for this user
3. **Second factor (TOTP):** User enters 6-digit code from authenticator app
4. **MFA token issuance:** On successful MFA, issue a JWT with `amr` (authentication methods reference) claim

Implementation with a "partial authentication" pattern:
```java
// Custom success handler for first factor
public class MfaAwareAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, 
                                       HttpServletResponse response, Authentication auth) {
        UserDetails user = (UserDetails) auth.getPrincipal();
        if (user.isMfaEnabled()) {
            // Issue temporary token (limited scope)
            String mfaToken = jwtService.generateMfaToken(user.getUsername());
            // Redirect to MFA challenge page
            response.sendRedirect("/mfa/challenge?token=" + mfaToken);
            return;
        }
        super.onAuthenticationSuccess(request, response, auth);
    }
}
```

The MFA verification endpoint validates the TOTP against the user's registered secret, then issues the final access JWT. Audit logging captures successful and failed MFA attempts.

**Interviewer:** How would you implement rate limiting per user in Spring Security?

**Candidate:** Spring Security doesn't have built-in rate limiting, but it can be integrated:
1. **Filter-based:** Custom `OncePerRequestFilter` that checks Redis-based rate counter
2. **Bucket4j + Spring:** Use Bucket4j library with Redis backend for distributed rate limiting
3. **Spring Cloud Gateway:** Built-in `RequestRateLimiter` filter with Redis
4. **Resilience4J:** Rate limiter module with `@RateLimiter` annotation

Implementation approach:
```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    private final RedisTemplate<String, Integer> redis;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                   HttpServletResponse response, FilterChain chain) {
        String userId = extractUserId(request);
        String key = "rate_limit:" + userId + ":" + LocalDate.now();
        Integer count = redis.opsForValue().increment(key);
        if (count == 1) redis.expire(key, Duration.ofDays(1));
        if (count > 1000) {
            response.setStatus(429); // Too Many Requests
            return;
        }
        chain.doFilter(request, response);
    }
}
```

---

## Interviewer Feedback

**Strengths:** Good understanding of filter chain, JWT implementation, practical MFA design  
**Areas to Improve:** Could discuss OAuth2 authorization server patterns  
**Verdict:** Hire

---

*Lab 06 MOCK_INTERVIEW.md — Part of Backend Academy Interview Preparation*
