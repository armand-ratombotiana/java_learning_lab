# Step by Step — Annotations

## Step 1: Define the Annotation
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface LogExecution {
    long maxTimeMs() default 1000;
}
```

## Step 2: Apply Annotation to Code
```java
class DataService {
    @LogExecution(maxTimeMs = 500)
    public List<String> fetchData() {
        // ...
    }
}
```

## Step 3: Write Runtime Processor
```java
public static void inspect(Object obj) {
    for (Method m : obj.getClass().getDeclaredMethods()) {
        LogExecution log = m.getAnnotation(LogExecution.class);
        if (log != null) {
            System.out.println("Method " + m.getName() + " max time: " + log.maxTimeMs());
        }
    }
}
```

## Step 4: Add Dynamic Proxy (AOP-style)
```java
public <T> T createProxy(T target) {
    return (T) Proxy.newProxyInstance(
        target.getClass().getClassLoader(),
        target.getClass().getInterfaces(),
        (proxy, method, args) -> {
            LogExecution log = method.getAnnotation(LogExecution.class);
            if (log != null) {
                long start = System.nanoTime();
                Object result = method.invoke(target, args);
                long elapsed = (System.nanoTime() - start) / 1_000_000;
                if (elapsed > log.maxTimeMs()) {
                    System.err.println("SLOW: " + method.getName() + " took " + elapsed + "ms");
                }
                return result;
            }
            return method.invoke(target, args);
        }
    );
}
```
