# Micronaut Framework Flashcards

## Card 1: What is Micronaut?
A JVM-based framework for building microservice and serverless applications using compile-time DI instead of runtime reflection.

## Card 2: Compile-time DI
Micronaut processes annotations at compile time, generating bean definitions. Results in faster startup (~10-50ms) and lower memory (~50-100MB).

## Card 3: @Singleton vs @Prototype
- @Singleton: Single instance per application
- @Prototype: New instance per injection point

## Card 4: AOT Compilation
Ahead-of-Time compilation generates native code during build, optimizing:
- Service loading
- YAML to properties conversion
- Class initialization
- Reflective call reduction

## Card 5: GraalVM Native Image
Converts Java applications into standalone native executables with:
- Instant startup
- Lower memory footprint
- No JVM required

## Card 6: Micronaut Data
Database access framework using compile-time query generation. Supports:
- @MappedEntity for domain objects
- Repository interfaces extending CrudRepository
- Type-safe query methods

## Card 7: @Controller
```java
@Controller("/api/users")
public class UserController {
    @Get("/{id}")
    public User get(@PathVariable Long id) {...}
}
```

## Card 8: @Client
```java
@Client("/api/users")
public interface UserClient {
    @Get("/{id}")
    Mono<User> get(Long id);
}
```

## Card 9: @ConfigurationProperties
Binds external configuration to typed Java objects:
```java
@ConfigurationProperties("app.external")
public class ExternalConfig {
    private String apiKey;
}
```

## Card 10: @Filter
Server-side filter for HTTP requests:
```java
@Filter("/api/**")
public class AuthFilter implements HttpServerFilter {...}
```

## Card 11: Reactive Types
- **Mono**: Single value (0 or 1 element)
- **Flux**: Multiple values (0 to N elements)
- Use for non-blocking async operations

## Card 12: Micronaut vs Spring Boot
| Aspect | Micronaut | Spring Boot |
|--------|-----------|-------------|
| DI | Compile-time | Runtime |
| Startup | ~50ms | ~2-5s |
| Memory | 50-100MB | 200-500MB |
| Reflection | Minimal | Extensive |