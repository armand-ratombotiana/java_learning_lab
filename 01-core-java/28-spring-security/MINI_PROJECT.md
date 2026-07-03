# Module 28: Spring Security - Mini Project

**Project Name**: Stateless JWT Auth Service  
**Difficulty Level**: Advanced  
**Estimated Time**: 3-4 hours

---

## 🎯 Objective
Implement a robust, stateless Spring Security configuration using JSON Web Tokens (JWT) for authentication and Role-Based Access Control (RBAC) via method-level security annotations.

## 📝 Requirements

### Core Features

1. **Dependencies**:
   - `spring-boot-starter-security`
   - `spring-boot-starter-web`
   - A JWT library (e.g., `jjwt-api`, `jjwt-impl`, `jjwt-jackson` by okta).

2. **JWT Utility Class**:
   - Create a `JwtUtils` class.
   - Implement `String generateToken(String username, String role)` using a strong, secret signing key. Set the token expiration to 1 hour.
   - Implement `boolean validateToken(String token)` to verify the signature and expiration.
   - Implement `String extractUsername(String token)` and `String extractRole(String token)`.

3. **Authentication Filter**:
   - Create a `JwtAuthenticationFilter` extending `OncePerRequestFilter`.
   - Override `doFilterInternal`.
   - Extract the token from the `Authorization: Bearer <token>` header.
   - If the token is valid, extract the username and role, construct a `UsernamePasswordAuthenticationToken` (populating authorities with `new SimpleGrantedAuthority(role)`), and set it in the `SecurityContextHolder`.

4. **Security Configuration**:
   - Create a `SecurityConfig` class annotated with `@Configuration` and `@EnableWebSecurity`.
   - Define a `SecurityFilterChain` bean.
   - Disable CSRF (`csrf.disable()`) and set Session Management to `STATELESS`.
   - Configure authorization rules:
     - `/api/auth/login` -> `permitAll()`
     - `/api/admin/**` -> `hasRole("ADMIN")`
     - Any other request -> `authenticated()`
   - Register the `JwtAuthenticationFilter` before the `UsernamePasswordAuthenticationFilter`.
   - Provide a `PasswordEncoder` bean (`BCryptPasswordEncoder`).

5. **Controllers**:
   - Create an `AuthController` with a `POST /login` endpoint. Accept a DTO with username/password. Hardcode a dummy user check (e.g., username "admin", password "password123"). If correct, call `JwtUtils.generateToken("admin", "ROLE_ADMIN")` and return it.
   - Create a `DataController` with endpoints to test access:
     - `GET /api/data/public` (Allow all).
     - `GET /api/data/private` (Requires authentication).
     - `GET /api/admin/dashboard` (Requires ADMIN role).

---

## 💡 Solution Blueprint (Partial Configuration)

```java
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/login", "/api/data/public").permitAll()
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```