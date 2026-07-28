# Spring Security — Deep Interview Guide

## Table of Contents
1. [Security Architecture](#security-architecture)
2. [Authentication Flow](#authentication-flow)
3. [OAuth2 / OpenID Connect](#oauth2--openid-connect)
4. [JWT & Resource Server](#jwt--resource-server)
5. [Method Security](#method-security)
6. [Role Hierarchy & ACL](#role-hierarchy--acl)
7. [CSRF, CORS, Security Headers](#csrf-cors-security-headers)
8. [Reactive Security](#reactive-security)
9. [Java Code Examples](#java-code-examples)
10. [20+ Interview Questions](#20-interview-questions)

---

## Security Architecture

Spring Security provides comprehensive security services for Java applications. Its core architecture is based on:

### Core Components

| Component | Interface/Class | Purpose |
|-----------|----------------|---------|
| **SecurityContextHolder** | `ThreadLocal<SecurityContext>` | Holds the current security context |
| **SecurityContext** | Interface | Holds the Authentication object |
| **Authentication** | Interface | Principal, credentials, authorities |
| **GrantedAuthority** | Interface | Application permissions (roles) |
| **AuthenticationManager** | Interface | Processes authentication requests |
| **ProviderManager** | `AuthenticationManager` impl | Delegates to AuthenticationProviders |
| **AuthenticationProvider** | Interface | Performs specific authentication |
| **UserDetailsService** | Interface | Loads user data from a data source |
| **PasswordEncoder** | Interface | Encodes and verifies passwords |
| **SecurityFilterChain** | Interface | Chain of security filters |

### Filter Chain Architecture

```
Request
  │
  ▼
SecurityFilterChain (ordered list of filters)
  ┌─────────────────────────────┐
  │  SecurityContextHolderFilter │  → Populates SecurityContext
  │  HeaderWriterFilter          │  → Writes security headers
  │  CorsFilter                  │  → CORS handling
  │  CsrfFilter                  │  → CSRF protection
  │  LogoutFilter                │  → Logout handling
  │  UsernamePasswordAuthFilter  │  → Login processing
  │  BasicAuthenticationFilter   │  → HTTP Basic auth
  │  ExceptionTranslationFilter  │  → Exception handling
  │  FilterSecurityInterceptor   │  → Authorization decision
  └─────────────────────────────┘
  │
  ▼
  DispatcherServlet (resource)
```

---

## Authentication Flow

### Standard Form Login Flow

```
1. User submits credentials (username/password)
2. UsernamePasswordAuthenticationFilter creates
   UsernamePasswordAuthenticationToken (unauthenticated)
3. Filter delegates to AuthenticationManager (ProviderManager)
4. ProviderManager iterates over AuthenticationProviders
5. DaoAuthenticationProvider calls UserDetailsService.loadUserByUsername()
6. PasswordEncoder.matches() verifies password
7. On success: creates fully-populated Authentication object
8. SecurityContextHolder.setContext() stores the Authentication
9. Session management creates/reuses HTTP session
10. AuthenticationSuccessHandler is invoked
```

### Custom Authentication Provider

```java
@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    @Override
    public Authentication authenticate(Authentication authentication)
            throws AuthenticationException {
        String username = authentication.getName();
        String password = (String) authentication.getCredentials();

        UserDetails user = userDetailsService.loadUserByUsername(username);

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new BadCredentialsException("Invalid password");
        }

        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }

        return new UsernamePasswordAuthenticationToken(
            user, password, user.getAuthorities());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class
            .isAssignableFrom(authentication);
    }
}
```

---

## OAuth2 / OpenID Connect

### OAuth2 Roles

| Role | Description |
|------|-------------|
| **Resource Owner** | User who authorizes access to their data |
| **Client** | Application requesting access |
| **Authorization Server** | Issues access tokens |
| **Resource Server** | Serves protected resources |

### OAuth2 Grant Types

| Grant Type | Use Case |
|------------|----------|
| **Authorization Code** | Server-side web apps (most common) |
| **PKCE** | Mobile/native apps |
| **Client Credentials** | Server-to-server |
| **Refresh Token** | Obtain new access tokens |
| **Device Code** | Input-constrained devices |

### OpenID Connect

OIDC extends OAuth2 with identity layer:

- **ID Token** — JWT containing user identity claims
- **UserInfo Endpoint** — returns user claims
- **Discovery** — `/.well-known/openid-configuration`
- **scopes**: `openid`, `profile`, `email`, `address`, `phone`

### Spring Security OAuth2 Config

```java
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/oauth2/authorization/my-oauth2")
                .defaultSuccessUrl("/profile")
                .failureUrl("/login?error=true")
                .userInfoEndpoint(userInfo -> userInfo
                    .userService(customOAuth2UserService())
                    .oidcUserService(customOidcUserService())
                )
            )
            .oauth2Client(Customizer.withDefaults())
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthConverter())
                )
            );
        return http.build();
    }
}
```

---

## JWT & Resource Server

### JWT Structure

```
Header:    {"alg":"RS256","typ":"JWT","kid":"abc123"}
Payload:   {"sub":"user123","iss":"https://auth.example.com", "exp":1718000000}
Signature: RSASHA256(base64UrlEncode(header) + "." + base64UrlEncode(payload))
```

### Resource Server Configuration

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.issuer-uri}")
    private String issuerUri;

    @Bean
    public SecurityFilterChain resourceServerFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasAuthority("SCOPE_profile")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt
                    .jwtAuthenticationConverter(jwtAuthConverter())
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable);

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder
            .withJwkSetUri(issuerUri + "/.well-known/jwks.json")
            .jwsAlgorithm(SignatureAlgorithm.RS256)
            .build();
    }

    private Converter<Jwt, AbstractAuthenticationToken> jwtAuthConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthorities = new JwtGrantedAuthoritiesConverter();
        grantedAuthorities.setAuthorityPrefix("ROLE_");
        grantedAuthorities.setAuthoritiesClaimName("roles");

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(grantedAuthorities);
        converter.setPrincipalClaimName("sub");
        return converter;
    }
}
```

### Custom JWT Validation Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private final JwtValidator jwtValidator;
    private final UserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtValidator jwtValidator,
                                   UserDetailsService userDetailsService) {
        this.jwtValidator = jwtValidator;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = extractToken(request);

        if (token != null) {
            try {
                JwtClaims claims = jwtValidator.validateToken(token);

                UserDetails userDetails = userDetailsService
                    .loadUserByUsername(claims.subject());

                if (!userDetails.isEnabled()) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "User account is disabled");
                    return;
                }

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, token, userDetails.getAuthorities());

                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (JwtValidationException e) {
                log.warn("JWT validation failed: {}", e.getMessage());
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, e.getMessage());
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

---

## Method Security

### Annotation-Based Security

```java
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class MethodSecurityConfig {
    // Optionally define a PermissionEvaluator
}
```

### Available Annotations

| Annotation | Source | Example |
|------------|--------|---------|
| `@PreAuthorize` | Spring | `@PreAuthorize("hasRole('ADMIN')")` |
| `@PostAuthorize` | Spring | `@PostAuthorize("returnObject.owner == authentication.name")` |
| `@PreFilter` | Spring | `@PreFilter("filterObject.owner == authentication.name")` |
| `@PostFilter` | Spring | `@PostFilter("filterObject.owner == authentication.name")` |
| `@Secured` | Spring | `@Secured("ROLE_ADMIN")` |
| `@RolesAllowed` | JSR-250 | `@RolesAllowed("ADMIN")` |

### Expression-Based Security

```java
@Service
public class DocumentService {

    @PreAuthorize("hasRole('ADMIN') or #document.owner == authentication.name")
    public Document updateDocument(@Param("document") Document document) {
        // Only owner or admin can update
        return repository.save(document);
    }

    @PostAuthorize("returnObject.owner == authentication.name")
    public Document getDocument(Long id) {
        Document doc = repository.findById(id).orElseThrow();
        // Check ownership after retrieval
        return doc;
    }

    @PreAuthorize("hasPermission(#id, 'Document', 'read')")
    public Document readDocument(Long id) {
        return repository.findById(id).orElseThrow();
    }

    @PreFilter("filterObject.owner == authentication.name")
    public void saveDocuments(List<Document> documents) {
        // Only include documents owned by current user
        repository.saveAll(documents);
    }

    @PostFilter("filterObject.owner == authentication.name")
    public List<Document> getAllDocuments() {
        // Filter results to only return owned documents
        return repository.findAll();
    }
}
```

### Permission Evaluator

```java
@Component
public class DocumentPermissionEvaluator implements PermissionEvaluator {

    private final DocumentRepository documentRepository;

    @Override
    public boolean hasPermission(Authentication auth,
                                 Object targetDomainObject,
                                 Object permission) {
        if (targetDomainObject instanceof Document doc) {
            return hasPermissionOnDocument(auth, doc, permission.toString());
        }
        return false;
    }

    @Override
    public boolean hasPermission(Authentication auth,
                                 Serializable targetId,
                                 String targetType,
                                 Object permission) {
        if ("Document".equals(targetType)) {
            Document doc = documentRepository.findById((Long) targetId)
                .orElse(null);
            if (doc != null) {
                return hasPermissionOnDocument(auth, doc, permission.toString());
            }
        }
        return false;
    }

    private boolean hasPermissionOnDocument(Authentication auth,
                                            Document doc,
                                            String permission) {
        String username = auth.getName();

        return switch (permission) {
            case "read" -> doc.isPublic() || doc.getOwner().equals(username);
            case "write" -> doc.getOwner().equals(username);
            case "delete" -> doc.getOwner().equals(username)
                || auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            case "admin" -> auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            default -> false;
        };
    }
}
```

---

## Role Hierarchy & ACL

### Role Hierarchy

Role hierarchy allows expressing that one role implies another.

```java
@Bean
public RoleHierarchy roleHierarchy() {
    RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
    hierarchy.setHierarchy("""
        ROLE_SUPER_ADMIN > ROLE_ADMIN
        ROLE_ADMIN > ROLE_MODERATOR
        ROLE_MODERATOR > ROLE_USER
        """);
    return hierarchy;
}

@Bean
public MethodSecurityExpressionHandler methodSecurityExpressionHandler(
        RoleHierarchy roleHierarchy) {
    DefaultMethodSecurityExpressionHandler handler =
        new DefaultMethodSecurityExpressionHandler();
    handler.setRoleHierarchy(roleHierarchy);
    return handler;
}
```

With this hierarchy: a user with `ROLE_SUPER_ADMIN` automatically has all permissions of `ROLE_ADMIN`, `ROLE_MODERATOR`, and `ROLE_USER`.

### ACL (Access Control List)

Spring Security ACL provides granular object-level permissions.

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class AclConfig {

    @Bean
    public MutableAclService aclService(DataSource dataSource,
                                        LookupStrategy lookupStrategy,
                                        AclCache aclCache) {
        return new JdbcMutableAclService(dataSource, lookupStrategy, aclCache);
    }

    @Bean
    public LookupStrategy lookupStrategy(DataSource dataSource,
                                         AclCache aclCache) {
        return new BasicLookupStrategy(dataSource, aclCache,
            new AclAuthorizationStrategyImpl(
                new SimpleGrantedAuthority("ROLE_ADMIN")),
            new ConsoleAuditLogger());
    }

    @Bean
    public AclCache aclCache(CacheManager cacheManager) {
        return new SpringCacheBasedAclCache(
            cacheManager.getCache("aclCache"),
            new DefaultPermissionGrantingStrategy(new ConsoleAuditLogger()),
            new BasicAclAuthorizationStrategy(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );
    }
}
```

```java
@Service
public class AclService {

    private final MutableAclService aclService;
    private final ObjectIdentityRetrievalStrategy identityStrategy;

    public PermissionGrantingService(MutableAclService aclService) {
        this.aclService = aclService;
        this.identityStrategy =
            new ObjectIdentityRetrievalStrategyImpl();
    }

    @Transactional
    public void grantPermission(Authentication auth,
                                Object domainObject,
                                Permission permission) {
        ObjectIdentity identity = identityStrategy.getObjectIdentity(domainObject);
        Sid sid = new PrincipalSid(auth);

        MutableAcl acl;
        try {
            acl = (MutableAcl) aclService.readAclById(identity);
        } catch (NotFoundException e) {
            acl = aclService.createAcl(identity);
        }

        acl.insertAce(acl.getEntries().size(), permission, sid, true);
        aclService.updateAcl(acl);
    }

    public boolean hasPermission(Authentication auth,
                                 Object domainObject,
                                 Permission permission) {
        ObjectIdentity identity = identityStrategy.getObjectIdentity(domainObject);
        Sid sid = new PrincipalSid(auth);

        try {
            Acl acl = aclService.readAclById(identity, List.of(sid));
            return acl.isGranted(List.of(permission), List.of(sid), false);
        } catch (NotFoundException e) {
            return false;
        }
    }
}
```

---

## CSRF, CORS, Security Headers

### CSRF Protection

CSRF (Cross-Site Request Forgery) protection is enabled by default in Spring Security for state-changing operations.

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .csrf(csrf -> csrf
            // Default: HttpSessionCsrfTokenRepository
            .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
            // Exempt specific endpoints
            .ignoringRequestMatchers("/webhook/**")
            // Disable for stateless APIs
            // .disable()
        );
    return http.build();
}
```

**CSRF Token Flow:**
1. Server generates a CSRF token and sends it to the client (cookie or header)
2. Client includes the token in state-changing requests (header or form parameter)
3. Server verifies the token matches

### CORS Configuration

```java
@Configuration
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
            .allowedOrigins("https://myapp.com", "https://admin.myapp.com")
            .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
            .allowedHeaders("*")
            .allowCredentials(true)
            .maxAge(3600);
    }
}
```

Or via `SecurityFilterChain`:

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .cors(cors -> cors.configurationSource(corsConfigurationSource()));
    return http.build();
}

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();
    config.setAllowedOrigins(List.of("https://myapp.com"));
    config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE"));
    config.setAllowedHeaders(List.of("*"));
    config.setAllowCredentials(true);
    config.setMaxAge(3600L);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/api/**", config);
    return source;
}
```

### Security Headers

```java
@Bean
public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
    http
        .headers(headers -> headers
            .xssProtection(xss -> xss
                .headerValue(XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK))
            .contentSecurityPolicy(csp -> csp
                .policyDirectives("default-src 'self'; script-src 'self' cdn.example.com"))
            .contentTypeOptions(Customizer.withDefaults())
            .frameOptions(frame -> frame.sameOrigin())
            .httpStrictTransportSecurity(hsts -> hsts
                .includeSubDomains(true)
                .maxAgeInSeconds(31536000))
            .cacheControl(Customizer.withDefaults())
        );
    return http.build();
}
```

**Default Headers Set by Spring Security:**

| Header | Default Value | Purpose |
|--------|---------------|---------|
| `Cache-Control` | `no-cache, no-store, max-age=0, must-revalidate` | Prevent caching |
| `Pragma` | `no-cache` | HTTP 1.0 cache control |
| `Expires` | `0` | Expiration |
| `X-Content-Type-Options` | `nosniff` | MIME sniffing prevention |
| `X-Frame-Options` | `DENY` | Clickjacking prevention |
| `Strict-Transport-Security` (HSTS) | `max-age=31536000; includeSubDomains` | HTTPS enforcement |
| `X-XSS-Protection` | `0` | XSS filter |

---

## Reactive Security

### Reactive Security Configuration

```java
@Configuration
@EnableWebFluxSecurity
public class ReactiveSecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
            .authorizeExchange(exchanges -> exchanges
                .pathMatchers(HttpMethod.GET, "/api/public/**").permitAll()
                .pathMatchers("/api/admin/**").hasRole("ADMIN")
                .pathMatchers("/api/users/**").hasAuthority("SCOPE_profile")
                .anyExchange().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults())
            )
            .csrf(ServerHttpSecurity.CsrfSpec::disable)
            .build();
    }

    @Bean
    public ReactiveUserDetailsService userDetailsService(
            UserRepository userRepository) {
        return username -> userRepository.findByUsername(username)
            .map(user -> org.springframework.security.core.userdetails.User
                .withUsername(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRoles().toArray(String[]::new))
                .build()
            )
            .switchIfEmpty(Mono.error(
                new UsernameNotFoundException("User not found: " + username)));
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(
            ReactiveUserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        return new UserDetailsRepositoryReactiveAuthenticationManager(
            userDetailsService, passwordEncoder);
    }
}
```

### Reactive Security Context

```java
@RestController
@RequestMapping("/api")
public class ReactiveProfileController {

    @GetMapping("/profile")
    public Mono<ProfileResponse> getProfile(
            @AuthenticationPrincipal Mono<OAuth2User> principal) {
        return principal.map(user -> new ProfileResponse(
            user.getName(),
            user.getAttributes().get("email").toString(),
            user.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList()
        ));
    }

    @GetMapping("/r2dbc-profile")
    public Mono<ProfileResponse> getR2dbcProfile() {
        return ReactiveSecurityContextHolder.getContext()
            .map(SecurityContext::getAuthentication)
            .flatMap(auth -> {
                String username = auth.getName();
                return userRepository.findByUsername(username)
                    .map(user -> new ProfileResponse(
                        user.getUsername(),
                        user.getEmail(),
                        auth.getAuthorities().stream()
                            .map(GrantedAuthority::getAuthority)
                            .toList()
                    ));
            });
    }
}

record ProfileResponse(String username, String email, List<String> roles) {}
```

### Reactive Method Security

```java
@Configuration
@EnableReactiveMethodSecurity
public class ReactiveMethodSecurityConfig {
}

@RestController
@RequestMapping("/api/documents")
public class ReactiveDocumentController {

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public Mono<Void> deleteDocument(@PathVariable String id) {
        return documentService.delete(id);
    }

    @PostAuthorize("returnObject?.owner == authentication?.name")
    @GetMapping("/{id}")
    public Mono<Document> getDocument(@PathVariable String id) {
        return documentService.findById(id);
    }

    @PostFilter("filterObject.owner == authentication?.name")
    @GetMapping
    public Flux<Document> getAllDocuments() {
        return documentService.findAll();
    }
}
```

---

## Java Code Examples

### 1. Complete Security Configuration with Multiple Security Chains

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/api/**")
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/orders/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(Customizer.withDefaults()))
            .sessionManagement(sm -> sm
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .csrf(AbstractHttpConfigurer::disable)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType("application/json");
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write(
                        "{\"error\":\"Unauthorized\",\"message\":\"%s\"}"
                            .formatted(authException.getMessage()));
                }));

        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain formLoginSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/login", "/register", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/perform_login")
                .defaultSuccessUrl("/dashboard")
                .failureUrl("/login?error=true")
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .deleteCookies("JSESSIONID")
                .invalidateHttpSession(true)
            )
            .rememberMe(remember -> remember
                .key("unique-and-secret-key")
                .tokenValiditySeconds(86400 * 14) // 14 days
                .userDetailsService(userDetailsService())
            )
            .sessionManagement(sm -> sm
                .sessionFixation().migrateSession()
                .maximumSessions(1)
                .maxSessionsPreventsLogin(true)
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder().encode("admin-pass"))
            .roles("ADMIN", "USER")
            .build();

        UserDetails user = User.builder()
            .username("user")
            .password(passwordEncoder().encode("user-pass"))
            .roles("USER")
            .build();

        return new InMemoryUserDetailsManager(admin, user);
    }

    @Bean
    public RoleHierarchy roleHierarchy() {
        RoleHierarchyImpl hierarchy = new RoleHierarchyImpl();
        hierarchy.setHierarchy("ROLE_ADMIN > ROLE_USER");
        return hierarchy;
    }

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter> jwtFilterRegistration(
            JwtAuthenticationFilter filter) {
        FilterRegistrationBean<JwtAuthenticationFilter> registration =
            new FilterRegistrationBean<>(filter);
        registration.setEnabled(false); // Don't auto-register, we add it manually
        return registration;
    }
}
```

### 2. JWT Token Service

```java
// JwtTokenService.java
package com.example.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@Service
public class JwtTokenService {

    private static final Logger log = LoggerFactory.getLogger(JwtTokenService.class);

    private final SecretKey signingKey;
    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;

    public JwtTokenService(
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiration:3600000}") long accessExp,
            @Value("${jwt.refresh-token-expiration:604800000}") long refreshExp) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTokenExpiration = accessExp;
        this.refreshTokenExpiration = refreshExp;
    }

    public String generateAccessToken(String username, List<String> roles) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + accessTokenExpiration);

        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .claim("type", "access")
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact();
    }

    public String generateRefreshToken(String username) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + refreshTokenExpiration);

        return Jwts.builder()
            .subject(username)
            .claim("type", "refresh")
            .issuedAt(now)
            .expiration(expiry)
            .signWith(signingKey)
            .compact();
    }

    public JwtValidationResult validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();

            String username = claims.getSubject();
            String type = claims.get("type", String.class);
            @SuppressWarnings("unchecked")
            List<String> roles = claims.get("roles", List.class);

            if (claims.getExpiration().before(new Date())) {
                return JwtValidationResult.expired("Token expired");
            }

            return JwtValidationResult.valid(username, roles, type);

        } catch (JwtException e) {
            log.warn("JWT validation failed: {}", e.getMessage());
            return JwtValidationResult.invalid(e.getMessage());
        }
    }

    public record JwtValidationResult(boolean valid, String username,
                                      List<String> roles, String type,
                                      String error) {
        public static JwtValidationResult valid(String username,
                                                List<String> roles,
                                                String type) {
            return new JwtValidationResult(true, username, roles, type, null);
        }

        public static JwtValidationResult expired(String error) {
            return new JwtValidationResult(false, null, null, null, error);
        }

        public static JwtValidationResult invalid(String error) {
            return new JwtValidationResult(false, null, null, null, error);
        }
    }
}
```

### 3. Method Security with Custom Annotations

```java
// Custom annotation
@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or @orderSecurity.isOwner(#orderId, authentication)")
public @interface IsOwnerOrAdmin {
}

// Security service
@Component("orderSecurity")
public class OrderSecurity {

    private final OrderRepository orderRepository;

    public OrderSecurity(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public boolean isOwner(String orderId, Authentication authentication) {
        return orderRepository.findById(orderId)
            .map(order -> order.getUserId().equals(authentication.getName()))
            .orElse(false);
    }

    public boolean isOwnerOrOrgMember(String orderId, Authentication authentication) {
        return orderRepository.findById(orderId)
            .map(order -> {
                String userId = authentication.getName();
                return order.getUserId().equals(userId)
                    || order.getOrgMembers().contains(userId);
            })
            .orElse(false);
    }

    public boolean canCancel(String orderId, Authentication authentication) {
        return orderRepository.findById(orderId)
            .map(order -> {
                boolean isOwner = order.getUserId().equals(authentication.getName());
                boolean isPending = "PENDING".equals(order.getStatus());
                boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
                return (isOwner && isPending) || isAdmin;
            })
            .orElse(false);
    }
}

// Usage in controller
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/{id}")
    @IsOwnerOrAdmin  // custom annotation
    public Mono<Order> getOrder(@PathVariable String id) {
        return orderService.findById(id);
    }

    @PostMapping("/{id}/cancel")
    @PreAuthorize("@orderSecurity.canCancel(#id, authentication)")
    public Mono<Order> cancelOrder(@PathVariable String id) {
        return orderService.cancel(id);
    }
}
```

### 4. Security Audit Logging with Events

```java
// SecurityAuditEvent.java
package com.example.security.audit;

import org.springframework.context.ApplicationEvent;

public class SecurityAuditEvent extends ApplicationEvent {

    private final String username;
    private final String action;
    private final String resource;
    private final String outcome;
    private final String ipAddress;

    public SecurityAuditEvent(Object source, String username, String action,
                              String resource, String outcome, String ipAddress) {
        super(source);
        this.username = username;
        this.action = action;
        this.resource = resource;
        this.outcome = outcome;
        this.ipAddress = ipAddress;
    }

    // Getters omitted for brevity
}
```

```java
// AuditPublisher.java (helper)
@Component
public class AuditPublisher {

    private final ApplicationEventPublisher publisher;

    public AuditPublisher(ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public void publishLoginSuccess(String username, String ip) {
        publisher.publishEvent(new SecurityAuditEvent(
            this, username, "LOGIN", "SYSTEM", "SUCCESS", ip));
    }

    public void publishLoginFailure(String username, String ip) {
        publisher.publishEvent(new SecurityAuditEvent(
            this, username, "LOGIN", "SYSTEM", "FAILURE", ip));
    }

    public void publishAccessDenied(String username, String resource, String ip) {
        publisher.publishEvent(new SecurityAuditEvent(
            this, username, "ACCESS_DENIED", resource, "DENIED", ip));
    }
}
```

```java
// AuditEventListener.java
@Component
public class AuditEventListener {

    private static final Logger auditLog = LoggerFactory.getLogger("SECURITY_AUDIT");

    @EventListener
    public void handleSecurityEvent(SecurityAuditEvent event) {
        auditLog.info("USER={} | ACTION={} | RESOURCE={} | OUTCOME={} | IP={}",
            event.getUsername(),
            event.getAction(),
            event.getResource(),
            event.getOutcome(),
            event.getIpAddress());
    }
}
```

### 5. Custom Authentication Success/ Failure Handlers

```java
// CustomAuthenticationSuccessHandler.java
@Component
public class CustomAuthenticationSuccessHandler
        extends SavedRequestAwareAuthenticationSuccessHandler {

    private static final Logger log = LoggerFactory.getLogger(
        CustomAuthenticationSuccessHandler.class);

    private final AuditPublisher auditPublisher;

    public CustomAuthenticationSuccessHandler(AuditPublisher auditPublisher) {
        this.auditPublisher = auditPublisher;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException, ServletException {

        String username = authentication.getName();
        String ip = request.getRemoteAddr();

        log.info("User {} logged in successfully from {}", username, ip);
        auditPublisher.publishLoginSuccess(username, ip);

        super.onAuthenticationSuccess(request, response, authentication);
    }
}
```

```java
// CustomAuthenticationFailureHandler.java
@Component
public class CustomAuthenticationFailureHandler
        extends SimpleUrlAuthenticationFailureHandler {

    private static final Logger log = LoggerFactory.getLogger(
        CustomAuthenticationFailureHandler.class);

    private final AuditPublisher auditPublisher;

    public CustomAuthenticationFailureHandler(AuditPublisher auditPublisher) {
        this.auditPublisher = auditPublisher;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception)
            throws IOException, ServletException {

        String username = request.getParameter("username");
        String ip = request.getRemoteAddr();

        log.warn("Login failed for user {} from {}: {}",
            username, ip, exception.getMessage());
        auditPublisher.publishLoginFailure(username, ip);

        super.onAuthenticationFailure(request, response, exception);
    }
}
```

### 6. OAuth2 Client with Custom User Service

```java
// CustomOAuth2UserService.java
@Component
public class CustomOAuth2UserService
        implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    private static final Logger log = LoggerFactory.getLogger(
        CustomOAuth2UserService.class);

    private final UserRepository userRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate =
            new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration()
            .getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration()
            .getProviderDetails().getUserInfoEndpoint()
            .getUserNameAttributeName();

        Map<String, Object> attributes = oAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String name = (String) attributes.get("name");

        // Upsert user in local database
        User user = userRepository.findByEmail(email)
            .orElseGet(() -> {
                User newUser = new User();
                newUser.setEmail(email);
                newUser.setName(name);
                newUser.setProvider(registrationId);
                newUser.setRoles(List.of("ROLE_USER"));
                return userRepository.save(newUser);
            });

        log.info("OAuth2 login: user={}, provider={}", email, registrationId);

        Set<GrantedAuthority> authorities = new HashSet<>(oAuth2User.getAuthorities());
        authorities.addAll(user.getRoles().stream()
            .map(SimpleGrantedAuthority::new)
            .toList());

        return new DefaultOAuth2User(authorities, attributes, userNameAttributeName);
    }
}
```

---

## 20+ Interview Questions

### Q1: Explain the Spring Security authentication architecture.
**Answer**: Spring Security's authentication architecture is built on a chain of filters. The `SecurityContextHolder` stores the `SecurityContext` (typically `ThreadLocal`). The `AuthenticationManager` (typically `ProviderManager`) delegates to a list of `AuthenticationProvider`s. Each provider supports a specific authentication type (e.g., `DaoAuthenticationProvider` for username/password). On success, a fully-populated `Authentication` object is stored in the `SecurityContext`. The `SecurityContextHolderFilter` manages context persistence across requests.

### Q2: What is the difference between AuthenticationManager, ProviderManager, and AuthenticationProvider?
**Answer**: `AuthenticationManager` is the central interface for processing authentication requests. `ProviderManager` is the default implementation that delegates to a list of `AuthenticationProvider`s. Each `AuthenticationProvider` handles a specific authentication type (e.g., JWT, LDAP, database). `ProviderManager` iterates through providers until one returns a non-null response. If no provider authenticates, it throws `ProviderNotFoundException`.

### Q3: How does Spring Security handle session fixation?
**Answer**: By default, Spring Security creates a new HTTP session on login (`sessionFixation().migrateSession()`). This prevents attackers from fixing a session ID before the user logs in. Options: `migrateSession()` (default) — creates new session, copies attributes; `newSession()` — creates new empty session; `changeSessionId()` — changes session ID (Servlet 3.1+); `none()` — no protection (not recommended).

### Q4: Explain the JWT authentication flow in a stateless API.
**Answer**: (1) Client sends credentials to `/api/auth/login`; (2) Server validates credentials and returns a JWT access token (and optionally a refresh token); (3) Client stores the token (local storage, memory, httpOnly cookie); (4) Client includes the token in the `Authorization: Bearer <token>` header for subsequent requests; (5) A filter (e.g., `JwtAuthenticationFilter` or Spring's `BearerTokenAuthenticationFilter`) extracts and validates the token; (6) On success, the `SecurityContext` is populated; (7) No HTTP session is used — `SessionCreationPolicy.STATELESS`.

### Q5: What is the difference between hasRole() and hasAuthority()?
**Answer**: `hasRole('ADMIN')` checks for `ROLE_ADMIN` — it automatically adds the `ROLE_` prefix. `hasAuthority('ROLE_ADMIN')` checks for the exact authority string. `hasAuthority('SCOPE_profile')` checks for OAuth2 scopes. `hasRole` is equivalent to `hasAuthority('ROLE_' + role)`. Additionally, `hasRole` respects the `RoleHierarchy` (if configured), while `hasAuthority` does not.

### Q6: How does OAuth2 authorization code flow work with PKCE?
**Answer**: (1) Client generates a `code_verifier` (random string) and `code_challenge` (SHA-256 hash of verifier). (2) Client redirects user to authorization server with `response_type=code`, `code_challenge`, and `code_challenge_method=S256`. (3) User authenticates and consents. (4) Authorization server redirects back with an authorization code. (5) Client sends the code + original `code_verifier` to the token endpoint. (6) Server verifies verifier matches challenge, then returns access token. PKCE prevents interception attacks in public clients.

### Q7: Explain the Spring Security filter chain and how to customize it.
**Answer**: The `SecurityFilterChain` is an ordered list of filters that intercept every request. The order matters. Default filters include `SecurityContextHolderFilter`, `CsrfFilter`, `LogoutFilter`, `UsernamePasswordAuthenticationFilter`, `ExceptionTranslationFilter`, and `FilterSecurityInterceptor`. Customization: `http.addFilterBefore(myFilter, UsernamePasswordAuthenticationFilter.class)` or `http.addFilterAfter(myFilter, CsrfFilter.class)`. Multiple `SecurityFilterChain` beans can be defined with `@Order` and `securityMatcher` for different URL patterns.

### Q8: What is a SecurityContextRepository and how does it work?
**Answer**: `SecurityContextRepository` is an interface for loading and saving the `SecurityContext` between requests. Default implementations: `HttpSessionSecurityContextRepository` (session-based) and `RequestAttributeSecurityContextRepository` (stateless, per-request). When a filter chain starts, the `SecurityContextHolderFilter` loads the context from the repository and stores it in `SecurityContextHolder`. After the request completes, it saves the context back.

### Q9: How do you implement RBAC (Role-Based Access Control) in Spring Security?
**Answer**: (1) Define roles as `GrantedAuthority` objects (e.g., `ROLE_USER`, `ROLE_ADMIN`). (2) Use HTTP security: `.requestMatchers("/admin/**").hasRole("ADMIN")`. (3) Use method security: `@PreAuthorize("hasRole('ADMIN')")`. (4) For dynamic roles, load from database via `UserDetailsService`. (5) Use `RoleHierarchy` for role inheritance. (6) Use `PermissionEvaluator` for resource-level permissions. (7) For complex RBAC, consider a custom `AccessDecisionManager`.

### Q10: What is CSRF and how does Spring Security prevent it?
**Answer**: CSRF (Cross-Site Request Forgery) tricks an authenticated user into making unintended requests. Spring Security prevents it by: (1) Generating a unique CSRF token per session (or per request for CookieCsrfTokenRepository). (2) Requiring the token in state-changing requests (POST, PUT, DELETE, PATCH). (3) Verifying the token server-side. For stateless APIs (JWT), CSRF is typically disabled because tokens are not session-based.

### Q11: How does Spring Security integrate with OpenID Connect?
**Answer**: Spring Security's OAuth2 client module supports OIDC. The `oauth2Login()` DSL configures the login flow. After user authentication, the framework exchanges the authorization code for tokens. The `OidcUserService` loads the OpenID Connect user info. The ID Token is validated (signature, issuer, audience, expiration). Claims are mapped to authorities via `OAuth2UserService` and `OidcUserService`. The `OAuth2AuthorizedClientService` manages token storage.

### Q12: Explain the difference between @PreAuthorize and @PostAuthorize.
**Answer**: `@PreAuthorize` evaluates the security expression **before** the method executes. Use for: access control based on method arguments. `@PostAuthorize` evaluates **after** the method returns. Use for: access control based on the return value (e.g., `returnObject.owner == authentication.name`). `@PostAuthorize` requires a `MethodSecurityExpressionHandler` bean. Note that `@PostAuthorize` doesn't prevent the method from executing — it only controls whether the result is returned or an exception is thrown.

### Q13: How do you implement a custom PasswordEncoder?
**Answer**: Implement the `PasswordEncoder` interface with two methods: `encode(CharSequence rawPassword)` returns the encoded password, and `matches(CharSequence rawPassword, String encodedPassword)` verifies a raw password against an encoded one. Spring Security provides: `BCryptPasswordEncoder`, `SCryptPasswordEncoder`, `Argon2PasswordEncoder`, `Pbkdf2PasswordEncoder`. Custom encoders can implement adaptive one-way hashing, salting, and key stretching.

### Q14: What is the SecurityContextHolder strategy and how does it work?
**Answer**: `SecurityContextHolder` stores the `SecurityContext` for the current thread. Strategies: `MODE_THREADLOCAL` (default) — stores in `ThreadLocal` (not shared across threads); `MODE_INHERITABLETHREADLOCAL` — uses `InheritableThreadLocal` (propagated to child threads); `MODE_GLOBAL` — uses a static field (shared across all threads). In reactive applications, the context is stored in Reactor's `Context` instead of `ThreadLocal`.

### Q15: How does Spring Security handle CORS?
**Answer**: Spring Security integrates with Spring MVC's CORS support. The `HttpSecurity.cors()` configuration source applies CORS logic. When a preflight `OPTIONS` request is received, Spring Security's filter chain processes CORS headers. The `CorsFilter` or `CorsProcessor` checks the request against configured `CorsConfiguration` (allowed origins, methods, headers, credentials). If the request doesn't match, it's rejected. CORS is configured separately from authorization.

### Q16: What is the DelegatingPasswordEncoder and why is it useful?
**Answer**: `DelegatingPasswordEncoder` supports multiple password encoding formats. It delegates to different `PasswordEncoder` implementations based on the prefix of the stored password (e.g., `{bcrypt}...`, `{scrypt}...`, `{pbkdf2}...`). This is useful during password encoding migration — new passwords use a stronger encoder while old passwords continue to work. The default format in Spring Security 5+ is `{bcrypt}`.

### Q17: Explain the authentication event system in Spring Security.
**Answer**: Spring Security publishes `AbstractAuthenticationEvent` subclasses: `AuthenticationSuccessEvent`, `AuthenticationFailureBadCredentialsEvent`, `AuthenticationFailureLockedEvent`, etc. Use `@EventListener` in a Spring bean to handle these events. Events are published by the `ProviderManager` and authentication filters. This is useful for audit logging, account locking after failed attempts, and security monitoring.

### Q18: How do you implement "remember me" functionality?
**Answer**: (1) Configure `http.rememberMe()` with a key and `UserDetailsService`. (2) On login with remember-me checked, a cookie with a token is set. (3) The `RememberMeAuthenticationFilter` intercepts requests without a session but with the remember-me cookie. (4) The `TokenBasedRememberMeServices` validates the cookie (username, expiration, MD5 hash). (5) On successful validation, the user is automatically authenticated. (6) Persistent remember-me uses a database table (`PersistentRememberMeToken`).

### Q19: What is the difference between @EnableWebSecurity and @EnableGlobalMethodSecurity?
**Answer**: `@EnableWebSecurity` is used to enable HTTP security configuration using `SecurityFilterChain` beans. It replaces the older `WebSecurityConfigurerAdapter`. `@EnableMethodSecurity` (Spring Security 6+) enables method-level security annotations (`@PreAuthorize`, `@PostAuthorize`, `@Secured`, `@RolesAllowed`). You typically use both: `@EnableWebSecurity` for HTTP security and `@EnableMethodSecurity` for method security. In older versions, `@EnableGlobalMethodSecurity` was used.

### Q20: How does Spring Security's authorization architecture work at the filter level?
**Answer**: The `FilterSecurityInterceptor` (or `AuthorizationFilter` in Spring Security 6) is the last filter in the chain. It uses: (1) `SecurityMetadataSource` — reads security attributes from request patterns (e.g., `hasRole('ADMIN')`); (2) `AccessDecisionManager` — makes authorization decisions. The default is `AffirmativeBased` (any voter grants access). With `AuthorizationManager` (6.x): `RequestMatcherDelegatingAuthorizationManager` maps request matchers to authorization rules. If access is denied, `ExceptionTranslationFilter` handles the `AccessDeniedException`.

### Q21: How do you implement multi-tenancy in Spring Security?
**Answer**: Common approaches: (1) **Database per tenant** — `TenantContext` stores the tenant ID (from JWT or header), `AbstractRoutingDataSource` selects the database. (2) **Schema per tenant** — `MultiTenantConnectionProvider` (Hibernate). (3) **Discriminator column** — filter by tenant ID in queries. Spring Security integrates by: extracting tenant ID from JWT claims, storing in `SecurityContext`, and passing to repository queries. Filter: `http.addFilterBefore(tenantFilter, SecurityContextHolderFilter.class)`.

### Q22: Explain the resource server configuration for JWT validation.
**Answer**: Resource server configuration: (1) Configure `NimbusJwtDecoder` with `jwkSetUri()` (endpoint for public keys) or `secretKey()` (HMAC). (2) Configure `JwtAuthenticationConverter` to map JWT claims to `GrantedAuthority` objects (default uses `scope`/`scp` claim). (3) Configure authorization rules: `.oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))`. (4) Token validation includes: signature verification, expiration, issuer, audience, not-before.

### Q23: What are the different ways to store the SecurityContext?
**Answer**: (1) **Session-based** — `HttpSessionSecurityContextRepository` stores in HTTP session (default for form-login apps). (2) **Request-based** — `RequestAttributeSecurityContextRepository` stores per request (stateless). (3) **Reactive** — Reactor `Context`. (4) **Custom** — implement `SecurityContextRepository` to store in Redis, database, or JWT. Stateless APIs typically use request-based with no session (reduces server memory, enables horizontal scaling).

### Q24: How do you test Spring Security configurations?
**Answer**: (1) **@WithMockUser** — annotate test methods to run with a mock user. (2) **@WithAnonymousUser** — run as anonymous. (3) **Custom @WithSecurityContext** — create custom user factories. (4) **MockMvc** — `mockMvc.perform(get("/admin").with(user("admin").roles("ADMIN")))`. (5) **WebTestClient** — for reactive apps. (6) **SecurityMockServerConfigurers** — for JWT/OAuth2 mock tokens. (7) Integration tests with `@SpringBootTest` and `TestRestTemplate`.

### Q25: Explain the interaction between AuthenticationEntryPoint and AccessDeniedHandler.
**Answer**: `AuthenticationEntryPoint` handles situations where the user is not authenticated (no valid credentials). It typically redirects to login page (for form-login) or returns 401 (for APIs). `AccessDeniedHandler` handles situations where the user is authenticated but doesn't have the required permissions. It typically returns 403. Both can be configured via `http.exceptionHandling()`. Spring provides default implementations (`LoginUrlAuthenticationEntryPoint`, `Http403ForbiddenEntryPoint`).
