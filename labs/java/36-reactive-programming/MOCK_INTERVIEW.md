# Mock Interview Transcript: Reactive Programming

## Interviewer: Staff Engineer, Netflix
## Candidate: Senior Java developer
## Time: 45 minutes
## Focus: Project Reactor, WebFlux, backpressure, testing

---

**Q1: Explain the Reactive Streams specification. What are the four interfaces?**

**Candidate**: The Reactive Streams spec (org.reactivestreams) defines four interfaces:
1. `Publisher<T>` — emits items to subscribers. Has one method: `subscribe(Subscriber)`.
2. `Subscriber<T>` — receives items. Methods: `onSubscribe(Subscription)`, `onNext(T)`, `onError(Throwable)`, `onComplete()`.
3. `Subscription` — links Publisher and Subscriber. Methods: `request(long n)` (demand), `cancel()`.
4. `Processor<T,R>` — both Publisher and Subscriber.
Backpressure is enforced through `request(n)` — the subscriber controls the flow rate.

**Interviewer**: Write a reactive endpoint using WebFlux that fetches user data.

**Candidate**: 
```java
@RestController
class UserController {
    
    @GetMapping("/users/{id}")
    Mono<User> getUser(@PathVariable Long id) {
        return userService.findById(id)
            .switchIfEmpty(Mono.error(new UserNotFoundException(id)))
            .timeout(Duration.ofSeconds(5))
            .doOnError(e -> log.error("Error fetching user {}", id, e));
    }
    
    @GetMapping("/users")
    Flux<User> listUsers(@RequestParam(defaultValue = "0") int page,
                         @RequestParam(defaultValue = "20") int size) {
        return userService.findAll(page, size)
            .delaySequence(Duration.ofMillis(100));  // Throttle for backpressure demo
    }
}
```

**Interviewer**: How does `publishOn` vs `subscribeOn` work?

**Candidate**: `subscribeOn` affects the subscription process (the source). It changes the thread where the Publisher's `subscribe()` call happens. `publishOn` affects downstream operators — it moves subsequent operations to a different Scheduler. Multiple `publishOn` calls switch threads multiple times. `subscribeOn` only matters once (first one wins for subscription).

**Interviewer**: Write a reactive retry with exponential backoff.

**Candidate**: 
```java
Flux<Data> fetchWithRetry() {
    return externalService.getData()
        .timeout(Duration.ofSeconds(2))
        .retryWhen(Retry.backoff(3, Duration.ofMillis(100))
            .maxBackoff(Duration.ofSeconds(2))
            .jitter(0.5)
            .onRetryExhaustedThrow((spec, signal) -> 
                new RuntimeException("Retries exhausted", signal.failure())))
        .onErrorResume(e -> Flux.just(fallbackData()));
}
```

**Interviewer**: How do you test reactive streams?

**Candidate**: Using `StepVerifier`:
```java
@Test
void shouldEmitUsers() {
    StepVerifier.create(userService.findAll(0, 10))
        .expectNextMatches(u -> u.name() != null)
        .expectNextCount(9)  // remaining 9 items
        .expectComplete()
        .verify(Duration.ofSeconds(5));
}

@Test
void shouldHandleError() {
    StepVerifier.create(userService.findById(-1L))
        .expectErrorSatisfies(e -> 
            assertThat(e).isInstanceOf(UserNotFoundException.class))
        .verify();
}

@Test
void shouldHandleBackpressure() {
    StepVerifier.create(userService.findAll(0, 100), 0)  // Initial demand 0
        .thenAwait(Duration.ofMillis(100))
        .thenRequest(10)  // Now request 10
        .expectNextCount(10)
        .thenRequest(90)
        .expectNextCount(90)
        .expectComplete()
        .verify();
}
```

**Interviewer**: How does Reactor's `Hooks.onOperatorDebug()` help?

**Candidate**: In production, reactive stack traces are unhelpful — they show only the handler and Scheduler. `Hooks.onOperatorDebug()` records the assembly trace (where the Flux/Mono was created) for each operator. This makes debugging much easier but has a performance cost (it captures stack traces). Only enable in development or production debugging sessions.

**Interviewer**: Explain the difference between `flatMap`, `concatMap`, and `switchMap`.

**Candidate**: `flatMap` — subscribes eagerly to all inner publishers (unordered merging). `concatMap` — subscribes to inner publishers sequentially (preserves order). `switchMap` — subscribes to the latest inner publisher and cancels previous ones. Use `flatMap` for parallel operations where order doesn't matter. Use `concatMap` for ordered sequential operations. Use `switchMap` for search-as-you-type (cancel previous search, use latest).

**Interviewer**: Final: When would you NOT use reactive programming?

**Candidate**: (1) Simple CRUD apps — the complexity isn't justified. (2) When your database doesn't have a reactive driver. (3) When your team isn't experienced with reactive — debugging is harder. (4) For CPU-bound workloads — virtual threads are a better fit. (5) When you need transaction management across multiple resources. (6) When you need to use ThreadLocal-based frameworks (many Java EE frameworks).

---

## Feedback

**Strengths**:
- Complete Reactive Streams spec knowledge
- Proficient WebFlux and StepVerifier usage
- Clear publishOn/subscribeOn distinction
- Correct flatMap/concatMap/switchMap use cases

**Areas for Improvement**:
- Could discuss `Mono.defer()` for lazy evaluation
- Might mention Reactor's Context for request-scoped state

**Score**: 4.5/5 — Excellent reactive programming skills
