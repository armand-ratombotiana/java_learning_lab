# Visual Guide — Reactive Programming (Lab 36)

## Reactive Streams Publisher → Subscriber Flow

```
   ┌────────────┐        request(n)        ┌────────────┐
   │ Publisher  │ ◄─────────────────────── │ Subscriber  │
   │ (source)   │                          │             │
   └──────┬─────┘                          └─────────────┘
          │
          │  onNext(item1)  ───────────────► onNext(T item)
          │  onNext(item2)  ───────────────► onNext(T item)
          │  onNext(item3)  ───────────────► onNext(T item)
          │  ...
          │  onComplete()   ───────────────► onComplete()
          │     OR
          │  onError(e)     ───────────────► onError(Throwable)
          ▼
```

- **Backpressure**: Subscriber controls data flow via `request(n)`. Publisher never emits more than `n` items without a new request.
- **Subscription** ties the two: created when `subscribe()` is called.
- Reactive Streams spec: 4 interfaces — `Publisher`, `Subscriber`, `Subscription`, `Processor`.
- Java 9+ provides `java.util.concurrent.Flow` with these interfaces.

## Operators Chain (Project Reactor / RxJava)

```
  Flux.just("user1", "user2", "user3")    // Source
      │
      ├── .map(String::toUpperCase)        // Sync transform
      │    → "USER1", "USER2", "USER3"
      │
      ├── .filter(s -> s.startsWith("U"))  // Predicate
      │    → "USER1", "USER2"
      │
      ├── .flatMap(this::fetchDetails)     // Async: 1→N
      │    → Flux<Details>
      │
      ├── .timeout(Duration.ofSeconds(5))  // Error if slow
      │
      └── .subscribe(                      // Terminal
              data  → log("Got: {}", data),
              error → log("Failed", error),
              ()    → log("Done")
          )
```

- **Assembly-time**: operators build a chain of `Flux`/`Mono` objects (nothing happens yet).
- **Subscription-time**: the chain materializes and data flows.
- **Schedulers** control threading: `.publishOn(Schedulers.boundedElastic())` moves work to another thread pool.
