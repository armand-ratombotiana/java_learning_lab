# Java Security Projects - Module 11

This module covers Java security fundamentals, OAuth 2.0, JWT (JSON Web Tokens), authentication, authorization, and security best practices.

## Mini-Project: JWT Authentication System (2-4 hours)

### Overview
Implement a JWT-based authentication system with Spring Security, covering token generation, validation, and protected endpoints.

### Project Structure
```
security-jwt-demo/
├── pom.xml
├── src/main/java/com/learning/security/
│   ├── SecurityJwtApplication.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── JwtConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── ProductController.java
│   ├── model/
│   │   ├── User.java
│   │   └── UserRole.java
│   ├── service/
│   │   ├── JwtService.java
│   │   ├── UserService.java
│   │   └── CustomUserDetailsService.java
│   └── filter/
│       └── JwtAuthenticationFilter.java
└── src/main/resources/
    └── application.yml
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>security-jwt-demo</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.nimbusds</groupId>
            <artifactId>nimbus-jose-jwt</artifactId>
            <version>9.37.3</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

#### User Entity
```java
package com.learning.security.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    @Column(unique = true, nullable = false)
    private String email;
    
    private boolean enabled;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Set<UserRole> roles = new HashSet<>();
    
    public User() {
        this.enabled = true;
    }
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.enabled = true;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public Set<UserRole> getRoles() { return roles; }
    public void setRoles(Set<UserRole> roles) { this.roles = roles; }
    
    public void addRole(UserRole role) {
        this.roles.add(role);
    }
}

public enum UserRole {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MODERATOR
}
```

#### JwtService
```java
package com.learning.security.service;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class JwtService {
    
    private static final String SECRET_KEY = "YourSecretKeyForJWTTokenGenerationMustBeLongEnough256Bits";
    private static final long EXPIRATION_TIME = TimeUnit.HOURS.toMillis(24);
    private static final long REFRESH_EXPIRATION_TIME = TimeUnit.DAYS.toMillis(7);
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", userDetails.getAuthorities().stream()
            .map(auth -> auth.getAuthority())
            .toList());
        
        return createToken(claims, userDetails.getUsername(), EXPIRATION_TIME);
    }
    
    public String generateRefreshToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");
        
        return createToken(claims, userDetails.getUsername(), REFRESH_EXPIRATION_TIME);
    }
    
    private String createToken(Map<String, Object> claims, String subject, long expiration) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration);
        
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
            .subject(subject)
            .issuer("security-jwt-demo")
            .issueTime(now)
            .expirationTime(expiryDate)
            .claim("claims", claims)
            .build();
        
        try {
            JWSHeader header = new JWSHeader(JWSAlgorithm.HS256);
            SignedJWT signedJWT = new SignedJWT(header, jwtClaimsSet);
            
            MACSigner signer = new MACSigner(SECRET_KEY.getBytes());
            signedJWT.sign(signer);
            
            return signedJWT.serialize();
        } catch (Exception e) {
            throw new RuntimeException("Error generating JWT token", e);
        }
    }
    
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }
    
    public String extractUsername(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return signedJWT.getJWTClaimsSet().getSubject();
        } catch (Exception e) {
            throw new RuntimeException("Error extracting username from token", e);
        }
    }
    
    public boolean isTokenExpired(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            Date expiration = signedJWT.getJWTClaimsSet().getExpirationTime();
            return expiration.before(new Date());
        } catch (Exception e) {
            return true;
        }
    }
    
    public Map<String, Object> extractAllClaims(String token) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            return (Map<String, Object>) signedJWT.getJWTClaimsSet().getClaim("claims");
        } catch (Exception e) {
            throw new RuntimeException("Error extracting claims", e);
        }
    }
}
```

#### CustomUserDetailsService
```java
package com.learning.security.service;

import com.learning.security.model.User;
import com.learning.security.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true,
            true,
            true,
            user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.name()))
                .collect(Collectors.toList())
        );
    }
}
```

#### JwtAuthenticationFilter
```java
package com.learning.security.filter;

import com.learning.security.service.CustomUserDetailsService;
import com.learning.security.service.JwtService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    
    public JwtAuthenticationFilter(JwtService jwtService, 
                                   CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                    HttpServletResponse response, 
                                    FilterChain filterChain) 
            throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        
        try {
            username = jwtService.extractUsername(jwt);
            
            if (username != null && 
                SecurityContextHolder.getContext().getAuthentication() == null) {
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                
                if (jwtService.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = 
                        new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                        );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication", e);
        }
        
        filterChain.doFilter(request, response);
    }
}
```

#### SecurityConfig
```java
package com.learning.security.config;

import com.learning.security.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthFilter;
    private final UserDetailsService userDetailsService;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter,
                          UserDetailsService userDetailsService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.userDetailsService = userDetailsService;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/**").hasAnyRole("USER", "ADMIN")
                .anyRequest().authenticated()
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) 
            throws Exception {
        return config.getAuthenticationManager();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

#### AuthController
```java
package com.learning.security.controller;

import com.learning.security.model.User;
import com.learning.security.model.UserRole;
import com.learning.security.repository.UserRepository;
import com.learning.security.service.JwtService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthController(AuthenticationManager authenticationManager,
                         JwtService jwtService,
                         UserRepository userRepository,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody Map<String, String> credentials) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                credentials.get("username"),
                credentials.get("password")
            )
        );
        
        String username = authentication.getName();
        String token = jwtService.generateToken(
            (org.springframework.security.core.userdetails.UserDetails) 
                authentication.getPrincipal()
        );
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type", "Bearer"
        ));
    }
    
    @PostMapping("/register")
    public ResponseEntity<Map<String, String>> register(@RequestBody RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            return ResponseEntity.badRequest()
                .body(Map.of("message", "Username already exists"));
        }
        
        User user = new User(
            request.username(),
            passwordEncoder.encode(request.password()),
            request.email()
        );
        
        user.addRole(UserRole.ROLE_USER);
        
        userRepository.save(user);
        
        String token = jwtService.generateToken(user);
        
        return ResponseEntity.ok(Map.of(
            "token", token,
            "type", "Bearer"
        ));
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestHeader("Authorization") String authHeader) {
        String refreshToken = authHeader.substring(7);
        
        String username = jwtService.extractUsername(refreshToken);
        
        var userDetails = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        var userDetailsImpl = new org.springframework.security.core.userdetails.User(
            userDetails.getUsername(),
            userDetails.getPassword(),
            userDetails.isEnabled(),
            true,
            true,
            true,
            userDetails.getRoles().stream()
                .map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority(role.name()))
                .toList()
        );
        
        String newToken = jwtService.generateToken(userDetailsImpl);
        
        return ResponseEntity.ok(Map.of("token", newToken));
    }
}

record RegisterRequest(String username, String password, String email) {}
```

#### ProductController
```java
package com.learning.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class ProductController {
    
    @GetMapping("/public/products")
    public ResponseEntity<Map<String, Object>> getPublicProducts() {
        return ResponseEntity.ok(Map.of(
            "products", java.util.List.of(
                Map.of("id", 1, "name", "Product A", "price", 29.99),
                Map.of("id", 2, "name", "Product B", "price", 49.99)
            )
        ));
    }
    
    @GetMapping("/user/products")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, Object>> getUserProducts() {
        return ResponseEntity.ok(Map.of(
            "products", java.util.List.of(
                Map.of("id", 1, "name", "Product A", "price", 29.99, "discount", 5),
                Map.of("id", 2, "name", "Product B", "price", 49.99, "discount", 10)
            )
        ));
    }
    
    @PostMapping("/user/orders")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody Map<String, Object> order) {
        return ResponseEntity.ok(Map.of(
            "orderId", UUID.randomUUID().toString(),
            "status", "CREATED"
        ));
    }
    
    @GetMapping("/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, Object>> getAllUsers() {
        return ResponseEntity.ok(Map.of(
            "users", java.util.List.of(
                Map.of("id", 1, "username", "admin", "email", "admin@example.com"),
                Map.of("id", 2, "username", "user1", "email", "user1@example.com")
            )
        ));
    }
    
    @DeleteMapping("/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        return ResponseEntity.noContent().build();
    }
}
```

#### application.yml
```yaml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:securitydb
    driver-class-name: org.h2.Driver
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: false

jwt:
  secret: YourSecretKeyForJWTTokenGenerationMustBeLongEnough256Bits
  expiration: 86400000
```

### Build and Run Instructions
```bash
# Build the project
cd security-jwt-demo
mvn clean package

# Run the application
java -jar target/security-jwt-demo-1.0.0.jar

# Register a new user
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123","email":"john@example.com"}'

# Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"john","password":"password123"}'

# Access public endpoint
curl http://localhost:8080/api/public/products

# Access protected endpoint with token
curl http://localhost:8080/api/user/products \
  -H "Authorization: Bearer <token>"

# Access admin endpoint
curl http://localhost:8080/api/admin/users \
  -H "Authorization: Bearer <token>"
```

---

## Real-World Project: OAuth 2.0 Authorization Server (8+ hours)

### Overview
Build a complete OAuth 2.0 authorization server with support for multiple grant types, JWT token management, and integration with external identity providers.

### Architecture
- **Authorization Server**: Keycloak or custom Spring Authorization Server
- **Resource Server**: Protected APIs with JWT validation
- **Client Applications**: Single Page App and backend services
- **Identity Providers**: Integration with Google, GitHub, Okta

### Project Structure
```
oauth2-realworld/
├── authorization-server/
│   ├── pom.xml
│   ├── src/main/java/com/learning/auth/
│   │   ├── AuthServerApplication.java
│   │   ├── config/
│   │   ├── controller/
│   │   └── service/
│   └── src/main/resources/
├── resource-server/
│   ├── pom.xml
│   └── src/main/java/com/learning/resource/
├── client-application/
│   ├── pom.xml
│   └── src/main/java/com/learning/client/
└── docker-compose.yml
```

### Implementation

#### Authorization Server pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>authorization-server</artifactId>
    <version>1.0.0</version>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-security.version>6.2.0</spring-security.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-security</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-oauth2-client</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.security</groupId>
            <artifactId>spring-security-oauth2-authorization-server</artifactId>
            <version>1.1.0</version>
        </dependency>
        <dependency>
            <groupId>com.nimbusds</groupId>
            <artifactId>nimbus-jose-jwt</artifactId>
            <version>9.37.3</version>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

#### Authorization Server Configuration
```java
// AuthorizationServerConfig.java
package com.learning.auth.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.oauth2.server.authorization.settings.TokenSettings;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;
import org.springframework.security.web.util.matcher.MediaTypeRequestMatcher;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.time.Duration;
import java.util.UUID;

@Configuration
@EnableWebSecurity
public class AuthorizationServerConfig {
    
    @Bean
    @Order(1)
    public SecurityFilterChain authorizationServerSecurityFilterChain(HttpSecurity http) 
            throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(http);
        
        http.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
            .oidc(Customizer.withDefaults());
        
        http.exceptionHandling(exceptions -> exceptions
                .defaultAuthenticationEntryPoint(new LoginUrlAuthenticationEntryPoint("/login")))
            .oauth2ResourceServer(resourceServer -> resourceServer.jwt(Customizer.withDefaults()));
        
        return http.build();
    }
    
    @Bean
    @Order(2)
    public SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) 
            throws Exception {
        http
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/oauth2/**", "/login/**", "/error").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        
        return http.build();
    }
    
    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        RegisteredClient authorizationCodeClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("messaging-client")
            .clientSecret("{noop}secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .redirectUri("http://127.0.0.1:3000/login/oauth2/code/messaging-client-oidc")
            .redirectUri("http://127.0.0.1:3000/authorized")
            .scope(OidcScopes.OPENID)
            .scope("message.read")
            .scope("message.write")
            .clientSettings(ClientSettings.builder()
                .requireAuthorizationConsent(true)
                .requireProofKey(false)
                .build())
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofMinutes(15))
                .refreshTokenTimeToLive(Duration.ofHours(24))
                .reuseRefreshTokens(false)
                .build())
            .build();
        
        RegisteredClient clientCredClient = RegisteredClient.withId(UUID.randomUUID().toString())
            .clientId("service-client")
            .clientSecret("{noop}service-secret")
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS)
            .scope("message.read")
            .scope("admin")
            .tokenSettings(TokenSettings.builder()
                .accessTokenTimeToLive(Duration.ofHours(1))
                .build())
            .build();
        
        return new InMemoryRegisteredClientRepository(authorizationCodeClient, clientCredClient);
    }
    
    @Bean
    public JWKSource<SecurityContext> jwkSource() {
        KeyPair keyPair = generateRsaKey();
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();
        RSAKey rsaKey = new RSAKey.Builder(publicKey)
            .privateKey(privateKey)
            .keyID(UUID.randomUUID().toString())
            .build();
        JWKSet jwkSet = new JWKSet(rsaKey.toJWK());
        return new ImmutableJWKSet<>(jwkSet);
    }
    
    private static KeyPair generateRsaKey() {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(2048);
            return keyPairGenerator.generateKeyPair();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to generate RSA key pair", e);
        }
    }
    
    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }
    
    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder()
            .issuer("http://localhost:9000")
            .build();
    }
}
```

#### Resource Server Configuration
```java
package com.learning.resource.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class ResourceServerConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("SCOPE_admin")
                .requestMatchers("/api/user/**").hasAnyAuthority("SCOPE_message.read", "SCOPE_message.write")
                .anyRequest().authenticated()
            )
            .oauth2ResourceServer(oauth2 -> oauth2
                .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
            );
        
        return http.build();
    }
    
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthoritiesClaimName("scope");
        grantedAuthoritiesConverter.setAuthorityPrefix("SCOPE_");
        
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        
        return jwtAuthenticationConverter;
    }
}
```

#### OAuth2 Client Registration for External Providers
```java
package com.learning.auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;
import org.springframework.security.oauth2.core.oidc.user.OidcUserAuthority;

import java.util.HashSet;
import java.util.Set;

@Configuration
@EnableWebSecurity
public class OAuth2ClientSecurityConfig {
    
    @Bean
    public SecurityFilterChain oauth2ClientSecurityFilterChain(HttpSecurity http) throws Exception {
        http
            .oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .failureUrl("/login?error=true")
            )
            .oauth2Client(oauth2 -> oauth2
                .authorizationCodeGrant(grant -> grant
                    .authorizationRequestRepository(authorizationRequestRepository())
                    .authorizationRequestResolver(authorizationRequestResolver())
                )
            );
        
        return http.build();
    }
    
    @Bean
    public org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler 
            authenticationFailureHandler() {
        Set<String> errorWhitelist = new HashSet<>();
        errorWhitelist.add("access_denied");
        errorWhitelist.add("invalid_grant");
        
        return new org.springframework.security.web.authentication.DelegatingAuthenticationFailureHandler(
            errorWhitelist,
            new org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler("/login?error=unknown")
        );
    }
    
    private org.springframework.security.oauth2.client.web.AuthorizationRequestRepository<?>
            authorizationRequestRepository() {
        return new org.springframework.security.oauth2.client.web.HttpSessionOAuth2AuthorizationRequestRepository();
    }
    
    private org.springframework.security.oauth2.client.web.AuthorizationRequestResolver
            authorizationRequestResolver() {
        return new org.springframework.security.oauth2.client.web.DefaultOAuth2AuthorizationRequestResolver(
            new org.springframework.security.oauth2.client.RegistrationRepositoryRegistrationAdapter(
                org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository.builder()
                    .clientId("google-client")
                    .clientSecret("google-secret")
                    .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
                    .redirectUri("http://localhost:9000/login/oauth2/code/google")
                    .authorizationUri("https://accounts.google.com/o/oauth2/v2/auth")
                    .tokenUri("https://oauth2.googleapis.com/token")
                    .jwkSetUri("https://www.googleapis.com/oauth2/v3/certs")
                    .scope("openid", "profile", "email")
                    .clientName("Google")
                    .build()
            ),
            "/oauth2/authorization/"
        );
    }
}
```

#### Protected Resource Controller
```java
package com.learning.resource.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ResourceController {
    
    @GetMapping("/public/messages")
    public ResponseEntity<Map<String, Object>> getPublicMessages() {
        return ResponseEntity.ok(Map.of(
            "messages", java.util.List.of(
                Map.of("id", 1, "content", "Public message 1", "timestamp", System.currentTimeMillis()),
                Map.of("id", 2, "content", "Public message 2", "timestamp", System.currentTimeMillis())
            ),
            "total", 2
        ));
    }
    
    @GetMapping("/user/messages")
    @PreAuthorize("hasAuthority('SCOPE_message.read')")
    public ResponseEntity<Map<String, Object>> getUserMessages(
            @AuthenticationPrincipal Jwt jwt) {
        
        String subject = jwt.getSubject();
        Map<String, Object> claims = jwt.getClaims();
        
        return ResponseEntity.ok(Map.of(
            "user", subject,
            "messages", java.util.List.of(
                Map.of("id", 1, "content", "Private message 1", "from", "user@example.com"),
                Map.of("id", 2, "content", "Private message 2", "from", "admin@example.com")
            ),
            "claims", claims
        ));
    }
    
    @PostMapping("/user/messages")
    @PreAuthorize("hasAuthority('SCOPE_message.write')")
    public ResponseEntity<Map<String, String>> createMessage(
            @AuthenticationPrincipal Jwt jwt,
            @RequestBody Map<String, String> message) {
        
        String user = jwt.getSubject();
        
        return ResponseEntity.ok(Map.of(
            "id", String.valueOf(System.currentTimeMillis()),
            "content", message.get("content"),
            "author", user,
            "status", "CREATED"
        ));
    }
    
    @GetMapping("/admin/users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Map<String, Object>> getAllUsers(
            @AuthenticationPrincipal Jwt jwt) {
        
        return ResponseEntity.ok(Map.of(
            "users", java.util.List.of(
                Map.of("id", 1, "username", "admin", "email", "admin@example.com", "role", "ADMIN"),
                Map.of("id", 2, "username", "user1", "email", "user1@example.com", "role", "USER")
            )
        ));
    }
    
    @GetMapping("/user/profile")
    public ResponseEntity<Map<String, Object>> getUserProfile(
            @AuthenticationPrincipal Jwt jwt) {
        
        return ResponseEntity.ok(Map.of(
            "subject", jwt.getSubject(),
            "issuer", jwt.getIssuer().toString(),
            "issuedAt", jwt.getIssuedAt(),
            "expiresAt", jwt.getExpiresAt(),
            "claims", jwt.getClaims()
        ));
    }
}
```

#### Client Application with OAuth2 Login
```java
package com.learning.client.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.annotation.OAuth2Client;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.reactive.function.client.WebClient;
import java.util.Map;

@Controller
public class ClientController {
    
    private final WebClient webClient;
    
    public ClientController(WebClient webClient) {
        this.webClient = webClient;
    }
    
    @GetMapping("/")
    public String index() {
        return "index";
    }
    
    @GetMapping("/home")
    public String home(Model model, @AuthenticationPrincipal OAuth2User principal) {
        model.addAttribute("name", principal.getAttribute("name"));
        model.addAttribute("email", principal.getAttribute("email"));
        return "home";
    }
    
    @GetMapping("/messages")
    public String messages(Model model, 
                          @OAuth2Client("messaging-client") OAuth2AuthorizedClient authorizedClient) {
        
        String accessToken = authorizedClient.getAccessToken().getTokenValue();
        
        Map<String, Object> messages = webClient.get()
            .uri("http://localhost:8081/api/user/messages")
            .header("Authorization", "Bearer " + accessToken)
            .retrieve()
            .bodyToMono(Map.class)
            .block();
        
        model.addAttribute("messages", messages);
        return "messages";
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
```

#### docker-compose.yml
```yaml
version: '3.8'
services:
  authorization-server:
    build: ./authorization-server
    ports:
      - "9000:9000"
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:mem:authdb
      - SERVER_SSL_ENABLED=false

  resource-server:
    build: ./resource-server
    ports:
      - "8081:8080"
    environment:
      - SPRING_SECURITY_OAUTH2_RESOURCESERVER_JWT_ISSUER_URI=http://authorization-server:9000

  client-application:
    build: ./client-application
    ports:
      - "3000:3000"
    environment:
      - SPRING_SECURITY_OAUTH2_CLIENT_PROVIDER_MESSAGING_CLIENT_ISSUER_URI=http://authorization-server:9000

  keycloak:
    image: quay.io/keycloak/keycloak:23.0
    command: start-dev
    ports:
      - "8180:8080"
    environment:
      - KEYCLOAK_ADMIN=admin
      - KEYCLOAK_ADMIN_ADMIN=admin
```

### Build and Run Instructions
```bash
# Build all services
cd authorization-server && mvn clean package
cd resource-server && mvn clean package
cd client-application && mvn clean package

# Run with docker-compose
docker-compose up -d

# Access Authorization Server
# http://localhost:9000

# Register OAuth2 client using developer console or API

# Test authorization code flow
# 1. Navigate to: http://localhost:9000/oauth2/authorize?
#    response_type=code&
#    client_id=messaging-client&
#    scope=message.read%20message.write&
#    redirect_uri=http://127.0.0.1:3000/authorized

# 2. Login with user credentials

# 3. Authorize the client

# 4. Get authorization code and exchange for tokens

# Test client credentials flow
curl -X POST http://localhost:9000/oauth/token \
  -H "Authorization: Basic c2VydmljZS1jbGllbnQ6c2VydmljZS1zZWNyZXQ=" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "grant_type=client_credentials&scope=message.read"

# Access protected resource
curl http://localhost:8081/api/user/messages \
  -H "Authorization: Bearer <access_token>"
```

### Learning Outcomes
- Implement OAuth 2.0 authorization server
- Configure multiple grant types (Authorization Code, Client Credentials, Refresh Token)
- Set up JWT token management
- Integrate external identity providers
- Build resource server with JWT validation
- Implement OAuth2 client application
- Configure security with scopes and authorities