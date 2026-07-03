# Module 28: Spring Security - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-27, specifically Module 18 (Security), Module 25 (Spring Boot) & Module 27 (REST API)  
**Estimated Reading Time**: 90 minutes  

---

## 📚 Table of Contents

1. [Introduction to Spring Security](#intro)
2. [Architecture & Filter Chain](#architecture)
3. [Authentication vs. Authorization](#auth)
4. [Configuring Security (Java Configuration)](#config)
5. [JWT (JSON Web Tokens) Integration](#jwt)
6. [Method Level Security](#method-security)

---

## 1. Introduction to Spring Security <a name="intro"></a>
Spring Security is a powerful and highly customizable authentication and access-control framework. It is the de-facto standard for securing Spring-based applications.

---

## 2. Architecture & Filter Chain <a name="architecture"></a>
Spring Security works primarily via a chain of Servlet Filters (`SecurityFilterChain`). Each request passes through these filters (e.g., `UsernamePasswordAuthenticationFilter`, `BasicAuthenticationFilter`) to determine if the user is authenticated and authorized before hitting the controllers.

---

## 3. Authentication vs. Authorization <a name="auth"></a>
- **Authentication**: Who are you? (Identity verification, e.g., verifying a username and password against a database).
- **Authorization**: What are you allowed to do? (Access control, e.g., checking if the user has the `ROLE_ADMIN`).

`UserDetails` and `UserDetailsService` are the core interfaces used to load user data during authentication.

---

## 4. Configuring Security (Java Configuration) <a name="config"></a>
Modern Spring Security uses `SecurityFilterChain` beans rather than extending `WebSecurityConfigurerAdapter`.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable()) // Disable CSRF for stateless REST APIs
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .httpBasic(basic -> {}); // Enable Basic Auth for simplicity (or use JWT)

        return http.build();
    }
}
```

---

## 5. JWT (JSON Web Tokens) Integration <a name="jwt"></a>
For stateless REST APIs, Session/Cookie-based auth is often replaced with JWT.
1. The client logs in, generating a JWT string.
2. The client sends the JWT in the `Authorization: Bearer <token>` header for subsequent requests.
3. A custom `OncePerRequestFilter` intercepts the request, validates the token signature, and populates the `SecurityContextHolder`.

---

## 6. Method Level Security <a name="method-security"></a>
You can secure individual service methods using annotations. Ensure `@EnableMethodSecurity` is applied on a configuration class.

```java
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;

@Service
public class DocumentService {

    @PreAuthorize("hasRole('ADMIN') or #owner == authentication.name")
    public void deleteDocument(String docId, String owner) {
        // Only an admin, or the owner of the document can delete it.
    }
}
```