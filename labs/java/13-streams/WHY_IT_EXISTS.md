# Why Streams Exist

## The Problem: Verbose Collection Processing

Before streams (Java 8), processing collections required external iteration with loops:

```java
// Old style:
List<Order> highValueOrders = new ArrayList<>();
for (Order order : allOrders) {
    if (order.getAmount() > 1000) {
        highValueOrders.add(order);
    }
}
Collections.sort(highValueOrders, new Comparator<Order>() {
    public int compare(Order a, Order b) {
        return b.getAmount().compareTo(a.getAmount());
    }
});
```

This is verbose, error-prone, and mixes what with how.

## The Solution: Internal Iteration

Streams flip the model — the library controls iteration:

```java
// Stream style:
List<Order> highValueOrders = allOrders.stream()
    .filter(o -> o.getAmount() > 1000)
    .sorted(Comparator.comparing(Order::getAmount).reversed())
    .toList();
```

- **Declarative**: Say what, not how
- **Composable**: Pipeline of operations
- **Parallelizable**: One word (`parallelStream()`) enables concurrency

## Why Not Just Add Methods to Collections?

Adding `filter()`, `map()` etc. directly to `Collection` would:
- Force every collection to implement every operation
- Make parallel execution coordination difficult
- Prevent lazy evaluation (chained operations would materialize intermediate results)

Streams are a separate abstraction that decouples data source from processing logic.

## Historical Context

Java 8 (2014) introduced streams as part of the broader "Project Lambda" effort:

- **Lambdas** (JSR 335) enabled functional programming in Java
- **Streams** provided the API to leverage lambdas for data processing
- **Collectors** provided the bridge back to collections

The design was influenced by Google Guava, Scala collections, and .NET LINQ.
