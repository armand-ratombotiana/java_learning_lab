# Security — Concurrency

## Race Conditions in Security
```java
if (user.hasRole("ADMIN")) {
    securityContext.grantAccess(); // Race: role may change between check and action
}
```
**Fix:** Perform check and action atomically.

## Thread Safety of Security Contexts
Security contexts (e.g., `Subject`, `AccessControlContext`) must be propagated correctly across threads:
```java
var ctx = AccessController.getContext();
executor.submit(() -> AccessController.doPrivileged(ctx, task));
```

## CompletableFuture and Cascading
```java
CompletableFuture.supplyAsync(() -> fetchCredentials())
    .thenApply(this::validateCredentials)
    .exceptionally(ex -> { /* handle auth failure */ });
```

## Denial of Service (Thread Starvation)
Too many threads can exhaust resources. Limit thread pools:
```java
Executors.newFixedThreadPool(10); // Bounded
```

## Thread Interruption as Cancel
Interruption can cancel security-critical operations — always check `Thread.interrupted()` in long-running security validation code.
