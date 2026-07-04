# Debugging 09-authorization-rbac

## Common Issues and Solutions

### Issue 1: Authentication Always Fails

**Debug Steps:**

1. Enable debug logging:
```properties
logging.level.org.springframework.security=DEBUG
logging.level.org.springframework.security.web=TRACE
```

2. Check the UserDetailsService is being called:
```java
@Service
public class DebugUserDetailsService implements UserDetailsService {
    private static final Logger log = LoggerFactory.getLogger(getClass());

    @Override
    public UserDetails loadUserByUsername(String username) {
        log.debug("Loading user: {}", username);
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }
}
```

3. Verify password encoder matches:
```java
@Test
void testPasswordMatching() {
    PasswordEncoder encoder = new BCryptPasswordEncoder();
    String hash = encoder.encode("password");
    assertTrue(encoder.matches("password", hash));
}
```

### Issue 2: Filter Chain Issues

**Debug Steps:**

1. Print the filter chain at startup:
```java
@Bean
public CommandLineRunner inspectFilterChain(FilterChainProxy proxy) {
    return args -> {
        for (SecurityFilterChain chain : proxy.getFilterChains()) {
            System.out.println("Chain: " + chain.getClass().getSimpleName());
            for (Filter filter : chain.getFilters()) {
                System.out.println("  Filter: " + filter.getClass().getSimpleName());
            }
        }
    };
}
```

### Issue 3: CORS Issues

**Debug Steps:**

1. Check the actual response headers in browser DevTools
2. Verify CORS configuration:
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
    config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(Arrays.asList("*"));
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);
    return source;
}
```

### Issue 4: CSRF Token Issues

**Debug Steps:**

1. Verify CSRF token is included in the request
2. Check if CSRF protection is appropriate for your API type
3. For stateless APIs, CSRF should be disabled:
```java
http.csrf(csrf -> csrf.disable());
```

### Issue 5: Session Management Problems

**Debug Steps:**

1. Track session creation:
```properties
logging.level.org.springframework.security.web.session=TRACE
```

2. Monitor session registry:
```java
@Bean
public HttpSessionEventPublisher httpSessionEventPublisher() {
    return new HttpSessionEventPublisher();
}
```

## Debugging Tools

### Browser Developer Tools
- Network tab: Inspect request/response headers
- Application tab: View cookies and session storage

### Spring Security Test Utilities
```java
@Test
@WithMockUser(username = "test", roles = "USER")
void testWithMockUser() {
    // Test with mock security context
}
```
