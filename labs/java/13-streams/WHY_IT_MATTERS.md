# Why Streams Matter

## Code Conciseness

Streams dramatically reduce boilerplate:

```java
// Without streams:
List<String> result = new ArrayList<>();
for (String s : list) {
    if (s != null && !s.isEmpty()) {
        result.add(s.trim().toLowerCase());
    }
}

// With streams:
List<String> result = list.stream()
    .filter(s -> s != null && !s.isEmpty())
    .map(s -> s.trim().toLowerCase())
    .toList();
```

## Declarative Focus

Streams separate **what** to do from **how** to do it. The same pipeline works for sequential or parallel execution:

```java
// Sequential:
double avg = orders.stream().mapToDouble(Order::getAmount).average().orElse(0);

// Parallel (one word change!):
double avg = orders.parallelStream().mapToDouble(Order::getAmount).average().orElse(0);
```

## Lazy Evaluation

Intermediate operations don't execute until needed:

```java
long count = hugeList.stream()
    .filter(this::slowFilter)
    .map(this::slowMap)
    .findFirst()  // Only processes enough elements to satisfy
    .orElse(null);
// Stops processing after finding first match — not after processing all elements!
```

Lazy evaluation can dramatically reduce work for short-circuiting operations.

## Immutability and Thread Safety

Stream operations encourage side-effect-free functions, reducing bugs:

```java
// Bad — shared mutable state:
List<String> result = new ArrayList<>();
list.stream().filter(pred).forEach(result::add);  // Thread-unsafe!

// Good — immutable collector:
List<String> result = list.stream().filter(pred).toList();  // Thread-safe
```

## Readability

Stream pipelines read like a description of the transformation:

```java
Map<Status, List<Order>> ordersByStatus = orders.stream()
    .filter(Order::isActive)
    .sorted(byDate)
    .collect(groupingBy(Order::getStatus));
```

"Take orders, filter active, sort by date, group by status" — the code reads as the specification.

## Database-Like Operations

Streams bring SQL-style operations to in-memory data:

```java
orders.stream()
    .filter(o -> o.getDate().isAfter(lastWeek))
    .flatMap(o -> o.getLineItems().stream())
    .collect(groupingBy(
        LineItem::getProduct,
        summingDouble(LineItem::getTotal)
    ));
```
