# Why Multi-Model Databases & Polyglot Persistence Exist

Modern applications handle diverse data types that don't fit a single model:

- **Transactional data** (orders, payments): Needs ACID, relational integrity
- **Content** (CMS, products): Needs flexible schema, nested documents
- **Sessions**: Needs fast key-based access with TTL expiration
- **Relationships** (social, recommendations): Needs graph traversal
- **Analytics**: Needs columnar storage for aggregation
- **Vectors**: Needs similarity search for ML embeddings

No single database excels at all these workloads. Polyglot persistence lets developers choose the right tool per job. Multi-model databases reduce operational complexity by consolidating multiple models under one engine.
