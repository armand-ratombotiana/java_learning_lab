# Security Considerations for Lambdas

## Lambda Security Concerns

### Captured Variables and Mutable State

**Risk**: Lambdas capturing mutable state can cause race conditions:
```java
public class Counter {
    private int count = 0;
    
    public Function<Integer, Integer> getAdder() {
        return x -> {
            count += x; // Race condition in parallel context!
            return count;
        };
    }
}
```

**Fix**: Use stateless lambdas or proper synchronization:
```java
public Function<Integer, Integer> getAdder() {
    return x -> x; // Stateless
}
```

### Resource Leaks in Lambda

**Risk**: Lambda capturing resources that don't close:
```java
Stream<String> stream = Files.lines(Path.of("file.txt"))
    .map(s -> { /* access external resource */ });
// Forgot to close stream!
```

**Fix**: Use try-with-resources or ensure streams are closed:
```java
try (Stream<String> stream = Files.lines(Path.of("file.txt"))) {
    stream.map(...).collect(...);
}
```

### Malicious Code via Method References

**Risk**: Reflection can invoke methods unexpectedly:
```java
// Untrusted code could pass dangerous method reference
someService.execute(userInput::toString);
```

**Fix**: Validate input and avoid reflection with user-controlled method references

## Best Practices

1. Avoid capturing mutable state in lambdas used in parallel streams
2. Use immutable objects in lambdas
3. Properly manage resources opened within lambdas
4. Be cautious with method references from untrusted sources
5. Avoid capturing sensitive data (passwords, keys) in lambdas