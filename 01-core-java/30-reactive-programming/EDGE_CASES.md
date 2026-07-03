# Module 30: Reactive Programming (Project Reactor) - Edge Cases & Pitfalls

---

## Pitfall 1: Blocking in a Reactive Stream

### ❌ Wrong
Calling a blocking API (like a standard JDBC call, `Thread.sleep()`, or a blocking HTTP call) inside a reactive operator. Since reactive applications run on a small number of threads (often matching CPU cores), blocking one thread can starve the entire application.
```java
Flux.just(1, 2, 3)
    .map(id -> blockingJdbcRepository.findById(id)) // ❌ Starves Event Loop threads
    .subscribe();
```

### ✅ Correct
Always use reactive, non-blocking APIs (like R2DBC for databases, or `WebClient` for HTTP). If you *must* use a blocking API, offload it to a dedicated scheduler using `subscribeOn(Schedulers.boundedElastic())`.
```java
Flux.just(1, 2, 3)
    .flatMap(id -> Mono.fromCallable(() -> blockingJdbcRepository.findById(id))
                       .subscribeOn(Schedulers.boundedElastic())) // ✅ Offloads blocking work safely
    .subscribe();
```

---

## Pitfall 2: Forgetting to Subscribe

### ❌ Wrong
Declaring a reactive pipeline and expecting it to execute automatically. Reactive streams are lazy!
```java
Mono<User> userMono = userRepository.save(new User("John"));
// Nothing happens here. The database is never touched!
```

### ✅ Correct
You must `subscribe()` to trigger the flow (or return the `Publisher` to a framework like Spring WebFlux, which subscribes for you).
```java
userRepository.save(new User("John"))
              .subscribe(user -> System.out.println("Saved: " + user.getName()));
```

---

## Pitfall 3: Swallowing Errors

### ❌ Wrong
Not handling errors at the end of the pipeline. If an unhandled error occurs, the subscription is terminated, and the error might be swallowed silently.
```java
Flux.just(1, 0, 2)
    .map(i -> 10 / i)
    .subscribe(System.out::println); // Will print 10, then crash and swallow the error internally.
```

### ✅ Correct
Provide an error consumer in the `subscribe()` method or use operators like `onErrorResume` or `onErrorReturn`.
```java
Flux.just(1, 0, 2)
    .map(i -> 10 / i)
    .subscribe(
        System.out::println, // Data consumer
        err -> System.err.println("Error occurred: " + err.getMessage()) // Error consumer
    );
```