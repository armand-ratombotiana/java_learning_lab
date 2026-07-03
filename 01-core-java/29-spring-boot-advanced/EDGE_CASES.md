# Module 29: Spring Boot Advanced - Edge Cases & Pitfalls

---

## Pitfall 1: Exposing Sensitive Actuator Endpoints

### ❌ Wrong
Exposing all actuator endpoints (e.g., `/actuator/env`, `/actuator/heapdump`) publicly without any security. This leaks sensitive environment variables, passwords, and server internals to attackers.
```properties
management.endpoints.web.exposure.include=*
# No security configured!
```

### ✅ Correct
Only expose safe endpoints (like `/health` or `/info`) publicly. Secure sensitive endpoints using Spring Security so that only admins can access them.
```properties
management.endpoints.web.exposure.include=health,info
```

---

## Pitfall 2: Overusing @SpringBootTest

### ❌ Wrong
Using `@SpringBootTest` for every single test. This loads the entire application context, making the test suite incredibly slow and brittle.

### ✅ Correct
Use test slices like `@WebMvcTest`, `@DataJpaTest`, or plain Mockito unit tests without loading the Spring context whenever possible.

---

## Pitfall 3: Ineffective Caching due to Internal Method Calls

### ❌ Wrong
Calling a `@Cacheable` method from within the same class. Spring's cache abstraction relies on AOP proxies. Internal method calls bypass the proxy, meaning the caching annotations are ignored.
```java
public Product getAndProcessProduct(Long id) {
    // This internal call bypasses the proxy; caching won't work!
    Product p = getProduct(id); 
    return process(p);
}

@Cacheable("products")
public Product getProduct(Long id) { ... }
```

### ✅ Correct
Move the `@Cacheable` method to a separate Spring Bean or inject the class into itself (self-injection) to ensure calls go through the proxy.