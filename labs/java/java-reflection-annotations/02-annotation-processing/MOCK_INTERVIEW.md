# MOCK_INTERVIEW — Annotation Processing

## Scenario
Design a caching annotation `@Cacheable` that stores method return values in a map.

## Interviewer Notes
- Candidate should define `@Cacheable(ttl)` with runtime retention
- Use `Proxy` or `MethodInterceptor` to intercept calls
- Discuss thread‑safe cache implementation

## Expected Solution Sketch
```java
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@interface Cacheable { long ttl() default 60000; }

public class CacheProxy {
    public static <T> T wrap(T target) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            (proxy, method, args) -> {
                if (!method.isAnnotationPresent(Cacheable.class)) {
                    return method.invoke(target, args);
                }
                // check cache, invoke if miss, store result
                return method.invoke(target, args);
            });
    }
}
```

## Follow‑Up
- How to handle TTL expiration?
- What about null return values?
