# Performance: R2DBC

## Connection Pool Tuning
```properties
spring.r2dbc.pool.initial-size=10
spring.r2dbc.pool.max-size=30
spring.r2dbc.pool.max-idle-time=30m
spring.r2dbc.pool.validation-query=SELECT 1
```

## Backpressure Tuning
```java
// Limit fetch size to control memory
repository.findAll()
    .limitRate(100)     // request 100 at a time
    .buffer(50)         // batch into lists of 50
    .concatMap(batch -> processBatch(batch));
```

## Avoid Blocking Operations
- Never call `.block()` on a reactive stream in a WebFlux handler
- Use `Schedulers.boundedElastic()` for blocking I/O wrappers
- Keep computation on the event loop (`Schedulers.parallel()` for CPU work)

## Batch Operations
```java
// Use Flux for batch inserts
Flux.fromIterable(users)
    .flatMap(user -> repository.save(user))
    .then();
```

## Comparison: JDBC vs R2DBC

| Metric | JDBC | R2DBC |
|---|---|---|
| Thread usage | 1 thread per connection | Shared event loop |
| Max throughput (100 conn) | ~500 req/s | ~2000 req/s |
| Memory per connection | ~1MB thread stack | ~100 bytes |
| P99 latency under load | Degrades sharply | Stable |
