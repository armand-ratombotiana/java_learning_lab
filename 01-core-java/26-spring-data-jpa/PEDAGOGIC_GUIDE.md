# Pedagogic Guide: Spring Data JPA

## Learning Path

### Phase 1: Entity Mapping & Repositories (Beginner)
- **Objective**: Map domain models to database tables and perform basic CRUD.
- **Concepts**: `@Entity`, `@Id`, `@GeneratedValue`, `CrudRepository`, `JpaRepository`.
- **Validation**: Can successfully save, retrieve, update, and delete single entities.

### Phase 2: Queries & Pagination (Intermediate)
- **Objective**: Retrieve specific data efficiently.
- **Concepts**: Query method derivation (`findBy...`), `@Query` (JPQL & Native), `Pageable`, `Sort`.
- **Validation**: Can implement complex searches and paginate results.

### Phase 3: Relationships & Transactions (Advanced)
- **Objective**: Manage complex entity associations and ensure data integrity.
- **Concepts**: `@OneToMany`, `@ManyToOne`, `@ManyToMany`, `CascadeType`, `FetchType`, `@Transactional`.
- **Validation**: Can navigate entity graphs, avoid N+1 select problems, and manage transactional boundaries.

### Phase 4: Projections & Auditing (Expert)
- **Objective**: Optimize data retrieval and track entity changes automatically.
- **Concepts**: Interface Projections, DTOs, `@CreatedDate`, `@LastModifiedDate`, `@EnableJpaAuditing`.
- **Validation**: Can retrieve partial entity data and automatically track creation/modification timestamps.

## Key Concepts Matrix
| Concept | Implementation Focus | Common Pitfalls |
|---------|---------------------|-----------------|
| **Entities** | Map objects to rows via `@Entity` and `@Table` | Forgetting no-args constructor, incorrect ID generation |
| **Relationships** | `@ManyToOne` (owning side) vs `@OneToMany` (mappedBy) | Infinite recursion in `toString()`, N+1 queries |
| **Query Methods** | Derived names vs explicit `@Query` | Unduly long method names for complex queries |
| **Transactions** | Boundary demarcation with `@Transactional` | Self-invocation bypassing transactional proxies |

## Next Steps
- [27-spring-rest-api](../27-spring-rest-api) - Exposing JPA entities via REST
- [EXERCISES.md](./EXERCISES.md) - Practical assignments