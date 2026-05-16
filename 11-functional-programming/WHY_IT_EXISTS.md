# Why Functional Programming Exists

## Core Motivation

Functional programming (FP) exists because:

1. **Imperative code is hard to reason about**: State changes make code unpredictable
2. **Concurrency is hard**: Shared mutable state causes race conditions
3. **Testing is difficult**: Complex stateful code is hard to test in isolation
4. **Code reuse is limited**: Objects tie behavior to state

## What FP Provides

### Pure Functions
- Same input → same output
- No side effects
- Easier to test and reason about

### Declarative Over Imperative
Instead of:
```java
List<Integer> result = new ArrayList<>();
for (Integer i : list) {
    if (i > 5) {
        result.add(i * 2);
    }
}
```

Use:
```java
List<Integer> result = list.stream()
    .filter(i -> i > 5)
    .map(i -> i * 2)
    .collect(Collectors.toList());
```

### Why It Matters

- **Parallel processing**: Pure functions can safely run in parallel
- **Easier testing**: No mocking of external state needed
- **Composable**: Small functions combine into complex systems
- **Less bugs**: Immutable data avoids many common errors