# Architecture: Relational Database Integration Patterns

## Multi-Tier Architecture

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│   Frontend    │────▶│   API Layer   │────▶│  Application │
│  (React/Web)  │     │  (Spring MVC) │     │  Service     │
└──────────────┘     └──────────────┘     └──────────────┘
                                                 │
                                                 ▼
                                          ┌──────────────┐
                                          │  Repository   │
                                          │  (JPA/JDBC)   │
                                          └──────────────┘
                                                 │
                                                 ▼
                                          ┌──────────────┐
                                          │   Database    │
                                          │  (PostgreSQL) │
                                          └──────────────┘
```

## Repository Pattern (Spring Data JPA)

```java
public interface OrderRepository extends JpaRepository<Order, Long>,
        JpaSpecificationExecutor<Order> {

    // Query methods
    List<Order> findByCustomerId(Long customerId);

    // Pagination
    Page<Order> findByStatus(String status, Pageable pageable);

    // Custom JPQL
    @Query("SELECT o FROM Order o WHERE o.total > :minTotal")
    List<Order> findExpensiveOrders(@Param("minTotal") BigDecimal minTotal);
}
```

## CQRS Pattern (Read/Write Separation)

```
┌──────────┐    Write (INSERT/UPDATE/DELETE)    ┌──────────┐
│  Command  │──────────────────────────────────▶│ Primary   │
│  Service  │                                    │ DB        │
└──────────┘                                    └──────────┘
                                                       │
                                                       │ Sync
                                                       ▼
┌──────────┐    Read (SELECT)                  ┌──────────┐
│  Query    │◀──────────────────────────────────│  Read     │
│  Service  │                                   │  Replica  │
└──────────┘                                   └──────────┘
```

## Multi-Tenant Architectures

| Approach | Pros | Cons |
|---|---|---|
| Separate Database | Strong isolation | Higher cost |
| Separate Schema | Good isolation | Complex migrations |
| Shared Table (tenant_id) | Simple | Leaky isolation |
| Row-Level Security | Automatic | DB-specific |

## Event-Driven Integration (Outbox Pattern)

```java
@Entity
public class OutboxEvent {
    @Id @GeneratedValue
    private Long id;
    private String aggregateType;
    private String aggregateId;
    private String eventType;
    @Lob
    private String payload;
    private Boolean published = false;
}
```
