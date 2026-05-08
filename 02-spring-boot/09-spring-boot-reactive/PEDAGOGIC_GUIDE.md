# Spring Boot Reactive - Pedagogic Guide

## Learning Path

### Phase 1: Reactive Concepts (Day 1)
1. **Reactive Manifesto** - Responsive, resilient, elastic, message-driven
2. **Mono<T>** - 0 or 1 element stream
3. **Flux<T>** - 0 to N elements stream
4. **Subscription** - Lazy execution model

### Phase 2: Operators (Day 2)
1. **Transformation** - map, flatMap
2. **Filtering** - filter, distinct, take
3. **Combination** - merge, zip, combineLatest
4. **Error Handling** - onErrorResume, retry

### Phase 3: Integration (Day 3)
1. **ReactiveWeb** - WebFlux controllers
2. **ReactiveData** - R2DBC repositories
3. **WebClient** - Async HTTP client
4. **Testing** - StepVerifier, TestPublisher

## Key Concepts

### Reactive vs Blocking
- Blocking: Thread waits for I/O
- Reactive: Thread notified on completion
- Better resource utilization

### Execution Model
```java
Mono.just("hello")
    .map(String::toUpperCase)
    .subscribe(System.out::println);  // Terminal op
// Nothing executes until subscribed!
```

### Common Operators
```java
flux.map(x -> x * 2)
flux.filter(x -> x > 5)
flux.flatMap(this::processAsync)
```

## Best Practices
- Never block in reactive chain
- Always subscribe (or use block())
- Handle backpressure
- Use non-blocking drivers