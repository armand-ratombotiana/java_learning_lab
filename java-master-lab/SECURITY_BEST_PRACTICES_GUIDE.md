# Java Master Lab - Security Best Practices Guide

## 🔒 Comprehensive Security Framework

**Purpose**: Guide for implementing secure Java applications  
**Target Audience**: All developers  
**Focus**: Security, compliance, and protection  

---

## 🛡️ AUTHENTICATION & AUTHORIZATION

### Password Security

#### Hashing Passwords
```java
// ✅ Good - Using BCrypt
@Configuration
public class SecurityConfig {
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
}

@Service
public class UserService {
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    public void registerUser(User user) {
        String hashedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(hashedPassword);
        userRepository.save(user);
    }
    
    public boolean authenticateUser(String email, String password) {
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException(email));
        return passwordEncoder.matches(password, user.getPassword());
    }
}

// ❌ Bad - Storing plain text passwords
user.setPassword(password);
userRepository.save(user);
```

#### Password Validation
```java
public class PasswordValidator {
    
    private static final String PASSWORD_PATTERN = 
        "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$";
    
    public static boolean isValidPassword(String password) {
        return password != null && password.matches(PASSWORD_PATTERN);
    }
    
    public static void validatePassword(String password) {
        if (!isValidPassword(password)) {
            throw new IllegalArgumentException(
                "Password must contain at least 8 characters, " +
                "including uppercase, lowercase, digit, and special character"
            );
        }
    }
}
```

### JWT Token Security

```java
@Configuration
public class JwtConfig {
    
    @Value("${jwt.secret}")
    private String jwtSecret;
    
    @Value("${jwt.expiration}")
    private long jwtExpiration;
    
    @Bean
    public JwtTokenProvider jwtTokenProvider() {
        return new JwtTokenProvider(jwtSecret, jwtExpiration);
    }
}

@Component
public class JwtTokenProvider {
    
    private final String jwtSecret;
    private final long jwtExpiration;
    
    public JwtTokenProvider(String jwtSecret, long jwtExpiration) {
        this.jwtSecret = jwtSecret;
        this.jwtExpiration = jwtExpiration;
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities());
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpiration);
        
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(subject)
            .setIssuedAt(now)
            .setExpiration(expiryDate)
            .signWith(SignatureAlgorithm.HS512, jwtSecret)
            .compact();
    }
    
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
            .setSigningKey(jwtSecret)
            .parseClaimsJws(token)
            .getBody()
            .getSubject();
    }
    
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }
}
```

### Role-Based Access Control (RBAC)

```java
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .antMatchers("/api/admin/**").hasRole("ADMIN")
                .antMatchers("/api/user/**").hasRole("USER")
                .anyRequest().authenticated()
            .and()
            .formLogin()
                .loginPage("/login")
                .permitAll()
            .and()
            .logout()
                .permitAll();
    }
}

@RestController
@RequestMapping("/api")
public class UserController {
    
    @GetMapping("/public/info")
    public String publicInfo() {
        return "Public information";
    }
    
    @GetMapping("/user/profile")
    @PreAuthorize("hasRole('USER')")
    public User getUserProfile() {
        return getCurrentUser();
    }
    
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
    }
}
```

---

## 🔐 DATA PROTECTION

### Encryption

#### Symmetric Encryption
```java
public class EncryptionService {
    
    private final Cipher cipher;
    private final SecretKey secretKey;
    
    public EncryptionService(String encryptionKey) throws Exception {
        this.cipher = Cipher.getInstance("AES");
        this.secretKey = new SecretKeySpec(
            encryptionKey.getBytes(StandardCharsets.UTF_8), 
            0, 16, "AES"
        );
    }
    
    public String encrypt(String plaintext) throws Exception {
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes());
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    public String decrypt(String ciphertext) throws Exception {
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decodedBytes = Base64.getDecoder().decode(ciphertext);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes);
    }
}
```

#### Hashing Sensitive Data
```java
public class HashingService {
    
    public static String hashData(String data) throws NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hash);
    }
    
    public static boolean verifyHash(String data, String hash) 
            throws NoSuchAlgorithmException {
        String computedHash = hashData(data);
        return computedHash.equals(hash);
    }
}
```

### HTTPS/TLS Configuration

```yaml
# application.properties
server.ssl.key-store=classpath:keystore.p12
server.ssl.key-store-password=${SSL_KEYSTORE_PASSWORD}
server.ssl.key-store-type=PKCS12
server.ssl.key-alias=tomcat
server.ssl.enabled=true
server.port=8443

# Force HTTPS
server.http2.enabled=true
server.servlet.session.cookie.secure=true
server.servlet.session.cookie.http-only=true
```

---

## 🚫 INPUT VALIDATION & SANITIZATION

### Input Validation

```java
@RestController
@RequestMapping("/api/users")
public class UserController {
    
    @PostMapping
    public ResponseEntity<User> createUser(@Valid @RequestBody UserDTO userDTO) {
        // Validation is automatically performed by @Valid
        User user = new User();
        user.setEmail(userDTO.getEmail());
        user.setName(userDTO.getName());
        return ResponseEntity.ok(userRepository.save(user));
    }
}

@Data
public class UserDTO {
    
    @NotNull(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;
    
    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    private String password;
}
```

### SQL Injection Prevention

```java
// ❌ Bad - Vulnerable to SQL injection
String query = "SELECT * FROM users WHERE email = '" + email + "'";
User user = jdbcTemplate.queryForObject(query, new UserRowMapper());

// ✅ Good - Using parameterized queries
String query = "SELECT * FROM users WHERE email = ?";
User user = jdbcTemplate.queryForObject(query, new UserRowMapper(), email);

// ✅ Better - Using JPA
Optional<User> user = userRepository.findByEmail(email);
```

### XSS Prevention

```java
@Configuration
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers()
                .xssProtection()
                .and()
                .contentSecurityPolicy("default-src 'self'");
        return http.build();
    }
}

// Sanitize user input
public class HtmlSanitizer {
    
    private static final Policy POLICY = Policy.getInstance(
        "classpath:antisamy-ebay.xml"
    );
    
    public static String sanitize(String input) throws ScanException, PolicyException {
        AntiSamy antiSamy = new AntiSamy();
        CleanResults cr = antiSamy.scan(input, POLICY);
        return cr.getCleanHTML();
    }
}
```

---

## 🔍 SECURITY HEADERS

### HTTP Security Headers

```java
@Configuration
public class SecurityHeadersConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .headers()
                // Prevent clickjacking
                .frameOptions().deny()
                .and()
                // Prevent MIME type sniffing
                .contentTypeOptions().disable()
                .and()
                // Enable XSS protection
                .xssProtection()
                .and()
                // HSTS
                .httpStrictTransportSecurity()
                    .maxAgeInSeconds(31536000)
                    .includeSubDomains(true)
                .and()
                // CSP
                .contentSecurityPolicy("default-src 'self'");
        return http.build();
    }
}
```

---

## 🛡️ CSRF PROTECTION

### CSRF Token Configuration

```java
@Configuration
public class CsrfConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf()
                .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                .and()
            .authorizeRequests()
                .antMatchers("/api/public/**").permitAll()
                .anyRequest().authenticated();
        return http.build();
    }
}

@RestController
@RequestMapping("/api")
public class ApiController {
    
    @PostMapping("/data")
    public ResponseEntity<String> postData(
            @RequestParam String data,
            CsrfToken csrfToken) {
        // CSRF token is automatically validated
        return ResponseEntity.ok("Data received");
    }
}
```

---

## 🔐 SECURE COMMUNICATION

### API Security

```java
@RestController
@RequestMapping("/api/v1")
public class SecureApiController {
    
    @PostMapping("/sensitive-data")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<SensitiveData> processSensitiveData(
            @Valid @RequestBody SensitiveDataRequest request,
            HttpServletRequest httpRequest) {
        
        // Verify request origin
        String origin = httpRequest.getHeader("Origin");
        if (!isAllowedOrigin(origin)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        // Process data
        SensitiveData result = processData(request);
        
        return ResponseEntity.ok()
            .header("X-Content-Type-Options", "nosniff")
            .header("X-Frame-Options", "DENY")
            .body(result);
    }
    
    private boolean isAllowedOrigin(String origin) {
        return origin != null && 
               (origin.equals("https://trusted-domain.com") ||
                origin.equals("https://app.trusted-domain.com"));
    }
}
```

### Rate Limiting

```java
@Component
public class RateLimitingFilter extends OncePerRequestFilter {
    
    private final Map<String, RateLimitInfo> rateLimitMap = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS = 100;
    private static final long TIME_WINDOW_MS = 60000; // 1 minute
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
            HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String clientId = getClientId(request);
        RateLimitInfo info = rateLimitMap.computeIfAbsent(
            clientId, k -> new RateLimitInfo()
        );
        
        if (info.isLimited()) {
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded");
            return;
        }
        
        info.recordRequest();
        filterChain.doFilter(request, response);
    }
    
    private String getClientId(HttpServletRequest request) {
        String clientIp = request.getHeader("X-Forwarded-For");
        if (clientIp == null || clientIp.isEmpty()) {
            clientIp = request.getRemoteAddr();
        }
        return clientIp;
    }
    
    private static class RateLimitInfo {
        private long firstRequestTime;
        private int requestCount;
        
        synchronized boolean isLimited() {
            long now = System.currentTimeMillis();
            if (now - firstRequestTime > TIME_WINDOW_MS) {
                firstRequestTime = now;
                requestCount = 0;
            }
            return requestCount >= MAX_REQUESTS;
        }
        
        synchronized void recordRequest() {
            if (firstRequestTime == 0) {
                firstRequestTime = System.currentTimeMillis();
            }
            requestCount++;
        }
    }
}
```

---

## 📋 SECURITY AUDIT LOGGING

### Audit Logging

```java
@Aspect
@Component
public class SecurityAuditAspect {
    
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT");
    
    @Before("@annotation(com.learning.security.Auditable)")
    public void auditSecurityEvent(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String user = SecurityContextHolder.getContext()
            .getAuthentication().getName();
        
        auditLogger.info("SECURITY_EVENT: user={}, class={}, method={}, timestamp={}",
            user, className, methodName, System.currentTimeMillis());
    }
}

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Auditable {
}

@RestController
@RequestMapping("/api/admin")
public class AdminController {
    
    @DeleteMapping("/users/{id}")
    @Auditable
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
```

---

## 🔍 VULNERABILITY SCANNING

### Dependency Vulnerability Scanning

```bash
# Using OWASP Dependency-Check
mvn org.owasp:dependency-check-maven:check

# Using Snyk
npm install -g snyk
snyk test

# Using Maven Security Plugin
mvn org.apache.maven.plugins:maven-enforcer-plugin:enforce
```

### Code Security Analysis

```bash
# Using SonarQube
mvn sonar:sonar \
  -Dsonar.projectKey=java-master-lab \
  -Dsonar.sources=src \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.login=<token>

# Using SpotBugs
mvn spotbugs:check
```

---

## 📄 Document Information

| Property | Value |
|----------|-------|
| **Document Type** | Security Best Practices Guide |
| **Version** | 1.0 |
| **Created** | 2024 |
| **Status** | Active |
| **Focus** | Security |

---

**Java Master Lab - Security Best Practices Guide**

*Comprehensive Security Framework for Java Applications*

**Status: Active | Focus: Security | Impact: Protection**

---

*Secure your applications with best practices!* 🔒