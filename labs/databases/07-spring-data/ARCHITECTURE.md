# Architecture: Spring Data JPA in Layered Applications

```
┌─────────────────────────────────────────────┐
│           Presentation Layer                │
│     REST Controllers / GraphQL / MVC         │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│             Service Layer                    │
│    @Service with @Transactional boundaries   │
│    Business logic, validation, orchestration │
└──────────────────┬──────────────────────────┘
                   │
┌──────────────────▼──────────────────────────┐
│            Repository Layer                  │
│    JpaRepository<Entity, ID> interfaces      │
│    @Query, Specifications, EntityGraph       │
└──────────────────┬──────────────────────────┘
                   │ Spring Data JPA
┌──────────────────▼──────────────────────────┐
│           Data Access Layer                  │
│    EntityManager / JPA / Hibernate           │
│    Connection pooling (HikariCP)             │
└──────────────────┬──────────────────────────┘
                   │ JDBC
┌──────────────────▼──────────────────────────┐
│               Database                       │
└─────────────────────────────────────────────┘
```

## Considerations
- Repositories should not leak `EntityManager` or JPA specifics to services
- Use DTO projections or `MapStruct` to decouple entities from API layer
- Keep repository interfaces focused; avoid "god repositories"
- Compose specifications in the service layer, not the repository
