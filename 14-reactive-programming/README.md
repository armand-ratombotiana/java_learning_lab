# 14 - Reactive Programming

Reactive programming with Project Reactor. Covers Reactor core types (Mono for single values, Flux for multiple values), reactive operators (map, flatMap, filter, collectList, compose), backpressure handling, and error recovery strategies.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Mono: publisher for 0..1 elements, `Mono.just()`, `Mono.error()`, `onErrorResume()`
- Flux: publisher for 0..N elements, `Flux.just()`, `Flux.range()`, `Flux.interval()`
- Reactive operators: `map`, `flatMap`, `filter`, `collectList`
- Backpressure and demand management
- Error handling: `onErrorReturn`, `onErrorResume`, `onErrorMap`
- Threading model: Schedulers, subscribeOn, publishOn

## Module Structure

- `01-reactor/` - Project Reactor core concepts

## Learning Objectives

- Create reactive pipelines with Mono and Flux
- Transform and combine reactive streams
- Handle errors and backpressure in reactive systems

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 14-reactive-programming
mvn clean package
```

Run the lab:

```bash
cd 01-reactor
mvn compile exec:java -Dexec.mainClass="com.learning.reactive.ReactorLab"
```
