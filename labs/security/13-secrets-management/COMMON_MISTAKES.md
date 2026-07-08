# COMMON MISTAKES: 13-secrets-management

## Common Mistakes

### Critical Mistakes

### Mistake 1: Hardcoded Secrets
**Problem**: Embedding API keys, passwords, or tokens in source code
`java
// WRONG - Secret in source code
private static final String API_KEY = "sk-abc123def456";
`
**Fix**: Use environment variables or a secrets manager
`java
// CORRECT - Secret from environment
private final String apiKey = System.getenv("API_KEY");
`

### Mistake 2: Disabling Security
**Problem**: Turning off security for testing and forgetting
`java
// WRONG - Disabled security
spring.security.enabled=false
`
**Fix**: Use profile-specific configuration with warnings
`yaml
# CORRECT - Profile-specific override
spring.security.enabled: true
`

### Mistake 3: Swallowing Security Exceptions
**Problem**: Catching security exceptions without handling
`java
// WRONG - Silent failure
try {
    authenticate(token);
} catch (AuthenticationException e) {
    // Do nothing - security silent failure
}
`
**Fix**: Properly handle and log security events
`java
// CORRECT - Log and respond
try {
    authenticate(token);
} catch (AuthenticationException e) {
    log.warn("Authentication failed: {}", e.getMessage());
    throw new UnauthorizedException("Invalid credentials");
}
`

### Mistake 4: Insufficient Token Validation
**Problem**: Not validating all token properties
`java
// WRONG - Only checks signature
jwt.verify(token);
`
**Fix**: Validate all claims
`java
// CORRECT - Full validation
jwt.verify(token)
   .validateExpiration()
   .validateIssuer(expectedIssuer)
   .validateAudience(expectedAudience);
`

### Mistake 5: Missing Input Validation
**Problem**: Trusting client-provided data without validation
`java
// WRONG - Direct use of client input
String role = request.getParameter("role");
`
**Fix**: Validate against allowed values
`java
// CORRECT - Validate input
String role = request.getParameter("role");
if (!VALID_ROLES.contains(role)) {
    throw new InvalidInputException("Invalid role: " + role);
}
`

### Mistake 6: Logging Sensitive Data
**Problem**: Including secrets in log output
`java
// WRONG - Logs sensitive data
log.info("User {} authenticated with token {}", user, token);
`
**Fix**: Mask or omit sensitive data
`java
// CORRECT - Masked output
log.info("User {} authenticated successfully", user);
`

### Mistake 7: Incorrect Error Messages
**Problem**: Revealing too much information in error responses
`java
// WRONG - Reveals user existence
throw new AuthenticationException("User not found: " + username);
`
**Fix**: Generic error messages
`java
// CORRECT - Generic message
throw new AuthenticationException("Invalid credentials");
`

### Mistake 8: Ignoring Race Conditions
**Problem**: Non-thread-safe security operations
`java
// WRONG - Not thread-safe
private int loginAttempts;
`
**Fix**: Use concurrent data structures
`java
// CORRECT - Thread-safe
private final AtomicInteger loginAttempts = new AtomicInteger();
`

### Prevention Checklist

- [ ] No hardcoded secrets
- [ ] Security never disabled in production
- [ ] All security exceptions properly handled
- [ ] Complete token validation
- [ ] Input validation on all endpoints
- [ ] No sensitive data in logs
- [ ] Generic error messages to clients
- [ ] Thread-safe security operations
- [ ] Regular security review of configuration
- [ ] Automated security testing in CI/CD
