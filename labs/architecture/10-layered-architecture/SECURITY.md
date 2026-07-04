# Layered Architecture Security

## Security by Layer

### Presentation Layer
```java
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) {
        return http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/users/**").authenticated()
                .anyRequest().permitAll()
            )
            .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt)
            .build();
    }
}
```

### Business Layer
```java
@Service
public class UserService {

    @PreAuthorize("hasRole('ADMIN')")
    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    @PostAuthorize("returnObject.email == authentication.name")
    public UserResponse getUser(Long id) {
        return UserResponse.from(userRepository.findById(id).orElseThrow());
    }
}
```

### Persistence Layer (Data Filtering)
```java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("SELECT u FROM User u WHERE u.tenantId = ?#{principal.tenantId}")
    List<User> findAllForCurrentTenant();
}
```

## Security Principles
- Input validation in controller
- Authorization in service layer
- Data filtering in repository layer
- Cross-cutting security via AOP
- Encrypt sensitive data in entities
- Audit logging at all layers
