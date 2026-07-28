# Deep Dive: Pattern Matching

## 1. Type Patterns with instanceof

```java
// Java 16+
if (obj instanceof String s) {
    System.out.println(s.length());
}
```

The pattern variable `s` is in scope only within the `if` block — this is **flow-sensitive scoping**.

## 2. Switch Pattern Matching (Java 21)

```java
Object obj = ...;
String result = switch (obj) {
    case Integer i -> "int: " + i;
    case String s  -> "string: " + s.length();
    case null      -> "null!";
    default        -> "unknown: " + obj;
};
```

### Guarded Patterns with `when`

```java
String classify(Integer x) {
    return switch (x) {
        case Integer i when i < 0 -> "negative";
        case Integer i when i == 0 -> "zero";
        case Integer i -> "positive";
    };
}
```

## 3. Record Patterns

```java
record Box<T>(T value) {}

String describe(Box<?> box) {
    return switch (box) {
        case Box(String s) -> "String box: " + s;
        case Box(Integer i) -> "Integer box: " + i;
        default -> "Unknown box";
    };
}
```

### Nested Record Patterns

```java
record Order(String id, List<Item> items) {}
record Item(String name, double price) {}

double totalDiscounted(Order order) {
    return switch (order) {
        case Order(String id, List<Item> items)
            when items.size() >= 5 -> items.stream()
                .mapToDouble(Item::price).sum() * 0.9;
        case Order(var id, var items) -> items.stream()
                .mapToDouble(Item::price).sum();
    };
}
```

## 4. Exhaustiveness

The compiler enforces switch exhaustiveness on sealed types and booleans:

```java
sealed interface Color permits Red, Green, Blue {}
record Red() implements Color {}
record Green() implements Color {}
record Blue() implements Color {}

String hex(Color c) {
    return switch (c) { // no default needed — all cases covered
        case Red _   -> "#FF0000";
        case Green _ -> "#00FF00";
        case Blue _  -> "#0000FF";
    };
}
```

## 5. Null Handling

Switch on `null` is explicit — no `NullPointerException` by default:

```java
String check(Object obj) {
    return switch (obj) {
        case null     -> "null";
        case String s -> "string";
        case Integer i -> "int";
        default       -> "other";
    };
}
```

## 6. Pattern Matching with Generics

Due to type erasure, generic type parameters are not available in patterns:

```java
// This does NOT compile:
case Box<Integer> b -> ...; // error: illegal pattern

// Use raw or wildcard:
case Box(var value) -> ...; // OK
case Box b -> ...;          // OK
```

## 7. Common Pitfalls

- **Dominance**: A more specific pattern must appear before a general one
- **Type erasure**: Cannot match on generic type parameters
- **Scope**: Pattern variables in `||` conditions are restricted
