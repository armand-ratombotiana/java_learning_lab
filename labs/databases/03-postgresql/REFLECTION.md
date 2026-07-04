# Reflection: PostgreSQL

## Key Takeaways

- PostgreSQL is the world's most advanced open-source relational database
- MVCC architecture provides excellent concurrency without read/write conflicts
- Rich indexing support enables query optimization for varied access patterns
- JSONB and full-text search bridge relational and document database capabilities
- WAL is the foundation of durability, replication, and point-in-time recovery
- Connection pooling (HikariCP) is essential for production Java applications
- Autovacuum requires tuning for high-update workloads to prevent bloat

## Common Pitfalls Encountered

1. Not matching Hibernate/Spring dialect to the PostgreSQL version
2. Ignoring autovacuum configuration leading to table bloat
3. Using `GenerationType.TABLE` instead of `SEQUENCE` or `IDENTITY`
4. Missing GIN indexes on JSONB columns
5. Not using `CONCURRENTLY` for index creation on production tables
6. Inadequate `shared_buffers` and `work_mem` configuration

## Questions for Further Study

- How does PostgreSQL's parallel query execution work across multiple CPUs?
- What are the tradeoffs between logical and physical replication?
- How does the query optimizer handle correlated subqueries?
- When should you choose BRIN over B-tree indexes for time-series data?
- How does PostgreSQL's foreign data wrapper (FDW) compare to database links?

## Application to Java/Spring Projects

```yaml
# Production PostgreSQL configuration for Spring Boot
spring:
  datasource:
    url: jdbc:postgresql://primary:5432/db?targetServerType=primary
    hikari:
      maximum-pool-size: 20
      minimum-idle: 5
      connection-timeout: 10000
      idle-timeout: 300000
      max-lifetime: 1800000
      leak-detection-threshold: 60000
  jpa:
    properties:
      hibernate:
        jdbc.batch_size: 50
        order_inserts: true
        order_updates: true
        query.in_clause_parameter_padding: true
```
