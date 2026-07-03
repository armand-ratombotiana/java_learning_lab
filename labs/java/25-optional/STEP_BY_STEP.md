# Step-by-Step Guide to Optional

## Step 1: Creating Optionals

```java
import java.util.Optional;

public class OptionalBasics {
    public static void main(String[] args) {
        // 1. Empty Optional
        Optional<String> emptyOpt = Optional.empty();
        System.out.println("Empty: " + emptyOpt);  // Optional.empty
        
        // 2. Present Optional (value must be non-null)
        Optional<String> presentOpt = Optional.of("Hello");
        System.out.println("Present: " + presentOpt);  // Optional[Hello]
        
        // 3. Nullable Optional
        String nullable = null;
        Optional<String> nullableOpt = Optional.ofNullable(nullable);
        System.out.println("Nullable null: " + nullableOpt);  // Optional.empty
        
        String nonNull = "World";
        Optional<String> nullablePresent = Optional.ofNullable(nonNull);
        System.out.println("Nullable value: " + nullablePresent);  // Optional[World]
        
        // What happens with Optional.of(null)?
        // Optional<String> error = Optional.of(null);  // NullPointerException!
    }
}
```

## Step 2: Checking and Getting Values

```java
public class CheckAndGet {
    public static void main(String[] args) {
        Optional<String> opt = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        
        // Check if present
        System.out.println("isPresent: " + opt.isPresent());    // true
        System.out.println("isEmpty: " + opt.isEmpty());        // false (Java 11+)
        
        // Get value (dangerous - throws if empty)
        System.out.println("get: " + opt.get());                // "Hello"
        // System.out.println(empty.get());                     // NoSuchElementException!
        
        // Safe get with default
        System.out.println("orElse: " + opt.orElse("Default"));  // "Hello"
        System.out.println("orElse: " + empty.orElse("Default")); // "Default"
        
        // Lazy default
        System.out.println("orElseGet: " + empty.orElseGet(() -> computeDefault()));
        
        // Throw custom exception
        String result = empty.orElseThrow(() -> new RuntimeException("Value missing"));
        
        // Throw NoSuchElementException (Java 10+)
        String val = opt.orElseThrow();  // "Hello"
    }
    
    static String computeDefault() {
        System.out.println("Computing default...");
        return "Computed Default";
    }
}
```

## Step 3: ifPresent and ifPresentOrElse

```java
public class IfPresent {
    public static void main(String[] args) {
        Optional<String> opt = Optional.of("Hello");
        Optional<String> empty = Optional.empty();
        
        // Execute if present
        opt.ifPresent(s -> System.out.println("Value: " + s));  // "Value: Hello"
        empty.ifPresent(s -> System.out.println("Value: " + s));  // Nothing printed
        
        // Execute one of two actions (Java 9+)
        opt.ifPresentOrElse(
            s -> System.out.println("Value: " + s),
            () -> System.out.println("No value")
        );  // "Value: Hello"
        
        empty.ifPresentOrElse(
            s -> System.out.println("Value: " + s),
            () -> System.out.println("No value")
        );  // "No value"
    }
}
```

## Step 4: Map and Filter

```java
public class MapAndFilter {
    public static void main(String[] args) {
        Optional<String> opt = Optional.of("Hello World");
        Optional<String> empty = Optional.empty();
        
        // map: transform if present
        Optional<Integer> length = opt.map(String::length);
        System.out.println(length);  // Optional[11]
        
        Optional<Integer> emptyLength = empty.map(String::length);
        System.out.println(emptyLength);  // Optional.empty
        
        // filter: keep only if predicate matches
        Optional<String> filtered = opt.filter(s -> s.length() > 5);
        System.out.println(filtered);  // Optional[Hello World]
        
        Optional<String> noMatch = opt.filter(s -> s.length() > 20);
        System.out.println(noMatch);  // Optional.empty
        
        // Chaining
        Optional<Integer> result = Optional.of("hello")
            .filter(s -> s.startsWith("h"))
            .map(s -> s.length() * 2)
            .filter(n -> n > 5);
        System.out.println(result);  // Optional[10]
    }
}
```

## Step 5: FlatMap for Chaining

```java
public class FlatMapDemo {
    static Optional<String> getNickname(String name) {
        return switch (name) {
            case "Alexander" -> Optional.of("Alex");
            case "Elizabeth" -> Optional.of("Liz");
            default -> Optional.empty();
        };
    }
    
    public static void main(String[] args) {
        // Without flatMap — creates nested Optional
        Optional<Optional<String>> nested = 
            Optional.of("Alexander").map(FlatMapDemo::getNickname);
        System.out.println(nested);  // Optional[Optional[Alex]]
        
        // With flatMap — flattens to single Optional
        Optional<String> flat = 
            Optional.of("Alexander").flatMap(FlatMapDemo::getNickname);
        System.out.println(flat);  // Optional[Alex]
        
        // Chaining flatMap operations
        String result = Optional.of("Alexander")
            .flatMap(FlatMapDemo::getNickname)
            .map(String::toUpperCase)
            .orElse("No nickname");
        System.out.println(result);  // "ALEX"
        
        // Missing value in chain
        String missing = Optional.of("John")
            .flatMap(FlatMapDemo::getNickname)
            .orElse("No nickname");
        System.out.println(missing);  // "No nickname"
    }
}
```

## Step 6: or() for Fallback Chains (Java 9+)

```java
public class OrChain {
    static Optional<String> findInCache(String key) {
        System.out.println("Checking cache for: " + key);
        return Optional.empty();  // Miss
    }
    
    static Optional<String> findInDb(String key) {
        System.out.println("Checking DB for: " + key);
        return Optional.of("db:" + key);  // Found in DB
    }
    
    static Optional<String> findInRemote(String key) {
        System.out.println("Checking remote for: " + key);
        return Optional.of("remote:" + key);
    }
    
    public static void main(String[] args) {
        // Try cache, then DB, then remote (short-circuits on first hit)
        Optional<String> result = findInCache("key1")
            .or(() -> findInDb("key1"))
            .or(() -> findInRemote("key1"));
        
        System.out.println(result);  // Checking cache, Checking DB, Optional[db:key1]
        // Note: findInRemote is NOT called (short-circuit)
    }
}
```

## Step 7: Optional with Streams

```java
public class OptionalStreams {
    public static void main(String[] args) {
        List<Optional<String>> items = List.of(
            Optional.of("A"),
            Optional.empty(),
            Optional.of("B"),
            Optional.empty(),
            Optional.of("C")
        );
        
        // Java 8 way: filter + map
        List<String> result1 = items.stream()
            .filter(Optional::isPresent)
            .map(Optional::get)
            .toList();
        System.out.println(result1);  // [A, B, C]
        
        // Java 9+ way: flatMap with Optional::stream
        List<String> result2 = items.stream()
            .flatMap(Optional::stream)
            .toList();
        System.out.println(result2);  // [A, B, C]
        
        // Practical: map to Optional, then flatten
        List<String> ids = List.of("1", "2", "3", "invalid");
        List<Integer> validIds = ids.stream()
            .map(s -> {
                try { return Optional.of(Integer.parseInt(s)); }
                catch (NumberFormatException e) { return Optional.<Integer>empty(); }
            })
            .flatMap(Optional::stream)
            .toList();
        System.out.println(validIds);  // [1, 2, 3]
    }
}
```

## Step 8: Optional in Method Signatures

```java
public class ServiceExample {
    // GOOD: Return Optional for potentially absent values
    public Optional<Customer> findCustomer(String id) {
        // ...
        return Optional.empty();  // or Optional.of(customer)
    }
    
    // GOOD: Use orElseThrow for required values
    public Customer getCustomer(String id) {
        return findCustomer(id).orElseThrow(
            () -> new CustomerNotFoundException(id));
    }
    
    // AVOID: Optional parameters
    public void process(String name, Optional<String> middleName) {
        // This is an anti-pattern!
    }
    
    // PREFER: Method overloading
    public void process(String name) {
        process(name, null);
    }
    
    public void process(String name, String middleName) {
        // Single method with nullable parameter
    }
    
    // AVOID: Optional fields
    // public Optional<String> getMiddleName() { ... }  // OK for getter
    // private Optional<String> middleName;  // NOT OK for field
}
```
