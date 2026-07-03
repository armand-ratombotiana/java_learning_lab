# Security Considerations for Java 21 Features

## Virtual Threads Security

### Thread Safety in Virtual Threads
Virtual threads share the same memory visibility guarantees as platform threads. The Java Memory Model applies equally. However, because virtual threads encourage writing synchronous code at scale, developers may inadvertently introduce shared mutable state:

```java
// UNSAFE: Shared mutable state in virtual threads
private int requestCounter = 0;

public void handleRequest() {
    requestCounter++;  // Race condition! Not atomic
}

// SAFE: Use atomic primitives or synchronization
private final AtomicInteger requestCounter = new AtomicInteger(0);

public void handleRequest() {
    requestCounter.incrementAndGet();  // Thread-safe
}
```

### Denial of Service via Virtual Thread Exhaustion
While virtual threads are lightweight, they still consume heap memory (~200 bytes each). An attacker who can trigger a large number of concurrent requests could exhaust heap memory:

```java
// Mitigation: Limit concurrent virtual threads with a semaphore
private final Semaphore concurrencyLimit = new Semaphore(10_000);

public void handleRequest() {
    if (!concurrencyLimit.tryAcquire(5, TimeUnit.SECONDS)) {
        throw new TooManyRequestsException();
    }
    try {
        // Process with virtual thread
    } finally {
        concurrencyLimit.release();
    }
}
```

### SecurityManager Interaction
Virtual threads inherit the security context of their parent. This is important when using `AccessController.doPrivileged` or `SecurityManager` (deprecated but still used in some environments).

## Pattern Matching Security

### Exhaustiveness and Auditing
Exhaustive pattern matching on sealed types provides compile-time guarantees that all cases are handled. This is a security feature: it prevents cases where a new subtype is added but the switch statement isn't updated, which could lead to unexpected behavior (e.g., returning sensitive data through a default case that should never be reached).

```java
// Audit requirement: All document types must be explicitly handled
sealed interface Document permits PublicDoc, ConfidentialDoc, SecretDoc {}

String process(Document doc) {
    return switch (doc) {
        case PublicDoc d -> d.content();  // Safe to return
        case ConfidentialDoc d -> d.redacted();  // Redact sensitive parts
        case SecretDoc d -> "ACCESS DENIED";  // Never expose
        // No default: compiler ensures all types are covered
    };
}
```

## String Templates Security

### Injection Prevention (Primary Security Feature)
String templates provide built-in protection against injection attacks when using appropriate processors:

```java
// INSECURE: SQL injection via concatenation
String query = "SELECT * FROM users WHERE name = '" + userName + "'";

// SECURE: Template processor builds parameterized query
var query = SQL."SELECT * FROM users WHERE name = \{userName}";
// Generates: "SELECT * FROM users WHERE name = ?" with userName as parameter
```

### Cross-Site Scripting (XSS) Prevention
```java
// Custom HTML template processor that escapes output
StringTemplate.Processor<String, RuntimeException> HTML =
    StringTemplate.Processor.of(st -> {
        StringBuilder sb = new StringBuilder();
        List<String> fragments = st.fragments();
        List<Object> values = st.values();
        for (int i = 0; i < fragments.size(); i++) {
            sb.append(fragments.get(i));
            if (i < values.size()) {
                sb.append(escapeHtml(String.valueOf(values.get(i))));
            }
        }
        return sb.toString();
    });

// Safe: User input is automatically escaped
String safeHtml = HTML."<div>\{userInput}</div>";
```

### Template Injection
If attackers can control template expressions, they could inject arbitrary expressions. Always ensure template expressions come from trusted sources:

```java
// DANGEROUS: User controls template content
String userControl = "Hello \{1+1}";
String result = STR."\{userControl}";  // Not a template expression!

// SAFE: User controls values, not template structure
String userName = getUserInput();
String result = STR."Hello \{userName}";  // User only provides the value
```

## Structured Concurrency Security

### Task Isolation
Structured concurrency enforces task boundaries. If one subtask is compromised, it cannot affect others through shared scope-specific resources:

```java
try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
    Future<String> untrusted = scope.fork(() -> untrustedService.call());
    Future<String> trusted = scope.fork(() -> trustedService.call());
    
    scope.join();
    scope.throwIfFailed();
    
    // Even if untrustedService throws, trustedService's result
    // is available (if it already completed)
    return trusted.resultNow();
}
```

### Resource Exhaustion Mitigation
Structured concurrency ensures that if a parent scope is cancelled, all child tasks are cancelled, preventing resource leaks that could be exploited for DoS attacks.

## General Security Best Practices

1. **Enable the Security Manager** (until removed) for sandboxing untrusted code in virtual threads
2. **Use ScopedValue not ThreadLocal** for authentication tokens and security contexts
3. **Validate template values** even when using safe template processors
4. **Monitor virtual thread creation** for unusual spikes that could indicate an attack
5. **Audit pattern matching exhaustiveness** in security-critical code paths
