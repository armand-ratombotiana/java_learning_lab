# Security Theory

## 1. Introduction to Application Security

### Why Security Matters

Application security protects:
- User data and privacy
- Business assets
- System integrity
- Reputation and trust

### Security Principles

1. **Defense in Depth**: Multiple layers of security
2. **Least Privilege**: Minimum necessary permissions
3. **Fail Secure**: Safe defaults when failures occur
4. **Separation of Duties**: Divide responsibilities

---

## 2. Authentication vs Authorization

### Authentication

Verifies identity of user:
- Something you know (password)
- Something you have (token, phone)
- Something you are (biometrics)

### Authorization

Determines what authenticated user can do:
- Role-based access control (RBAC)
- Permission-based access
- Resource-level authorization

---

## 3. Spring Security Architecture

### Core Components

```
┌─────────────────────────────────────────────────────────────┐
│                    Spring Security Filter Chain             │
├─────────────────────────────────────────────────────────────┤
│  SecurityContextPersistenceFilter                          │
│  UsernamePasswordAuthenticationFilter                       │
│  BasicAuthenticationFilter                                 │
│  AuthorizationFilter                                        │
│  ExceptionTranslationFilter                                 │
│  ...                                                        │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    Authentication Manager                    │
│                    (ProviderManager)                         │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │ DaoAuth      │  │ LDAPAuth     │  │ OAuth2Auth   │     │
│  │ Provider     │  │ Provider     │  │ Provider     │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
└─────────────────────────────────────────────────────────────┘
                              ↓
┌─────────────────────────────────────────────────────────────┐
│                    UserDetailsService                        │
└─────────────────────────────────────────────────────────────┘
```

### Basic Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        
        return http.build();
    }
}
```

---

## 4. JWT (JSON Web Token)

### JWT Structure

```
xxxxx.yyyyy.zzzzz
  │      │    │
  │      │    └─ Signature (base64)
  │      └───── Payload (base64)
  └───────────── Header (base64)
```

### JWT Header

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

### JWT Payload (Claims)

```json
{
  "sub": "user123",
  "name": "John",
  "roles": ["USER", "ADMIN"],
  "iat": 1516239022,
  "exp": 1516242622
}
```

### Creating JWT

```java
public String generateToken(UserDetails user) {
    Map<String, Object> claims = new HashMap<>();
    claims.put("roles", user.getAuthorities().stream()
        .map(GrantedAuthority::getAuthority)
        .toList());
    
    return Jwts.builder()
        .setClaims(claims)
        .setSubject(user.getUsername())
        .setIssuedAt(new Date())
        .setExpiration(new Date(System.currentTimeMillis() + 86400000))
        .signWith(SignatureAlgorithm.HS256, secret)
        .compact();
}
```

### Validating JWT

```java
public boolean validateToken(String token, UserDetails user) {
    try {
        Jwts.parser()
            .setSigningKey(secret)
            .parseClaimsJws(token);
        
        return user.getUsername().equals(getUsernameFromToken(token))
            && !isTokenExpired(token);
    } catch (Exception e) {
        return false;
    }
}
```

---

## 5. OAuth 2.0

### OAuth 2.0 Roles

1. **Resource Owner**: User authorizing access
2. **Client**: Application requesting access
3. **Authorization Server**: Authenticates and issues tokens
4. **Resource Server**: Hosts protected resources

### OAuth 2.0 Flows

1. **Authorization Code**: For server-side apps (recommended)
2. **Implicit**: For SPAs (deprecated, use PKCE)
3. **Client Credentials**: For machine-to-machine
4. **Resource Owner Password**: For trusted clients only

### Authorization Code Flow

```
User → App → Authorization Server → User Login → 
Authorization Code → App → Token → Resource Server
```

### Spring Security OAuth2

```java
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2Login(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        
        return http.build();
    }
}
```

```yaml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: your-client-id
            client-secret: your-client-secret
            scope: profile, email
        provider:
          google:
            user-name-attribute: sub
```

---

## 6. Password Encoding

### BCrypt

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
}

// Usage
String encoded = passwordEncoder.encode("password123");
boolean matches = passwordEncoder.matches("password123", encoded);
```

### Other Encoders

- **Argon2**: Modern, memory-hard algorithm
- **PBKDF2**: NIST approved
- **SCrypt**: Memory-hard alternative

---

## 7. Method-Level Security

### @PreAuthorize

```java
@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }

@PreAuthorize("#owner == authentication.principal.username")
public void updateProfile(String owner, Profile profile) { }

@PreAuthorize("hasAuthority('USER_READ')")
public User getUser(Long id) { }
```

### @Secured

```java
@Secured({"ROLE_ADMIN", "ROLE_USER"})
public void method() { }
```

### @RolesAllowed

```java
@RolesAllowed({"ADMIN", "MANAGER"})
public void method() { }
```

---

## 8. CORS Configuration

```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
}
```

---

## 9. Common Security Vulnerabilities

### OWASP Top 10

1. **Injection**: SQL, NoSQL injection
2. **Broken Authentication**: Weak passwords, session issues
3. **Sensitive Data Exposure**: Unencrypted data
4. **XML External Entities**: Parsing untrusted XML
5. **Broken Access Control**: Missing authorization checks
6. **Security Misconfiguration**: Default configs, verbose errors
7. **Cross-Site Scripting (XSS)**: Unescaped output
8. **Insecure Deserialization**: Untrusted data
9. **Using Components with Known Vulnerabilities**
10. **Insufficient Logging & Monitoring**

---

## 10. Best Practices

1. Use HTTPS everywhere
2. Implement proper authentication (OAuth2, JWT)
3. Hash passwords with BCrypt/Argon2
4. Implement rate limiting
5. Use CSRF protection
6. Implement proper authorization checks
7. Log security events
8. Regular security audits
9. Keep dependencies updated
10. Use security headers

---

## Summary

Spring Security provides comprehensive authentication and authorization:
- Filter chain for request processing
- Multiple authentication providers
- JWT for stateless authentication
- OAuth2 for external identity providers
- Method-level security annotations
- CORS and CSRF handling