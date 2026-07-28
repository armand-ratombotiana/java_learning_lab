# Spring WebFlux — Deep Interview Guide

## Table of Contents
1. [Reactive Fundamentals](#reactive-fundamentals)
2. [Reactor Core (Mono, Flux)](#reactor-core-mono-flux)
3. [WebFlux vs MVC](#webflux-vs-mvc)
4. [Backpressure](#backpressure)
5. [Error Handling & Retry](#error-handling--retry)
6. [Reactive Data Access](#reactive-data-access)
7. [Threading & Schedulers](#threading--schedulers)
8. [Java Code Examples](#java-code-examples)
9. [15+ Interview Questions](#15-interview-questions)

---

## Reactive Fundamentals

Reactive programming is a declarative programming paradigm concerned with data streams and the propagation of change. It enables building asynchronous, non-blocking, event-driven applications that can handle high concurrency with fewer resources.

### Reactive Streams Specification

The Reactive Streams specification (java.util.concurrent.Flow since Java 9) defines four core interfaces:

| Interface | Method | Description |
|-----------|--------|-------------|
| **Publisher<T>** | `subscribe(Subscriber)` | Produces data |
| **Subscriber<T>** | `onNext(T)`, `onError(Throwable)`, `onComplete()` | Consumes data |
| **Subscription** | `request(long)`, `cancel()` | Controls demand |
| **Processor<T,R>** | Both Publisher and Subscriber | Transforms data |

### The Reactive Manifesto

Four principles:
1. **Responsive** — systems respond in a timely manner
2. **Resilient** — systems stay responsive under failure
3. **Elastic** — systems stay responsive under varying load
4. **Message-driven** — asynchronous message passing enables loose coupling

---

## Reactor Core (Mono, Flux)

Project Reactor is the reactive library that underpins Spring WebFlux. It provides two core types:

### Mono< T >

A publisher that emits **0 or 1** item.

```java
// Create
Mono<String> empty = Mono.empty();
Mono<String> just = Mono.just("Hello");
Mono<String> fromCallable = Mono.fromCallable(() -> expensiveCall());
Mono<String> deferred = Mono.defer(() -> Mono.just(System.currentTimeMillis()));
Mono<String> error = Mono.error(new RuntimeException("boom"));

// Transform
Mono<String> upper = just.map(String::toUpperCase);
Mono<Integer> length = just.flatMap(s -> Mono.just(s.length()));

// Combine
Mono<String> combined = Mono.zip(
    Mono.just("Hello"),
    Mono.just("World"),
    (a, b) -> a + " " + b
);

// Conditional
Mono<String> result = someMono
    .defaultIfEmpty("fallback")
    .switchIfEmpty(Mono.just("other"));
```

### Flux< T >

A publisher that emits **0..N** items.

```java
// Create
Flux<String> empty = Flux.empty();
Flux<String> just = Flux.just("a", "b", "c");
Flux<Integer> range = Flux.range(1, 10);
Flux<Long> interval = Flux.interval(Duration.ofSeconds(1));
Flux<String> fromIterable = Flux.fromIterable(List.of("x", "y", "z"));

// Transform
Flux<String> uppercased = just.map(String::toUpperCase);
Flux<Integer> flattened = just.flatMap(s -> Flux.fromArray(s.split("")));

// Filter
Flux<String> filtered = just.filter(s -> s.startsWith("a"));

// Aggregate
Mono<List<String>> list = just.collectList();
Mono<String> joined = just.collectJoining(", ");

// Window & Buffer
Flux<Flux<String>> windowed = just.window(2);
Flux<List<String>> buffered = just.buffer(2);
```

### Key Operators

| Operator | Type | Description |
|----------|------|-------------|
| `map` | Sync | Transform 1:1 synchronous |
| `flatMap` | Async | Transform 1:N, inner publishers are subscribed eagerly |
| `concatMap` | Async | Transform 1:N, inner publishers are subscribed sequentially |
| `flatMapSequential` | Async | Like flatMap but preserves order of outer elements |
| `transform` | Build | Composes operators into a reusable transformer |
| `compose` | Build | Creates a new Mono/Flux per subscriber |
| `defer` | Build | Lazily creates the publisher per subscription |
| `zip` | Combine | Combines multiple publishers element-wise |
| `merge` | Combine | Interleaves elements from multiple publishers |
| `concat` | Combine | Concatenates publishers sequentially |
| `switchMap` | Transform | Switches to a new inner publisher on each outer emission |
| `groupBy` | Partition | Groups elements by key into grouped Fluxes |
| `delayElements` | Timing | Delays each element by a duration |

---

## WebFlux vs MVC

### Threading Model Comparison

```
Spring MVC                                          Spring WebFlux
┌───────────────────────┐                           ┌───────────────────────┐
│ Tomcat Thread Pool    │                           │ Event Loop Group      │
│ (200 threads)         │                           │ (CPU cores × 2)       │
│                       │                           │                       │
│ req1 → thread1        │                           │ req1 → eventloop1     │
│ req2 → thread2        │                           │ req2 → eventloop2     │
│ req3 → thread3        │                           │ req3 → eventloop1     │
│ ...                   │                           │ ...                   │
│ req200 → thread200    │                           │ many reqs per thread  │
└───────────────────────┘                           └───────────────────────┘
```

**Key Differences**:

| Aspect | Spring MVC | Spring WebFlux |
|--------|------------|----------------|
| **Model** | Servlet API (blocking) | Reactive (non-blocking) |
| **Server** | Tomcat, Jetty, Undertow | Netty, Tomcat, Undertow, Jetty |
| **Threads** | One per request (1:1) | Event loop (N:1) |
| **Concurrency** | Limited by thread pool | Thousands of concurrent connections |
| **Latency** | Blocking I/O | Non-blocking I/O |
| **Database** | JPA (blocking) | R2DBC, MongoDB Reactive |
| **Learnability** | Easier | Steeper learning curve |

### When to Use What

**Use Spring MVC when**:
- You have blocking I/O (JDBC, JPA, JMS)
- Your team is not familiar with reactive programming
- You need mature Servlet API features

**Use Spring WebFlux when**:
- You have non-blocking I/O (MongoDB, Cassandra, Redis)
- You need to handle high concurrency with limited resources
- You have streaming use cases (SSE, WebSocket)
- You're building a gateway or proxy

---

## Backpressure

Backpressure is the ability of a consumer to signal demand to a producer, preventing overwhelming slow consumers.

### Demand Signals

```
Publisher                    Subscriber
   │                             │
   │───────── onSubscribe ──────→│
   │←────── request(10) ────────│  (demand for 10 items)
   │───────── onNext(item1) ────→│
   │───────── onNext(item2) ────→│
   │───────── onNext(item3) ────→│
   │←────── request(5) ─────────│  (demand for 5 more)
   │───────── onNext(item4) ────→│
```

### Backpressure Strategies

| Strategy | Operator | Behavior |
|----------|----------|----------|
| **BUFFER** | `onBackpressureBuffer()` | Buffers all items (may OOM) |
| **DROP** | `onBackpressureDrop()` | Drops items when downstream is full |
| **LATEST** | `onBackpressureLatest()` | Keeps only the latest item |
| **ERROR** | `onBackpressureError()` | Throws an error |
| Custom | `onBackpressureBuffer(64, BufferOverflowStrategy.DROP_OLDEST)` | Custom buffer and overflow strategy |

### Example

```java
Flux.interval(Duration.ofMillis(1))    // fast producer
    .onBackpressureDrop(dropped ->      // drops when downstream can't keep up
        log.warn("Dropped: {}", dropped))
    .concatMap(tick -> slowService())   // slow consumer
    .subscribe();
```

---

## Error Handling & Retry

### Error Handling Operators

```java
// Catch and return default
Flux<String> withDefault = flux.onErrorReturn("fallback");

// Catch and switch to another publisher
Flux<String> withFallback = flux.onErrorResume(e ->
    Flux.just("backup1", "backup2"));

// Catch and continue (skip error elements)
Flux<String> skipping = flux.onErrorContinue((e, obj) ->
    log.error("Skipping {} due to {}", obj, e.getMessage()));

// Map error
Flux<String> mapped = flux.onErrorMap(e ->
    new BusinessException("Processing failed", e));

// Do something on error then propagate
flux.doOnError(e -> log.error("Error occurred", e));

// Finally (like try-catch-finally)
flux.doFinally(signalType -> closeResources());
```

### Retry Operators

```java
// Simple retry (3 attempts total)
flux.retry(2);

// Retry with backoff
flux.retryWhen(Retry.backoff(3, Duration.ofSeconds(1))
    .maxBackoff(Duration.ofSeconds(10))
    .jitter(0.5)
    .onRetryExhaustedThrow((spec, signal) ->
        new RuntimeException("Retries exhausted", signal.failure())));

// Retry with filter
flux.retryWhen(Retry.max(3)
    .filter(throwable -> throwable instanceof IOException));

// Retry with transient (selectively retry)
flux.retryWhen(Retry.max(3)
    .transientErrors(true));

// Exponential backoff
flux.retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(2)));
```

### Timeout

```java
flux.timeout(Duration.ofSeconds(5))
    .onErrorResume(TimeoutException.class, e -> flux.fallback());

Mono<String> result = mono.timeout(Duration.ofSeconds(3))
    .switchIfEmpty(Mono.just("timeout-default"));
```

---

## Reactive Data Access

### R2DBC (Reactive Relational Database Connectivity)

R2DBC enables reactive programming with relational databases.

```java
// Configuration in application.yml
// spring.r2dbc.url=r2dbc:postgresql://localhost:5432/mydb
// spring.r2dbc.username=user
// spring.r2dbc.password=pass

// Entity
@Data
@Table("products")
public class Product {
    @Id
    private Long id;
    private String name;
    private BigDecimal price;
    private String category;
}

// Repository
public interface ProductRepository extends ReactiveCrudRepository<Product, Long> {
    Flux<Product> findByCategory(String category);
    Mono<Product> findByName(String name);
}

// Service
@Service
public class ProductService {
    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Flux<Product> getProductsByCategory(String category) {
        return repository.findByCategory(category);
    }

    public Mono<Product> getProductById(Long id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(
                new ProductNotFoundException("Product not found: " + id)));
    }
}

// Controller
@RestController
@RequestMapping("/api/products")
public class ProductController {
    private final ProductService service;

    @GetMapping
    public Flux<Product> list(@RequestParam String category) {
        return service.getProductsByCategory(category);
    }

    @GetMapping("/{id}")
    public Mono<Product> get(@PathVariable Long id) {
        return service.getProductById(id);
    }
}
```

### MongoDB Reactive

```java
// Configuration: spring.data.mongodb.reactive.uri=mongodb://localhost:27017/mydb

// Repository
public interface ReactiveProductRepository
    extends ReactiveMongoRepository<Product, String> {
    Flux<Product> findByCategory(String category);
    Flux<Product> findByPriceBetween(BigDecimal min, BigDecimal max);
}

// Aggregation
@Service
public class ProductAnalyticsService {
    private final ReactiveMongoTemplate template;

    public Flux<CategoryStats> getCategoryStats() {
        Aggregation agg = Aggregation.newAggregation(
            Aggregation.group("category")
                .count().as("count")
                .avg("price").as("averagePrice")
                .sum("price").as("totalValue"),
            Aggregation.sort(Sort.by(Direction.DESC, "count"))
        );
        return template.aggregate(agg, "products", CategoryStats.class);
    }
}
```

---

## Threading & Schedulers

### Schedulers in Reactor

| Scheduler | Description | Typical Use |
|-----------|-------------|-------------|
| `Schedulers.immediate()` | Runs on the current thread | Testing |
| `Schedulers.single()` | Single reusable thread | Low-overhead tasks |
| `Schedulers.parallel()` | Fixed pool (CPU cores) | CPU-bound operations |
| `Schedulers.boundedElastic()` | Bounded elastic pool (10x CPU cores, default 10*cores) | Blocking I/O |
| `Schedulers.newBoundedElastic(...)` | Custom bounded elastic pool | Custom thread pools |

### Switching Threads

```java
flux.subscribeOn(Schedulers.boundedElastic())   // source runs on elastic
    .publishOn(Schedulers.parallel());           // downstream runs on parallel
```

**`subscribeOn`** affects the upstream (source), **`publishOn`** affects the downstream operators.

### Avoiding Blocking Calls

```java
// BAD — blocks the event loop
Mono<String> bad = Mono.just(blockingCall());

// GOOD — wraps blocking call on elastic scheduler
Mono<String> good = Mono.fromCallable(this::blockingCall)
    .subscribeOn(Schedulers.boundedElastic());

// Also good using block() wrapper
Mono<String> good2 = Mono.fromRunnable(this::blockingRunnable)
    .subscribeOn(Schedulers.boundedElastic())
    .then(Mono.just("done"));
```

---

## Java Code Examples

### 1. Reactive REST API with Full CRUD

```java
// ProductController.java
package com.example.webflux.controller;

import com.example.webflux.model.Product;
import com.example.webflux.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    public Flux<Product> getAll(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return productService.findAll(page, size);
    }

    @GetMapping("/{id}")
    public Mono<Product> getById(@PathVariable String id) {
        return productService.findById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> create(@Valid @RequestBody Product product) {
        return productService.create(product);
    }

    @PutMapping("/{id}")
    public Mono<Product> update(
            @PathVariable String id,
            @Valid @RequestBody Product product) {
        return productService.update(id, product);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return productService.delete(id);
    }

    @GetMapping("/stream")
    public Flux<ServerSentEvent<Product>> stream() {
        return productService.findAll()
            .delayElements(Duration.ofMillis(500))
            .map(product -> ServerSentEvent.<Product>builder()
                .id(product.getId())
                .event("product-update")
                .data(product)
                .build());
    }

    @GetMapping(value = "/stream-json", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Product> streamJson() {
        return productService.findAll()
            .delayElements(Duration.ofSeconds(1))
            .log("product-stream");
    }
}
```

```java
// ProductService.java
package com.example.webflux.service;

import com.example.webflux.exception.ProductNotFoundException;
import com.example.webflux.model.Product;
import com.example.webflux.repository.ProductRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.UUID;

@Service
public class ProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductRepository repository;

    public ProductService(ProductRepository repository) {
        this.repository = repository;
    }

    public Flux<Product> findAll(int page, int size) {
        return repository.findAll()
            .skip((long) page * size)
            .take(size);
    }

    public Flux<Product> findAll() {
        return repository.findAll();
    }

    public Mono<Product> findById(String id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(
                new ProductNotFoundException("Product not found: " + id)))
            .doOnNext(product -> log.debug("Found product: {}", product.getId()));
    }

    public Mono<Product> create(Product product) {
        if (product.getId() == null) {
            product.setId(UUID.randomUUID().toString());
        }
        return repository.save(product)
            .doOnSuccess(p -> log.info("Created product: {}", p.getId()));
    }

    public Mono<Product> update(String id, Product product) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(
                new ProductNotFoundException("Cannot update — not found: " + id)))
            .flatMap(existing -> {
                existing.setName(product.getName());
                existing.setPrice(product.getPrice());
                existing.setCategory(product.getCategory());
                existing.setDescription(product.getDescription());
                return repository.save(existing);
            })
            .doOnSuccess(p -> log.info("Updated product: {}", p.getId()));
    }

    public Mono<Void> delete(String id) {
        return repository.findById(id)
            .switchIfEmpty(Mono.error(
                new ProductNotFoundException("Cannot delete — not found: " + id)))
            .flatMap(repository::delete);
    }

    // Multiple async calls combined
    public Mono<ProductSummary> getProductSummary(String id) {
        Mono<Product> productMono = findById(id);
        Mono<Long> viewCountMono = getViewCount(id);
        Mono<Double> avgRatingMono = getAverageRating(id);

        return Mono.zip(productMono, viewCountMono, avgRatingMono)
            .map(tuple -> new ProductSummary(
                tuple.getT1(),
                tuple.getT2(),
                tuple.getT3()));
    }

    private Mono<Long> getViewCount(String productId) {
        // Simulate remote call
        return Mono.delay(Duration.ofMillis(50))
            .thenReturn((long) (Math.random() * 1000));
    }

    private Mono<Double> getAverageRating(String productId) {
        // Simulate remote call
        return Mono.delay(Duration.ofMillis(30))
            .thenReturn(3.5 + Math.random() * 1.5);
    }
}

record ProductSummary(Product product, long viewCount, double avgRating) {}
```

```java
// Global Error Handler
package com.example.webflux.config;

import com.example.webflux.exception.ProductNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.error.ErrorWebExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.net.URI;

@Configuration
@Order(-1)
public class GlobalErrorWebExceptionHandler implements ErrorWebExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(
        GlobalErrorWebExceptionHandler.class);

    @Override
    public Mono<Void> handle(ServerWebExchange exchange, Throwable ex) {
        log.error("Error handling request: {}", exchange.getRequest().getURI(), ex);

        ProblemDetail problemDetail;

        if (ex instanceof ProductNotFoundException pnf) {
            problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, pnf.getMessage());
            problemDetail.setTitle("Product Not Found");
        } else if (ex instanceof WebExchangeBindException webEx) {
            problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Validation failed");
            problemDetail.setTitle("Validation Error");
            problemDetail.setProperty("errors", webEx.getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList());
        } else if (ex instanceof ResponseStatusException rse) {
            problemDetail = ProblemDetail.forStatusAndDetail(
                rse.getStatusCode(), rse.getReason());
            problemDetail.setTitle("Request Error");
        } else {
            problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error");
            problemDetail.setTitle("Internal Error");
        }

        problemDetail.setInstance(
            URI.create(exchange.getRequest().getURI().getPath()));

        exchange.getResponse().setStatusCode(
            HttpStatus.valueOf(problemDetail.getStatus()));
        exchange.getResponse().getHeaders()
            .setContentType(org.springframework.http.MediaType.APPLICATION_PROBLEM_JSON);

        return exchange.getResponse()
            .writeWith(Mono.just(exchange.getResponse()
                .bufferFactory()
                .wrap(problemDetail.toString().getBytes())));
    }
}
```

### 2. Reactive Streaming — Large File Processing

```java
// ReactiveFileService.java
package com.example.webflux.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.*;
import java.nio.channels.AsynchronousFileChannel;
import java.nio.channels.FileChannel;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

@Service
public class ReactiveFileService {

    private static final Logger log = LoggerFactory.getLogger(ReactiveFileService.class);

    private static final int BUFFER_SIZE = 8192;

    public Flux<String> readLines(Path filePath) {
        return Flux.using(
            () -> new BufferedReader(
                new InputStreamReader(
                    new FileInputStream(filePath.toFile()),
                    StandardCharsets.UTF_8)),
            reader -> Flux.generate(sink -> {
                try {
                    String line = reader.readLine();
                    if (line != null) {
                        sink.next(line);
                    } else {
                        sink.complete();
                    }
                } catch (IOException e) {
                    sink.error(e);
                }
            }),
            reader -> {
                try {
                    reader.close();
                } catch (IOException e) {
                    log.error("Failed to close reader", e);
                }
            }
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<DataBuffer> readAsDataBuffer(Path filePath) {
        return DataBufferUtils.readAsynchronousFileChannel(
            () -> AsynchronousFileChannel.open(
                filePath, StandardOpenOption.READ),
            new DefaultDataBufferFactory(),
            BUFFER_SIZE
        ).subscribeOn(Schedulers.boundedElastic());
    }

    public Flux<String> processLargeFile(Path filePath) {
        return readLines(filePath)
            .map(String::trim)
            .filter(line -> !line.isBlank() && !line.startsWith("#"))
            .buffer(100)
            .flatMap(batch -> processBatch(batch)
                .subscribeOn(Schedulers.parallel()))
            .doOnError(e -> log.error("Error processing file", e))
            .retryWhen(org.springframework.retry.support.RetryTemplate.builder()
                .maxAttempts(3)
                .exponentialBackoff(100, 2, 1000)
                .build()
                .withBackoffScheduler(Schedulers.parallel()));
    }

    private Flux<String> processBatch(java.util.List<String> lines) {
        return Flux.fromIterable(lines)
            .map(line -> "[PROCESSED] " + line.toUpperCase());
    }

    public Mono<Void> writeToFile(Flux<String> data, Path outputPath) {
        Flux<DataBuffer> bufferFlux = data
            .map(s -> s + System.lineSeparator())
            .map(s -> new DefaultDataBufferFactory()
                .wrap(s.getBytes(StandardCharsets.UTF_8)));

        return DataBufferUtils.write(
            bufferFlux,
            outputPath,
            StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING
        ).then();
    }
}
```

### 3. Error Handling with Fallback and Retry

```java
// ResilientService.java
package com.example.webflux.service;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.reactor.circuitbreaker.operator.CircuitBreakerOperator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeoutException;

@Service
public class ResilientService {

    private static final Logger log = LoggerFactory.getLogger(ResilientService.class);

    private final WebClient webClient;
    private final CircuitBreaker circuitBreaker;

    public ResilientService(WebClient.Builder webClientBuilder, CircuitBreaker circuitBreaker) {
        this.webClient = webClientBuilder.baseUrl("https://api.example.com").build();
        this.circuitBreaker = circuitBreaker;
    }

    public Mono<String> fetchWithRetryAndFallback(String id) {
        return fetchFromRemote(id)
            // Timeout
            .timeout(Duration.ofSeconds(5))
            .doOnError(e -> log.warn("Timeout or error for id={}", id, e))

            // Retry with exponential backoff for transient errors
            .retryWhen(Retry.backoff(3, Duration.ofMillis(500))
                .maxBackoff(Duration.ofSeconds(5))
                .jitter(0.5)
                .filter(throwable -> throwable instanceof TimeoutException
                    || throwable.getMessage() != null
                    && throwable.getMessage().contains("503")))

            // Circuit breaker
            .transformDeferred(CircuitBreakerOperator.of(circuitBreaker))

            // Fallback
            .onErrorResume(ex -> {
                log.error("All retries exhausted for id={}, using fallback", id, ex);
                return getCachedValue(id);
            })

            // Log the result
            .doOnNext(result -> log.info("Successfully fetched id={}", id));
    }

    private Mono<String> fetchFromRemote(String id) {
        return webClient.get()
            .uri("/api/data/{id}", id)
            .retrieve()
            .bodyToMono(String.class);
    }

    private Mono<String> getCachedValue(String id) {
        return Mono.just("cached-value-for-" + id);
    }

    /**
     * Batch processing with per-item error handling
     */
    public Flux<String> processBatchWithErrorSkipping(List<String> items) {
        return Flux.fromIterable(items)
            .flatMap(item -> processSingleItem(item)
                // Skip failed items, don't fail the whole stream
                .onErrorContinue((error, obj) ->
                    log.warn("Skipping item {}: {}", obj, error.getMessage())))
            .doOnComplete(() -> log.info("Batch processing completed"));
    }

    private Mono<String> processSingleItem(String item) {
        return Mono.fromCallable(() -> {
            if (item.contains("bad")) {
                throw new RuntimeException("Invalid item: " + item);
            }
            return "processed-" + item;
        });
    }
}
```

### 4. WebClient with Reactive Composition

```java
// OrderOrchestrationService.java
package com.example.webflux.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class OrderOrchestrationService {

    private static final Logger log = LoggerFactory.getLogger(
        OrderOrchestrationService.class);

    private final WebClient webClient;

    public OrderOrchestrationService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
            .baseUrl("http://localhost:8080")
            .build();
    }

    // Sequential composition
    public Mono<OrderResult> placeOrder(String userId, String productId) {
        Mono<User> userMono = getUser(userId);
        Mono<Product> productMono = getProduct(productId);

        return Mono.zip(userMono, productMono)
            .flatMap(tuple -> {
                User user = tuple.getT1();
                Product product = tuple.getT2();
                return createOrder(user, product);
            })
            .flatMap(order -> chargePayment(order))
            .flatMap(order -> sendConfirmation(order))
            .timeout(Duration.ofSeconds(10))
            .doOnError(e -> log.error("Order placement failed", e))
            .retryWhen(Retry.backoff(2, Duration.ofMillis(200)));
    }

    // Parallel fan-out
    public Mono<DashboardData> getDashboard() {
        Mono<List<Order>> recentOrders = getRecentOrders().collectList();
        Mono<Long> totalUsers = getUserCount();
        Mono<Double> revenue = getRevenue();

        return Mono.zip(recentOrders, totalUsers, revenue)
            .map(tuple -> new DashboardData(
                tuple.getT1(), tuple.getT2(), tuple.getT3()));
    }

    // Fan-out with timeout per request
    public <T> Flux<T> fanOutWithTimeout(
            List<String> ids,
            java.util.function.Function<String, Mono<T>> fetcher,
            Duration timeout) {
        return Flux.fromIterable(ids)
            .flatMap(id -> fetcher.apply(id)
                .timeout(timeout)
                .onErrorResume(e -> {
                    log.warn("Fan-out failed for id={}", id, e);
                    return Mono.empty();
                }), 10);  // concurrency limit
    }

    private Mono<User> getUser(String userId) {
        return webClient.get()
            .uri("/api/users/{id}", userId)
            .retrieve()
            .bodyToMono(User.class);
    }

    private Mono<Product> getProduct(String productId) {
        return webClient.get()
            .uri("/api/products/{id}", productId)
            .retrieve()
            .bodyToMono(Product.class);
    }

    private Mono<Order> createOrder(User user, Product product) {
        return webClient.post()
            .uri("/api/orders")
            .bodyValue(new CreateOrderRequest(user.id(), product.id()))
            .retrieve()
            .bodyToMono(Order.class);
    }

    private Mono<Order> chargePayment(Order order) {
        return webClient.post()
            .uri("/api/payments/charge")
            .bodyValue(new PaymentRequest(order.id(), order.total()))
            .retrieve()
            .bodyToMono(Order.class);
    }

    private Mono<Order> sendConfirmation(Order order) {
        return webClient.post()
            .uri("/api/notifications/send")
            .bodyValue(new NotificationRequest(order.userId(), "Order confirmed"))
            .retrieve()
            .bodyToMono(Order.class);
    }

    private Flux<Order> getRecentOrders() {
        return webClient.get()
            .uri("/api/orders/recent")
            .retrieve()
            .bodyToFlux(Order.class);
    }

    private Mono<Long> getUserCount() {
        return webClient.get()
            .uri("/api/users/count")
            .retrieve()
            .bodyToMono(Long.class);
    }

    private Mono<Double> getRevenue() {
        return webClient.get()
            .uri("/api/analytics/revenue")
            .retrieve()
            .bodyToMono(Double.class);
    }

    record User(String id, String name, String email) {}
    record Product(String id, String name, double price) {}
    record Order(String id, String userId, double total) {}
    record CreateOrderRequest(String userId, String productId) {}
    record PaymentRequest(String orderId, double amount) {}
    record NotificationRequest(String userId, String message) {}
    record DashboardData(List<Order> recentOrders, long totalUsers, double revenue) {}
    record OrderResult(String orderId, String status) {}
}
```

### 5. Custom Reactive Operator — Rate Limiter

```java
// RateLimiterOperator.java
package com.example.webflux.operator;

import org.reactivestreams.Publisher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.CoreSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Custom operator that limits the rate of elements flowing through.
 */
public class RateLimiterOperator<T> extends Flux<T> {

    private static final Logger log = LoggerFactory.getLogger(RateLimiterOperator.class);

    private final Flux<T> source;
    private final long maxPermits;
    private final Duration period;

    public RateLimiterOperator(Flux<T> source, long maxPermits, Duration period) {
        this.source = source;
        this.maxPermits = maxPermits;
        this.period = period;
    }

    @Override
    public void subscribe(CoreSubscriber<? super T> actual) {
        source.subscribe(new RateLimiterSubscriber<>(actual, maxPermits, period));
    }

    private static class RateLimiterSubscriber<T>
            implements CoreSubscriber<T>, org.reactivestreams.Subscription {

        private final CoreSubscriber<? super T> actual;
        private final long maxPermits;
        private final Duration period;
        private final AtomicLong tokens = new AtomicLong(0);
        private volatile long lastRefill = System.nanoTime();
        private org.reactivestreams.Subscription upstream;

        RateLimiterSubscriber(CoreSubscriber<? super T> actual,
                              long maxPermits, Duration period) {
            this.actual = actual;
            this.maxPermits = maxPermits;
            this.period = period;
        }

        @Override
        public void onSubscribe(org.reactivestreams.Subscription s) {
            this.upstream = s;
            actual.onSubscribe(this);
        }

        @Override
        public void onNext(T item) {
            refillTokens();
            long available = tokens.get();
            if (available > 0) {
                if (tokens.compareAndSet(available, available - 1)) {
                    actual.onNext(item);
                } else {
                    // CAS failed, retry on next element
                    // In production, use a proper queue-based approach
                    log.warn("Rate limit exceeded, dropping item");
                }
            } else {
                log.warn("Rate limit exceeded, dropping item");
            }
        }

        @Override
        public void onError(Throwable t) {
            actual.onError(t);
        }

        @Override
        public void onComplete() {
            actual.onComplete();
        }

        @Override
        public void request(long n) {
            upstream.request(n);
        }

        @Override
        public void cancel() {
            upstream.cancel();
        }

        private void refillTokens() {
            long now = System.nanoTime();
            long elapsed = now - lastRefill;
            long refillCount = (long) (elapsed * maxPermits / period.toNanos());

            if (refillCount > 0) {
                lastRefill = now;
                tokens.updateAndGet(current ->
                    Math.min(current + refillCount, maxPermits));
            }
        }
    }
}

// Usage:
// Flux.interval(Duration.ofMillis(10))
//     .transform(flux -> new RateLimiterOperator<>(flux, 10, Duration.ofSeconds(1)))
//     .subscribe(System.out::println);
```

### 6. Reactive WebSocket Handler

```java
// ReactiveWebSocketHandler.java
package com.example.webflux.websocket;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final Logger log = LoggerFactory.getLogger(ReactiveWebSocketHandler.class);

    private final ObjectMapper objectMapper;
    private final Map<String, Sinks.Many<String>> userSessions = new ConcurrentHashMap<>();

    public ReactiveWebSocketHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        String sessionId = session.getId();
        log.info("WebSocket connection established: {}", sessionId);

        // Create a sink for outgoing messages to this session
        Sinks.Many<String> sink = Sinks.many().unicast().onBackpressureBuffer();
        userSessions.put(sessionId, sink);

        // Handle incoming messages
        Mono<Void> input = session.receive()
            .map(WebSocketMessage::getPayloadAsText)
            .doOnNext(msg -> log.info("Received: {}", msg))
            .flatMap(msg -> handleIncomingMessage(sessionId, msg))
            .then()
            .doFinally(signal -> {
                log.info("Input stream ended for session: {}", sessionId);
                userSessions.remove(sessionId);
            });

        // Send outgoing messages as a heartbeat + data stream
        Flux<WebSocketMessage> heartbeat = Flux.interval(Duration.ofSeconds(30))
            .map(tick -> session.textMessage("{\"type\":\"ping\"}"));

        Flux<WebSocketMessage> dataStream = sink.asFlux()
            .map(session::textMessage);

        Mono<Void> output = session.send(
            Flux.merge(heartbeat, dataStream))
            .doFinally(signal -> {
                log.info("Output stream ended for session: {}", sessionId);
                sink.tryEmitComplete();
            });

        return Mono.zip(input, output).then();
    }

    private Mono<Void> handleIncomingMessage(String sessionId, String message) {
        try {
            @SuppressWarnings("unchecked")
            Map<String, Object> msg = objectMapper.readValue(message, Map.class);
            String type = (String) msg.get("type");

            if ("subscribe".equals(type)) {
                String channel = (String) msg.get("channel");
                log.info("Session {} subscribed to channel {}", sessionId, channel);
            }
        } catch (Exception e) {
            log.error("Failed to parse message from session {}", sessionId, e);
        }
        return Mono.empty();
    }

    public void broadcast(String channel, Object payload) {
        try {
            String json = objectMapper.writeValueAsString(Map.of(
                "channel", channel,
                "data", payload
            ));
            userSessions.values().forEach(sink ->
                sink.tryEmitNext(json));
        } catch (Exception e) {
            log.error("Failed to broadcast message", e);
        }
    }

    public void sendToSession(String sessionId, Object payload) {
        Sinks.Many<String> sink = userSessions.get(sessionId);
        if (sink != null) {
            try {
                String json = objectMapper.writeValueAsString(payload);
                sink.tryEmitNext(json);
            } catch (Exception e) {
                log.error("Failed to send to session {}", sessionId, e);
            }
        }
    }
}
```

---

## 15+ Interview Questions

### Q1: What is the difference between Spring MVC and Spring WebFlux?
**Answer**: Spring MVC is built on the Servlet API (blocking, thread-per-request model). Spring WebFlux is built on Reactive Streams (non-blocking, event-loop model). MVC uses Tomcat/Jetty with one thread per request. WebFlux uses Netty with an event loop (few threads handle many requests). MVC is simpler for typical CRUD apps. WebFlux excels at high concurrency, streaming, and low-latency scenarios.

### Q2: Explain Mono and Flux in Project Reactor.
**Answer**: `Mono<T>` represents a publisher that emits 0 or 1 item (like `Optional` or `CompletableFuture`). `Flux<T>` represents a publisher that emits 0 to N items (like `Stream`). Both implement `Publisher<T>` from Reactive Streams. They support operators like `map`, `flatMap`, `filter`, `zip`, `onErrorResume`, `retry`, etc.

### Q3: How does backpressure work in Reactor?
**Answer**: Backpressure is the ability of a subscriber to control the rate of data emission from the publisher. The subscriber calls `request(n)` on the subscription to signal demand for N items. Operators like `onBackpressureBuffer()`, `onBackpressureDrop()`, `onBackpressureLatest()` handle overflow when demand is insufficient. `Buffer` queues items (risk OOM), `Drop` discards items, `Latest` keeps only the newest.

### Q4: What is the threading model in WebFlux?
**Answer**: WebFlux uses an event-loop model with a small number of threads (typically `Runtime.getRuntime().availableProcessors() * 2`). The event loop handles multiple connections concurrently. Blocking operations should be offloaded to `Schedulers.boundedElastic()`. CPU-bound work should use `Schedulers.parallel()`. `subscribeOn()` changes the scheduler for the source; `publishOn()` changes the scheduler for downstream operators.

### Q5: How do you handle errors in reactive streams?
**Answer**: Use operators: `onErrorReturn()` — return a default value; `onErrorResume()` — switch to a fallback publisher; `onErrorContinue()` — skip the failed element and continue; `onErrorMap()` — transform the error; `doOnError()` — side effect on error; `retry()` / `retryWhen()` — retry on failure; `timeout()` — fail if no signal within duration. Error signals are terminal in reactive streams (unless handled).

### Q6: What is the difference between flatMap and concatMap?
**Answer**: Both transform each outer element into an inner publisher and flatten. `flatMap` subscribes to inner publishers eagerly (potentially interleaving results). `concatMap` subscribes to inner publishers sequentially (preserving order). `flatMapSequential` subscribes eagerly but reorders results to match outer element order. Use `concatMap` when order matters and inner streams are short. Use `flatMap` for parallel fan-out.

### Q7: How do you integrate a blocking database (JPA) with WebFlux?
**Answer**: Option 1: Wrap blocking calls in `Mono.fromCallable(() -> jpaRepository.findById(id)).subscribeOn(Schedulers.boundedElastic())`. This offloads blocking to a dedicated thread pool. Option 2: Use R2DBC for fully reactive database access. Option 3: Use Hibernate Reactive (experimental). The recommended approach for new projects is to use R2DBC with a reactive repository.

### Q8: What is the purpose of Schedulers in Reactor?
**Answer**: Schedulers provide thread pools for executing reactive operations. `Schedulers.immediate()` runs on current thread. `Schedulers.single()` uses a single reusable thread. `Schedulers.parallel()` uses a fixed pool sized to CPU cores (for CPU-bound work). `Schedulers.boundedElastic()` uses a bounded, elastic thread pool (for blocking I/O). `Schedulers.newBoundedElastic(...)` creates a custom bounded elastic pool.

### Q9: How does WebFlux handle context propagation?
**Answer**: Reactor provides `reactor.util.context.Context` — an immutable map that propagates through the operator chain implicitly. It's bound to a reactive sequence (not thread-local). Use `Mono.deferContextual(ctx -> ...)` or `Flux.deferContextual(ctx -> ...)` to read context. Use `.contextWrite(ctx -> ctx.put("key", "value"))` to write context. This is used for MDC logging, security context, correlation IDs.

### Q10: Explain the difference between Mono.zip and Mono.merge.
**Answer**: `Mono.zip` combines multiple Monos into a single result (all must complete successfully, combined via a function). `Mono.merge` is for Monos that emit the same type — it merges their emissions into a single Mono (if any emits, that's the result). For Flux: `Flux.zip` combines elements pairwise; `Flux.merge` interleaves elements from multiple publishers; `Flux.concat` concatenates sequentially.

### Q11: How do you test reactive code?
**Answer**: Use `StepVerifier` (from `reactor-test`). Create a scenario, apply the publisher, define expectations: `expectNext(...)`, `expectNextCount(...)`, `expectComplete()`, `expectError(...)`. For virtual time: `StepVerifier.withVirtualTime(() -> publisher.delayElements(...))`. For `WebTestClient`: test WebFlux controllers by making HTTP assertions against reactive endpoints. Example: `StepVerifier.create(flux).expectNext("a", "b").verifyComplete()`.

### Q12: What are Hot and Cold publishers?
**Answer**: **Cold publishers** create a new data source for each subscriber (each subscriber gets all the data). Example: `Flux.just`, `Flux.fromIterable`. **Hot publishers** share a single data source across subscribers (late subscribers miss earlier data). Example: `Sinks.many().multicast()`, `Flux.interval().share()`, `ConnectableFlux`. Use `publish().refCount()` to convert cold to hot.

### Q13: How do you implement caching in a reactive application?
**Answer**: Use `Mono.cache()` or `Flux.cache()` to cache the result of an already-computed sequence. Combine with TTL: `Mono.fromCallable(expensive).cache(Duration.ofMinutes(5))`. Use `ReactiveCache` (Spring Cache abstraction for reactive). Or use Redis Reactive with `StringRedisTemplate.opsForValue().get(key)` / `set(key, value, Duration)`. Example: `Mono.defer(() -> cache.get(key)).switchIfEmpty(compute().flatMap(v -> cache.put(key, v).thenReturn(v)))`.

### Q14: What is the difference between Spring Cloud Gateway and Zuul?
**Answer**: Spring Cloud Gateway is built on WebFlux (reactive, non-blocking, Netty). Zuul 1.x is built on Servlet API (blocking, thread-per-request). Gateway supports WebSocket, SSE, better performance, and route-based filtering. Zuul 2.x is also Netty-based but Gateway is the recommended choice for new Spring Cloud projects. Gateway uses `RouteLocator` DSL: `routes().route("id", r -> r.path("/api/**").uri("lb://service"))`.

### Q15: How does Spring Security integrate with WebFlux?
**Answer**: Spring Security WebFlux uses `SecurityWebFilterChain` (replacing `SecurityFilterChain` from MVC). Key differences: (1) `ReactiveAuthenticationManager` instead of `AuthenticationManager`; (2) `ServerHttpSecurity` instead of `HttpSecurity`; (3) `@EnableWebFluxSecurity` instead of `@EnableWebSecurity`; (4) `ReactiveUserDetailsService` for user details; (5) `SecurityContext` is stored in Reactor Context, not thread-local. Authentication works the same with JWTs, OAuth2, etc.

### Q16: What is the purpose of the Displacement in resilience4j with reactive?
**Answer**: Resilience4j provides reactive operators: `CircuitBreakerOperator`, `RateLimiterOperator`, `BulkheadOperator`, `RetryOperator`, `TimeLimiterOperator`. They integrate with Reactor via `.transformDeferred(CircuitBreakerOperator.of(circuitBreaker))`. These operators work correctly with reactive types — they wrap `Mono`/`Flux` publishers, handle signals properly, and don't block.

### Q17: Explain the event loop architecture in Netty (used by WebFlux).
**Answer**: Netty has an event loop group with one or more event loops (threads). Each event loop manages multiple channels (connections) using non-blocking I/O (selector pattern). Events (connect, read, write, close) are processed by the event loop. This allows a single thread to handle thousands of connections. The event loop runs a task queue — user tasks can be scheduled on the event loop. NioEventLoopGroup is the most common implementation.

### Q18: How is ReactiveTypeFactory used in Spring WebFlux?
**Answer**: `ReactiveTypeFactory` is a Spring SPI for registering reactive library types. Spring WebFlux detects available reactive libraries at runtime (Reactor, RxJava, R2DBC, etc.). It registers adapters so these libraries can be used interchangeably. The `ReactiveAdapterRegistry` stores adapters that convert between reactive types. `MonoToListenableFutureAdapter` is an example adapter.

### Q19: What is the difference between doOnNext and map?
**Answer**: `doOnNext` is a "side-effect" operator — it executes a callback when an element passes through, but doesn't modify the element. It's used for logging, metrics, or auditing. `map` transforms each element into a new element. `doOnNext(item) { log(item); return item; }` vs `map(item) -> transformedItem`. Do not use `doOnNext` for transformation — that's what `map` is for.

### Q20: How does Spring WebFlux handle multipart file uploads?
**Answer**: WebFlux supports multipart through `@RequestPart` in controllers for file parts. The underlying parts are represented as `FilePart`. The controller returns `Mono<Void>` or `Flux<DataBuffer>`. Use `filePart.transferTo(path)` or read `filePart.content()` as a `Flux<DataBuffer>`. Example: `@PostMapping("/upload") public Mono<Void> upload(@RequestPart("file") FilePart file) { return file.transferTo(Paths.get("/uploads/" + file.filename())); }`.
