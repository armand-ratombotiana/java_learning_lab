# Module 30: Reactive Programming (Project Reactor) - Interview Preparation

---

## 📝 Conceptual Questions

### Q1: Explain Backpressure in the context of Reactive Streams.
**Answer**:
Backpressure is a feedback mechanism that allows a Subscriber to tell a Publisher how much data it is ready to process. 
In a traditional push model, a fast producer can overwhelm a slow consumer, leading to `OutOfMemoryError`s as the consumer buffers unprocessed messages. In Reactive Streams, the Subscriber uses the `Subscription.request(n)` method to explicitly pull `n` items at a time from the Publisher. This ensures the producer only emits data when the consumer has explicitly stated it can handle it, preventing memory exhaustion and creating a robust, flow-controlled pipeline.

### Q2: What happens if you block the main thread (event loop) in a WebFlux application?
**Answer**:
Spring WebFlux runs on an event-loop model (typically powered by Netty) which uses a very small number of worker threads (usually equivalent to the number of CPU cores). 
If you perform a blocking operation (like `Thread.sleep()`, a synchronous JDBC call, or `RestTemplate.getForObject()`) on one of these event-loop threads, the thread halts and cannot process any other incoming requests. If all event-loop threads get blocked, the entire application will hang and stop accepting new traffic, resulting in a complete Denial of Service.
Blocking calls must be completely eliminated, or carefully offloaded to a separate, dedicated thread pool using `Schedulers.boundedElastic()`.

### Q3: What is the difference between `map` and `flatMap` in Project Reactor?
**Answer**:
- `map(Function<T, U>)`: Used for synchronous, non-blocking, 1-to-1 transformations. It takes an item `T`, transforms it to `U`, and directly emits `U`.
- `flatMap(Function<T, Publisher<V>>)`: Used for asynchronous, non-blocking transformations. It takes an item `T` and transforms it into a *new Publisher* (like a `Mono` or `Flux`). Reactor subscribes to these inner publishers and merges (flattens) their emitted items back into the main stream. You must use `flatMap` whenever your transformation logic involves calling another reactive method (like a database call returning a `Mono`).

---

## 💻 Whiteboarding Scenarios

### Scenario 1: Reactive Error Handling
**Problem**: You have a method `Mono<User> findUser(String id)`. If the user is not found, it emits a `UserNotFoundException`. Write a reactive pipeline that attempts to find the user, but if the exception is thrown, it falls back to returning a default "Guest" user instead of failing the stream.

**Solution**:
Use the `onErrorResume` or `onErrorReturn` operators.

```java
public Mono<User> getUserOrDefault(String id) {
    return userRepository.findById(id)
        .onErrorReturn(UserNotFoundException.class, new User("Guest"));
        
    // Alternatively, if the fallback requires another reactive call:
    // .onErrorResume(UserNotFoundException.class, e -> guestUserRepository.getDefaultUser());
}
```

### Scenario 2: Zipping Multiple Monos
**Problem**: You are building an API endpoint that needs to fetch a `User` profile from `userService.getUser(id)` and their `OrderHistory` from `orderService.getHistory(id)`. Both methods return a `Mono`. Since these two calls don't depend on each other, you want to fetch them simultaneously (concurrently) and combine them into a single `UserProfileResponse`. Write the code.

**Solution**:
Use `Mono.zip()` to execute multiple Monos concurrently and combine their results.

```java
public Mono<UserProfileResponse> getUserProfile(String id) {
    Mono<User> userMono = userService.getUser(id);
    Mono<OrderHistory> historyMono = orderService.getHistory(id);

    return Mono.zip(userMono, historyMono, (user, history) -> {
        // This lambda is called only when BOTH Monos have completed successfully
        return new UserProfileResponse(user.getName(), history.getOrders());
    });
}
```