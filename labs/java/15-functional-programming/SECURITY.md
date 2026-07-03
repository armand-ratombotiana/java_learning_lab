# Security — Functional Programming

## Immutability Prevents Data Corruption
Immutable objects cannot be modified after creation — prevents tampering by malicious code.

## No Side Effects = No Information Leakage
Pure functions cannot leak data through shared mutable state, global variables, or I/O side effects.

## Optional Prevents NullPointerException
```java
Optional.ofNullable(untrustedInput)
    .map(this::sanitise)
    .ifPresent(this::process);
```
Without Optional, a null from untrusted input could propagate unexpectedly.

## Defensive Copies with Records
```java
record UserConfig(String apiKey, int rateLimit) {}
// Safe to pass around — no one can modify apiKey
```

## Secure Function Composition
Compose validation functions as pure predicates — each step is independently testable and cannot contaminate others:
```java
Predicate<String> notEmpty = s -> !s.isEmpty();
Predicate<String> noSql = s -> !s.contains("'");
Predicate<String> sanitised = notEmpty.and(noSql);
```
