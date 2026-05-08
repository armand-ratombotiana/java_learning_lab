# Spring WebFlux

Reactive Web framework using Spring WebFlux.

## Overview

- Reactive, non-blocking web framework
- Netty server (instead of Tomcat)
- Functional and annotation-based programming
- Mono and Flux reactive types

## Key Concepts

- **Mono** - 0 or 1 element
- **Flux** - Stream of 0 to N elements
- **WebClient** - Reactive HTTP client
- **R2DBC** - Reactive database access

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-boot-starter-webflux

## Reactive Types

```java
Mono<String> singleValue = Mono.just("value");
Flux<Integer> stream = Flux.just(1, 2, 3);
```

## Operators

- map() - Transform
- filter() - Filter elements
- flatMap() - Transform to Mono/Flux
- merge() - Combine streams

## Server

Uses Netty by default for non-blocking I/O.

## Version

Spring Boot 3.3.0