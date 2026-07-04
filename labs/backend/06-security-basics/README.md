# Security Basics

Authentication and authorization with Spring Security.

## Topics
- Spring Security architecture (filter chain)
- Authentication providers
- Security configuration with @EnableWebSecurity
- JWT-based authentication
- OAuth2 and OIDC integration
- Method-level security (@PreAuthorize, @Secured)
- CSRF protection
- CORS configuration

## Example
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(auth -> auth
            .requestMatchers("/api/admin/**").hasRole("ADMIN")
            .requestMatchers("/api/**").authenticated()
            .anyRequest().permitAll()
        ).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        return http.build();
    }
}
```
