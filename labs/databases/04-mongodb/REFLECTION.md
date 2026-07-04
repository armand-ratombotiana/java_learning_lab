# Reflection: MongoDB

## Key Takeaways

- MongoDB's document model eliminates the object-relational impedance mismatch
- Schema flexibility enables rapid development but requires discipline at scale
- The aggregation pipeline is a powerful alternative to SQL for data processing
- Sharding enables theoretically unlimited horizontal scalability
- ACID transactions make MongoDB viable for financial workloads
- Change streams enable event-driven architectures
- Proper indexing is critical — unindexed queries impact performance at any scale

## Common Pitfalls

1. Treating MongoDB like a relational database (excessive $lookup, normalized schemas)
2. Unbounded array growth leading to document size limits
3. Wrong shard key causing uneven data distribution
4. Missing write concern leading to data loss on failover
5. Not planning for schema migration strategy

## Questions for Further Study

- When does MongoDB's document model break down for complex relational data?
- How does Atlas search compare to dedicated search engines like Elasticsearch?
- What are the practical limits of multi-document transactions at scale?
- How does MongoDB's consistency model compare to CockroachDB or Spanner?

## Application to Java Projects

```yaml
# Spring Boot + MongoDB configuration
spring:
  data:
    mongodb:
      uri: mongodb+srv://user:pass@cluster.mongodb.net/db
      auto-index-creation: true
      uuid-representation: standard
logging:
  level:
    org.springframework.data.mongodb: DEBUG
    org.mongodb.driver: WARN
```
