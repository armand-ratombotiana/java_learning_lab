# Micronaut Framework Theory

## 1. Introduction to Micronaut

### What is Micronaut?

Micronaut is a modern, JVM-based framework designed for building modular, easily testable microservice and serverless applications. Unlike traditional frameworks that rely on runtime reflection and proxies, Micronaut uses ahead-of-time (AOT) compilation to generate beans and proxies at compile time.

### Key Characteristics

- **Compile-time DI**: No runtime reflection for dependency injection
- **AOT Compilation**: Generates native code during build
- **Reactive Runtime**: Built-in support for reactive programming
- **Cloud-native**: Designed for Kubernetes, AWS Lambda, Azure Functions
- **GraalVM Native Image**: Support for creating native executables

### Micronaut vs Spring Boot

| Feature | Micronaut | Spring Boot |
|---------|-----------|-------------|
| DI Approach | Compile-time | Runtime reflection |
| Startup Time | ~10-50ms | ~2-5 seconds |
| Memory Usage | Low (50-100MB) | Higher (200-500MB) |
| Native Image | First-class support | Supported |
| Reflection Usage | Minimal | Extensive |

## 2. Dependency Injection (DI)

### Compile-time DI

Micronaut processes annotations at compile time and generates the necessary bean definitions.

```java
@Singleton
public class UserService {
    private final UserRepository userRepository;
    
    @Inject
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
}
```

### Injection Types

```java
@Controller("/users")
public class UserController {
    
    // Constructor injection (recommended)
    private final UserService userService;
    
    @Inject
    public UserController(UserService userService) {
        this.userService = userService;
    }
    
    // Field injection
    @Inject
    private EmailService emailService;
    
    // Method injection
    @Inject
    void configure(FeatureToggleService toggle) {
        // configure on initialization
    }
}
```

### Qualifiers and Named Injection

```java
@Singleton
@Named("production")
public class ProductionDatabase implements Database { }

@Singleton
@Named("dev")
public class DevDatabase implements Database { }

@Singleton
public class DataService {
    @Inject
    @Named("production")
    private Database database;
}
```

### Scopes

```java
@Singleton       // Single instance per application
@Prototype        // New instance per injection
@Context          // Single instance per context
@ThreadLocal      // Per-thread scope
@Refreshable     // Scope that can be refreshed
@View            // View-scoped for UI frameworks
```

## 3. Ahead-of-Time (AOT) Compilation

### How AOT Works

1. **Annotation Processing**: Micronaut's compiler processes annotations during compilation
2. **Bean Definitions**: Generates Java classes defining beans and their dependencies
3. **Introspection**: Creates type metadata for reflection-free access
4. **Native Image Support**: Prepares metadata for GraalVM native compilation

### Processing Pipeline

```
Source Code → Annotation Processing → Generated Code → Compilation → Native Executable
```

### Configuration

```xml
<!-- pom.xml -->
<plugin>
    <groupId>io.micronaut.maven</groupId>
    <artifactId>micronaut-maven-plugin</artifactId>
    <configuration>
        <aot>
            <optimizeServiceLoading>true</optimizeServiceLoading>
            <convertYamlToProperties>true</convertYamlToProperties>
            <preloadEntities>true</preloadEntities>
        </aot>
    </configuration>
</plugin>
```

### AOT Optimizations

```yaml
# application.yml
micronaut:
  aot:
    optimize-service-loading: true
    convert-yaml-to-properties: true
    preload-entities: true
    optimize-class-initialization: true
    reduce-reflective-calls: true
```

## 4. GraalVM Native Image

### Overview

GraalVM Native Image converts Java applications into standalone native executables with faster startup and lower memory.

### Maven Configuration

```xml
<plugin>
    <groupId>io.micronaut.maven</groupId>
    <artifactId>micronaut-maven-plugin</artifactId>
    <configuration>
        <mainClass>com.example.Application</mainClass>
    </configuration>
    <executions>
        <execution>
            <id>build-native</id>
            <goals>
                <goal>native-maven-plugin</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

### Application Configuration

```java
@ContextClass  // For GraalVM - indicates runtime initialization
public class Application {
    public static void main(String[] args) {
        Micronaut.run(Application.class, args);
    }
}
```

### Native Image Resource Configuration

```java
// runtime package
public class RuntimePackage {
    static void initialize() {
        // Initialize runtime
    }
}
```

```json
// META-INF/native-image/resource-config.json
{
  "resources": [
    {"pattern": ".*\\.properties$"},
    {"pattern": "application.yml"}
  ]
}
```

## 5. Micronaut Data

### Overview

Micronaut Data is a database access framework that uses compile-time data access.

### Entity Definition

```java
@MappedEntity("users")
public class User {
    @Id
    @GeneratedValue(GeneratedType.AUTO)
    private Long id;
    
    @Column(name = "username")
    private String username;
    
    @DateCreated
    private LocalDateTime createdAt;
    
    // getters and setters
}
```

### Repository Interface

```java
@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    List<User> findByActiveTrue();
    
    @Query("SELECT u FROM User u WHERE u.email = :email")
    Optional<User> findByEmail(String email);
    
    void updateUsername(@Id Long id, String username);
    
    @Delete
    void deleteById(Long id);
}
```

### Pagination and Sorting

```java
public interface UserRepository extends CrudRepository<User, Long> {
    Page<User> findByActive(boolean active, Pageable pageable);
    
    List<User> findByActive(boolean active, Sort sort);
}

// Usage
Page<User> page = repository.findByActive(true, Page.from(0, 10));
```

### Transactions

```java
@Singleton
public class UserService {
    
    @Transactional
    public void createUser(User user) {
        repository.save(user);
    }
    
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWithNewTransaction(User user) {
        repository.update(user);
    }
}
```

## 6. HTTP Server and Client

### HTTP Server

```java
@Controller("/api/users")
public class UserController {
    
    @Get("/")
    public List<User> list() {
        return userService.findAll();
    }
    
    @Get("/{id}")
    public User get(@PathVariable Long id) {
        return userService.findById(id).orElseThrow();
    }
    
    @Post("/")
    @Status(HttpStatus.CREATED)
    public User create(@Body User user) {
        return userService.save(user);
    }
    
    @Put("/{id}")
    public User update(@PathVariable Long id, @Body User user) {
        user.setId(id);
        return userService.update(user);
    }
    
    @Delete("/{id}")
    @Status(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        userService.delete(id);
    }
}
```

### HTTP Client

```java
@Client("/api/users")
public interface UserClient {
    
    @Get("/")
    CompletableFuture<List<User>> list();
    
    @Get("/{id}")
    Mono<User> get(Long id);
    
    @Post("/")
    Mono<User> create(User user);
}
```

### Filter and Interceptors

```java
@Filter("/api/**")
public class AuthenticationFilter implements HttpServerFilter {
    
    @Override
    public Publisher<MutableHttpResponse<?>> filter(
            ServerRequest request, 
            FilterChain chain) {
        String token = request.getHeaders().get("Authorization");
        if (token == null) {
            return Publishers.just(
                HttpResponse.unauthorized()
            );
        }
        return chain.proceed(request);
    }
}
```

## 7. Configuration Management

### Application Configuration

```yaml
micronaut:
  application:
    name: myapp
  server:
    host: localhost
    port: 8080
  config-locations: classpath:application.yml,file:./config.yml
```

### Configuration Properties

```java
@ConfigurationProperties("app.external")
public class ExternalConfig {
    private String apiKey;
    private String endpoint;
    private int timeout;
    
    // getters and setters
}
```

### Property Placeholders

```yaml
app:
  api:
    url: ${API_BASE_URL:https://api.example.com}
    key: ${API_KEY}
```

### Environment-specific Configuration

```yaml
# application-prod.yml
micronaut:
  server:
    port: ${PORT}
```

## 8. Micronaut Security

### Basic Security

```yaml
micronaut:
  security:
    enabled: true
    authentication: bearer
    token:
      jwt:
        signatures:
          secret:
            generator:
              secret: '"${JWT_SECRET}"'
```

### Security Annotations

```java
@Controller("/admin")
@Secured(SecurityRule.IS_ANONYMOUS)
public class AdminController {
    
    @Get("/dashboard")
    @Secured({"ROLE_ADMIN", "ROLE_USER"})
    public String dashboard() {
        return "dashboard";
    }
    
    @Post("/users")
    @Secured(SecurityRule.IS_AUTHENTICATED)
    public User createUser(@Body User user) {
        return userService.create(user);
    }
}
```

## 9. Reactive Programming with Micronaut

### Micronaut Reactive Types

```java
@Controller("/reactive")
public class ReactiveController {
    
    @Get("/single")
    public Mono<String> single() {
        return Mono.just("Hello");
    }
    
    @Get("/multi")
    public Flux<String> multi() {
        return Flux.just("A", "B", "C");
    }
    
    @Get("/stream")
    public Publisher<String> stream() {
        return Flux.interval(Duration.ofSeconds(1))
            .map(i -> "Message " + i);
    }
}
```

### Async Execution

```java
@Singleton
public class AsyncService {
    
    @ExecutorService
    private ExecutorService executor;
    
    @Executable
    public CompletableFuture<String> async() {
        return CompletableFuture.supplyAsync(() -> {
            // long-running task
            return "Result";
        }, executor);
    }
}
```

## 10. Module Summary

Micronaut provides a modern approach to Java development with:

- **Compile-time DI**: Faster startup, less memory
- **AOT Compilation**: Optimized for cloud-native deployment
- **GraalVM Support**: Native executable generation
- **Micronaut Data**: Type-safe database access
- **Reactive Support**: Built-in reactive programming
- **Security**: Comprehensive security framework

These features make Micronaut ideal for:
- Microservices requiring fast startup
- Serverless functions (Lambda, Azure Functions)
- Containerized applications
- GraalVM native image deployments