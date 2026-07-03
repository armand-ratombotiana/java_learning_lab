# Module 28: Spring Security - Quizzes

---

## Q1: Core Security Mechanism
Under the hood, how does Spring Security intercept HTTP requests to perform authentication and authorization?

A) Using database triggers.
B) Through a chain of Servlet Filters (the `SecurityFilterChain`).
C) By modifying the Tomcat server source code.
D) Through Java Reflection inside Controllers.

**Answer**: B
**Explanation**: Spring Security intercepts requests at the web layer using a chain of Servlet Filters before the request ever reaches the Spring DispatcherServlet or your custom Controllers.

---

## Q2: Security Annotations
Which annotation allows you to secure a method based on the current user's roles or evaluating complex SpEL (Spring Expression Language) expressions?

A) `@SecuredMethod`
B) `@RequireRole`
C) `@PreAuthorize`
D) `@Authenticate`

**Answer**: C
**Explanation**: `@PreAuthorize` is heavily used in method-level security to evaluate SpEL expressions before the method is invoked (e.g., `@PreAuthorize("hasRole('ADMIN')")`).

---

## Q3: User Details
Which core Spring Security interface must be implemented to tell the framework how to load user-specific data (like username, password hash, and roles) during the authentication process?

A) `UserDetailsService`
B) `AuthenticationManager`
C) `SecurityContext`
D) `JwtDecoder`

**Answer**: A
**Explanation**: `UserDetailsService` contains a single method, `loadUserByUsername(String username)`, which Spring Security calls to fetch user data from your database (or other storage) to verify credentials.