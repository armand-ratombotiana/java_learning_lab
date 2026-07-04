# Architecture: R2DBC in Reactive Applications

```
┌────────────────────────────────────────────┐
│         Reactive Web Layer                 │
│   WebFlux Controller / Router Function     │
│   @RequestMapping → Mono<ResponseEntity>   │
└──────────────────┬─────────────────────────┘
                   │ Flux / Mono
┌──────────────────▼─────────────────────────┐
│         Reactive Service Layer              │
│   @Service, @Transactional                 │
│   Flux<User>, Mono<Order>                  │
│   .map, .flatMap, .filter                  │
└──────────────────┬─────────────────────────┘
                   │ Spring Data R2DBC
┌──────────────────▼─────────────────────────┐
│         Reactive Repository Layer          │
│   ReactiveCrudRepository, R2dbcRepository  │
│   DatabaseClient (for custom queries)      │
│   @Query (if using Spring Data R2DBC 2.x)  │
└──────────────────┬─────────────────────────┘
                   │ R2DBC SPI
┌──────────────────▼─────────────────────────┐
│         R2DBC Driver Layer                 │
│   ConnectionFactory → ConnectionPool       │
│   Reactor Netty / NIO event loop           │
└──────────────────┬─────────────────────────┘
                   │ TCP
┌──────────────────▼─────────────────────────┐
│               Database                      │
└────────────────────────────────────────────┘
```

## When to Use R2DBC
- Full reactive stack: WebFlux + R2DBC
- High-concurrency applications
- Stream processing of database data
- IoT / real-time data pipelines

## When to Stay with JDBC
- Servlet-based applications (Spring MVC)
- Existing JPA/Hibernate investment
- Complex reporting with JPA criteria
- Blocking I/O elsewhere in the stack (no benefit)
