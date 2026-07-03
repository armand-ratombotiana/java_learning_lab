# Mini Project: Building a Custom @Retry Annotation using Dynamic Proxies

## Objective
Build a lightweight AOP framework using JDK Dynamic Proxies. You will create a custom `@Retry` annotation and a proxy factory that intercepts method calls. If a method annotated with `@Retry` throws an exception, the proxy will automatically retry the execution up to 3 times before finally throwing the exception.

## Prerequisites
*   Java 17+

## Step 1: Create the Annotation
Create a runtime annotation that we can apply to methods.

```java
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Retry {
    int maxAttempts() default 3;
}
```

## Step 2: Define the Business Logic (Interface and Implementation)
JDK Dynamic Proxies require an interface.

```java
// 1. The Interface
public interface NetworkClient {
    String fetchData(String url) throws Exception;
}

// 2. The Implementation (simulates a flaky network)
public class FlakyNetworkClient implements NetworkClient {
    private int attemptCounter = 0;

    @Override
    @Retry(maxAttempts = 3)
    public String fetchData(String url) throws Exception {
        attemptCounter++;
        System.out.println("--- Attempt " + attemptCounter + " to fetch " + url);
        
        if (attemptCounter < 3) {
            throw new RuntimeException("Network timeout!");
        }
        
        return "Success: Data from " + url;
    }
}
```

## Step 3: Create the InvocationHandler
This is where the magic happens. We intercept the call, check for the annotation, and implement the retry loop.

```java
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class RetryInvocationHandler implements InvocationHandler {
    private final Object target;

    public RetryInvocationHandler(Object target) {
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        // Since we are proxying the interface, we need to find the annotation on the actual implementation class
        Method realMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());
        
        if (realMethod.isAnnotationPresent(Retry.class)) {
            Retry retryAnnotation = realMethod.getAnnotation(Retry.class);
            int maxAttempts = retryAnnotation.maxAttempts();
            int attempts = 0;
            
            while (attempts < maxAttempts) {
                try {
                    attempts++;
                    // Delegate to the real object
                    return method.invoke(target, args);
                } catch (Exception e) {
                    System.out.println("[PROXY] Caught exception on attempt " + attempts + ". " + 
                                       (attempts < maxAttempts ? "Retrying..." : "Failing."));
                    if (attempts >= maxAttempts) {
                        throw e.getCause(); // Unwrap InvocationTargetException
                    }
                }
            }
        }
        
        // If no annotation, just execute normally
        return method.invoke(target, args);
    }
}
```

## Step 4: Create the Proxy Factory and Test
Create a utility to wrap objects in our proxy and run the test.

```java
import java.lang.reflect.Proxy;

public class Main {
    
    @SuppressWarnings("unchecked")
    public static <T> T createRetryProxy(T target, Class<T> interfaceType) {
        return (T) Proxy.newProxyInstance(
            interfaceType.getClassLoader(),
            new Class<?>[]{interfaceType},
            new RetryInvocationHandler(target)
        );
    }

    public static void main(String[] args) {
        // 1. Create the real object
        NetworkClient realClient = new FlakyNetworkClient();
        
        // 2. Wrap it in our proxy
        NetworkClient proxyClient = createRetryProxy(realClient, NetworkClient.class);
        
        // 3. Execute!
        try {
            System.out.println("Starting fetch...");
            String result = proxyClient.fetchData("http://example.com");
            System.out.println("Final Result: " + result);
        } catch (Exception e) {
            System.out.println("Operation failed completely: " + e.getMessage());
        }
    }
}
```

## Expected Output
```text
Starting fetch...
--- Attempt 1 to fetch http://example.com
[PROXY] Caught exception on attempt 1. Retrying...
--- Attempt 2 to fetch http://example.com
[PROXY] Caught exception on attempt 2. Retrying...
--- Attempt 3 to fetch http://example.com
Final Result: Success: Data from http://example.com
```