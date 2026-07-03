# Deep Dive: Proxy Patterns & Dynamic Proxies

## 1. The Proxy Pattern
The Proxy Pattern is a structural design pattern that lets you provide a substitute or placeholder for another object. A proxy controls access to the original object, allowing you to perform something either before or after the request gets through to the original object.

### Common Use Cases:
*   **Lazy Initialization (Virtual Proxy)**: Delaying the creation of a heavy object until it is actually needed.
*   **Access Control (Protection Proxy)**: Checking if a client has permissions to execute a method.
*   **Local Execution (Remote Proxy)**: Representing a remote object locally (e.g., RMI, gRPC stubs).
*   **Logging/Caching**: Intercepting calls to cache results or log execution times.

## 2. Static Proxies
A static proxy is written manually. You implement the same interface as the target class, hold a reference to the target, and delegate calls to it.

```java
// The Interface
public interface Image { void display(); }

// The Real Object
public class RealImage implements Image {
    public RealImage(String filename) { loadFromDisk(filename); }
    public void display() { System.out.println("Displaying"); }
}

// The Proxy
public class ProxyImage implements Image {
    private RealImage realImage;
    private String filename;

    public ProxyImage(String filename) { this.filename = filename; }

    public void display() {
        if (realImage == null) { realImage = new RealImage(filename); }
        realImage.display();
    }
}
```
*Problem*: If you have 50 interfaces, you have to write 50 proxy classes manually. This violates the DRY (Don't Repeat Yourself) principle.

## 3. JDK Dynamic Proxies
Java provides a built-in mechanism to create proxies dynamically at runtime using `java.lang.reflect.Proxy`. 
*   **Constraint**: JDK Dynamic Proxies can *only* proxy interfaces, not concrete classes.

### How it works:
1.  Implement an `InvocationHandler`. This class contains the logic that will run when *any* method on the proxy is called.
2.  Call `Proxy.newProxyInstance()`, passing the classloader, the interfaces to implement, and your `InvocationHandler`.

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

public class TimingInvocationHandler implements InvocationHandler {
    private final Object target;

    public TimingInvocationHandler(Object target) { this.target = target; }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        long start = System.nanoTime();
        Object result = method.invoke(target, args); // Delegate to real object
        long end = System.nanoTime();
        System.out.println(method.getName() + " took " + (end - start) + " ns");
        return result;
    }
}

// Usage:
UserService realService = new UserServiceImpl();
UserService proxyService = (UserService) Proxy.newProxyInstance(
    UserService.class.getClassLoader(),
    new Class<?>[]{UserService.class},
    new TimingInvocationHandler(realService)
);
proxyService.getUser(1); // Call is intercepted!
```

## 4. CGLIB (Code Generation Library)
Because JDK proxies require interfaces, Spring and Hibernate often use CGLIB. CGLIB generates a subclass of your target class at runtime and overrides its methods to intercept calls.
*   **Advantage**: Can proxy concrete classes (no interfaces required).
*   **Disadvantage**: Cannot proxy `final` classes or `final` methods (because they cannot be overridden).

*Note: Spring Boot uses CGLIB proxies by default since version 1.4.*

## 5. Proxies in Enterprise Frameworks (AOP)
Aspect-Oriented Programming (AOP) in Spring is entirely built on top of dynamic proxies. When you annotate a method with `@Transactional`, `@Cacheable`, or `@Async`, Spring doesn't modify your bytecode. Instead, it creates a proxy around your bean. When another bean calls your method, it actually calls the proxy, which starts the transaction, delegates to your real method, and then commits the transaction.