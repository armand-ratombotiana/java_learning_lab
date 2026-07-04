# Theory: Spring Data JPA

## Repository Abstraction Pattern
- **Generic DAO pattern** – type-safe, interface-based data access
- **Proxy-based implementation** – Spring creates proxy instances at runtime
- **Template method pattern** – `SimpleJpaRepository` provides default implementations

## Query Derivation
Method name parsing grammar:
```
find[First|TopN][Distinct]By[Property][Comparator][And|Or]...
```

Supported comparators: `Equals`, `Is`, `Between`, `LessThan`, `GreaterThan`, `Like`, `StartingWith`, `EndingWith`, `Containing`, `In`, `IsNull`, `IsNotNull`, `Before`, `After`

## Specification Pattern
- Based on JPA Criteria API
- `Specification<T>` is a functional interface with `toPredicate(Root<T>, CriteriaQuery, CriteriaBuilder)`
- Compose with `and()`, `or()`, `not()`

## Auditing
Spring Data integrates with JPA's `@PrePersist`/`@PreUpdate` lifecycle callbacks via `AuditingEntityListener`. Requires `@EnableJpaAuditing`.

## Transaction Management
- `@Transactional` on repository methods (read-only queries marked as such)
- Default propagation: REQUIRED
- Rollback on `RuntimeException`, not checked exceptions
