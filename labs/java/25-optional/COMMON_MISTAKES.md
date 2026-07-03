# Common Mistakes with Optional

## Mistake 1: Using Optional.get() Without Checking

```java
// BAD: Blind get()
Optional<String> opt = findValue();
String val = opt.get();  // NoSuchElementException if empty!

// GOOD: Safe retrieval
String val = opt.orElse("default");
String val = opt.orElseGet(() -> computeDefault());
String val = opt.orElseThrow(() -> new IllegalStateException());
```

## Mistake 2: Using Optional for Fields

```java
// BAD: Optional as a field type
public class Customer {
    private Optional<String> middleName = Optional.empty();  // Not serializable!
    private Optional<Address> address = Optional.empty();    // Extra object overhead
    
    public Optional<String> getMiddleName() { return middleName; }
}

// GOOD: Nullable fields with Optional return
public class Customer {
    private String middleName;
    private Address address;
    
    public Optional<String> getMiddleName() {
        return Optional.ofNullable(middleName);
    }
}
```

## Mistake 3: Using Optional as Method Parameter

```java
// BAD: Optional parameter
public void process(Optional<String> value) {
    // Caller must create Optional, confusing API
}

// GOOD: Method overloading
public void process(String value) {
    // Non-null required
}
public void process() {
    // Absent value
}
```

## Mistake 4: Using isPresent()-get() Instead of map/filter/orElse

```java
// BAD: Imperative style with Optional
if (opt.isPresent()) {
    String val = opt.get();
    String processed = process(val);
    System.out.println(processed);
} else {
    System.out.println("default");
}

// GOOD: Functional style
String result = opt.map(this::process).orElse("default");
System.out.println(result);
```

## Mistake 5: Returning null Instead of Optional.empty()

```java
// BAD: Returning null from Optional-returning method
public Optional<String> findName() {
    if (notFound) return null;  // This breaks the contract!
    return Optional.of(name);
}
// Now caller's opt.orElse() will throw NPE (because opt itself is null)

// GOOD: Return empty Optional
public Optional<String> findName() {
    if (notFound) return Optional.empty();
    return Optional.of(name);
}
```

## Mistake 6: Using orElse When orElseGet Is Better

```java
// BAD: orElse with expensive computation
String result = opt.orElse(expensiveComputation());  
// expensiveComputation() is ALWAYS called, even if opt is present!

// GOOD: orElseGet with lazy computation
String result = opt.orElseGet(() -> expensiveComputation());
// expensiveComputation() is only called if opt is empty
```

## Mistake 7: Nesting Optionals with map Instead of flatMap

```java
// BAD: Nested Optional
Optional<Optional<String>> nested = opt.map(s -> findNickname(s));

// GOOD: Flat Optional
Optional<String> flat = opt.flatMap(s -> findNickname(s));
```

## Mistake 8: Using Optional for Collections

```java
// BAD: Optional wrapping a collection
public Optional<List<String>> getTags() { ... }
// What does Optional.empty() mean vs. Collections.emptyList()?

// GOOD: Return empty collection
public List<String> getTags() {
    return tags != null ? tags : Collections.emptyList();
}
```

## Mistake 9: Checking Optional.isEmpty() Then Calling get()

```java
// BAD: Redundant check
if (opt.isPresent()) {
    process(opt.get());  // The check and get are redundant
}

// BAD: Also redundant
if (opt.isEmpty()) return;
String val = opt.get();  // Why not just use orElse?

// GOOD: Use ifPresent or map
opt.ifPresent(this::process);
```

## Mistake 10: Using Optional.of() When ofNullable() Is Needed

```java
// BAD: of() with potentially null value
Optional<String> opt = Optional.of(service.getValue());  
// NPE if getValue() returns null!

// GOOD: ofNullable for potentially null values
Optional<String> opt = Optional.ofNullable(service.getValue());
```

## Mistake 11: Treating Optional as a General Null Replacement

Optional is not a general replacement for null. It's designed for return types where absence is a valid result. Don't use Optional for:
- Fields (use nullable fields)
- Method parameters (use overloading)
- Serialization (Optional is not Serializable)
- Collections (return empty collections)
- Performance-critical code (Optional adds allocation overhead)
