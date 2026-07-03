# Security — Java Streams

## Do Not Expose Internal Collections
```java
public Stream<Secret> getSecrets() {
    return secrets.stream(); // Caller can modify source if not immutable
}
```
**Fix:** Return an unmodifiable view or a defensive copy via `collect(toUnmodifiableList())`.

## Data Filtering in Secure Contexts
Stream pipelines that process sensitive data (PII, credentials) must not leak through `peek()` or logging:
```java
stream.peek(System.out::println); // Security risk in production
```

## Denial of Service via Infinite Streams
```java
Stream.iterate(0, i -> i + 1).forEach(...); // Never terminates
```
**Fix:** Always use `limit()` or a finite source.

## Thread Safety in Parallel Streams
Shared mutable state in parallel streams can lead to data races compromising integrity:
```java
// Unsafe
List<String> unsafe = new ArrayList<>();
list.parallelStream().forEach(unsafe::add);
```

## Resource Leaks
Always close stream-backed resources (e.g. `Files.lines()`) in try-with-resources to avoid file descriptor leaks.
