# Architecture — Exceptions

## Exception Handling Strategy

Define a consistent strategy for exception handling across layers:
- **Controller layer**: Catch exceptions, translate to HTTP responses
- **Service layer**: Business exceptions, validation errors
- **Repository layer**: Data access exceptions, optimistic locking

## Error Boundaries

Define error boundaries where exceptions are caught and handled. Let exceptions propagate within a layer; catch at layer boundaries.

## Exception Translation

Translate low-level exceptions to domain-meaningful ones:
```java
catch (SQLException e) {
    throw new DataAccessException("Failed to save order", e);
}
```

## Global Exception Handler

One centralized place to handle uncaught exceptions:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(InvalidOrderException.class)
    public ResponseEntity<String> handleInvalidOrder(InvalidOrderException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }
}
```

## Result Type Pattern

Instead of exceptions for expected failures, return a result type:
```java
sealed interface Result<T> { }
record Success<T>(T value) implements Result { }
record Failure<T>(String error) implements Result { }
```

## Resilience Patterns

- **Circuit Breaker**: Detect failures and prevent cascading
- **Retry**: Retry transient failures with backoff
- **Timeout**: Fail fast instead of waiting indefinitely
- **Bulkhead**: Isolate failures to prevent systemic impact
