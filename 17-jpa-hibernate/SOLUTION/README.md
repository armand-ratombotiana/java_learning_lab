# JPA/Hibernate Solution

Reference implementation for JPA entities, repositories, and JPQL.

## Entities
- `@Entity`, `@Table`, `@Id`, `@GeneratedValue`
- `@Column`, `@Version` for optimistic locking
- One-to-One, One-to-Many, Many-to-Many relationships

## Repository
- Custom repository interfaces
- `findByEmail`, `findByNameContaining`
- `InMemoryUserRepository` implementation

## JPQL & Criteria API
- `JpQLQueryBuilder` for query construction
- `CriteriaBuilder` for type-safe queries
- Parameter binding support

## Pagination
- `PagedResult<T>` for paginated queries
- `hasNext()` for navigation

## Test Coverage: 40+ tests