# Micronaut Framework Interview Questions

## 1. How does Micronaut's DI differ from Spring?

Micronaut uses compile-time dependency injection while Spring uses runtime reflection. Micronaut processes annotations during compilation and generates the bean definitions, eliminating the need for runtime reflection. This results in faster startup times and lower memory consumption.

## 2. What are the advantages of AOT compilation in Micronaut?

Ahead-of-time compilation provides:
- Faster application startup (10-50ms vs seconds)
- Lower memory footprint (50-100MB vs 200-500MB)
- Native image support via GraalVM
- Optimized service loading
- Reduced reflective calls

## 3. Explain Micronaut Data and how it differs from JPA/Hibernate.

Micronaut Data generates query implementations at compile time rather than using runtime reflection like JPA/Hibernate. This provides:
- Type-safe query derivation
- No runtime bytecode generation
- Better performance for simple queries
- Native query support

## 4. How do you create a native image with Micronaut?

```bash
./mvnw package -Dpackaging=native-image
```

Or using GraalVM directly:
```bash
native-image --jar target/app.jar -p $GRAALVM_HOME/lib -H:Class=Application
```

## 5. What scopes are available in Micronaut?

- `@Singleton` - Single instance per application
- `@Prototype` - New instance per injection
- `@Context` - Single instance per context
- `@ThreadLocal` - Per-thread scope
- `@Refreshable` - Can be refreshed at runtime

## 6. How do you implement security in Micronaut?

1. Add dependency: `micronaut-security`
2. Configure in `application.yml`:
```yaml
micronaut:
  security:
    enabled: true
    token:
      jwt:
        signatures:
          secret:
            generator:
              secret: "secret"
```
3. Use annotations: `@Secured`, `@Requires`

## 7. Describe the difference between Mono and Flux.

- **Mono**: Represents a single value or empty result (0 or 1 element)
- **Flux**: Represents zero to many elements (0 to N elements)

## 8. How do you create an HTTP client in Micronaut?

```java
@Client("/api")
public interface ProductClient {
    @Get("/products")
    Flux<Product> getProducts();
    
    @Get("/products/{id}")
    Mono<Product> getProduct(@PathVariable Long id);
}
```

## 9. What is the purpose of @ConfigurationProperties?

It binds external configuration (YAML, properties files, environment variables) to typed Java beans:

```java
@ConfigurationProperties("app.service")
public class ServiceConfig {
    private String endpoint;
    private int timeout;
    // getters/setters
}
```

## 10. How does Micronaut handle transactions?

```java
@Transactional
public void saveData() {
    repository.save(entity);
}
```

Available propagation levels: REQUIRED, REQUIRES_NEW, SUPPORTS, NOT_SUPPORTED, MANDATORY, NEVER.

## 11. Explain the @Filter annotation usage.

Used for HTTP request/response filtering:

```java
@Filter("/api/**")
public class LoggingFilter implements HttpServerFilter {
    @Override
    public Publisher<MutableHttpResponse<?>> filter(
            ServerRequest request, 
            FilterChain chain) {
        // pre-processing
        return chain.proceed(request);
    }
}
```

## 12. What are the key Micronaut annotations for HTTP methods?

- `@Controller` - Marks a class as HTTP controller
- `@Get`, `@Post`, `@Put`, `@Delete`, `@Patch` - HTTP method mappings
- `@PathVariable` - Path parameter binding
- `@Body` - Request body binding
- `@QueryValue` - Query parameter binding

## 13. How do you implement retry logic in Micronaut?

```java
@Retryable(attempts = "3", delay = "500ms")
public Result process() {
    // business logic
}
```

## 14. What is the GraalVM annotation used for runtime initialization?

`@ContextClass` marks the main application class for native image runtime initialization.

## 15. How do you test Micronaut applications?

```java
@MicronautTest
class MyTest {
    @Inject
    ApplicationUnderTest application;
    
    @Test
    void testSomething() {
        // test logic
    }
}
```

For HTTP client testing:
```java
@MockServer(Port = 8888)
interface MyClient {...}
```