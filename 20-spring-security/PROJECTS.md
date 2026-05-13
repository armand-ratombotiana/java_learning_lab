# Spring Security Module - PROJECTS.md

---

# Mini-Projects Overview

| Concept | Duration | Description |
|---------|----------|-------------|
| JWT Authentication | 2 hours | Token generation, validation, filter |
| User Details Service | 2 hours | Custom user details, BCrypt encoding |
| Role-based Access Control | 2 hours | Method security, route authorization |
| OAuth2 Integration | 2 hours | Resource server, JWT decoder |
| Custom Filters | 2 hours | Request filtering, authentication entry point |
| Real-world: E-Commerce Security | 15+ hours | Complete security with audit, MFA support |

---

# Mini-Project: JWT Authentication System

## Project Overview

**Duration**: 5-6 hours  
**Difficulty**: Intermediate  
**Concepts Used**: JWT Token Authentication, User Details Service, BCrypt Password Encoding, Role-based Access Control, Filter Chains

This mini-project demonstrates Spring Security fundamentals with JWT-based authentication, custom user details service, and role-based authorization.

---

## Project Structure

```
20-spring-security/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   └── JwtTokenProvider.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   └── ProductController.java
│   ├── service/
│   │   ├── UserService.java
│   │   └── JwtAuthenticationService.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Role.java
│   │   └── Product.java
│   ├── filter/
│   │   └── JwtAuthenticationFilter.java
│   └── repository/
│       └── UserRepository.java
└── src/main/resources/
    └── application.yml
```

---

## Step 1: POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>spring-security-demo</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
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
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Step 2: Model Classes

```java
// model/User.java
package com.learning.model;

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
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    private boolean enabled = true;
    
    public User() {}
    
    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public void addRole(String role) {
        roles.add(role);
    }
}
```

```java
// model/Role.java
package com.learning.model;

public enum Role {
    ROLE_USER,
    ROLE_ADMIN,
    ROLE_MANAGER
}
```

```java
// model/Product.java
package com.learning.model;

import java.math.BigDecimal;

public class Product {
    private Long id;
    private String name;
    private String description;
    private BigDecimal price;
    private Integer stock;
    private String category;
    
    public Product() {}
    
    public Product(Long id, String name, String description, BigDecimal price, Integer stock) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stock = stock;
    }
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
```

---

## Step 3: JWT Token Provider

```java
// config/JwtTokenProvider.java
package com.learning.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtTokenProvider {
    
    @Value("${jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration}")
    private Long expiration;
    
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
    
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims extractAllClaims(String token) {
        return Jwts.parser()
            .verifyWith(getSigningKey())
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }
    
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
            .claims(claims)
            .subject(subject)
            .issuedAt(new Date(System.currentTimeMillis()))
            .expiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey())
            .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
}
```

---

## Step 4: Custom User Details Service

```java
// service/UserService.java
package com.learning.service;

import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);
        
        if (user == null) {
            throw new UsernameNotFoundException("User not found: " + username);
        }
        
        return new org.springframework.security.core.userdetails.User(
            user.getUsername(),
            user.getPassword(),
            user.isEnabled(),
            true,
            true,
            true,
            user.getRoles().stream()
                .map(SimpleGrantedAuthority::new)
                .toList()
        );
    }
    
    public User saveUser(User user) {
        return userRepository.save(user);
    }
    
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }
}
```

```java
// repository/UserRepository.java
package com.learning.repository;

import com.learning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    User findByEmail(String email);
}
```

---

## Step 5: JWT Authentication Filter

```java
// filter/JwtAuthenticationFilter.java
package com.learning.filter;

import com.learning.config.JwtTokenProvider;
import com.learning.service.UserService;
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
    
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;
    
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider, UserService userService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, 
                                    FilterChain filterChain) throws ServletException, IOException {
        
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String username;
        
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }
        
        jwt = authHeader.substring(7);
        
        try {
            username = jwtTokenProvider.extractUsername(jwt);
            
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userService.loadUserByUsername(username);
                
                if (jwtTokenProvider.validateToken(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                    );
                    
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: " + e.getMessage());
        }
        
        filterChain.doFilter(request, response);
    }
}
```

---

## Step 6: Security Configuration

```java
// config/SecurityConfig.java
package com.learning.config;

import com.learning.filter.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/manager/**").hasAnyRole("ADMIN", "MANAGER")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## Step 7: Controllers

```java
// controller/AuthController.java
package com.learning.controller;

import com.learning.config.JwtTokenProvider;
import com.learning.model.User;
import com.learning.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;
    
    public AuthController(UserService userService, JwtTokenProvider jwtTokenProvider,
                         AuthenticationManager authenticationManager,
                         UserDetailsService userDetailsService,
                         PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userService.findByUsername(request.username()) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Username already exists"));
        }
        
        User user = new User(request.username(), passwordEncoder.encode(request.password()), request.email());
        user.addRole("ROLE_USER");
        userService.saveUser(user);
        
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
            String token = jwtTokenProvider.generateToken(userDetails);
            
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("message", "Invalid credentials"));
        }
    }
    
    public record RegisterRequest(String username, String password, String email) {}
    public record LoginRequest(String username, String password) {}
}
```

```java
// controller/ProductController.java
package com.learning.controller;

import com.learning.model.Product;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/products")
public class ProductController {
    
    private final Map<Long, Product> products = new ConcurrentHashMap<>();
    
    public ProductController() {
        products.put(1L, new Product(1L, "Laptop", "High-performance laptop", new BigDecimal("1299.99"), 50));
        products.put(2L, new Product(2L, "Phone", "Smartphone", new BigDecimal("899.99"), 100));
        products.put(3L, new Product(3L, "Headphones", "Wireless headphones", new BigDecimal("299.99"), 75));
    }
    
    @GetMapping
    public List<Product> getAllProducts() {
        return new ArrayList<>(products.values());
    }
    
    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return products.get(id);
    }
    
    @PostMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    public Product createProduct(@RequestBody Product product) {
        Long id = products.size() + 1L;
        product.setId(id);
        products.put(id, product);
        return product;
    }
    
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Product updateProduct(@PathVariable Long id, @RequestBody Product product) {
        product.setId(id);
        products.put(id, product);
        return product;
    }
    
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void deleteProduct(@PathVariable Long id) {
        products.remove(id);
    }
}
```

---

## Step 8: Application Properties

```yaml
# src/main/resources/application.yml
server:
  port: 8080

spring:
  datasource:
    url: jdbc:h2:mem:securitydb
    driver-class-name: org.h2.Driver
    username: sa
    password:
  h2:
    console:
      enabled: true
  jpa:
    hibernate:
      ddl-auto: create-drop
    show-sql: true

jwt:
  secret: mySecretKeyForJWTTokenGenerationMustBeLongEnough256Bits123456789
  expiration: 86400000
```

---

## Step 9: Main Application

```java
// Main.java
package com.learning;

import com.learning.model.User;
import com.learning.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
    
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    
    public Main(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        initializeUsers();
    }
    
    private void initializeUsers() {
        if (userService.findByUsername("admin") == null) {
            User admin = new User("admin", passwordEncoder.encode("admin123"), "admin@company.com");
            admin.addRole("ROLE_ADMIN");
            admin.addRole("ROLE_MANAGER");
            userService.saveUser(admin);
        }
        
        if (userService.findByUsername("user") == null) {
            User user = new User("user", passwordEncoder.encode("user123"), "user@company.com");
            user.addRole("ROLE_USER");
            userService.saveUser(user);
        }
        
        System.out.println("Initialized default users: admin, user");
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 20-spring-security
mvn clean compile
mvn spring-boot:run

# Test endpoints:
# 1. Register: POST /api/auth/register
# 2. Login: POST /api/auth/login (returns JWT token)
# 3. Access products: GET /api/products (with Authorization header)
```

---

# Real-World Project: E-Commerce Security System

## Project Overview

**Duration**: 15+ hours  
**Difficulty**: Advanced  
**Concepts Used**: OAuth2, JWT Refresh Tokens, Method-level Security, Custom Authentication Provider, CSRF Protection, Session Management, Encrypted Properties

This comprehensive project implements enterprise-grade security for an e-commerce platform with OAuth2 integration, multi-factor authentication support, and advanced role-based access control.

---

## Project Structure

```
20-spring-security/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   │   ├── SecurityConfig.java
│   │   ├── OAuth2Config.java
│   │   ├── JwtTokenProvider.java
│   │   └── EncryptionConfig.java
│   ├── controller/
│   │   ├── AuthController.java
│   │   ├── ProductController.java
│   │   ├── OrderController.java
│   │   └── AdminController.java
│   ├── service/
│   │   ├── CustomUserDetailsService.java
│   │   ├── JwtAuthenticationService.java
│   │   └── AuditService.java
│   ├── model/
│   │   ├── User.java
│   │   ├── Role.java
│   │   ├── Permission.java
│   │   └── AuditLog.java
│   ├── filter/
│   │   ├── JwtAuthenticationFilter.java
│   │   └── RequestLoggingFilter.java
│   ├── exception/
│   │   ├── GlobalExceptionHandler.java
│   │   └── CustomAuthenticationEntryPoint.java
│   └── repository/
│       ├── UserRepository.java
│       └── AuditLogRepository.java
└── src/main/resources/
    ├── application.yml
    └── data.sql
```

---

## POM.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>ecommerce-security</artifactId>
    <version>1.0-SNAPSHOT</version>
    <packaging>jar</packaging>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>17</java.version>
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
            <artifactId>spring-boot-starter-oauth2-resource-server</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-validation</artifactId>
        </dependency>
        <dependency>
            <groupId>com.h2database</groupId>
            <artifactId>h2</artifactId>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-api</artifactId>
            <version>0.12.3</version>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-impl</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>io.jsonwebtoken</groupId>
            <artifactId>jjwt-jackson</artifactId>
            <version>0.12.3</version>
            <scope>runtime</scope>
        </dependency>
    </dependencies>
</project>
```

---

## Enhanced Security Configuration

```java
// config/SecurityConfig.java
package com.learning.config;

import com.learning.filter.JwtAuthenticationFilter;
import com.learning.exception.CustomAuthenticationEntryPoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
public class SecurityConfig {
    
    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final UserDetailsService userDetailsService;
    private final CustomAuthenticationEntryPoint customAuthenticationEntryPoint;
    
    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                         UserDetailsService userDetailsService,
                         CustomAuthenticationEntryPoint customAuthenticationEntryPoint) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.userDetailsService = userDetailsService;
        this.customAuthenticationEntryPoint = customAuthenticationEntryPoint;
    }
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .ignoringRequestMatchers("/api/**")
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasAuthority("ROLE_ADMIN")
                .requestMatchers("/api/manager/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER")
                .requestMatchers("/api/user/**").hasAnyAuthority("ROLE_ADMIN", "ROLE_MANAGER", "ROLE_USER")
                .anyRequest().authenticated()
            )
            .authenticationProvider(authenticationProvider())
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint(customAuthenticationEntryPoint)
            );
        
        return http.build();
    }
    
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }
    
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
```

---

## OAuth2 Configuration

```java
// config/OAuth2Config.java
package com.learning.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import javax.crypto.spec.SecretKeySpec;

@Configuration
public class OAuth2Config {
    
    @Bean
    public JwtDecoder jwtDecoder() {
        String secretKey = "mySecretKeyForJWTTokenGenerationMustBeLongEnough256Bits123456789";
        SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(), "HmacSHA256");
        return NimbusJwtDecoder.withSecretKey(secretKeySpec).build();
    }
}
```

---

## Audit Service

```java
// service/AuditService.java
package com.learning.service;

import com.learning.model.AuditLog;
import com.learning.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuditService {
    
    private final AuditLogRepository auditLogRepository;
    
    public AuditService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }
    
    public void logAccess(String username, String action, String resource, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(action);
        log.setResource(resource);
        log.setIpAddress(ipAddress);
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
    
    public void logAuthentication(String username, boolean success, String ipAddress) {
        AuditLog log = new AuditLog();
        log.setUsername(username);
        log.setAction(success ? "LOGIN_SUCCESS" : "LOGIN_FAILURE");
        log.setResource("/api/auth/login");
        log.setIpAddress(ipAddress);
        log.setTimestamp(LocalDateTime.now());
        
        auditLogRepository.save(log);
    }
}
```

---

## Model Classes

```java
// model/User.java
package com.learning.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
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
    
    private String firstName;
    private String lastName;
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    private Set<String> roles = new HashSet<>();
    
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_permissions", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "permission")
    private Set<String> permissions = new HashSet<>();
    
    private boolean enabled = true;
    private boolean accountNonLocked = true;
    private int failedLoginAttempts = 0;
    private LocalDateTime lockoutEnd;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    public User() {}
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    
    public Set<String> getRoles() { return roles; }
    public void setRoles(Set<String> roles) { this.roles = roles; }
    
    public Set<String> getPermissions() { return permissions; }
    public void setPermissions(Set<String> permissions) { this.permissions = permissions; }
    
    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    
    public boolean isAccountNonLocked() { return accountNonLocked; }
    public void setAccountNonLocked(boolean accountNonLocked) { this.accountNonLocked = accountNonLocked; }
    
    public int getFailedLoginAttempts() { return failedLoginAttempts; }
    public void setFailedLoginAttempts(int failedLoginAttempts) { this.failedLoginAttempts = failedLoginAttempts; }
    
    public LocalDateTime getLockoutEnd() { return lockoutEnd; }
    public void setLockoutEnd(LocalDateTime lockoutEnd) { this.lockoutEnd = lockoutEnd; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    public LocalDateTime getLastLogin() { return lastLogin; }
    public void setLastLogin(LocalDateTime lastLogin) { this.lastLogin = lastLogin; }
    
    public void addRole(String role) { roles.add(role); }
    public void addPermission(String permission) { permissions.add(permission); }
}
```

```java
// model/AuditLog.java
package com.learning.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "audit_logs")
public class AuditLog {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String username;
    private String action;
    private String resource;
    private String ipAddress;
    private LocalDateTime timestamp;
    
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    
    public String getResource() { return resource; }
    public void setResource(String resource) { this.resource = resource; }
    
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    
    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
```

---

## Repositories

```java
// repository/UserRepository.java
package com.learning.repository;

import com.learning.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
```

```java
// repository/AuditLogRepository.java
package com.learning.repository;

import com.learning.model.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {
    List<AuditLog> findByUsername(String username);
    List<AuditLog> findByTimestampBetween(LocalDateTime start, LocalDateTime end);
    
    @Query("SELECT a FROM AuditLog a WHERE a.action = 'LOGIN_FAILURE' AND a.timestamp > ?1")
    List<AuditLog> findRecentFailedLogins(LocalDateTime since);
}
```

---

## Controllers

```java
// controller/AuthController.java
package com.learning.controller;

import com.learning.config.JwtTokenProvider;
import com.learning.model.User;
import com.learning.repository.UserRepository;
import com.learning.service.AuditService;
import com.learning.service.JwtAuthenticationService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final JwtAuthenticationService jwtAuthenticationService;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final PasswordEncoder passwordEncoder;
    
    public AuthController(AuthenticationManager authenticationManager,
                         UserDetailsService userDetailsService,
                         JwtTokenProvider jwtTokenProvider,
                         JwtAuthenticationService jwtAuthenticationService,
                         UserRepository userRepository,
                         AuditService auditService,
                         PasswordEncoder passwordEncoder) {
        this.authenticationManager = authenticationManager;
        this.userDetailsService = userDetailsService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.jwtAuthenticationService = jwtAuthenticationService;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.passwordEncoder = passwordEncoder;
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username already exists"));
        }
        
        if (userRepository.existsByEmail(request.email())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Email already exists"));
        }
        
        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.addRole("ROLE_USER");
        
        userRepository.save(user);
        
        return ResponseEntity.ok(Map.of("message", "User registered successfully"));
    }
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        try {
            authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
            );
            
            UserDetails userDetails = userDetailsService.loadUserByUsername(request.username());
            String token = jwtTokenProvider.generateToken(userDetails);
            String refreshToken = jwtAuthenticationService.generateRefreshToken(userDetails);
            
            User user = userRepository.findByUsername(request.username()).orElse(null);
            if (user != null) {
                user.setLastLogin(java.time.LocalDateTime.now());
                user.setFailedLoginAttempts(0);
                userRepository.save(user);
            }
            
            auditService.logAuthentication(request.username(), true, httpRequest.getRemoteAddr());
            
            return ResponseEntity.ok(Map.of(
                "token", token,
                "refreshToken", refreshToken,
                "type", "Bearer"
            ));
            
        } catch (LockedException e) {
            auditService.logAuthentication(request.username(), false, httpRequest.getRemoteAddr());
            return ResponseEntity.status(423).body(Map.of("error", "Account is locked"));
        } catch (BadCredentialsException e) {
            User user = userRepository.findByUsername(request.username()).orElse(null);
            if (user != null) {
                user.setFailedLoginAttempts(user.getFailedLoginAttempts() + 1);
                if (user.getFailedLoginAttempts() >= 5) {
                    user.setAccountNonLocked(false);
                    user.setLockoutEnd(java.time.LocalDateTime.now().plusMinutes(30));
                }
                userRepository.save(user);
            }
            
            auditService.logAuthentication(request.username(), false, httpRequest.getRemoteAddr());
            return ResponseEntity.status(401).body(Map.of("error", "Invalid credentials"));
        }
    }
    
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshRequest request) {
        return jwtAuthenticationService.refreshToken(request.refreshToken())
            .map(token -> ResponseEntity.ok(Map.of("token", token)))
            .orElse(ResponseEntity.badRequest().body(Map.of("error", "Invalid refresh token")));
    }
    
    public record RegisterRequest(String username, String password, String email, 
                                  String firstName, String lastName) {}
    public record LoginRequest(String username, String password) {}
    public record RefreshRequest(String refreshToken) {}
}
```

```java
// controller/AdminController.java
package com.learning.controller;

import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    
    private final UserRepository userRepository;
    
    public AdminController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    
    @GetMapping("/users")
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    @GetMapping("/users/{id}")
    public User getUserById(@PathVariable Long id) {
        return userRepository.findById(id).orElse(null);
    }
    
    @PutMapping("/users/{id}/enable")
    public ResponseEntity<?> enableUser(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                user.setEnabled(true);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "User enabled"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @PutMapping("/users/{id}/disable")
    public ResponseEntity<?> disableUser(@PathVariable Long id) {
        return userRepository.findById(id)
            .map(user -> {
                user.setEnabled(false);
                userRepository.save(user);
                return ResponseEntity.ok(Map.of("message", "User disabled"));
            })
            .orElse(ResponseEntity.notFound().build());
    }
    
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        if (userRepository.existsById(id)) {
            userRepository.deleteById(id);
            return ResponseEntity.ok(Map.of("message", "User deleted"));
        }
        return ResponseEntity.notFound().build();
    }
}
```

---

## Main Application with Data Initialization

```java
// Main.java
package com.learning;

import com.learning.model.User;
import com.learning.repository.UserRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class Main {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public Main(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        initializeData();
    }
    
    private void initializeData() {
        if (userRepository.count() == 0) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword(passwordEncoder.encode("admin123"));
            admin.setEmail("admin@company.com");
            admin.setFirstName("Admin");
            admin.setLastName("User");
            admin.addRole("ROLE_ADMIN");
            admin.addPermission("READ");
            admin.addPermission("WRITE");
            admin.addPermission("DELETE");
            userRepository.save(admin);
            
            User manager = new User();
            manager.setUsername("manager");
            manager.setPassword(passwordEncoder.encode("manager123"));
            manager.setEmail("manager@company.com");
            manager.setFirstName("Manager");
            manager.setLastName("User");
            manager.addRole("ROLE_MANAGER");
            manager.addPermission("READ");
            manager.addPermission("WRITE");
            userRepository.save(manager);
            
            User user = new User();
            user.setUsername("user");
            user.setPassword(passwordEncoder.encode("user123"));
            user.setEmail("user@company.com");
            user.setFirstName("Regular");
            user.setLastName("User");
            user.addRole("ROLE_USER");
            user.addPermission("READ");
            userRepository.save(user);
            
            System.out.println("Initialized default users: admin, manager, user");
        }
    }
    
    @GetMapping("/health")
    public String health() {
        return "OK";
    }
    
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }
}
```

---

## Build Instructions

```bash
cd 20-spring-security
mvn clean compile
mvn spring-boot:run

# Test flow:
# 1. POST /api/auth/register - Register new user
# 2. POST /api/auth/login - Login to get JWT token
# 3. Use token in Authorization header for protected endpoints
# 4. POST /api/auth/refresh - Refresh expired token
```

This comprehensive security project demonstrates enterprise-grade authentication and authorization with JWT, role-based access control, audit logging, and account lockout protection.