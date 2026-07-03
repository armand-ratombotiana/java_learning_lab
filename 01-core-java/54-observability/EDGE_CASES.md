# Module 54: Observability & Distributed Tracing - Edge Cases & Pitfalls

---

## Pitfall 1: Broken Trace Context Propagation

### ❌ Wrong
Creating a new thread (e.g., using `CompletableFuture.supplyAsync()` or `@Async`) in a Spring Boot application and logging a message inside that thread. By default, the `Trace ID` and `Span ID` are stored in a `ThreadLocal`. When you jump to a new thread, the context is lost, and the logs in the new thread become "orphaned" from the main trace.

### ✅ Correct
Use framework-provided wrappers that automatically copy the MDC (Mapped Diagnostic Context) from the main thread to the worker thread, such as Spring's `DelegatingSecurityContextExecutor` or Micrometer's Context Propagation API (`ContextSnapshot.capture()`).

---

## Pitfall 2: High Cardinality Metrics

### ❌ Wrong
Tagging a Micrometer metric with a highly variable user input.
```java
// ❌ WRONG: UserId has millions of unique values
registry.counter("http.requests", "userId", request.getUserId()).increment();
```

### ✅ Correct
High cardinality tags will explode the memory of your time-series database (like Prometheus), crashing the monitoring system. Only tag metrics with bounded, low-cardinality data (like HTTP Status Code, Region, or Endpoint Name).
```java
// ✅ CORRECT: Status code only has a few possible values
registry.counter("http.requests", "status", "200").increment();
```

---

## Pitfall 3: Logging PII or Secrets

### ❌ Wrong
Using `@RestControllerAdvice` to log the raw HTTP Request Body whenever a `400 Bad Request` occurs, accidentally logging users' plain-text passwords or credit card numbers into Elasticsearch.

### ✅ Correct
Implement strict log masking/scrubbing rules in your logging framework (e.g., Logback regex replace) to obfuscate sensitive fields before they ever leave the JVM.