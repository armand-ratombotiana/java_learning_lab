# Module 30: Reactive Programming (Project Reactor) - Deep Dive

**Difficulty Level**: Advanced  
**Prerequisites**: Modules 01-29, specifically Module 10 (Lambdas) and Module 04 (Streams API)  
**Estimated Reading Time**: 90 minutes  

---

## 📚 Table of Contents

1. [Introduction to Reactive Programming](#intro)
2. [Reactive Streams Specification](#specification)
3. [Mono and Flux (Project Reactor)](#mono-flux)
4. [Operators and Transformations](#operators)
5. [Schedulers and Concurrency](#schedulers)
6. [Error Handling](#error)
7. [Spring WebFlux Integration](#webflux)

---

## 1. Introduction to Reactive Programming <a name="intro"></a>
Reactive programming is a declarative programming paradigm concerned with data streams and the propagation of change. It aims to build non-blocking, asynchronous applications capable of handling large amounts of data and high concurrency with a small number of threads.

---

## 2. Reactive Streams Specification <a name="specification"></a>
The Reactive Streams specification consists of four core interfaces:
- **Publisher**: Emits a sequence of events.
- **Subscriber**: Consumes events.
- **Subscription**: Represents the link between a Publisher and a Subscriber (handles backpressure).
- **Processor**: Acts as both a Publisher and a Subscriber.

---

## 3. Mono and Flux (Project Reactor) <a name="mono-flux"></a>
Project Reactor provides two main Publishers:
- **Mono**: Emits 0 or 1 element.
- **Flux**: Emits 0 to N elements.

```java
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public class ReactorBasics {
    public void createPublishers() {
        Mono<String> mono = Mono.just("Hello");
        Flux<Integer> flux = Flux.just(1, 2, 3, 4, 5);
        Flux<String> emptyFlux = Flux.empty();
    }
}
```

---

## 4. Operators and Transformations <a name="operators"></a>
Operators allow you to transform, filter, and manipulate data streams cleanly.

```java
Flux<Integer> numbers = Flux.range(1, 10);
numbers.filter(n -> n % 2 == 0)      // Keep even numbers
       .map(n -> n * 10)             // Multiply by 10
       .subscribe(System.out::println); // Consume
```

---

## 5. Schedulers and Concurrency <a name="schedulers"></a>
By default, Reactor runs synchronously on the subscribing thread. You can change this behavior using Schedulers.

- `publishOn()`: Changes the thread for all subsequent operators down the chain.
- `subscribeOn()`: Changes the thread that the entire chain subscribes on from the very beginning.

```java
Flux.just("a", "b", "c")
    .map(String::toUpperCase)
    .subscribeOn(Schedulers.boundedElastic()) // Executes source on IO-optimized pool
    .publishOn(Schedulers.parallel())         // Subsequent operators run on CPU pool
    .subscribe(System.out::println);
```

---

## 6. Error Handling <a name="error"></a>
Handling errors gracefully in streams without terminating the application.

```java
Flux.just(1, 2, 0, 4)
    .map(i -> 10 / i)
    .onErrorReturn(-1) // If error occurs, return -1
    // .onErrorResume(e -> Flux.just(99, 100)) // Alternatively, fallback to another stream
    .subscribe(System.out::println);
```

---

## 7. Spring WebFlux Integration <a name="webflux"></a>
Spring WebFlux is the reactive-stack web framework built on Project Reactor.

```java
@RestController
@RequestMapping("/api/reactive/users")
public class ReactiveUserController {

    @GetMapping("/{id}")
    public Mono<User> getUserById(@PathVariable String id) {
        return userRepository.findById(id); // Returns a Mono
    }

    @GetMapping
    public Flux<User> getAllUsers() {
        return userRepository.findAll(); // Returns a Flux
    }
}
```