# Security Interview Questions

## 1. Spring Security Basics

### Q1: How does Spring Security work?

**Answer:**
Spring Security works through a filter chain:
1. Request enters filter chain
2. Security filters check authentication
3. If authenticated, authorization checks happen
4. Request reaches controller if authorized
5. Response goes back through filters

Key components:
- SecurityFilterChain: Defines security rules
- AuthenticationManager: Manages authentication
- UserDetailsService: Loads user data
- PasswordEncoder: Hashes passwords

---

### Q2: What is the difference between authentication and authorization?

**Answer:**
**Authentication** - Verifies identity (who you are)
- Process of validating user credentials
- Examples: Username/password, OAuth2, JWT

**Authorization** - Determines permissions (what you can do)
- Process of checking access rights
- Examples: Role-based, permission-based

---

### Q3: How do you configure Spring Security?

**Answer:**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        
        return http.build();
    }
}
```

---

## 2. JWT

### Q4: Explain JWT structure

**Answer:**
JWT has three parts separated by dots:

1. **Header**: Algorithm and token type
```json
{"alg": "HS256", "typ": "JWT"}
```

2. **Payload**: Claims (data)
```json
{"sub": "user123", "roles": ["ADMIN"], "exp": 1234567890}
```

3. **Signature**: Verify token integrity
```
HMACSHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload), secret)
```

---

### Q5: How do you implement JWT authentication?

**Answer:**
1. **Generate token on login**:
```java
String token = Jwts.builder()
    .setClaims(claims)
    .setSubject(username)
    .setExpiration(new Date(now + expiration))
    .signWith(SignatureAlgorithm.HS256, secret)
    .compact();
```

2. **Validate token on requests**:
```java
Jws<Claims> claims = Jwts.parser()
    .setSigningKey(secret)
    .parseClaimsJws(token);
```

3. **Set security context**:
```java
UsernamePasswordAuthenticationToken auth = 
    new UsernamePasswordAuthenticationToken(userDetails, null, authorities());
SecurityContextHolder.getContext().setAuthentication(auth);
```

---

### Q6: What are the best practices for JWT?

**Answer:**
1. Store secrets securely
2. Use short expiration times
3. Store token in HttpOnly cookie (not localStorage)
4. Implement token refresh
5. Use HTTPS
6. Include necessary claims only
7. Validate all claims (expiration, issuer, audience)

---

## 3. OAuth2

### Q7: Explain OAuth2 flows

**Answer:**
1. **Authorization Code** (recommended for web):
   - User → Auth Server (login) → Authorization Code
   - Client exchanges code for tokens
   - Most secure

2. **Implicit** (deprecated for SPAs):
   - Returns token directly
   - Security concerns

3. **Client Credentials** (M2M):
   - No user involvement
   - Client authenticates itself

4. **Password Grant** (trusted clients only):
   - User gives credentials to client
   - Client exchanges for token

---

### Q8: What is the difference between OAuth2 and OpenID Connect?

**Answer:**
- **OAuth2**: Authorization framework (access tokens)
- **OpenID Connect**: Identity layer on top of OAuth2 (ID tokens)
- OIDC provides: user identity, profile info, standard claims

---

## 4. Password Security

### Q9: How do you store passwords securely?

**Answer:**
1. Never store plain text
2. Use strong hashing (BCrypt, Argon2)
3. Use unique salt per password
4. Configure appropriate work factor
5. Use password encoder in Spring:

```java
@Bean
public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder(12);
}
```

---

### Q10: What is the difference between encryption and hashing?

**Answer:**
- **Encryption**: Reversible, used for data protection
- **Hashing**: One-way, used for passwords
- Hashing is irreversible - cannot retrieve original password
- Hashing with salt prevents rainbow table attacks

---

## 5. Authorization

### Q11: What is the difference between hasRole and hasAuthority?

**Answer:**
- **hasRole("ADMIN")** → checks for "ROLE_ADMIN"
- **hasAuthority("ADMIN")** → checks for exact "ADMIN"

Use hasRole for role-based, hasAuthority for specific permissions.

---

### Q12: How do you implement method-level security?

**Answer:**
```java
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig { }

@PreAuthorize("hasRole('ADMIN')")
public void deleteUser(Long id) { }

@PreAuthorize("#userId == principal.id or hasRole('ADMIN')")
public User getUser(Long userId) { }

@Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
public void method() { }
```

---

## 6. CORS and CSRF

### Q13: How do you configure CORS in Spring Security?

**Answer:**
```java
@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("http://localhost:3000"));
    config.setAllowedMethods(List.of("GET", "POST"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    
    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

---

### Q14: How does CSRF protection work?

**Answer:**
1. Server generates CSRF token
2. Token sent to client (cookie or hidden field)
3. Client includes token in state-changing requests
4. Server validates token matches

Spring Security enables CSRF by default. Use CsrfTokenRepository to customize.

---

### Q15: When can CSRF be disabled?

**Answer:**
- Stateless APIs (JWT in Authorization header)
- Non-browser clients (mobile apps)
- When all clients are trusted

Never disable for browser-based applications with session authentication.

---

## 7. Best Practices

### Q16: What are security best practices?

**Answer:**
1. Use HTTPS everywhere
2. Implement proper authentication (OAuth2, JWT)
3. Hash passwords (BCrypt/Argon2)
4. Implement rate limiting
5. Use security headers
6. Validate and sanitize inputs
7. Log security events
8. Keep dependencies updated
9. Use parameterized queries (prevent SQL injection)
10. Implement proper session management

---

### Q17: How do you secure REST API?

**Answer:**
1. Use stateless authentication (JWT)
2. Implement authorization checks
3. Configure CORS properly
4. Enable CSRF (or use JWT)
5. Use HTTPS
6. Implement rate limiting
7. Validate all inputs
8. Use secure headers

```java
@Bean
public SecurityFilterChain apiFilterChain(HttpSecurity http) {
    http
        .csrf(csrf -> csrf.disable())
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/auth/**").permitAll()
            .anyRequest().authenticated()
        )
        .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    
    return http.build();
}
```

---

### Q18: How do you handle sessions securely?

**Answer:**
1. Use secure, HttpOnly cookies
2. Implement session timeout
3. Regenerate session ID after login
4. Invalidate sessions on logout
5. Use secure session cookies (secure flag)

---

### Q19: What is OWASP Top 10?

**Answer:**
1. Injection (SQL, NoSQL)
2. Broken Authentication
3. Sensitive Data Exposure
4. XML External Entities
5. Broken Access Control
6. Security Misconfiguration
7. XSS
8. Insecure Deserialization
9. Using Components with Known Vulnerabilities
10. Insufficient Logging & Monitoring

---

### Q20: How do you test security?

**Answer:**
1. Unit tests with mock security context
2. Integration tests with TestSecurityContextRunner
3. Static analysis (SonarQube)
4. Dynamic scanning (OWASP ZAP)
5. Penetration testing
6. Dependency checking (OWASP Dependency-Check)

---

### Q21: What is the purpose of SecurityContextHolder?

**Answer:**
Holds authenticated user information:
```java
// Get current user
UserDetails user = (UserDetails) SecurityContextHolder.getContext()
    .getAuthentication().getPrincipal();

// Set authentication
SecurityContextHolder.getContext().setAuthentication(authentication);
```

---

### Q22: How do you implement custom UserDetailsService?

**Answer:**
```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Override
    public UserDetails loadUserByUsername(String username) 
            throws UsernameNotFoundException {
        
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Not found"));
        
        return User.builder()
            .username(user.getUsername())
            .password(user.getPassword())
            .roles("USER")
            .build();
    }
}
```

---

### Q23: What is the difference between Stateless and Stateful authentication?

**Answer:**
- **Stateful**: Server maintains session (traditional)
- **Stateless**: No server session (JWT)
- Stateful: Session ID in cookie
- Stateless: Token in each request

---

### Q24: How do you implement logout in Spring Security?

**Answer:**
```java
http.logout(logout -> logout
    .logoutUrl("/logout")
    .logoutSuccessUrl("/login?logout")
    .invalidateHttpSession(true)
    .deleteCookies("JSESSIONID", "JWT_TOKEN")
    .clearAuthentication(true)
)
```

---

### Q25: What is the purpose of @AuthenticationPrincipal?

**Answer:**
Injects authenticated user into controller:
```java
@GetMapping("/profile")
public String getProfile(@AuthenticationPrincipal UserDetails user) {
    return user.getUsername();
}
```