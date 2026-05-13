# 52 - Micronaut Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Annotation Processing | Compile-time DI, no reflection |
| @Inject | Dependency injection |
| @Factory | Bean factory methods |
| @Executable | Compile-time method execution |
| Micronaut Data | Reactive database access |
| Gradle | Build tool with Kotlin support |

## Controllers

```java
// Basic controller
@Controller("/api/users")
public class UserController {
    
    @Get("/{id}")
    public Optional<User> getUser(@PathVariable Long id) {
        return userService.findById(id);
    }
    
    @Post
    @Status(HttpStatus.CREATED)
    public User create(@Body User user) {
        return userService.create(user);
    }
    
    @Get
    public List<User> list(
            @QueryValue(defaultValue = "10") int limit,
            @QueryValue Optional<String> filter) {
        return userService.findAll(limit, filter);
    }
}

// Reactive controller
@Controller("/api/users")
public class ReactiveUserController {
    
    @Get("/{id}")
    public Mono<User> getUser(@PathVariable Long id) {
        return userService.findByIdReactive(id);
    }
    
    @Get
    public Flux<User> list() {
        return userService.findAllReactive();
    }
}
```

## Dependency Injection

```java
// Inject by type
@Inject UserService userService;

// Inject with qualifier
@Inject @Named("production") DataSource dataSource;

// Factory method
@Factory
public class ServiceFactory {
    @Singleton
    public UserService buildService(
            @Client("${service.url}") String serviceUrl) {
        return new UserService(serviceUrl);
    }
}

// Bean replacement
@Replaces(UserService.class)
public class MockUserService extends UserService {}
```

## Configuration

```yaml
# application.yml
micronaut:
  application:
    name: myapp
  server:
    port: 8080
    host: 0.0.0.0
  config-locations: classpath:application.yml

datasources:
  default:
    url: jdbc:postgresql://localhost:5432/db
    driverClassName: org.postgresql.Driver
    username: admin
    password: secret
    dialect: POSTGRES

jackson:
  serialization:
    writeDatesAsTimestamps: false

endpoints:
  health:
    enabled: true
    sensitive: false
```

## Data Access

```java
// JDBC repository
@JdbcRepository(dialect = Dialect.POSTGRES)
public interface UserRepository 
        extends CrudRepository<User, Long> {
    
    @Query("SELECT * FROM users WHERE email = :email")
    Optional<User> findByEmail(String email);
    
    @Query("UPDATE users SET status = :status WHERE id = :id")
    void updateStatus(@Id Long id, String status);
}

// MongoDB repository
@MongoRepository
public interface UserRepository 
        extends ReactiveJavaBeanRepository<User, String> {
    Mono<User> findByEmail(String email);
}

// Data validation
@Introspected
public class User {
    @NotBlank
    private String name;
    
    @Email
    private String email;
    
    @Min(18)
    private int age;
}
```

## HTTP Client

```java
// Declarative client
@Client("/api/users")
public interface UserClient {
    
    @Get("/{id}")
    User getUser(@PathVariable Long id);
    
    @Post
    @Header("Authorization")
    User create(User user);
    
    @Get
    List<User> list(@QueryValue int limit);
}

// Reactive client
@Client("/api/users")
public interface ReactiveUserClient {
    
    @Get("/{id}")
    Mono<User> getUser(@PathVariable Long id);
    
    @Post
    Mono<User> create(User user);
}
```

## Testing

```java
// Unit test with mocking
@MicronautTest
class UserServiceTest {
    
    @MockBean(UserRepository.class)
    UserRepository userRepository() {
        return mock(UserRepository.class);
    }
    
    @Test
    void testFindById() {
        when(repository.findById(1L))
            .thenReturn(Optional.of(new User("John")));
        
        User result = service.findById(1L);
        assertEquals("John", result.getName());
    }
}
```

## Security

```java
// Authentication provider
@Singleton
public class AuthenticationProviderUserPassword 
        implements AuthenticationProviderUserPassword<UserDetails> {
    
    @Override
    public Optional<Authentication<UserDetails>> authenticate(
            @Nullable String username,
            @Nullable String password) {
        // Validate credentials
        if (valid(username, password)) {
            return Optional.of(
                Authentication.build(username, List.of("ROLE_USER"))
            );
        }
        return Optional.empty();
    }
}

// Secured endpoint
@Secured(SecurityRule.IS_AUTHENTICATED)
class SecureController {}
```

## Best Practices

Use compile-time DI for fast startup. Leverage reactive types for I/O operations. Use Micronaut Data for database productivity. Take advantage of property placeholders. Configure environment-specific settings with application-{env}.yml.