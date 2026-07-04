# Reflection: Multi-Model & Polyglot

## Key Takeaways
- No single database is optimal for all workloads
- Polyglot persistence lets you choose the right tool per data type
- Multi-model databases reduce operational complexity at the cost of specialization
- Consistency across heterogeneous databases is hard – use outbox + CDC or Saga
- Start simple (single DB), add specialized databases only when justified
- Each new database adds operational burden – not just code, but backup, monitoring, scaling

## When to Use Polyglot Persistence
- Different data types have fundamentally different access patterns
- Performance requirements demand specialized engines
- Team size justifies dedicated database expertise
- Each bounded context in a microservice can own its database

## When to Stay Single-Model
- Simple CRUD with one data type
- Small team with limited ops capacity
- Moderate performance requirements
- Strong cross-entity consistency needed everywhere
