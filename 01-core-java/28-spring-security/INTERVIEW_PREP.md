# Module 28: Spring Security - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Explain the flow of a typical Spring Security authentication process.
**Answer**:
1. A client sends an HTTP request containing credentials (e.g., Basic Auth headers, a form POST, or a JWT).
2. The request hits the `DelegatingFilterProxy`, which delegates to the Spring Security `FilterChainProxy`.
3. The request passes through a chain of filters. An authentication filter (e.g., `UsernamePasswordAuthenticationFilter`) intercepts the request and extracts the credentials.
4. The filter creates an unauthenticated `Authentication` token and passes it to the `AuthenticationManager`.
5. The `AuthenticationManager` delegates to one or more `AuthenticationProvider`s.
6. The provider uses a `UserDetailsService` to load the user's details from the database and uses a `PasswordEncoder` to verify the hash.
7. If successful, the provider returns a fully populated, authenticated `Authentication` token containing the user's authorities (roles).
8. The filter places this token into the `SecurityContextHolder`, establishing the security context for the duration of the thread/request.

### Q2: Why is Session Creation set to `STATELESS` when using JWTs?
**Answer**:
By default, Spring Security uses HTTP Sessions to store the `SecurityContext` between requests, relying on a `JSESSIONID` cookie. 
JWT (JSON Web Token) is designed to be completely stateless—all the information needed to authenticate the user (identity, roles, expiration) is contained *within the token itself*. The server does not need to store anything in memory or in a database session table. 
Setting `SessionCreationPolicy.STATELESS` tells Spring Security *not* to create or use an HTTP session. This drastically improves the application's horizontal scalability (since load balancers don't need sticky sessions) and reduces memory overhead.

### Q3: What is Cross-Site Request Forgery (CSRF), and why do we disable it for JWT-based APIs?
**Answer**:
CSRF is an attack that forces an end user to execute unwanted actions on a web application in which they're currently authenticated. It exploits the fact that web browsers automatically include session cookies with every request sent to a domain.
Spring Security enables CSRF protection by default, requiring a synchronizer token to be passed with every state-changing request (POST, PUT, DELETE).
However, if your REST API is purely stateless and uses a Bearer Token (JWT) sent in the `Authorization` header rather than using browser cookies, it is immune to CSRF attacks by design (browsers do not automatically attach the `Authorization` header). Therefore, CSRF protection is unnecessary overhead and can be safely disabled.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Password Storage Migration
**Problem**: You inherited a legacy application that stores passwords in plain text. You are migrating it to use Spring Security and BCrypt. Describe the strategy for smoothly transitioning user passwords to BCrypt without forcing every single user to reset their password immediately.

**Solution**:
Implement a custom `PasswordEncoder` that handles both legacy and new hashes.
1. When a user attempts to log in, query the database.
2. Check if the stored password matches the BCrypt pattern (e.g., starts with `$2a$`).
3. If it is BCrypt, verify normally.
4. If it is plain text, verify the plain text directly.
5. **The Migration Step**: If the plain text verification is successful, immediately generate a BCrypt hash of the plain text password in the background, update the database record with the new hash, and then proceed with the login.
Over time, as active users log in, their passwords will automatically migrate to BCrypt hashes.

### Scenario 2: Method-Level Security (SpEL)
**Problem**: You have a `DocumentService` with a method `Document getDocument(Long docId)`. Write the Spring Security annotation needed to ensure that only a user with the role `ROLE_ADMIN` **OR** the user who is the actual owner of the document can access it. Assume `Document` has a method `getOwnerUsername()`.

**Solution**:
Enable global method security (`@EnableMethodSecurity`), and use `@PostAuthorize` since we need the return object to evaluate the owner constraint.

```java
@PostAuthorize("hasRole('ADMIN') or returnObject.ownerUsername == authentication.name")
public Document getDocument(Long docId) {
    return documentRepository.findById(docId).orElseThrow();
}
```
*(Alternatively, use `@PreAuthorize` if you check against a repository prior to the method execution, but `@PostAuthorize` is cleaner here).*