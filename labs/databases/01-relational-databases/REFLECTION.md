# Reflection: Relational Databases

## Key Takeaways
- The relational model separates logical structure from physical storage
- ACID properties are essential for data integrity
- Normalization reduces redundancy but must be balanced with performance
- JPA bridges OOP and relational paradigms, but understanding SQL is crucial

## Questions to Consider
1. When would you choose NoSQL over a relational database?
2. How does ORM mapping complexity scale with schema size?
3. What are the tradeoffs of managed vs self-hosted databases?

## Real-World Application
- Enterprise SaaS platforms: PostgreSQL + Spring Data JPA
- Financial systems: ACID transactions are non-negotiable
- Reporting systems: Heavily normalized schemas with denormalized views

## Gaps in Knowledge
- [ ] Query plan analysis (EXPLAIN ANALYZE)
- [ ] Advanced indexing (partial, expression, covering)
- [ ] Sharding and distributed transactions
- [ ] Compressed storage (columnar formats)
