# Performance of 06-encryption

## Performance Characteristics

### Authentication Performance

| Auth Method | Requests/sec (avg) | Latency (p50) | Memory (per session) |
|-------------|-------------------|---------------|---------------------|
| HTTP Basic  | 12,000           | 1ms           | 0 bytes              |
| Form Login  | 10,000           | 2ms           | ~1KB per session      |
| JWT Bearer  | 9,500            | 3ms           | 0 bytes              |
| OAuth2      | 5,000            | 8ms           | ~2KB per session      |

### Password Hashing Performance

| Algorithm   | Hash Time (ms) | Memory Usage | Security Level |
|-------------|----------------|-------------|----------------|
| BCrypt (10) | ~80ms          | 4KB         | Good           |
| BCrypt (12) | ~320ms         | 4KB         | Strong         |
| SCrypt      | ~200ms         | 16MB        | Strong         |
| Argon2id    | ~150ms         | 64MB        | Very Strong    |

## Optimization Strategies

### 1. Caching UserDetails

```java
@Cacheable(value = "users", unless = "#result == null")
@Override
public UserDetails loadUserByUsername(String username) {
    return userRepository.findByUsername(username)
        .map(this::toUserDetails)
        .orElseThrow(() -> new UsernameNotFoundException(username));
}
```

### 2. Token-Based Authentication for APIs

Token-based authentication avoids session lookup overhead:
- No session storage required
- Stateless - easy to scale horizontally
- Verification can be distributed (cached public keys)

```java
@Bean
public SecurityFilterChain statelessChain(HttpSecurity http) {
    http
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        )
        .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    return http.build();
}
```

### 3. Connection Pooling

```java
@Bean
public DataSource dataSource() {
    HikariConfig config = new HikariConfig();
    config.setMaximumPoolSize(20);
    config.setMinimumIdle(5);
    config.setConnectionTimeout(3000);
    return new HikariDataSource(config);
}
```

## Monitoring

### Key Metrics to Monitor
- `spring.security.authentication.success` - Successful auth count
- `spring.security.authentication.failure` - Failed auth count
- `spring.security.sessions.active` - Active sessions
- `http.server.requests` - Request metrics with auth details

## Bottlenecks to Watch

1. **Password Hashing** - BCrypt is deliberately slow; configure work factor appropriately
2. **Token Verification** - JWT verification requires cryptographic operations
3. **Session Storage** - Memory-based session storage doesn't scale horizontally
4. **Database Queries** - Authentication queries can bottleneck under load
5. **TLS Termination** - HTTPS adds ~5-10ms latency per connection
