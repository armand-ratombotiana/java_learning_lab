# Module 29: Spring Boot Advanced - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: How does Spring Boot's Auto-Configuration actually work?
**Answer**:
When an application is annotated with `@SpringBootApplication`, it enables `@EnableAutoConfiguration`. 
Spring Boot scans the classpath and looks for files named `META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports` (in older versions, `spring.factories`) inside all included jar files. These files contain lists of fully qualified configuration class names.
Spring loads these classes but evaluates them conditionally using annotations like `@ConditionalOnClass`, `@ConditionalOnMissingBean`, and `@ConditionalOnProperty`. If the conditions are met (e.g., a specific database driver is found on the classpath), Spring automatically instantiates the beans defined in that configuration.

### Q2: What is the purpose of Spring Boot Actuator? Name a risk associated with it.
**Answer**:
Spring Boot Actuator provides production-ready features to monitor and manage applications. It exposes HTTP endpoints (or JMX beans) for auditing, health metrics, thread dumps, heap dumps, and environment variables (e.g., `/actuator/health`, `/actuator/env`).
**The Risk**: Actuator endpoints often expose highly sensitive internal application states. Exposing endpoints like `/env` or `/heapdump` to the public internet can lead to severe data breaches (leaking database passwords or AWS keys). All sensitive actuator endpoints must be strictly secured behind an authentication filter (Spring Security) or blocked from external access entirely.

### Q3: What is the difference between `@SpringBootTest` and Test Slices (e.g., `@WebMvcTest`, `@DataJpaTest`)?
**Answer**:
- `@SpringBootTest` loads the **entire** Spring Application Context. It starts the embedded web server, database connections, all services, and repositories. It is used for true End-to-End or broad Integration testing. It is relatively slow.
- **Test Slices** (`@WebMvcTest`, `@DataJpaTest`) load only a *slice* of the application context relevant to the specific layer being tested. For example, `@WebMvcTest` only instantiates Controllers, ControllerAdvices, and web filters. It leaves the Service and Repository layers uninstantiated, forcing you to use `@MockBean`. This makes the tests run significantly faster and isolates the testing scope.

---

## 💻 Whiteboarding Scenarios

### Scenario 1: AOP and Caching Failure
**Problem**: You have a `ProductService` class with a method `getProduct(Long id)` that hits a slow external API. You annotated it with `@Cacheable`. Another method in the same class, `refreshProduct(Long id)`, calls `getProduct(Long id)` internally. During testing, you notice that when `refreshProduct` calls `getProduct`, the cache is completely ignored, and the slow API is hit every time. Why?

**Solution**:
Spring's caching (and `@Transactional`) relies on Aspect-Oriented Programming (AOP) proxies. When a bean is created, Spring wraps it in a proxy. When an *external* class calls the method on the proxy, the proxy intercepts the call, checks the cache, and then either returns the cached value or passes the call to the real object.
However, when a method makes an *internal* call to another method within the exact same class (`this.getProduct()`), it bypasses the proxy entirely and goes straight to the real method, completely ignoring the `@Cacheable` annotation.
**Fix**: To solve this, you must either move the cached method to a separate `@Service` class, or self-inject the service (`@Autowired ProductService self`) and call `self.getProduct()`.

### Scenario 2: Conditional Bean Creation
**Problem**: Write a configuration class that defines a bean of type `NotificationService`. It should only be instantiated if the property `app.notifications.enabled` is set to `true`, and it should only be instantiated if the user hasn't already defined their own `NotificationService` bean somewhere else.

**Solution**:
```java
@Configuration
public class NotificationAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "app.notifications.enabled", havingValue = "true")
    @ConditionalOnMissingBean(NotificationService.class)
    public NotificationService notificationService() {
        return new DefaultNotificationService();
    }
}
```