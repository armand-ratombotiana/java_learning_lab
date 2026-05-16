# Security Exercises

## Exercise 1: Basic Spring Security Configuration

### Task
Configure Spring Security with form login and custom access rules.

### Solution

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/public/**", "/css/**", "/js/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/access-denied")
            );
        
        return http.build();
    }
    
    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails user = User.builder()
            .username("user")
            .password("{bcrypt}$2a$10$...")
            .roles("USER")
            .build();
        
        UserDetails admin = User.builder()
            .username("admin")
            .password("{bcrypt}$2a$10$...")
            .roles("USER", "ADMIN")
            .build();
        
        return new InMemoryUserDetailsManager(user, admin);
    }
}
```

---

## Exercise 2: JWT Token Generation and Validation

### Task
Implement JWT token generation and validation utility.

### Solution

```java
@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    public String generateToken(Authentication authentication) {
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .toList());
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date())
            .setExpiration(new Date(System.currentTimeMillis() + jwtExpiration))
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return getClaimsFromToken(token).getSubject();
    }
    
    public boolean validateToken(String token, UserDetails userDetails) {
        try {
            Claims claims = getClaimsFromToken(token);
            String username = claims.getSubject();
            return username.equals(userDetails.getUsername()) 
                && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    private Claims getClaimsFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody();
    }
    
    private boolean isTokenExpired(String token) {
        Date expiration = getClaimsFromToken(token).getExpiration();
        return expiration.before(new Date());
    }
}

// JWT Authentication Filter
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider tokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        String token = extractToken(request);
        
        if (token != null) {
            String username = tokenProvider.getUsernameFromToken(token);
            
            if (username != null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (tokenProvider.validateToken(token, userDetails)) {
                    UsernamePasswordAuthenticationToken auth = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(auth);
                }
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

## Exercise 3: Custom UserDetailsService

### Task
Implement custom UserDetailsService loading users from database.

### Solution

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        List<GrantedAuthority> authorities = user.getRoles().stream()
            .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
            .collect(Collectors.toList());
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true, true, true,
            authorities
        );
    }
}

// Registration
public class UserRegistrationService {
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public UserEntity register(String username, String password, String email) {
        String encodedPassword = passwordEncoder.encode(password);
        
        UserEntity user = new UserEntity();
        user.setUsername(username);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        user.setEnabled(true);
        
        return userRepository.save(user);
    }
}
```

---

## Exercise 4: Method-Level Security

### Task
Implement method-level security with @PreAuthorize.

### Solution

```java
@Service
public class UserManagementService {
    
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long userId) {
        userRepository.deleteById(userId);
    }
    
    @PreAuthorize("#userId == authentication.principal.id or hasRole('ADMIN')")
    public UserProfile getUserProfile(Long userId) {
        return userRepository.findById(userId)
            .map(this::toProfile)
            .orElseThrow(() -> new UserNotFoundException(userId));
    }
    
    @PreAuthorize("hasAuthority('USER_WRITE')")
    public User updateUser(Long userId, UserUpdateRequest request) {
        // Update logic
    }
    
    @Secured({"ROLE_ADMIN", "ROLE_MANAGER"})
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @RolesAllowed({"ROLE_ADMIN"})
    public void assignRole(Long userId, String roleName) {
        // Role assignment logic
    }
}

// Enable method security
@Configuration
@EnableMethodSecurity
public class MethodSecurityConfig {
}
```

---

## Exercise 5: OAuth2 Login Configuration

### Task
Configure Google OAuth2 login.

### Solution

```java
@Configuration
@EnableWebSecurity
public class OAuth2SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/login", "/public/**").permitAll()
                .anyRequest().authenticated()
            );
        
        return http.build();
    }
}

// application.yml
spring:
  security:
    oauth2:
      client:
        registration:
          google:
            client-id: ${GOOGLE_CLIENT_ID}
            client-secret: ${GOOGLE_CLIENT_SECRET}
            scope: profile, email
        provider:
          google:
            user-name-attribute: sub

// Custom OAuth2 User Service
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {
    
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) 
            throws OAuth2AuthenticationException {
        
        OAuth2User user = super.loadUser(userRequest);
        
        // Save or update user in database
        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        
        return new DefaultOAuth2User(
            user.getAuthorities(),
            user.getAttributes(),
            "email"
        );
    }
}
```

---

## Exercise 6: JWT Filter with Security Context

### Task
Implement JWT authentication filter with proper security context.

### Solution

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtTokenProvider tokenProvider;
    private final UserDetailsService userDetailsService;
    private final JwtAuthenticationConverter jwtAuthenticationConverter;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        try {
            String jwt = extractJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                String username = tokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(
                        userDetails, 
                        null, 
                        userDetails.getAuthorities()
                    );
                
                authentication.setDetails(
                    new WebAuthenticationDetailsSource().buildDetails(request)
                );
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String extractJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

// Configuration
@Configuration
@EnableWebSecurity
public class JwtSecurityConfig {
    
    @Bean
    public SecurityFilterChain jwtFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, 
                UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```

---

## Exercise 7: CORS Configuration

### Task
Configure CORS for a REST API.

### Solution

```java
@Configuration
@EnableWebSecurity
public class CorsSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable());
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        
        configuration.setAllowedOrigins(List.of("http://localhost:3000"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/api/**", configuration);
        
        return source;
    }
}

// Or using Spring Boot properties
// application.yml
spring:
  mvc:
    cors:
      allowed-origins: http://localhost:3000
      allowed-methods: GET,POST,PUT,DELETE
      allowed-headers: "*"
      allow-credentials: true
      max-age: 3600
```

---

## Exercise 8: Password Encoding and Validation

### Task
Implement secure password encoding and validation.

### Solution

```java
@Configuration
public class PasswordConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

@Service
public class PasswordService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public String encodePassword(String rawPassword) {
        return passwordEncoder.encode(rawPassword);
    }
    
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
    
    public boolean isPasswordStrong(String password) {
        // At least 8 characters, 1 uppercase, 1 lowercase, 1 digit, 1 special
        Pattern pattern = Pattern.compile(
            "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"
        );
        return pattern.matcher(password).matches();
    }
}

// Validation example
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    @Autowired
    private PasswordService passwordService;
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationRequest request) {
        
        if (!passwordService.isPasswordStrong(request.getPassword())) {
            return ResponseEntity.badRequest()
                .body("Password is not strong enough");
        }
        
        // Register user with encoded password
        return ResponseEntity.ok("User registered");
    }
}
```

---

## Exercise 9: CSRF Protection

### Task
Configure CSRF protection with tokens.

### Solution

```java
@Configuration
@EnableWebSecurity
public class CsrfSecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .csrfTokenRequestHandler(new SpaCsrfTokenRequestHandler())
            )
            .addFilterAfter(new CsrfCookieFilter(), BasicAuthenticationFilter.class);
        
        return http.build();
    }
}

// Custom CSRF Filter
public class CsrfCookieFilter extends OncePerRequestFilter {
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        CsrfToken csrfToken = (CsrfToken) request.getAttribute("_csrf");
        
        if (csrfToken != null) {
            response.setHeader("X-XSRF-TOKEN", csrfToken.getToken());
        }
        
        filterChain.doFilter(request, response);
    }
}

// SPA Handler
public class SpaCsrfTokenRequestHandler extends CsrfTokenRequestAttributeHandler {
    private final CsrfTokenRequestHandler delegate = new CookieCsrfTokenRequestHandler();
    
    @Override
    public void handle(HttpServletRequest request, 
                       HttpServletResponse response, 
                       Supplier<CsrfToken> csrfToken) {
        delegate.handle(request, response, csrfToken);
    }
}
```

---

## Exercise 10: Rate Limiting

### Task
Implement rate limiting using Bucket4j.

### Solution

```java
@Configuration
public class RateLimitingFilter {
    
    @Bean
    public FilterRegistrationBean<RateLimitFilter> rateLimitFilter() {
        FilterRegistrationBean<RateLimitFilter> registration = new FilterRegistrationBean<>();
        registration.setFilter(new RateLimitFilter());
        registration.addUrlPatterns("/api/*");
        registration.setOrder(1);
        return registration;
    }
}

@Component
public class RateLimitFilter implements Filter {
    
    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    
    @Override
    public void doFilter(HttpServletRequest request, 
                         HttpServletResponse response, 
                         FilterChain filterChain) {
        
        String clientId = getClientId(request);
        Bucket bucket = buckets.computeIfAbsent(clientId, this::createBucket);
        
        if (bucket.tryConsume(1)) {
            filterChain.doFilter(request, response);
        } else {
            response.setStatus(429);
            response.getWriter().write("Rate limit exceeded");
        }
    }
    
    private Bucket createBucket(String clientId) {
        Bandwidth limit = Bandwidth.classic(100, Refill.intervally(100, Duration.ofMinutes(1)));
        return Bucket.builder().addLimit(limit).build();
    }
    
    private String getClientId(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth != null) {
            return auth;
        }
        return request.getRemoteAddr();
    }
}

// Using Bucket4j with Spring Security
@Configuration
public class RateLimitConfig {
    
    @Bean
    public SecurityFilterChain rateLimitFilterChain(HttpSecurity http) throws Exception {
        http
            .addFilterBefore(rateLimitFilter(), UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
}
```