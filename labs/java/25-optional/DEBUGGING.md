# Debugging Optional

## Common Exceptions

### NoSuchElementException from get()
Occurs when calling `get()` on an empty Optional. The stack trace shows the exact line. **Fix**: Replace `.get()` with `.orElse()`, `.orElseGet()`, or `.orElseThrow()`.

### NullPointerException from of()
Occurs when passing null to `Optional.of()`. **Fix**: Use `Optional.ofNullable()` if the value might be null, or ensure the argument is non-null.

### NullPointerException from orElse()
Can occur if the Optional instance itself is null:
```java
Optional<String> opt = null;  // Should never happen
opt.orElse("default");  // NPE because opt is null!
```
**Fix**: Never assign null to an Optional variable. Return `Optional.empty()` instead.

## Debugging Strategies

### Check Your Optional Chain
Use `peek`-style logging at each step (simulated with map):

```java
Optional<String> result = findUser(id)
    .map(user -> { 
        System.out.println("Found user: " + user); 
        return user; 
    })
    .flatMap(user -> { 
        System.out.println("Finding address...");
        return findAddress(user); 
    })
    .orElse("default");
```

### Test with Both Values
Always test with both present and empty Optionals:

```java
@Test
void testOptionalBehavior() {
    // Present
    Optional<String> present = Optional.of("hello");
    assertEquals("HELLO", present.map(String::toUpperCase).orElse("empty"));
    
    // Empty
    Optional<String> empty = Optional.empty();
    assertEquals("empty", empty.map(String::toUpperCase).orElse("empty"));
}
```

### Verify Optional Type
Ensure your method's return type is Optional:

```java
// BAD: Missing Optional in return type
public String findName(String id) {
    return Optional.ofNullable(name).orElse("default");  // Returns String, not Optional
}

// GOOD: Returns Optional
public Optional<String> findName(String id) {
    return Optional.ofNullable(name);
}
```

## IDE Support

### IntelliJ IDEA
- **Inspection**: Warns on `Optional.get()` without `isPresent()` check
- **Inspection**: Warns on `Optional` field usage
- **Inspection**: Warns on `Optional` parameter usage
- **Quick Fix**: Convert `if(opt.isPresent()) { opt.get() }` to `opt.ifPresent()`

### Eclipse
- **Null analysis**: Helps track Optional flow through method chains
- **Warning**: On `.get()` without preceding `.isPresent()` check

## Common Debugging Scenarios

### Scenario: Optional Chain Returns Empty Unexpectedly
```java
Optional<String> result = Optional.of("hello")
    .filter(s -> s.length() > 10)  // Nothing matches!
    .map(String::toUpperCase);
// result is empty, but why?
```

**Debug**: Add logging or breakpoint after each step to see where the chain breaks.

### Scenario: orElse vs orElseGet Confusion
```java
// This always calls computeDefault, even if opt is present:
String result = opt.orElse(computeDefault());

// This only calls computeDefault if opt is empty:
String result = opt.orElseGet(() -> computeDefault());
```

**Debug**: If you see unexpected computation, check which method you're using.

### Scenario: Nested Optional Confusion
```java
// Wrong: results in Optional<Optional<String>>
Optional<Optional<String>> wrong = findUser(id)
    .map(user -> findNickname(user));

// Correct: results in Optional<String>
Optional<String> correct = findUser(id)
    .flatMap(user -> findNickname(user));
```

**Debug**: Check the types of your intermediate variables. If you see `Optional<Optional<...>>`, you should be using `flatMap` instead of `map`.
