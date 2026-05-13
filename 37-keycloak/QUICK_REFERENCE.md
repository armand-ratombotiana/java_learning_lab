# Quick Reference: Keycloak

<div align="center">

![Module](https://img.shields.io/badge/Module-37-blue?style=for-the-badge)
![Topic](https://img.shields.io/badge/Topic-Keycloak-green?style=for-the-badge)

**Quick lookup guide for Keycloak IAM**

</div>

---

## 📋 Core Concepts

| Concept | Description |
|---------|-------------|
| **Realm** | Isolated security domain |
| **Client** | Application requesting auth |
| **Role** | Permission set |
| **User** | Authenticated identity |
| **Token** | JWT with claims |

---

## 🔑 Key Configurations

### Spring Security OAuth2
```yaml
spring:
  security:
    oauth2:
      resourceserver:
        jwt:
          issuer-uri: http://localhost:8180/realms/learning-realm
          jwk-set-uri: http://localhost:8180/realms/learning-realm/protocol/openid-connect/certs
```

### Security Config
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtDecoder(jwtDecoder()))
            );
        return http.build();
    }
}
```

---

## 📊 Token Validation

### Decode Token
```java
@Bean
public JwtDecoder jwtDecoder() {
    return NimbusJwtDecoder.withJwkSetUri(jwkSetUri).build();
}

// Extract claims
@GetMapping("/me")
public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
    return Map.of(
        "subject", jwt.getSubject(),
        "roles", jwt.getClaimAsStringList("roles"),
        "email", jwt.getClaimAsString("email")
    );
}
```

### Custom Filter
```java
@Component
public class CustomAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain chain) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Validate and extract token
        }
        chain.doFilter(request, response);
    }
}
```

---

## 🔐 Role-Based Access

### Resource Server Config
```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) {
    http
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin")
            .requestMatchers("/api/user/**").hasAnyAuthority("SCOPE_user", "SCOPE_admin")
        )
        .oauth2ResourceServer(oauth2 -> oauth2
            .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthConverter()))
        );
    return http;
}

private JwtAuthenticationConverter jwtAuthConverter() {
    JwtGrantedAuthoritiesConverter grantedConverter = new JwtGrantedAuthoritiesConverter();
    grantedConverter.setAuthoritiesClaimName("roles");
    grantedConverter.setAuthorityPrefix("ROLE_");
    
    JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
    converter.setJwtGrantedAuthoritiesConverter(grantedConverter);
    return converter;
}
```

---

## 📊 Admin API

```java
Keycloak keycloak = KeycloakBuilder.builder()
    .serverUrl("http://localhost:8180")
    .realm("master")
    .clientId("admin-cli")
    .username("admin")
    .password("admin")
    .build();

// Get realm
RealmResource realm = keycloak.realm("learning-realm");

// Create user
UserRepresentation user = new UserRepresentation();
user.setUsername("newuser");
user.setEnabled(true);
realm.users().create(user);

// Get users
realm.users().list().forEach(u -> System.out.println(u.getUsername()));
```

---

## ✅ Best Practices

- Use HTTPS in production
- Configure token expiration (15min default)
- Use refresh tokens for long sessions
- Implement role-based authorization

### ❌ DON'T
- Don't expose Keycloak admin console publicly
- Don't use default credentials

---

<div align="center">

[Back to Module →](./IMPLEMENTATION.md)

[Take Quizzes →](./PROJECTS.md)

</div>