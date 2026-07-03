# Why Functional Programming Matters

## Correctness by Construction
Pure functions eliminate entire categories of bugs (unexpected state changes, aliasing issues).

## Local Reasoning
When reading a pure function, you only need to understand its parameters — not the entire program state.

## Composability
Small, pure functions compose into complex logic:
```java
int total = orders.stream()
    .filter(Order::isPaid)
    .mapToInt(Order::getAmount)
    .sum();
```

## Safe Concurrency
No locks, no synchronised — pure functions are inherently thread-safe.

## Caching / Memoization
Pure functions are trivially cacheable — same input always produces the same output.
