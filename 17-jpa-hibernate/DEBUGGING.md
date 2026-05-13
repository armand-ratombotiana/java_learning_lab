# Debugging JPA and Hibernate Issues

## Common Failure Scenarios

### N+1 Query Problems

The N+1 query problem is the most notorious performance issue in JPA applications. It occurs when loading a collection of entities triggers separate queries for each related entity. For example, loading 100 orders and accessing their line items produces one query for orders plus 100 queries for each order's items, totaling 101 queries instead of two.

This manifests in application performance as slow page loads or API responses, high database load, and connection pool exhaustion under load. The application may work correctly with small datasets but degrade severely with larger ones. You notice database CPU spike but the application seems to wait most of the time.

The root cause is fetching strategy mismatches. By default, `@ManyToOne` uses EAGER fetching while `@OneToMany` uses LAZY. Mixing these strategies across relationships creates N+1 patterns. Even LAZY collections cause N+1 when accessed inside loops without proper batch fetching.

### Lazy Loading Exceptions

`LazyInitializationException` occurs when you try to access a lazily-loaded relationship outside of an active persistence context. This typically happens when you return an entity from a service method and then attempt to access its collections in the controller or view layer. The persistence context has closed, so Hibernate cannot load the missing data.

The stack trace clearly shows the cause: it includes the entity class, the field name, and indicates the session is closed. The error occurs at the exact line where you access the uninitialized collection, making it relatively easy to locate.

This exception often surfaces when converting entities to DTOs in service layers that don't have transactional boundaries, or when passing entities to asynchronous processing that executes after the original transaction ends.

### Stack Trace Examples

**N+1 query in logs:**
```
Hibernate: select * from orders where ...
Hibernate: select * from items where order_id=1
Hibernate: select * from items where order_id=2
Hibernate: select * from items where order_id=3
... (repeats for each order)
```

**LazyInitializationException:**
```
org.hibernate.LazyInitializationException: failed to lazily initialize a collection of role: com.example.Order.lineItems, could not initialize proxy - no Session
    at org.hibernate.collection.internal.PersistentSet.initialize(PersistentSet.java:42)
    at com.example.OrderController.getOrderDetails(OrderController.java:78)
```

**MultipleBagFetchException:**
```
org.hibernate.HibernateException: MultipleBagFetchException: cannot simultaneously fetch multiple bags
    at org.hibernate.query.spi.QueryBuilder.buildLoader(QueryBuilder.java:729)
    at com.example.service.OrderService.findAll(OrderService.java:45)
```

## Debugging Techniques

### Identifying N+1 Queries

Enable SQL logging with `spring.jpa.show-sql=true` and observe the query patterns. Looking for repeated similar queries with different parameters indicates N+1 behavior. Use pagination, and you'll see batch queries per page rather than bulk queries.

Use Hibernate statistics via `spring.jpa.properties.hibernate.generate_statistics=true`. This provides metrics on query counts, entity fetches, and collection loads. A high ratio of entity loads to collection loads suggests N+1 patterns.

Profile the database to identify the most frequently executed queries. Queries that always appear with different ID parameters suggest N+1. The database's query history helps pinpoint the exact relationship causing the problem.

### Resolving Lazy Loading Issues

The immediate fix is to ensure the data is loaded within the transaction that loads the parent entity. Use `@Transactional` on service methods to keep the persistence context open. For fetch-join scenarios, explicitly load needed associations in the query.

For entities returned from service layers to controllers, consider using entity graphs or fetch joins to load all needed relationships during the initial query. Alternatively, convert entities to DTOs before returning them, loading all required data during the conversion process.

The `@Fetch(FetchMode.SUBSELECT)` annotation loads collections in a single additional query using subselect, which can be more efficient than individual queries but still loads all data. `@BatchSize` limits the number of additional queries by batching lazy loads by ID.

## Best Practices

Design your data access patterns around fetch strategies. Use `@OneToOne` and `@ManyToOne` with LAZY by default. For `@OneToMany` and `@ManyToMany`, explicitly choose fetch strategy based on expected usage patterns.

Use entity graphs to define which associations to load in each use case. `@EntityGraph` and `@NamedEntityGraph` let you specify fetch requirements per query without changing the entity definition. This provides fine-grained control over the data loaded in different scenarios.

Avoid fetching multiple bag collections in a single query. Hibernate cannot generate a single query that fetches multiple bags correctly. Use `List` with `@OrderColumn` or convert to `Set` to resolve `MultipleBagFetchException`.

Prefer fetch joins in JPQL to load specific relationships. The `JOIN FETCH` syntax overrides lazy loading for the specified path and loads data in the same query. Use this for known required associations rather than relying on default fetch strategies.