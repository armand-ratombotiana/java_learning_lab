# How Optional Works

## Internal Structure

`Optional<T>` is a simple value class with two states:

```java
public final class Optional<T> {
    private static final Optional<?> EMPTY = new Optional<>(null);
    private final T value;
    
    private Optional(T value) {
        this.value = value;
    }
    
    public static <T> Optional<T> empty() {
        @SuppressWarnings("unchecked")
        Optional<T> t = (Optional<T>) EMPTY;
        return t;
    }
    
    public static <T> Optional<T> of(T value) {
        return new Optional<>(Objects.requireNonNull(value));
    }
    
    public static <T> Optional<T> ofNullable(T value) {
        return value == null ? empty() : new Optional<>(value);
    }
}
```

## Creating Optionals

```java
// Empty Optional — no value
Optional<String> empty = Optional.empty();

// Non-null value — throws NPE if null
Optional<String> present = Optional.of("hello");

// Nullable value — empty if null, present otherwise
Optional<String> nullable = Optional.ofNullable(maybeNull);
```

## Checking Presence

```java
Optional<String> opt = Optional.of("hello");

boolean present = opt.isPresent();    // true
boolean absent  = opt.isEmpty();      // false (Java 11+)

// Conditionally execute
opt.ifPresent(s -> System.out.println(s));

// Execute one of two actions
opt.ifPresentOrElse(
    s -> System.out.println(s),    // if present
    () -> System.out.println("empty")  // if absent
);
```

## Getting the Value

```java
Optional<String> opt = Optional.of("hello");
Optional<String> empty = Optional.empty();

// Throws NoSuchElementException if empty
String val1 = opt.get();          // "hello"
// String val2 = empty.get();     // throws NoSuchElementException

// Return default if absent
String val3 = opt.orElse("default");       // "hello"
String val4 = empty.orElse("default");     // "default"

// Compute default lazily
String val5 = empty.orElseGet(() -> computeDefault());

// Throw custom exception
String val6 = empty.orElseThrow(() -> new IllegalStateException("missing"));

// Throw NoSuchElementException (Java 10+)
String val7 = opt.orElseThrow();           // "hello"
```

## Transforming Values

```java
Optional<String> opt = Optional.of("hello");

// map: transform if present
Optional<Integer> length = opt.map(String::length);  // Optional.of(5)

// flatMap: transform to Optional, avoid nesting
Optional<String> upper = opt.flatMap(s -> Optional.of(s.toUpperCase()));

// Simple map vs flatMap:
// map:  Optional<String> → Function<String, Integer> → Optional<Integer>
// flatMap: Optional<String> → Function<String, Optional<Integer>> → Optional<Integer>

// Without flatMap, nesting occurs:
Optional<Optional<Integer>> nested = opt.map(s -> Optional.of(s.length()));
// With flatMap, nesting avoided:
Optional<Integer> flat = opt.flatMap(s -> Optional.of(s.length()));
```

## Filtering

```java
Optional<String> opt = Optional.of("hello world");

// Keep value only if predicate matches
Optional<String> longStr = opt.filter(s -> s.length() > 10);  // Optional.empty()
Optional<String> shortStr = opt.filter(s -> s.length() > 3);  // Optional.of("hello world")

// Filter chained with map:
Optional<Integer> result = Optional.of("hello")
    .filter(s -> s.startsWith("h"))
    .map(String::length);  // Optional.of(5)
```

## Chaining with or() (Java 9+)

```java
// Try multiple optionals in sequence:
Optional<String> result = findFromCache(key)
    .or(() -> findFromDatabase(key))
    .or(() -> findFromRemoteService(key));

// Equivalent to:
Optional<String> result = findFromCache(key);
if (result.isEmpty()) {
    result = findFromDatabase(key);
}
if (result.isEmpty()) {
    result = findFromRemoteService(key);
}
```

## Optional in Streams

```java
// Filter out empty optionals (Java 8 way)
Stream<Optional<String>> stream = getOptionals();
Stream<String> values = stream
    .filter(Optional::isPresent)
    .map(Optional::get);

// Using flatMap (Java 8 way)
Stream<String> values = stream
    .flatMap(opt -> opt.isPresent() ? Stream.of(opt.get()) : Stream.empty());

// Using Optional.stream() (Java 9+)
Stream<String> values = stream
    .flatMap(Optional::stream);

// In a single stream pipeline:
List<String> result = items.stream()
    .map(this::findValue)
    .flatMap(Optional::stream)
    .toList();
```

## Primitive Optionals

Java also provides specialized optionals for primitives:

```java
OptionalInt intOpt = OptionalInt.of(42);
OptionalLong longOpt = OptionalLong.empty();
OptionalDouble doubleOpt = OptionalDouble.of(3.14);

// No OptionalBoolean — use Optional<Boolean>
```

## Integration with Stream Terminal Operations

```java
// Many Stream operations return Optional:
Optional<String> first = stream.findFirst();
Optional<String> any = stream.findAny();
Optional<Integer> max = stream.max(Comparator.naturalOrder());
Optional<Integer> min = stream.min(Comparator.naturalOrder());

// Collectors that return Optional:
Optional<Integer> sum = stream.collect(Collectors.reducing(Integer::sum));
Optional<Map<Boolean, List<String>>> partitioned = stream.collect(
    Collectors.maxBy(Comparator.naturalOrder()));
```
