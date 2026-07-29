# PROBLEM WALKTHROUGH: Implement JWT Authentication Filter Chain

## Problem Statement

Implement a secure JWT (JSON Web Token) authentication and authorization filter chain for a Spring Boot REST API. The system should:

- Issue access tokens (short-lived, 15 min) and refresh tokens (long-lived, 7 days)
- Validate JWT signatures using RSA key pairs
- Extract user roles from JWT claims for authorization
- Support token refresh without re-authentication
- Handle token blacklisting on logout
- Implement a custom security filter chain with proper ordering
- Support both stateless (Bearer token) and stateful (refresh token) concerns

**Constraints:**
- Spring Boot 3.x with Spring Security 6.x
- Java 21+ with records for DTOs
- RSA-256 asymmetric key signing
- No OAuth2 or Keycloak dependencies — pure JWT implementation
- Thread-safe token blacklisting

---

## Step-by-Step Solution

### Step 1: JWT Token Record DTOs

```java
public record TokenPair(
    String accessToken,
    String refreshToken,
    Instant issuedAt,
    Instant accessTokenExpiresAt,
    Instant refreshTokenExpiresAt
) {}

public record AccessTokenPayload(
    String subject,
    String email,
    Set<String> roles,
    String tokenId,
    Instant issuedAt,
    Instant expiresAt
) {}

public record RefreshTokenPayload(
    String subject,
    String tokenId,
    Instant issuedAt,
    Instant expiresAt
) {}

public record AuthenticationRequest(
    String email,
    String password
) {}

public record AuthenticationResponse(
    String accessToken,
    String refreshToken,
    long expiresIn
) {}

public record RefreshRequest(
    String refreshToken
) {}

public record ErrorResponse(
    int status,
    String error,
    String message
) {}
```

### Step 2: RSA Key Management

```java
@Component
public class RsaKeyManager {

    private final Path keysDirectory;
    private KeyPair keyPair;

    public RsaKeyManager() {
        this.keysDirectory = Path.of("config/keys");
        loadOrGenerateKeys();
    }

    void loadOrGenerateKeys() {
        try {
            Path publicKeyPath = keysDirectory.resolve("public.pem");
            Path privateKeyPath = keysDirectory.resolve("private.pem");

            if (Files.exists(publicKeyPath) && Files.exists(privateKeyPath)) {
                this.keyPair = loadKeyPair(publicKeyPath, privateKeyPath);
            } else {
                Files.createDirectories(keysDirectory);
                this.keyPair = generateKeyPair();
                saveKeyPair(publicKeyPath, privateKeyPath);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize RSA keys", e);
        }
    }

    private KeyPair generateKeyPair() throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        return generator.generateKeyPair();
    }

    private KeyPair loadKeyPair(Path publicPath, Path privatePath) throws Exception {
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        byte[] publicBytes = Files.readAllBytes(publicPath);
        byte[] privateBytes = Files.readAllBytes(privatePath);

        PKCS8EncodedKeySpec privateSpec = new PKCS8EncodedKeySpec(privateBytes);
        X509EncodedKeySpec publicSpec = new X509EncodedKeySpec(publicBytes);

        return new KeyPair(
            keyFactory.generatePublic(publicSpec),
            keyFactory.generatePrivate(privateSpec)
        );
    }

    private void saveKeyPair(Path publicPath, Path privatePath) throws Exception {
        Files.write(publicPath, keyPair.getPublic().getEncoded());
        Files.write(privatePath, keyPair.getPrivate().getEncoded());
    }

    public PublicKey getPublicKey() { return keyPair.getPublic(); }
    public PrivateKey getPrivateKey() { return keyPair.getPrivate(); }
}
```

### Step 3: JWT Token Service

```java
@Service
public class JwtTokenService {

    private static final String ISSUER = "backend-academy";
    private static final String ROLE_CLAIM = "roles";
    private static final long ACCESS_TOKEN_EXPIRY = Duration.ofMinutes(15).toSeconds();
    private static final long REFRESH_TOKEN_EXPIRY = Duration.ofDays(7).toSeconds();

    private final RsaKeyManager keyManager;
    private final TokenBlacklistService blacklistService;

    public JwtTokenService(RsaKeyManager keyManager, TokenBlacklistService blacklistService) {
        this.keyManager = keyManager;
        this.blacklistService = blacklistService;
    }

    public TokenPair generateTokenPair(UserDetails user) {
        Instant now = Instant.now();
        String tokenId = UUID.randomUUID().toString();
        Set<String> roles = getRoles(user);

        String accessToken = buildAccessToken(user, now, tokenId, roles);
        String refreshToken = buildRefreshToken(user, now, tokenId);

        return new TokenPair(
            accessToken, refreshToken, now,
            now.plusSeconds(ACCESS_TOKEN_EXPIRY),
            now.plusSeconds(REFRESH_TOKEN_EXPIRY)
        );
    }

    public AccessTokenPayload validateAccessToken(String token) {
        if (blacklistService.isBlacklisted(token)) {
            throw new JwtAuthenticationException("Token is blacklisted");
        }
        try {
            JwtClaims claims = parseToken(token, keyManager.getPublicKey());
            return new AccessTokenPayload(
                claims.getSubject(),
                claims.getString("email"),
                Set.copyOf(claims.getStringList(ROLE_CLAIM)),
                claims.getId(),
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant()
            );
        } catch (JwtParseException | JwtVerificationException e) {
            throw new JwtAuthenticationException("Invalid or expired access token", e);
        }
    }

    public RefreshTokenPayload validateRefreshToken(String token) {
        if (blacklistService.isBlacklisted(token)) {
            throw new JwtAuthenticationException("Refresh token is blacklisted");
        }
        try {
            JwtClaims claims = parseToken(token, keyManager.getPublicKey());
            return new RefreshTokenPayload(
                claims.getSubject(),
                claims.getId(),
                claims.getIssuedAt().toInstant(),
                claims.getExpiration().toInstant()
            );
        } catch (Exception e) {
            throw new JwtAuthenticationException("Invalid or expired refresh token", e);
        }
    }

    String buildAccessToken(UserDetails user, Instant now, String tokenId, Set<String> roles) {
        JwtClaims claims = JwtClaims.builder()
            .issuer(ISSUER)
            .subject(user.getUsername())
            .id(tokenId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(ACCESS_TOKEN_EXPIRY)))
            .claim("email", user.getEmail())
            .claim(ROLE_CLAIM, List.copyOf(roles))
            .build();
        return Jwt.signWith(keyManager.getPrivateKey(), JwsAlgorithm.RS256, claims);
    }

    String buildRefreshToken(UserDetails user, Instant now, String tokenId) {
        JwtClaims claims = JwtClaims.builder()
            .issuer(ISSUER)
            .subject(user.getUsername())
            .id(tokenId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(now.plusSeconds(REFRESH_TOKEN_EXPIRY)))
            .claim("type", "refresh")
            .build();
        return Jwt.signWith(keyManager.getPrivateKey(), JwsAlgorithm.RS256, claims);
    }

    JwtClaims parseToken(String token, PublicKey publicKey) {
        return Jwt.parser()
            .requireIssuer(ISSUER)
            .verifyWith(publicKey)
            .build()
            .parse(token)
            .getPayload();
    }

    private Set<String> getRoles(UserDetails user) {
        return user.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toSet());
    }
}
```

### Step 4: Token Blacklist (In-Memory + Expiry)

```java
@Service
public class TokenBlacklistService {

    private final ConcurrentHashMap<String, Instant> blacklist = new ConcurrentHashMap<>();

    public void blacklist(String token, Instant expiry) {
        blacklist.put(token, expiry);
    }

    public boolean isBlacklisted(String token) {
        evictExpired();
        return blacklist.containsKey(token);
    }

    public void blacklistByUser(String username) {
        // In production, store user-token mapping for forced logout
    }

    @Scheduled(fixedRate = 300_000) // Every 5 minutes
    void evictExpired() {
        Instant now = Instant.now();
        blacklist.entrySet().removeIf(entry -> entry.getValue().isBefore(now));
    }
}
```

### Step 5: Custom User Details

```java
public record UserDetails(
    String id,
    String username,
    String email,
    String password,
    Set<SimpleGrantedAuthority> authorities,
    boolean enabled,
    boolean accountNonExpired,
    boolean accountNonLocked,
    boolean credentialsNonExpired
) implements org.springframework.security.core.userdetails.UserDetails {

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() { return authorities; }
    @Override
    public String getPassword() { return password; }
    @Override
    public String getUsername() { return username; }
    @Override public boolean isEnabled() { return enabled; }
    @Override public boolean isAccountNonExpired() { return accountNonExpired; }
    @Override public boolean isAccountNonLocked() { return accountNonLocked; }
    @Override public boolean isCredentialsNonExpired() { return credentialsNonExpired; }
}

@Service
public class CustomUserDetailsService implements org.springframework.security
        .core.userdetails.UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public org.springframework.security.core.userdetails.UserDetails loadUserByUsername(
            String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
            .map(this::toUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    private UserDetails toUserDetails(User user) {
        Set<SimpleGrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
            .collect(Collectors.toSet());
        return new UserDetails(
            user.getId(), user.getEmail(), user.getEmail(),
            user.getPassword(), authorities,
            user.isEnabled(), true, true, true
        );
    }
}
```

### Step 6: JWT Authentication Filter

```java
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(JwtAuthenticationFilter.class);
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenService jwtTokenService;
    private final CustomUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtTokenService jwtTokenService,
                                    CustomUserDetailsService userDetailsService) {
        this.jwtTokenService = jwtTokenService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain)
            throws ServletException, IOException {
        try {
            String token = extractToken(request);
            if (token != null) {
                AccessTokenPayload payload = jwtTokenService.validateAccessToken(token);
                UserDetails userDetails = (UserDetails) userDetailsService
                    .loadUserByUsername(payload.subject());

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request));

                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtAuthenticationException e) {
            log.warn("JWT authentication failed: {}", e.getMessage());
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);
        if (header != null && header.startsWith(BEARER_PREFIX)) {
            return header.substring(BEARER_PREFIX.length());
        }
        return null;
    }
}
```

### Step 7: Authentication Entry Point

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException)
            throws IOException {
        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(
            new ErrorResponse(401, "Unauthorized", authException.getMessage())));
    }
}
```

### Step 8: Access Denied Handler

```java
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                        AccessDeniedException accessDeniedException)
            throws IOException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(new ObjectMapper().writeValueAsString(
            new ErrorResponse(403, "Forbidden", "Insufficient permissions")));
    }
}
```

### Step 9: Security Configuration — Filter Chain

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          JwtAccessDeniedHandler jwtAccessDeniedHandler) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.jwtAccessDeniedHandler = jwtAccessDeniedHandler;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .formLogin(AbstractHttpConfigurer::disable)
            .httpBasic(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/actuator/health").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated())
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                .accessDeniedHandler(jwtAccessDeniedHandler))
            .addFilterBefore(jwtAuthenticationFilter,
                UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }
}
```

### Step 10: Authentication Controller

```java
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenService jwtTokenService;
    private final TokenBlacklistService blacklistService;
    private final CustomUserDetailsService userDetailsService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtTokenService jwtTokenService,
                          TokenBlacklistService blacklistService,
                          CustomUserDetailsService userDetailsService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenService = jwtTokenService;
        this.blacklistService = blacklistService;
        this.userDetailsService = userDetailsService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(
            @Valid @RequestBody AuthenticationRequest request) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(request.email(), request.password()));
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        TokenPair tokens = jwtTokenService.generateTokenPair(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            tokens.accessTokenExpiresAt().getEpochSecond()));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthenticationResponse> refresh(
            @Valid @RequestBody RefreshRequest request) {
        RefreshTokenPayload payload = jwtTokenService.validateRefreshToken(request.refreshToken());
        blacklistService.blacklist(request.refreshToken(), payload.expiresAt());
        UserDetails userDetails = (UserDetails) userDetailsService
            .loadUserByUsername(payload.subject());
        TokenPair tokens = jwtTokenService.generateTokenPair(userDetails);
        return ResponseEntity.ok(new AuthenticationResponse(
            tokens.accessToken(),
            tokens.refreshToken(),
            tokens.accessTokenExpiresAt().getEpochSecond()));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.replace("Bearer ", "");
        // Parse to get expiry for blacklist
        try {
            AccessTokenPayload payload = jwtTokenService.validateAccessToken(token);
            blacklistService.blacklist(token, payload.expiresAt());
        } catch (JwtAuthenticationException e) {
            // Even if token is invalid, clear it
        }
        SecurityContextHolder.clearContext();
        return ResponseEntity.noContent().build();
    }
}
```

### Step 11: Method-Level Security Example

```java
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    @GetMapping("/dashboard")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> dashboard() {
        return ResponseEntity.ok(Map.of(
            "message", "Admin dashboard",
            "timestamp", Instant.now()
        ));
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<List<UserSummary>> listUsers() {
        return ResponseEntity.ok(userRepository.findAll().stream()
            .map(u -> new UserSummary(u.getId(), u.getEmail(), u.getRoles()))
            .toList());
    }

    @PutMapping("/users/{id}/roles")
    @PreAuthorize("hasRole('ADMIN') and #id != authentication.principal.username")
    public ResponseEntity<Void> updateRoles(@PathVariable String id,
                                             @RequestBody Set<Role> roles) {
        userRepository.findById(id).ifPresent(user -> {
            user.setRoles(roles);
            userRepository.save(user);
        });
        return ResponseEntity.ok().build();
    }
}

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserProfile> me(@AuthenticationPrincipal UserDetails user) {
        return ResponseEntity.ok(new UserProfile(
            user.id(), user.username(), user.email(), user.authorities()));
    }
}
```

### Step 12: User Entity & Repository (Simplified)

```java
@Entity
@Table(name = "users")
public class User {
    @Id
    private String id = UUID.randomUUID().toString();

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @ElementCollection(fetch = FetchType.EAGER)
    @Enumerated(EnumType.STRING)
    private Set<Role> roles = new HashSet<>();

    private boolean enabled = true;
    private Instant createdAt = Instant.now();

    // Getters and setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public Set<Role> getRoles() { return roles; }
    public void setRoles(Set<Role> roles) { this.roles = roles; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
}

public enum Role {
    USER, ADMIN, MODERATOR
}

public interface UserRepository extends JpaRepository<User, String> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
}
```

### Step 13: Registration Endpoint

```java
@RestController
@RequestMapping("/api/public")
public class RegistrationController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public RegistrationController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(
            @Valid @RequestBody RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already in use"));
        }
        User user = new User();
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRoles(Set.of(Role.USER));
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(Map.of("message", "User registered successfully"));
    }
}

public record RegisterRequest(
    @Email @NotBlank String email,
    @NotBlank @Size(min = 8, max = 100) String password,
    @NotBlank String name
) {}
```

---

## Complexity Analysis

| Operation | Time | Space |
|-----------|------|-------|
| JWT generation (RSA-256 sign) | O(1) cryptographic operation | O(token length) |
| JWT validation (RSA-256 verify) | O(1) cryptographic operation | O(payload) |
| Token blacklist insert | O(1) hash put | O(1) per entry |
| Token blacklist lookup | O(1) hash get | O(1) |
| Blacklist expiry eviction | O(n) scan entries | O(1) |
| BCrypt password verification | O(1) hash comparison | O(1) |

---

## Follow-Up Questions

1. **How would you scale token blacklisting across multiple instances?** — Use Redis instead of in-memory map. Redis `SET` with TTL matching token expiry. Each instance checks Redis before validating.

2. **How do you handle token revocation for compromised tokens?** — Maintain a Redis set of revoked token IDs. Check against this set in the filter. Support immediate revocation for security incidents.

3. **What's the difference between opaque tokens and JWTs?** — JWTs are self-contained (payload in the token). Opaque tokens require server-side lookup. JWTs enable stateless auth but can't be revoked (need blacklist).

4. **How would you implement refresh token rotation?** — Every refresh invalidates the old refresh token and issues a new one. Track refresh token usage to detect theft (if old token is reused, revoke all tokens for user).

5. **How do you protect against CSRF when using JWTs?** — Store JWT in `Authorization` header (not cookie). Ensure browser doesn't automatically attach it. For cookie-based JWTs, use `SameSite=Strict` and CSRF tokens.

---

## Test Cases

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class JwtAuthenticationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        User user = new User();
        user.setEmail("test@example.com");
        user.setPassword(passwordEncoder.encode("password123"));
        user.setRoles(Set.of(Role.USER));
        userRepository.save(user);

        User admin = new User();
        admin.setEmail("admin@example.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setRoles(Set.of(Role.ADMIN));
        userRepository.save(admin);
    }

    @Test
    void shouldAuthenticateAndReturnToken() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "test@example.com", "password": "password123"}
                    """))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty())
            .andExpect(jsonPath("$.expiresIn").isNumber());
    }

    @Test
    void shouldRejectInvalidCredentials() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "test@example.com", "password": "wrong"}
                    """))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldAccessProtectedEndpointWithValidToken() throws Exception {
        String token = obtainAccessToken("test@example.com", "password123");

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    void shouldRejectRequestWithoutToken() throws Exception {
        mockMvc.perform(get("/api/users/me"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectRequestWithInvalidToken() throws Exception {
        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer invalid-token"))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRefreshToken() throws Exception {
        String loginResponse = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "test@example.com", "password": "password123"}
                    """))
            .andReturn().getResponse().getContentAsString();

        String refreshToken = new ObjectMapper().readTree(loginResponse)
            .get("refreshToken").asText();

        mockMvc.perform(post("/api/auth/refresh")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"refreshToken": "%s"}
                    """.formatted(refreshToken)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.accessToken").isNotEmpty())
            .andExpect(jsonPath("$.refreshToken").isNotEmpty());
    }

    @Test
    void shouldLogoutAndBlacklistToken() throws Exception {
        String token = obtainAccessToken("test@example.com", "password123");

        mockMvc.perform(post("/api/auth/logout")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/users/me")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldEnforceRoleBasedAccess() throws Exception {
        String userToken = obtainAccessToken("test@example.com", "password123");
        String adminToken = obtainAccessToken("admin@example.com", "admin123");

        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + userToken))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/admin/dashboard")
                .header("Authorization", "Bearer " + adminToken))
            .andExpect(status().isOk());
    }

    private String obtainAccessToken(String email, String password) throws Exception {
        String response = mockMvc.perform(post("/api/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "%s", "password": "%s"}
                    """.formatted(email, password)))
            .andReturn().getResponse().getContentAsString();
        return new ObjectMapper().readTree(response).get("accessToken").asText();
    }

    @Test
    void shouldRegisterNewUser() throws Exception {
        mockMvc.perform(post("/api/public/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "new@example.com", "password": "newpass123", "name": "New User"}
                    """))
            .andExpect(status().isCreated());
    }
}
```

---

## Summary

This JWT authentication implementation covers:
- **Asymmetric RSA-256 signing**: private key signs, public key verifies
- **Two-token strategy**: short-lived access token + long-lived refresh token
- **Token refresh**: obtain new access token without re-authentication
- **Token blacklisting**: prevent reuse of logged-out/revoked tokens
- **Role-based authorization**: `@PreAuthorize`, URL-based matchers, method security
- **Stateless architecture**: no `HttpSession`, no server-side session state
- **Proper filter chain ordering**: JWT filter runs before `UsernamePasswordAuthenticationFilter`
- **Exception handling**: consistent 401/403 JSON error responses