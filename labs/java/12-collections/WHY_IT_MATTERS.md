# Why Collections Matter

## Universal Dependency

Every non-trivial Java application uses collections. From a simple `ArrayList` in a CRUD app to complex `ConcurrentHashMap` in high-throughput systems — collections are the building blocks of data management.

## Algorithmic Foundation

Choosing the right collection is often the difference between O(1) and O(n²) performance:

```java
// O(n) lookup — slow for large datasets:
List<User> users = new ArrayList<>();
User user = users.stream().filter(u -> u.getId().equals(id)).findFirst().orElse(null);

// O(1) lookup — fast:
Map<String, User> userMap = new HashMap<>();
User user = userMap.get(id);
```

## Data Integrity

`Set` enforces uniqueness. `List` preserves order. `Queue` ensures processing order. Each collection type encodes behavioral guarantees that protect data integrity.

## Stream API Integration

Collections are the primary data source for the Stream API, enabling declarative data processing pipelines:

```java
List<Order> highValueOrders = orders.stream()
    .filter(o -> o.getAmount() > 1000)
    .sorted(byDate)
    .toList();
```

## Framework Foundation

- **Hibernate/JPA**: `List<Entity>`, `Set<Entity>`, `Map<Entity, Entity>`
- **Spring**: `ApplicationContext.getBean(Class<T>)` returns from internal Maps
- **Jackson**: Serializes/deserializes to `Map<String, Object>`
- **Web clients**: Response bodies parsed into Maps and Lists

## API Design

Well-designed Java APIs return and accept collection types:

```java
// Flexible — caller can pass any List:
public void processOrders(List<Order> orders) { ... }

// Self-documenting — returns Set guarantees uniqueness:
public Set<String> getUniqueTags() { ... }
```

## The Cost of Wrong Choice

- Using `ArrayList` for frequent insertions at index 0 → O(n²) performance
- Using `HashSet` when insertion order must be preserved → unpredictable iteration
- Using `TreeSet` with expensive comparisons → performance degradation
- Not specifying initial capacity → excessive resizing overhead
