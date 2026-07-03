# Module 30: Reactive Programming (Project Reactor) - Quizzes

---

## Q1: Mono vs Flux
In Project Reactor, what is the primary difference between a `Mono` and a `Flux`?

A) A `Mono` runs asynchronously, while a `Flux` runs synchronously.
B) A `Mono` emits 0 or 1 element, while a `Flux` emits 0 to N elements.
C) A `Mono` handles database transactions, while a `Flux` handles HTTP requests.
D) There is no difference; they are just aliases.

**Answer**: B
**Explanation**: `Mono<T>` is tailored for operations that return a single value or empty result (like finding a user by ID), whereas `Flux<T>` is for collections or streams of multiple values.

---

## Q2: The "Nothing Happens Until You Subscribe" Rule
Why does the following code NOT insert a user into the database?
```java
userRepository.save(new User("Alice"));
```

A) It throws a compile-time error.
B) Reactive publishers are lazy; nothing happens until a `Subscriber` actively subscribes to them.
C) You need to call `commit()` first.
D) The database is locked.

**Answer**: B
**Explanation**: Reactive streams follow a lazy execution model. The pipeline merely defines the blueprint of operations. It requires a `subscribe()` call to initiate data flow and execution.

---

## Q3: Schedulers
Which method is used to offload blocking, legacy IO work to a separate, dedicated thread pool so it does not starve the reactive event loop?

A) `publishOn(Schedulers.parallel())`
B) `subscribeOn(Schedulers.boundedElastic())`
C) `executeOn(Schedulers.immediate())`
D) `Mono.block()`

**Answer**: B
**Explanation**: `Schedulers.boundedElastic()` creates a thread pool that dynamically grows to handle blocking IO tasks without overwhelming the main non-blocking CPU cores. `subscribeOn` applies this scheduler to the source execution.