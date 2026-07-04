# API Design - SECURITY

## Authentication

### JWT Authentication
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
            .csrf(AbstractHttpConfigurer::disable)
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/v1/public/**").permitAll()
                .requestMatchers("/api/v1/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/v1/**").authenticated()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .build();
    }
}
```

## Authorization

### Role & Permission Checks
```java
@RestController
@RequestMapping("/api/v1/admin/products")
public class AdminProductController {

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteProduct(@PathVariable String id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
```

### Row-Level Security
```java
@PostAuthorize("returnObject.body.customerId == authentication.name")
@GetMapping("/orders/{id}")
public ResponseEntity<Order> getOrder(@PathVariable String id) {
    return ResponseEntity.ok(orderService.findById(id));
}
```

## Input Validation

```java
// Prevent injection attacks
public record CreateProductRequest(
    @NotBlank @Size(max = 255) String name,
    @Pattern(regexp = "^[a-zA-Z0-9 -]+$") String sku,
    @Positive BigDecimal price
) {}
```

## Rate Limiting

```java
@Component
public class RateLimitInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
            Object handler) throws Exception {
        String key = request.getRemoteAddr();
        if (rateLimiter.isExceeded(key)) {
            response.setStatus(429);
            response.setHeader("Retry-After", "60");
            return false;
        }
        return true;
    }
}
```

## API Security Checklist

- [ ] HTTPS only (HSTS header)
- [ ] Authentication on all endpoints (except public)
- [ ] Rate limiting per client
- [ ] Input validation and sanitization
- [ ] No sensitive data in error messages
- [ ] CORS configured for known origins
- [ ] CSRF protection for cookie-based auth
- [ ] Security headers (X-Content-Type-Options, X-Frame-Options)
- [ ] API keys rotation support
- [ ] Audit logging for mutations
- [ ] Request size limits
