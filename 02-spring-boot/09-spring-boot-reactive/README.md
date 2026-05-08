# Spring Boot Reactive

Reactive programming module with Spring WebFlux.

## Overview

- Reactive programming paradigm
- Non-blocking I/O
- Event-driven architecture
- Mono and Flux types

## Key Concepts

- **Mono** - Asynchronous 0 or 1 result
- **Flux** - Asynchronous stream
- **WebClient** - Non-blocking HTTP client
- **Reactive Repositories** - Non-blocking DB access

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-boot-starter-webflux

## Reactive Types

```java
Mono<String> single = Mono.just("value");
Flux<Integer> stream = Flux.just(1, 2, 3);
```

## Operators

| Operator | Purpose |
|----------|---------|
| map() | Transform elements |
| filter() | Filter stream |
| flatMap() | Async transformation |
| merge() | Combine streams |

## Execution

Reactive types are lazy - they don't execute until subscribed.

## Testing

Use StepVerifier for testing reactive streams.

## Version

Spring Boot 3.3.0