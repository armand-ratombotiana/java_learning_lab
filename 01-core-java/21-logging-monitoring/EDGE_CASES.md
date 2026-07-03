# Module 21: Logging & Monitoring - Edge Cases & Pitfalls

---

## Pitfall 1: String Concatenation in Logs

### ❌ Wrong
Concatenating strings inside logging statements is inefficient, as the concatenation happens even if the log level is disabled.
```java
// String concatenation happens before checking if debug is enabled
logger.debug("Processing user " + user.getId() + " with status " + status);
```

### ✅ Correct
Use parameterized logging to defer string construction until necessary.
```java
// Efficient: String is only formatted if debug is enabled
logger.debug("Processing user {} with status {}", user.getId(), status);
```

---

## Pitfall 2: Swallowing Exceptions

### ❌ Wrong
Logging an exception but not passing the exception object, losing the stack trace.
```java
try {
    doSomething();
} catch (Exception e) {
    logger.error("Something went wrong: " + e.getMessage()); // Stack trace is lost!
}
```

### ✅ Correct
Pass the exception as the last argument to the logging method to print the stack trace.
```java
try {
    doSomething();
} catch (Exception e) {
    logger.error("Something went wrong", e); // Stack trace is preserved
}
```

---

## Pitfall 3: Over-Logging in Production

### ❌ Wrong
Leaving the root logger level set to `DEBUG` or `TRACE` in a production environment can overwhelm the disk space and severely impact application performance due to I/O operations.

### ✅ Correct
Configure production environments to use `INFO` or `WARN` for the root logger. Use rolling file appenders to manage log file sizes automatically.