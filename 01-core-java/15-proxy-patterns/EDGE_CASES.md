# Edge Cases & Pitfalls: Proxy Patterns & Dynamic Proxies

Understanding how proxies work under the hood is critical because they frequently cause confusing bugs in enterprise applications (especially in Spring/Hibernate).

## 1. The "Self-Invocation" Problem
This is the most common pitfall in Spring AOP.
*   **The Scenario**: You have a class `UserService` with two methods: `public void methodA()` and `@Transactional public void methodB()`. Inside `methodA`, you call `this.methodB()`.
*   **The Pitfall**: The transaction will *not* start. Why? Because `this` refers to the *real* object, not the proxy. The caller invoked `methodA` on the proxy, the proxy delegated to the real object's `methodA`, and then the real object called its own `methodB` directly, completely bypassing the proxy and the `@Transactional` logic.
*   **Mitigation**:
    1.  Refactor `methodB` into a separate Spring bean and inject it.
    2.  Self-inject the proxy (inject `UserService` into `UserService` itself) and call the method on the injected instance (often considered a code smell).

## 2. Final Classes and Methods (CGLIB)
*   **The Scenario**: You are using CGLIB to proxy a concrete class, but you marked the class or one of its methods as `final`.
*   **The Pitfall**: CGLIB works by generating a subclass at runtime. If a class is `final`, it cannot be subclassed. If a method is `final`, it cannot be overridden. The proxy generation will fail at startup, or the final method will silently bypass interception.
*   **Mitigation**: Avoid using `final` on classes or methods that need to be proxied (e.g., Spring `@Configuration` classes, `@Transactional` methods). *Note: Kotlin classes are final by default, which is why the `kotlin-spring` compiler plugin exists to automatically open them.*

## 3. equals() and hashCode() Anomalies
*   **The Scenario**: You put a proxied object into a `HashSet` or compare it using `equals()`.
*   **The Pitfall**: The `InvocationHandler` intercepts *all* method calls, including `equals()` and `hashCode()`. If your handler doesn't explicitly account for these methods, it might delegate them to the target object, or worse, execute proxy logic that alters the hash code, breaking collections. Furthermore, `proxy.getClass() != target.getClass()`.
*   **Mitigation**: Most mature frameworks handle this gracefully, but if you are writing a custom `InvocationHandler`, you must explicitly check for `equals`, `hashCode`, and `toString` and handle them appropriately.

## 4. Type Casting Exceptions
*   **The Scenario**: You are using JDK Dynamic Proxies. You try to cast the proxy to the concrete implementation class instead of the interface.
    ```java
    // Fails! Proxy implements UserService, but is NOT a UserServiceImpl
    UserServiceImpl service = (UserServiceImpl) proxy; 
    ```
*   **The Pitfall**: Throws a `ClassCastException`. A JDK proxy implements the *interfaces* of the target, it does not extend the target class.
*   **Mitigation**: Always program to interfaces. Cast proxies to their interface types, not their concrete implementations.

## 5. Performance Overhead
*   **The Pitfall**: Reflection and proxy interception are not free. While the overhead is usually negligible for business logic (like database calls), wrapping tight, CPU-intensive loops inside a proxy can severely degrade performance.
*   **Mitigation**: Do not proxy highly granular, frequently called utility methods. Keep proxies at the service/facade boundary.