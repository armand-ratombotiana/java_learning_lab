# Refactoring 08-cors-headers

## Migration Patterns

### 1. XML Configuration to Java Config

**Before (XML-based):**
```xml
<http auto-config="true">
    <intercept-url pattern="/**" access="ROLE_USER"/>
    <form-login login-page="/login"/>
</http>
<authentication-manager>
    <authentication-provider>
        <user-service>
            <user name="user" password="pass" authorities="ROLE_USER"/>
        </user-service>
    </authentication-provider>
</authentication-manager>
```

**After (Java config):**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().hasRole("USER")
            )
            .formLogin(form -> form
                .loginPage("/login")
            );
        return http.build();
    }
}
```

### 2. WebSecurityConfigurerAdapter to Component-Based

**Before (extending Adapter - deprecated):**
```java
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests()
            .anyRequest().authenticated()
            .and()
            .formLogin();
    }
}
```

**After (component-based):**
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults());
        return http.build();
    }
}
```

### 3. In-Memory to Database User Storage

**Before (in-memory):**
```java
@Bean
public UserDetailsService userDetailsService() {
    return new InMemoryUserDetailsManager(
        User.withUsername("user").password("pass").roles("USER").build()
    );
}
```

**After (database):**
```java
@Service
public class DatabaseUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(this::toUserDetails)
            .orElseThrow(() -> new UsernameNotFoundException(username));
    }

    private UserDetails toUserDetails(UserEntity entity) {
        return User.withUsername(entity.getUsername())
            .password(entity.getPassword())
            .roles(entity.getRoles().toArray(String[]::new))
            .build();
    }
}
```

### 4. Custom Filter Integration

**Before (scattered logic):**
```java
@GetMapping("/api/data")
public String getData(HttpServletRequest request) {
    String apiKey = request.getHeader("X-API-Key");
    if (!apiKeyService.isValid(apiKey)) {
        throw new SecurityException("Invalid API key");
    }
    return dataService.getData();
}
```

**After (extracted filter):**
```java
@Component
public class ApiKeyFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response, FilterChain chain) {
        String apiKey = request.getHeader("X-API-Key");
        if (apiKeyService.isValid(apiKey)) {
            chain.doFilter(request, response);
        } else {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
        }
    }
}
```
