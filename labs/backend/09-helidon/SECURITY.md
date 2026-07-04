# Security for 09 Helidon

## Authentication
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/**").authenticated()
            .requestMatchers("/actuator/health").permitAll()
            .anyRequest().permitAll()
        ).oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
        return http.build();
    }
}
```

## Secure Configuration
```properties
# Encrypt sensitive properties
09helidon.api-key=${API_KEY}

# Use environment variables
09helidon.password=${SECRET_PASSWORD}
```

## Input Validation
```java
public class Request {
    @NotBlank
    @Size(min = 1, max = 100)
    private String input;

    @Pattern(regexp = "^[a-zA-Z0-9]+$")
    private String safeString;
}
```

## Best Practices
- Principle of least privilege
- Validate all inputs
- Use HTTPS in production
- Rotate secrets regularly
- Audit sensitive operations

