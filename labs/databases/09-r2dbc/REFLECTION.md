# Reflection: R2DBC

## Key Takeaways
- R2DBC brings relational databases into the reactive programming model
- Not an ORM – simpler but also lower-level than JPA
- True end-to-end non-blocking requires the entire stack to be reactive
- Backpressure is a powerful tool for controlling data flow
- Migration from JPA to R2DBC is not a drop-in replacement; significant API changes are required

## When to Use R2DBC
- Building new reactive microservices with WebFlux
- Applications requiring high concurrency with limited threads
- Streaming database results to clients (SSE, WebSocket)
- IoT / real-time systems

## When to Stay with JDBC
- Existing servlet-based applications
- Complex ORM features needed
- Team unfamiliar with reactive programming
- Mixed stack (reactive HTTP but blocking I/O elsewhere)
