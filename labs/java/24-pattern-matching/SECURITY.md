# Security Considerations for Pattern Matching

## Exhaustiveness as a Security Feature

The most significant security benefit of pattern matching is **compile-time exhaustiveness verification**. When processing sensitive data (permissions, roles, event types), exhaustiveness ensures every possible case is handled:

```java
sealed interface Permission permits Read, Write, Execute, Admin {}

boolean checkPermission(Permission required, Permission granted) {
    return switch (required) {
        case Read __    -> switch (granted) {
            case Read __, Write __, Admin __ -> true;
            default -> false;
        };
        case Write __   -> switch (granted) {
            case Write __, Admin __ -> true;
            default -> false;
        };
        case Execute __ -> switch (granted) {
            case Execute __, Admin __ -> true;
            default -> false;
        };
        case Admin __   -> switch (granted) {
            case Admin __ -> true;
            default -> false;
        };
    };
}
```

If a new `Permission` subtype is added, every `switch` over `Permission` fails to compile until updated. This prevents **silent security holes** where a new permission type falls through to a default case with inappropriate access.

## Preventing Type Confusion

Pattern matching prevents type confusion by binding the pattern variable with the appropriate type:

```java
// UNSAFE: Manual casting could introduce type confusion
if (obj instanceof AdminUser) {
    // But what if obj was also a GuestUser?
    // Manual cast could be wrong if conditions get complex
    AdminUser admin = (AdminUser) obj;
}

// SAFE: Pattern matching guarantees correct binding
if (obj instanceof AdminUser admin) {
    // admin is definitely an AdminUser
}
```

## Null Safety

Pattern matching improves null safety by requiring explicit null handling:

```java
// BEFORE: Null could cause NPE at unexpected points
if (obj instanceof String) {
    String s = (String) obj;
    // s is never null here, but we had to trust instanceof
}

// AFTER: Pattern variable is guaranteed non-null after match
if (obj instanceof String s) {
    // s is guaranteed non-null
}

// Switch: null must be handled explicitly
switch (obj) {
    case null -> handleNull();          // Explicit null
    case String s -> process(s);        // s is non-null
    case Integer i -> process(i);       // i is non-null
    // default: would also not match null in pattern switch
}
```

## Record Data Exposure

Record patterns expose all record components. For sensitive data, be careful:

```java
record User(String username, String passwordHash, boolean isAdmin) {}

// BAD: Pattern matching exposes sensitive fields
if (user instanceof User(var name, var hash, var admin)) {
    log.debug("User: " + name + " hash: " + hash);  // Hash exposed in logs!
}

// BETTER: Don't match on sensitive fields
if (user instanceof User(var name, _, _)) {
    log.debug("User: " + name);  // Only non-sensitive info
}
```

## MatchException Attack Surface

If a `MatchException` can be triggered (e.g., through corrupted serialization or custom class loaders that introduce unexpected subtypes), an attacker could cause unhandled cases to throw exceptions. Always ensure switches are exhaustive and consider adding `default` cases in security-critical code:

```java
// In security code, consider a defensive default:
boolean checkAccess(User user) {
    return switch (user.role()) {
        case AdminRole r -> true;
        case UserRole r -> r.hasAccess();
        // If a new Role subtype is added via class loader manipulation:
        default -> false;  // Deny by default
    };
}
```

## Guard Injection

If the guard expression uses untrusted input, be aware of potential injection:

```java
// POTENTIALLY INSECURE: Guard uses unsanitized input
case String s when s.contains(userInput) -> process(s);

// Ensure user input is sanitized or validated before use in guards
```

## Best Practices

1. **Prefer exhaustive switches**: Use sealed types + exhaustive switch instead of casting + default
2. **Explicit null handling**: Always add `case null` when null is possible
3. **Avoid sensitive data in patterns**: Don't match on passwords, tokens, or PII
4. **Defensive defaults in security code**: Add `default` to deny access for unexpected types
5. **Validate guard conditions**: Sanitize user input used in guards
6. **Log safely**: Don't log pattern-matched variable values that contain sensitive data
7. **Use sealed hierarchies**: Prevents unexpected subtypes in security-critical type hierarchies
