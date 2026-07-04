# Theory: WebFlux Reactive

## Reactive Streams Specification
Four core interfaces form the Reactive Streams contract:
1. **Publisher**: Emits data to subscribers
2. **Subscriber**: Receives data from publisher
3. **Subscription**: Links publisher and subscriber
4. **Processor**: Both publisher and subscriber

### Project Reactor
Reactor implements Reactive Streams with two main types:
- **Mono<T>**: 0 or 1 element (like CompletableFuture)
- **Flux<T>**: 0..N elements (like Stream)

### Operators
```java
Flux.range(1, 10)
    .filter(n -> n % 2 == 0)
    .map(n -> "Number: " + n)
    .flatMap(this::asyncOperation)
    .subscribe(System.out::println);
```

### WebFlux vs MVC
| Aspect | Spring MVC | Spring WebFlux |
|--------|------------|----------------|
| Model | Servlet API | Reactive Streams |
| Threading | Blocking (thread per request) | Non-blocking (event loop) |
| Database | JPA (blocking) | R2DBC, MongoDB reactive |
| Performance | Good for I/O bound | Better for high concurrency |

### Backpressure
Mechanism to control data flow:
```java
Flux.interval(Duration.ofMillis(10))
    .onBackpressureDrop()
    .subscribe(System.out::println);
```
