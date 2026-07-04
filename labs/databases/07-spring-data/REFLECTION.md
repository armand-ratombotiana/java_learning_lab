# Reflection: Spring Data JPA

## Key Takeaways
- Spring Data JPA eliminates DAO implementation boilerplate while keeping full SQL/JPQL control when needed
- The repository abstraction is powerful but requires understanding of the underlying JPA/hibernate behavior
- N+1 queries are the #1 performance pitfall – always validate generated SQL
- Specifications provide a clean pattern for dynamic queries that would otherwise require multiple repository methods

## When to Use Spring Data JPA
- CRUD-heavy applications with well-defined entities
- Projects already using Spring Boot / Spring Framework
- Teams wanting consistent data access patterns

## When to Consider Alternatives
- Complex reporting queries (consider JOOQ or MyBatis)
- Event sourcing / CQRS architectures
- Very high-throughput systems needing raw JDBC performance
