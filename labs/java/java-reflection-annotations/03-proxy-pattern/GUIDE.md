# GUIDE — Proxy Pattern

## Step 1: Define an Interface
```java
public interface Service {
    String execute(String input);
}
```

## Step 2: Create a Real Implementation
```java
public class RealService implements Service {
    public String execute(String input) {
        return "Processed: " + input;
    }
}
```

## Step 3: Write an InvocationHandler
```java
public class LoggingHandler implements InvocationHandler {
    private final Object target;
    public LoggingHandler(Object target) { this.target = target; }

    public Object invoke(Object proxy, Method m, Object[] args) throws Throwable {
        System.out.println("Before " + m.getName());
        Object result = m.invoke(target, args);
        System.out.println("After " + m.getName());
        return result;
    }
}
```

## Step 4: Create and Use the Proxy
```java
Service proxy = (Service) Proxy.newProxyInstance(
    RealService.class.getClassLoader(),
    new Class[]{Service.class},
    new LoggingHandler(new RealService())
);
proxy.execute("test");
```

## Step 5: Exercises
1. Add a caching proxy that returns cached results for repeated calls
2. Implement a transaction proxy that wraps methods in begin/commit/rollback
3. Compare performance of proxy vs direct calls
