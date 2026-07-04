# Microservices Security

## Security Patterns

### 1. API Gateway Authentication
```java
@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange()
                .pathMatchers("/api/public/**").permitAll()
                .pathMatchers("/api/orders/**").authenticated()
            .and()
            .oauth2ResourceServer()
                .jwt()
            .and()
            .build();
    }
}
```

### 2. Service-to-Service Authentication
```java
@Bean
public RequestInterceptor serviceAuthInterceptor() {
    return requestTemplate -> {
        // Add service-to-service JWT token
        requestTemplate.header("X-Service-Auth", generateServiceToken());
    };
}

private String generateServiceToken() {
    // Use client credentials flow
    return jwtTokenProvider.createServiceToken("order-service");
}
```

### 3. Mutual TLS (mTLS)
```yaml
# application.yml
server:
  ssl:
    enabled: true
    client-auth: need
    key-store: classpath:keystore.p12
    key-store-password: ${KEYSTORE_PASSWORD}
    trust-store: classpath:truststore.p12
```

### 4. Secret Management
```yaml
# Use Vault or Kubernetes secrets
spring:
  cloud:
    vault:
      uri: http://localhost:8200
      authentication: TOKEN
      token: ${VAULT_TOKEN}
```

### 5. Rate Limiting
```java
@Bean
public KeyResolver userKeyResolver() {
    return exchange -> Mono.just(
        exchange.getRequest().getRemoteAddress().getAddress().getHostAddress()
    );
}
```
