# Performance: Spring Data JPA

## N+1 Query Prevention
```java
// Use @EntityGraph
@EntityGraph(attributePaths = {"orders", "orders.items"})
List<User> findAllWithOrders();

// Or JOIN FETCH in @Query
@Query("SELECT u FROM User u JOIN FETCH u.orders WHERE u.id = :id")
User findByIdWithOrders(@Param("id") Long id);
```

## Batch Fetching
```properties
spring.jpa.properties.hibernate.default_batch_fetch_size=16
spring.jpa.properties.hibernate.jdbc.batch_size=30
```

## Read-Only Queries
```properties
spring.jpa.properties.hibernate.query.read_only=true
@Transactional(readOnly = true) // on service methods
```

## Pagination Best Practices
- Always `Page<T>` for listing, never `List<T>` on large tables
- Use `Slice<T>` instead of `Page<T>` when total count is unnecessary
- Use `countQuery` in native `@Query` for accurate pagination

## Caching
```java
// Second-level cache with Hibernate
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
@Entity
public class LookupTable { ... }
```

## Avoid
- `SELECT DISTINCT` with large result sets
- Fetching entire entity when only a few columns needed (use projections)
- `@OneToMany(fetch = FetchType.EAGER)` – almost never what you want
