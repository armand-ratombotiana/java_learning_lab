# How 04-spring-security Works

## Architecture Overview

### Request Processing Flow

1. **Client sends request** - HTTP request arrives at the server
2. **Security filters intercept** - DelegatingFilterProxy routes through Spring Security
3. **Authentication determination** - Filter identifies authentication type
4. **Authentication process** - AuthenticationManager processes credentials
5. **Security context established** - SecurityContextHolder is populated
6. **Authorization check** - AccessDecisionManager verifies permissions
7. **Resource access** - Request proceeds to the controller if authorized
8. **Response returned** - Response sent back with security headers

## Java Implementation

### Core Configuration

```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/public/**").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults());
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
```

### Authentication Flow

Spring Security processes authentication through a chain of components:

1. **Filter** intercepts the request and extracts credentials
2. **AuthenticationManager** coordinates the authentication process
3. **ProviderManager** iterates through configured AuthenticationProviders
4. **AuthenticationProvider** validates credentials against a source
5. **UserDetailsService** loads user information
6. **SecurityContext** stores the authenticated principal

### Authorization Flow

After authentication, the FilterSecurityInterceptor handles authorization:

1. Retrieves the Authentication from SecurityContext
2. Determines the required permissions for the request
3. Consults AccessDecisionManager
4. Grants or denies access based on voter decisions
