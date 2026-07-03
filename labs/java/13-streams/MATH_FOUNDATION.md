# Streams — Mathematical Foundation

## Lambda Calculus Foundations

Stream operations map to concepts from lambda calculus:
- `map()`: Functor mapping (apply function to each element)
- `filter()`: Subset selection (set comprehension)
- `flatMap()`: Monadic bind (join + map)
- `reduce()`: Catamorphism (fold over structure)

## Fold Operations

Reduction in streams is a **fold** from functional programming:

```java
// Left fold:
T result = stream.reduce(identity, (a, b) -> op(a, b));
```

**Associativity requirement** (for parallel): `op(a, op(b, c)) == op(op(a, b), c)`

Reduction must be **associative** and the identity must satisfy:
- `op(identity, x) == x` (left identity)
- `op(x, identity) == x` (right identity)

For sum: identity = 0, op = `+`
For product: identity = 1, op = `*`
For max: identity = `-∞` (or `Integer.MIN_VALUE`)

## GroupingBy Algebra

`groupingBy(classifier, downstream)` is equivalent to:

```
For each element e:
  key = classifier(e)
  bucket[key] = downstream.accumulator(bucket[key], e)
```

The downstream collector can be any `Collector`, enabling multi-level aggregation:

```java
Map<String, Map<Status, Long>> report = orders.stream()
    .collect(groupingBy(Order::getRegion,
             groupingBy(Order::getStatus, counting())));
```

## Short-Circuiting Logic

Short-circuiting operations (`findFirst`, `anyMatch`, `limit`) introduce dependencies:

```
findFirst():                    ↓element
  Returns first element.

anyMatch(p):                    ↓element
  Stops when p(x) is true.

limit(n):
  Stops after n elements.
  Must preserve encounter order.
```

## Parallel Speedup Formula (Amdahl's Law)

The potential speedup from parallel streams is limited by the sequential portion:

```
Speedup = 1 / ( (1 - P) + P/N )

Where:
  P = parallelizable fraction
  N = number of cores
```

For a stream operation that is 90% parallelizable on 8 cores:
```
Speedup = 1 / (0.1 + 0.9/8) = 1 / 0.2125 ≈ 4.7×
```

Even with infinite cores, the maximum speedup is:
```
Speedup_max = 1 / (1 - P) = 1 / 0.1 = 10×
```

## Collector Combinatorics

`Collectors.teeing()` (Java 12+) applies two collectors and merges results:

```
Stream → Collector1 → Result1
       ↘ Collector2 → Result2 → Merge → Final Result
```

This is the categorical **product** of two collectors.
