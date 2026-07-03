# Debugging — Functional Programming

## Breakpoint in a Lambda
```java
list.stream()
    .filter(x -> {
        return x > 5; // Set breakpoint here
    })
    .collect(Collectors.toList());
```

## Debugging Optional Chains
Split the chain to inspect intermediate values:
```java
Optional<String> intermediate = optional.map(String::trim);
System.out.println("After trim: " + intermediate);
String result = intermediate.orElse("default");
```

## Stream.peek() for Debugging
```java
long count = words.stream()
    .filter(w -> w.length() > 3)
    .peek(w -> System.out.println("After filter: " + w))
    .map(String::toUpperCase)
    .peek(w -> System.out.println("After map: " + w))
    .count();
```

## Immutability Benefits
Immutable objects are easy to debug — their state never changes, so a snapshot at any point is always valid.

## Pure Function Traceability
```java
// If this function returns unexpected output, the bug MUST be in this function
// (no external state to corrupt it)
public int computeTax(Order order) {
    return order.items().stream()
        .mapToInt(Item::price)
        .sum() * TAX_RATE;
}
```
