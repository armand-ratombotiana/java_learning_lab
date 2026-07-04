# Interview: Multi-Model & Polyglot

## Common Questions

**Q:** When would you use MongoDB over PostgreSQL?
**A:** When the data has a flexible schema, deeply nested structures, or is frequently changing shape (CMS, product catalogs, event logs). Also when rapid prototyping with schema-less data is needed. Use PostgreSQL when ACID compliance, referential integrity, and complex joins are required.

**Q:** How do you handle transactions across PostgreSQL and MongoDB?
**A:** You can't use traditional distributed transactions. Use the Saga pattern: sequence of local transactions with compensating actions on failure. Or use the outbox pattern: write to PostgreSQL, use CDC (Debezium) to propagate changes to MongoDB asynchronously.

**Q:** Explain the outbox pattern.
**A:** Instead of dual-writing to multiple databases, write an event to an "outbox" table within the same ACID transaction as your primary write. A separate process (CDC or scheduled) reads the outbox and publishes events to other databases. Ensures at-least-once delivery without distributed transactions.

**Q:** What factors should guide polyglot persistence decisions?
**A:** 1) Data access pattern (OLTP vs analytical). 2) Consistency requirements (strong vs eventual). 3) Schema rigidity. 4) Relationship complexity. 5) Query patterns (lookup, search, graph traversal). 6) Team expertise. 7) Operational overhead.

**Q:** Compare ArangoDB/Cosmos DB with using separate specialized databases.
**A:** Multi-model: less operational overhead, cross-model consistency, single query language. Specialized: best-in-class performance per model, independent scaling, ability to use best tool for each job. Choose multi-model for simpler architecture with moderate requirements; choose polyglot for high-performance, specialized workloads.
