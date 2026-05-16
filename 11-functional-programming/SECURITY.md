# Security in Functional Programming

## Lambda Security Concerns

### Capturing Sensitive Data

**Risk**: Lambda captures sensitive data that leaks
```java
String apiKey = getSecretKey();
users.stream()
    .map(u -> {
        // apiKey captured - could leak in stack traces
        return callApi(u, apiKey);
    });
```

**Fix**: Pass sensitive data as parameters
```java
users.stream()
    .map(u -> callApi(u, getSecretKey()));
```

### Resource Leaks

**Risk**: Stream doesn't close resources
```java
Files.lines(path)
    .map(this::processLine);  // Never closed!
```

**Fix**: Use try-with-resources
```java
try (Stream<String> lines = Files.lines(path)) {
    lines.map(this::processLine).collect(...);
}
```

## FP Best Practices for Security

1. **Stateless functions**: No state means no leakage
2. **Immutable data**: Can't be tampered with
3. **Side-effect free**: Predictable behavior
4. **Validate inputs**: Even in functional style
5. **Close resources**: Use try-with-resources for streams