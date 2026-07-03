# Security — Annotations

## Annotation Injection
Annotations are compile-time constructs — they cannot be injected at runtime. However, if a framework reads annotation configurations from external sources:
```java
// Theoretical vulnerability — avoid this pattern
String userAnnotationConfig = request.getParameter("config");
// Parsing and applying external annotation metadata
```

## @SuppressWarnings Misuse
```java
@SuppressWarnings("all")  // Silences security-relevant warnings
```
**Fix:** Be specific — only suppress known-safe warnings.

## Runtime Retention of Sensitive Metadata
```java
@Retention(RUNTIME)
@interface Credentials {
    String username();
    String password(); // Stored in class file, accessible via reflection
}
```
**Fix:** Use SOURCE retention or avoid storing secrets in annotations.

## Serialization
Annotations are not serializable — but their results can be stored. Ensure annotation-based configuration doesn't expose sensitive internals.
