# Collections Security

## Key Security Concerns

### 1. Serialization Vulnerabilities
- Avoid serializing collections with sensitive data
- Use custom serialization for sensitive collections
- Consider encryption for serialized collections

### 2. Untrusted Input
- Validate elements added to collections
- Prevent DoS via large collections
- Use bounded collections for untrusted input

```java
// Limit collection size
List<Item> items = new ArrayList<>(100);
if (untrustedInput.size() > 100) {
    throw new SecurityException("Too many items");
}
```

### 3. Type Safety
- Always use generics to prevent type confusion
- Avoid raw types that bypass type checking
- Use checked collections for added safety

```java
List<String> safeList = Collections.checkedList(
    new ArrayList<>(), String.class);
```

### 4. Immutability
- Use immutable collections for shared data
- Collections.unmodifiableXxx() for wrapping
- Prefer Java 9+ factory methods: List.of()

## Secure Patterns

- Copy collections before passing to untrusted code
- Return defensive copies
- Use immutable collections as much as possible
- Never expose internal mutable collections