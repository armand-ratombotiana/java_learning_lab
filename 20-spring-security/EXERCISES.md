# Spring Security - Exercises

## Exercise 1: Basic Security Configuration
**Objective**: Set up Spring Security with form login.

### Task
Configure basic security:
1. In-memory users
2. Form-based login
3. Protected endpoints
4. Custom access denied page

### Implementation
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
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
            );
        
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password("{noop}password")
            .roles("USER")
            .build();
        
        UserDetails admin = User.builder()
            .username("admin")
            .password("{noop}admin123")
            .roles("ADMIN", "USER")
            .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

---

## Exercise 2: JWT Authentication
**Objective**: Implement JWT-based stateless authentication.

### Task
Create JWT authentication:
1. JWT token generation
2. Token validation filter
3. Custom UserDetailsService
4. Token refresh mechanism

### Implementation
```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(a -> a.getAuthority()).toList());
        
        return Jwts.builder()
            .claims(claims)
            .subject(userDetails.getUsername())
            .issuedAt(new Date())
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .compact();
    }
    
    public String extractUsername(String token) {
        return Jwts.parser()
            .verifyWith(Keys.hmacShaKeyFor(secret.getBytes()))
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        return extractUsername(token).equals(userDetails.getUsername())
            && !isTokenExpired(token);
    }
}

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        
        String authHeader = request.getHeader("Authorization");
        
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            String username = jwtTokenProvider.extractUsername(token);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtTokenProvider.validateToken(token, userDetails)) {
                    var auth = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
            }
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

## Exercise 3: Password Encoding
**Objective**: Implement secure password storage.

### Task
Configure password encoding:
1. BCrypt encoder
2. Custom salt generation
3. Password validation
4. Password migration

### Implementation
```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

@Service
public class PasswordService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public String encode(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    // Migration: support old MD5 passwords while migrating to BCrypt
    public boolean migrateAndValidate(String rawPassword, String encodedPassword) {
        if (encodedPassword.startsWith("{md5}")) {
            String md5Hash = md5(rawPassword);
            if (md5Hash.equals(encodedPassword.substring(5))) {
                // Migrate to BCrypt
                String newEncoded = encode(rawPassword);
                userRepository.updatePassword(userId, newEncoded);
                return true;
            }
            return false;
        }
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}
```

---

## Exercise 4: Method-Level Security
**Objective**: Secure methods with annotations.

### Task
Implement method security:
1. @PreAuthorize for role checking
2. @Secured for permissions
3. @PostFilter for filtering results
4. Custom permission evaluators

### Implementation
```java
@Service
public class ProductService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }
    
    @PreAuthorize("#username == authentication.principal.username or hasRole('ADMIN')")
    public UserProfile getUserProfile(String username) {
        return userRepository.findByUsername(username);
    }
    
    @PreAuthorize("hasPermission(#product, 'WRITE')")
    public Product updateProduct(Product product) {
        return productRepository.save(product);
    }
    
    @Secured({"ROLE_USER", "ROLE_ADMIN"})
    public List<Order> getMyOrders() {
        return orderRepository.findByUserId(currentUserId());
    }
    
    @PostFilter("filterObject.owner == authentication.principal.username")
    public List<Document> getDocuments() {
        return documentRepository.findAll();
    }
}
```

---

## Exercise 5: OAuth2 Resource Server
**Objective**: Configure OAuth2 resource server.

### Task
Set up OAuth2:
1. JWT validation with JWKS
2. Custom token decoder
3. Role mapping from token
4. Protected resource configuration

### Implementation
```java
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    
    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**").permitAll()
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.decoder(jwtDecoder()))
            );
        
        return http.build();
    }
    
    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withJwkSetUri(
            "https://auth.example.com/.well-known/jwks.json"
        ).build();
    }
    
    @Bean
    public GrantedAuthoritiesMapper grantedAuthoritiesMapper() {
        return new SimpleGrantedAuthoritiesMapper() {
            @Override
            public Collection<? extends GrantedAuthority> mapAuthorities(
                    Collection<? extends GrantedAuthority> authorities) {
                return authorities.stream()
                    .map(auth -> new SimpleGrantedAuthority(
                        "ROLE_" + auth.getAuthority().toUpperCase()))
                    .collect(Collectors.toList());
            }
        };
    }
}
```

---

## Exercise 6: Custom Authentication Provider
**Objective**: Create custom authentication mechanism.

### Task
Implement custom authentication:
1. Custom AuthenticationFilter
2. Custom AuthenticationProvider
3. Multiple auth mechanisms
4. Authentication success/failure handlers

### Implementation
```java
@Component
public class ApiKeyAuthenticationFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        
        String apiKey = request.getHeader("X-API-Key");
        
        if (apiKey != null) {
            var auth = new ApiKeyAuthenticationToken(apiKey);
            var authenticated = authenticationManager.authenticate(auth);
            SecurityContextHolder.getContext().setAuthentication(authenticated);
        }
        
        filterChain.doFilter(request, response);
    }
}

@Component
public class ApiKeyAuthenticationProvider implements AuthenticationProvider {
    
    @Autowired
    private ApiKeyService apiKeyService;
    
    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        
        String apiKey = (String) authentication.getPrincipal();
        ApiKey apiKeyEntity = apiKeyService.findByKey(apiKey);
        
        if (apiKeyEntity == null || !apiKeyEntity.isActive()) {
            throw new BadCredentialsException("Invalid API Key");
        }
        
        return new ApiKeyAuthenticationToken(apiKey, 
            List.of(new SimpleGrantedAuthority("ROLE_API")));
    }
    
    @Override
    public boolean supports(Class<?> authentication) {
        return ApiKeyAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
```

---

## Exercise 7: Account Lockout & Protection
**Objective**: Implement account protection.

### Task
Add account protection:
1. Failed login tracking
2. Account lockout after N attempts
3. Temporary lockout duration
4. Unlock mechanism

### Implementation
```java
@Service
public class AccountLockoutService {
    
    private static final int MAX_ATTEMPTS = 5;
    private static final Duration LOCKOUT_DURATION = Duration.ofMinutes(30);
    
    @Autowired
    private UserRepository userRepository;
    
    public void recordFailedLogin(String username) {
        User user = userRepository.findByUsername(username).orElse(null);
        
        if (user != null) {
            user.setFailedAttempts(user.getFailedAttempts() + 1);
            
            if (user.getFailedAttempts() >= MAX_ATTEMPTS) {
                user.setLocked(true);
                user.setLockoutEnd(LocalDateTime.now().plus(LOCKOUT_DURATION));
            }
            
            userRepository.save(user);
        }
    }
    
    public void recordSuccessfulLogin(String username) {
        userRepository.findByUsername(username).ifPresent(user -> {
            user.setFailedAttempts(0);
            user.setLocked(false);
            user.setLockoutEnd(null);
            userRepository.save(user);
        });
    }
    
    public boolean isAccountLocked(String username) {
        return userRepository.findByUsername(username)
            .map(user -> user.isLocked() && 
                (user.getLockoutEnd() == null || 
                 user.getLockoutEnd().isAfter(LocalDateTime.now())))
            .orElse(false);
    }
}
```

---

## Exercise 8: Audit Logging
**Objective**: Implement security audit trail.

### Task
Create audit system:
1. Authentication events
2. Authorization checks
3. Data access logging
4. Audit query API

### Implementation
```java
@Component
public class SecurityAuditService {
    
    @Autowired
    private AuditLogRepository auditLogRepository;
    
    public void logAuthentication(String username, boolean success, 
                                   String ipAddress, String userAgent) {
        AuditLog log = new AuditLog();
        log.setEventType(success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE");
        log.setUsername(username);
        log.setIpAddress(ipAddress);
        log.setUserAgent(userAgent);
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
    
    public void logAuthorization(String username, String resource, 
                                 String action, boolean granted) {
        AuditLog log = new AuditLog();
        log.setEventType("AUTHORIZATION_CHECK");
        log.setUsername(username);
        log.setResource(resource);
        log.setAction(action);
        log.setDetails("Access " + (granted ? "granted" : "denied"));
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
}

@Repository
interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsername(String username);
    List<AuditLog> findByEventType(String eventType);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
}
```

---

## Exercise 9: CORS Configuration
**Objective**: Configure cross-origin requests.

### Task
Set up CORS:
1. Global CORS configuration
2. Custom CORS with authentication
3. Preflight request handling
4. Allowed origins configuration

### Implementation
```java
@Configuration
public class CorsConfig {
    
    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("https://*.example.com"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setMaxAge(3600L);
        
        source.registerCorsConfiguration("/api/**", config);
        
        return new CorsFilter(source);
    }
}
```

---

## Exercise 10: CSRF Protection
**Objective**: Implement CSRF protection.

### Task
Configure CSRF:
1. Token-based protection
2. CookieCsrfTokenRepository
3. Custom CSRF handler
4. Testing CSRF

### Implementation
```java
@Configuration
public class CsrfConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new XorCsrfTokenRequestAttributeHandler())
            )
            .addFilterAfter(new CsrfCookieFilter(), 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}

public class CsrfCookieFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) {
        
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        
        if (csrfToken != null) {
            response.setHeader("X-CSRF-TOKEN", csrfToken.getToken());
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

## Running Tests
```bash
# Run application
mvn spring-boot:run

# Test authentication
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'

# Test protected endpoint
curl -H "Authorization: Bearer <TOKEN>" http://localhost:8080/api/products
```