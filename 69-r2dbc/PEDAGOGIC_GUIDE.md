# Pedagogic Guide - R2DBC

## Learning Path

### Phase 1: Fundamentals
1. Understand reactive programming
2. R2DBC core concepts
3. Spring Data R2DBC basics

### Phase 2: Intermediate
1. Repository definition
2. Custom queries
3. Transaction management

### Phase 3: Advanced
1. Connection pooling
2. Performance optimization
3. Custom driver implementation

## Key Concepts

| Concept | Description |
|---------|-------------|
| R2dbcRepository | Reactive repository |
| Mono | Single result |
| Flux | Multiple results |
| DatabaseClient | Low-level API |

## Comparison with JDBC
- R2DBC: Non-blocking, reactive
- JDBC: Blocking, synchronous

## Best Practices
- Use connection pool
- Handle backpressure
- Properly dispose subscriptions