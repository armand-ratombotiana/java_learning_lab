# Security with Java 21 Features

## Virtual Thread Security

### ThreadLocal Security
- Each virtual thread gets its own ThreadLocal
- Ensure ThreadLocal doesn't hold sensitive data
- Clean up in finally block

```java
ThreadLocal<Sensitive> userContext = ThreadLocal.withInitial(() -> null);

try {
    userContext.set(loadUserContext());
    // Process
} finally {
    userContext.remove();  // Clean up!
}
```

### Synchronized Blocks
- Virtual threads can be "pinned" in synchronized blocks
- Pinning can cause performance issues
- Use java.util.concurrent locks for better scheduling

## Pattern Matching Security

- Compile-time type checking prevents ClassCastException
- No reflection needed for pattern matching
- More predictable behavior

## String Template Security

### Injection Prevention
```java
// Safe with STR
String query = STR."SELECT * FROM users WHERE name = '\{name}'";

// Be careful with raw strings or user input
// Use parameterized queries!
```