# Debugging Spring Security Issues

## Common Failure Scenarios

### Authentication Failures

Authentication failures can be difficult to diagnose because Spring Security returns similar error responses for different underlying issues. The most common is a "bad credentials" error, which is generic—it doesn't tell you whether the user wasn't found, the password was wrong, or the account is locked. Enable DEBUG logging on `org.springframework.security` to see detailed authentication flow.

A common issue is the UserDetailsService not being found—if you don't define one, Spring Security throws an exception during initialization or falls back to default behavior. Similarly, password encoder issues occur when you change encoding algorithms without migrating existing passwords or when using different encoders for different users.

Session management issues manifest as users being logged out unexpectedly. This happens when session expiration is too short, session fixation protection invalidates sessions on login, or the application runs in a cluster without session replication.

### CORS Issues

CORS misconfiguration is the most common reason for "CORS not working" complaints. The CORS filter must run before the authentication filter—if authentication rejects the request before CORS headers are added, the browser blocks the response. The order of filters in the security filter chain determines this.

Another issue is that CORS headers must be present on the actual response, not just the preflight (OPTIONS) response. If your POST request fails with 403 even though OPTIONS succeeded, the CORS headers might be missing on the error response.

The `Access-Control-Allow-Origin` header doesn't support wildcards when credentials are enabled. You must specify exact origins or use a custom CORS configuration that handles this correctly.

### Stack Trace Examples

**Access denied exception:**
```
org.springframework.security.access.AccessDeniedException: Access is denied
    at org.springframework.security.web.access.ExceptionTranslationFilter.handleAccessDenied(ExceptionTranslationFilter.java:134)
```

**CORS preflight failure:**
```
Access to fetch at 'https://api.example.com/data' from origin 'https://app.example.com' has been blocked by CORS policy: 
Response to preflight request doesn't pass access control check: No 'Access-Control-Allow-Origin' header is present.
```

**Authentication entry point issue:**
```
org.springframework.security.authentication.InternalAuthenticationServiceException: User is disabled
    at org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider.authenticate(AbstractUserDetailsAuthenticationProvider.java:154)
```

## Debugging Techniques

### Debugging Authentication

Enable detailed logging with `logging.level.org.springframework.security=DEBUG`. This shows each step of the authentication process, including which UserDetailsService was used, which exception was thrown, and why.

Use `AuthenticationManager` directly in integration tests to verify credentials before testing full security chains. This isolates authentication logic from authorization and filter chain issues.

For password issues, verify the password encoder matches what's stored. Use `PasswordEncoder.encode()` on a known password and compare with stored hashes. If you're using BCrypt, remember it includes the salt in the hash—each encode produces different output.

### CORS Troubleshooting

Verify CORS filter order by checking your `SecurityFilterChain` bean. Ensure CORS is configured with `.cors().and()` in the security filter chain. Without this, the default CORS filter may not be properly integrated with Spring Security.

Use browser developer tools to inspect the OPTIONS request and response. The request should have `Access-Control-Request-Method` and `Access-Control-Request-Headers`, and the response should have `Access-Control-Allow-Origin`, `Access-Control-Allow-Methods`, and `Access-Control-Allow-Headers`.

Test CORS independently by creating a test endpoint that returns hardcoded CORS headers. If that works but your security configuration doesn't, the issue is in the security-CORS integration.

## Best Practices

Use a single `SecurityFilterChain` bean with clear configuration for modern Spring Security. Avoid the legacy XML configuration style. Use Lambda DSL for readable filter chain configuration.

Configure CORS at the security level and also at the controller/method level if needed. The security-level configuration handles preflight requests, while method-level handles actual requests.

Use CSRF protection in production—it's enabled by default. If you're building an API that will be consumed by non-browser clients, disable CSRF or use stateless token-based authentication.

Implement a custom `AuthenticationEntryPoint` to return consistent error formats. The default redirects to login pages or returns generic 401/403 responses that aren't useful for APIs.