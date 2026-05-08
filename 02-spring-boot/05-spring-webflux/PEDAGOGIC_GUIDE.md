# Spring WebFlux - Pedagogic Guide

## Learning Path

### Phase 1: Reactive Fundamentals (Day 1)
1. **Reactive Programming** - Event-driven, non-blocking
2. **Mono<T>** - 0 or 1 element
3. **Flux<T>** - 0 to N elements
4. **Operators** - map, filter, flatMap

### Phase 2: WebFlux Controllers (Day 2)
1. **Annotated Controllers** - @GetMapping returns Mono/Flux
2. **Functional Endpoints** - RouterFunction, HandlerFunction
3. **ServerRequest/ServerResponse**
4. **Route Configuration**

### Phase 3: Reactive Data (Day 3)
1. **R2DBC** - Reactive database driver
2. **ReactiveRepository** - Extends ReactiveCrudRepository
3. **Non-blocking I/O** - Comparison with blocking
4. **Backpressure** - Flow control

## Key Concepts

### Reactive Types
```java
Mono<String> single = Mono.just("value");
Flux<Integer> list = Flux.just(1, 2, 3);
```

### Operators
```java
mono.map(String::toUpperCase)
flux.filter(x -> x > 5)
flux.flatMap(this::process)
```

### Why Reactive?
- Higher concurrency
- Less resource usage
- Better scalability
- Non-blocking I/O

## Common Patterns

### WebClient
```java
WebClient client = WebClient.create();
Mono<String> result = client.get()
    .uri("http://api.example.com/data")
    .retrieve()
    .bodyToMono(String.class);
```

### Error Handling
```java
mono.onErrorResume(e -> fallback)
mono.retry(3)
```

## Best Practices
- Don't block in reactive chain
- Use non-blocking drivers
- Handle backpressure
- Test with StepVerifier