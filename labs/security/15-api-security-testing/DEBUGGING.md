# DEBUGGING: 15-api-security-testing

## Debugging Guide

### Common Issues

### Issue 1: Configuration Not Loading
**Symptoms**: Application starts but security features not applied
**Diagnosis**:
- Check application.yml/properties for correct property names
- Verify @ConfigurationProperties binding
- Enable debug logging for security context
- Check for profile-specific overrides
**Fix**: Ensure configuration classes are scanned and properties are correctly mapped

### Issue 2: Authentication Failures
**Symptoms**: 401 Unauthorized for valid credentials
**Diagnosis**:
- Enable DEBUG logging for Security filter chain
- Check token format and expiration
- Verify user details service is returning correct data
- Examine security context holder state
**Fix**: Validate token parsing, check UserDetailsService implementation

### Issue 3: Authorization Not Applied
**Symptoms**: Users can access resources they should not
**Diagnosis**:
- Review @PreAuthorize annotations on methods
- Check method security configuration
- Verify role hierarchy configuration
- Test permission evaluator implementation
**Fix**: Ensure method security is enabled and annotations are on proxied methods

### Issue 4: Performance Degradation
**Symptoms**: Slow response times under load
**Diagnosis**:
- Profile with Async Profiler or JMC
- Check database query timing
- Monitor garbage collection logs
- Review thread pool configuration
**Fix**: Optimize hot paths, add caching, tune thread pools

### Issue 5: Memory Leak
**Symptoms**: Heap usage increases over time
**Diagnosis**:
- Take heap dump and analyze with Eclipse MAT
- Check for unclosed resources
- Review session management
- Monitor cache eviction
**Fix**: Close resources, implement proper eviction, review object references

### Debugging Tools

1. **IDE Debugger**: Set breakpoints in security filters and providers
2. **Actuator Endpoints**: /actuator/health, /actuator/metrics
3. **Logging**: Configure DEBUG for specific security packages
4. **Wireshark**: Analyze network traffic for protocol issues
5. **Thread Dumps**: Capture during performance issues

### Logging Configuration

`yaml
logging:
  level:
    com.security: DEBUG
    org.springframework.security: TRACE
    org.springframework.web: DEBUG
`

### Debugging Checklist

- [ ] Verify configuration is loaded correctly
- [ ] Check for exceptions in application logs
- [ ] Validate input data format
- [ ] Test with minimal configuration
- [ ] Reproduce issue in isolation
- [ ] Write failing test before fixing
- [ ] Verify fix with integration test

### Debugging Techniques

#### Remote Debugging
1. Enable JVM debug agent: -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005
2. Configure IDE to connect to remote JVM
3. Set breakpoints in security filter classes
4. Step through authentication and authorization flows

#### Log-Based Debugging
1. Add strategic log statements at entry/exit points
2. Log security context state before and after operations
3. Include correlation IDs for request tracing
4. Use MDC (Mapped Diagnostic Context) for contextual logging

#### Common Debugging Scenarios

| Scenario | Symptoms | Debug Approach |
|----------|----------|----------------|
| Auth fails unexpectedly | 401 for valid tokens | Check token parsing, signature verification, user lookup |
| Authorization bypass | 403 not enforced | Verify @PreAuthorize, check role hierarchy, test with multiple roles |
| Performance issue | Slow authentication | Profile password hashing, database queries, token generation |
| Session lost | User logged out randomly | Check session timeout, cookie attributes, Redis connectivity |

### Debugging Spring Security Filter Chain

`java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http.addFilterBefore(new DebugFilter(), UsernamePasswordAuthenticationFilter.class);
    return http.build();
}
`

### Debugging Checklist

- [ ] Enable TRACE logging for security packages
- [ ] Check thread names for async issues
- [ ] Verify security context propagation
- [ ] Inspect HTTP headers (Authorization, Cookie, X-CSRF)
- [ ] Validate SSL/TLS certificates
- [ ] Check for proxy/load balancer IP forwarding
- [ ] Review database connection pool status
- [ ] Monitor JVM heap and garbage collection
- [ ] Check file system permissions for config files
- [ ] Verify environment variable values

### Profiling Tools

- **Async Profiler**: CPU and allocation profiling
- **JMC (Java Mission Control)**: JVM monitoring
- **VisualVM**: Heap dump analysis
- **YourKit**: Commercial profiler
- **Wireshark**: Network traffic analysis
- **JProfiler**: Integration profiling
