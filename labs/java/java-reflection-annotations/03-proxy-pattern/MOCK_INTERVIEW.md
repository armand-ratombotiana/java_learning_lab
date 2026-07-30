# MOCK_INTERVIEW — Proxy Pattern

## Scenario
Implement a retry proxy: if a method throws an exception, retry up to `n` times.

## Interviewer Notes
- Candidate should use `Proxy.newProxyInstance()`
- Discuss backoff strategy (fixed, exponential)
- Edge cases: idempotency, checked vs unchecked exceptions

## Expected Solution Sketch
```java
public class RetryProxy {
    public static <T> T wrap(T target, int maxRetries) {
        return (T) Proxy.newProxyInstance(
            target.getClass().getClassLoader(),
            target.getClass().getInterfaces(),
            (proxy, method, args) -> {
                for (int i = 0; i < maxRetries; i++) {
                    try { return method.invoke(target, args); }
                    catch (Exception e) {
                        if (i == maxRetries - 1) throw e;
                        Thread.sleep(100L * (i + 1));
                    }
                }
                return null;
            });
    }
}
```

## Follow‑Up
- How to support `@Retryable(maxRetries, backoff)` annotation?
- What about `void` methods?
