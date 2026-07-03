# Security — Virtual Threads

## Thread Context Propagation
`AccessController.getContext()` works transparently with virtual threads. The security context associated with the creator thread is inherited.

## StructuredTaskScope and Error Handling
Ensure error handling in `StructuredTaskScope` does not leak sensitive data through exceptions:
```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    scope.fork(() -> processSecretData());
    scope.join();
    scope.throwIfFailed(); // May expose secret data in stack trace
}
```

## Resource Exhaustion
Creating unlimited virtual threads can still exhaust OS resources (file descriptors, open sockets). Always bound external resources.

## Pinning and DoS
If all carrier threads become pinned (e.g., many `synchronized I/O` operations), virtual threads stall — potential denial of service.

## ThreadLocal Security
Sensitive data in `ThreadLocal` may be accessible across different virtual threads if the carrier thread is reused. Use `ScopedValue` (JEP 446) for safer scoped propagation.
