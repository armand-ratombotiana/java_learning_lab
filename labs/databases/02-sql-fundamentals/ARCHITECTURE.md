# Architecture: SQL in Modern Applications

## Layered Query Architecture

```
┌──────────────────────────────────────────────────┐
│                 Presentation Layer                │
│            (REST API / GraphQL / gRPC)            │
└──────────────┬───────────────────────────────────┘
               │ Request
               ▼
┌──────────────────────────────────────────────────┐
│              Service Layer (Business Logic)       │
│         Transaction management, validation        │
└──────────────┬───────────────────────────────────┘
               │ Query
               ▼
┌──────────────────────────────────────────────────┐
│              Data Access Layer                    │
│       Repository / DAO / JDBC Template           │
└──────────────┬───────────────────────────────────┘
               │ SQL
               ▼
┌──────────────────────────────────────────────────┐
│                 Database                          │
│           Query planner, executor                 │
└──────────────────────────────────────────────────┘
```

## Repository Pattern (Spring Data JPA)

```java
public interface EmployeeRepository extends JpaRepository<Employee, Long>,
        JpaSpecificationExecutor<Employee> {

    // Derived query
    List<Employee> findByDepartmentName(String deptName);

    // JPQL
    @Query("SELECT e FROM Employee e WHERE e.salary > :min")
    List<Employee> findHighEarners(@Param("min") BigDecimal min);

    // Specification (dynamic queries)
    static Specification<Employee> hasDepartment(Long deptId) {
        return (root, query, cb) ->
            deptId == null ? null : cb.equal(root.get("department").get("id"), deptId);
    }
}
```

## CQRS with SQL

```
┌──────────────────┐     ┌──────────────────┐
│  Command Side     │     │  Query Side       │
│  (INSERT/UPDATE)  │     │  (SELECT)         │
│  Uses JPA/ORM     │     │  Uses JDBC/SQL    │
└──────────────────┘     └──────────────────┘
        │                        │
        ▼                        ▼
┌───────────────────────────────────────────┐
│              PostgreSQL                     │
│  Tables (normalized)          Views (denorm)│
│  + Materialized Views for read models      │
└───────────────────────────────────────────┘
```

## SQL and Event Sourcing (Outbox Pattern)
```java
@Transactional
public void createOrder(OrderCommand cmd) {
    Order order = new Order(cmd);
    em.persist(order);

    OutboxEvent event = new OutboxEvent();
    event.setAggregateType("Order");
    event.setAggregateId(order.getId().toString());
    event.setEventType("OrderCreated");
    event.setPayload(objectMapper.writeValueAsString(order));
    em.persist(event);
}
```
