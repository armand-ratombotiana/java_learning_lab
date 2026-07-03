# Security — Exceptions

## Information Leakage via Exceptions

Stack traces can reveal internal paths, SQL queries, file structures, and variable values. Never expose stack traces to end users — log internally, return generic messages.

## Exception Message Safety

```java
// BAD: reveals SQL in error message
catch (SQLException e) {
    return "Query failed: " + e.getMessage();  // Contains SQL!
}

// GOOD: generic user-facing message
catch (SQLException e) {
    log.error("Query failed", e);  // Full details in log
    return "A database error occurred";  // Safe for user
}
```

## Suppressing Exceptions

Catching and ignoring exceptions can leave the system in an inconsistent state. Log and handle appropriately.

## Resource Leak Denial of Service

Unclosed resources (connections, streams, files) cause resource exhaustion. Always use try-with-resources.

## Exception in Security Checks

```java
// BAD: exception-based access control
try {
    securityManager.checkPermission(permission);
    // proceed
} catch (AccessControlException e) {
    // deny
}
```

## Custom Exception Security

Custom exceptions should not carry sensitive data in their public API or serialized form. Override `toString()` to avoid including sensitive fields.
