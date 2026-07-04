# Common Mistakes: R2DBC

## Blocking in Reactive Pipeline
```java
// BAD: block() inside a reactive chain
return client.sql("SELECT * FROM users")
    .fetch()
    .all()
    .map(row -> someBlockingCall(row.get("id"))); // blocks event loop!

// GOOD: offload blocking call
.map(row -> Mono.fromCallable(() -> someBlockingCall(row.get("id")))
    .subscribeOn(Schedulers.boundedElastic()));
```

## Ignoring Backpressure
```java
// BAD: requesting unbounded
client.sql("SELECT * FROM huge_table").fetch().all()
    .subscribe(System.out::println); // no backpressure

// GOOD: explicit request or use operators like limitRate
client.sql("SELECT * FROM huge_table").fetch().all()
    .limitRate(100)
    .subscribe(System.out::println);
```

## Mixing JDBC and R2DBC in Same Transaction
Not supported. JDBC and R2DBC use different transaction managers and connection pools. Keep them separate.

## Missing @Transactional in reactive service
```java
// BAD: no transactional boundary
Flux<User> getAll() { return repository.findAll(); }
// If a streaming error occurs mid-stream, no rollback possible

// GOOD: use reactive @Transactional
@Transactional
Flux<User> getAllTransactional() { return repository.findAll(); }
```

## Ignoring Connection Leaks
Always ensure connections are released via reactive pipelines. R2DBC connection pools rely on `close()` being called via `Publisher<Void>`.
