# Module 54: Observability & Distributed Tracing - Mini Project

**Project Name**: Instrumented Microservice with Micrometer & Zipkin  
**Difficulty Level**: Advanced  
**Estimated Time**: 2-3 hours

---

## 🎯 Objective
Instrument a Spring Boot application using Micrometer Tracing. Observe how the Trace ID propagates into logs automatically, expose custom business metrics to Prometheus, and visualize a distributed trace in Zipkin.

## 📝 Requirements

### Core Features

1. **Project Setup**:
   - Add dependencies for `spring-boot-starter-actuator`, `micrometer-tracing-bridge-brave`, `micrometer-registry-prometheus`, and `zipkin-reporter-brave`.

2. **Metrics Configuration**:
   - Create a `BookController`.
   - Expose an endpoint `GET /api/books/{id}`.
   - Inject a `MeterRegistry`.
   - Create a `Timer` metric called `book.fetch.time`. Wrap the logic of the endpoint inside `timer.record(() -> { ... })` to measure how long it takes to fetch a book.
   - Add a `Counter` metric called `book.notfound.count`. Increment it if the requested ID does not exist.

3. **Tracing Configuration**:
   - In `application.properties`, configure tracing to sample 100% of requests: `management.tracing.sampling.probability=1.0`.
   - Expose the Prometheus endpoint: `management.endpoints.web.exposure.include=prometheus,health`.

4. **Context Propagation Simulation**:
   - Inside the Controller, create a new Thread using `CompletableFuture.runAsync()`. Notice that logging inside this thread does *not* contain the Trace ID.
   - Fix it by wrapping the runnable with `ContextSnapshot.captureAll().wrap(...)` to properly propagate the MDC/Trace context to the child thread.

5. **Execution & Visualization**:
   - Run Zipkin locally via Docker: `docker run -d -p 9411:9411 openzipkin/zipkin`.
   - Hit the `/api/books/1` endpoint several times.
   - Check the console logs. You should see `[TraceId, SpanId]` prepended to your log statements.
   - Navigate to `http://localhost:8080/actuator/prometheus` to verify your custom `Timer` and `Counter` metrics are exported correctly in Prometheus format.
   - Navigate to `http://localhost:9411` (Zipkin UI) to view the visual trace of your HTTP request.

---

## 💡 Solution Blueprint

**Controller & Metrics**:
```java
@RestController
@RequestMapping("/api/books")
public class BookController {
    private static final Logger log = LoggerFactory.getLogger(BookController.class);
    private final Timer fetchTimer;
    private final Counter notFoundCounter;

    public BookController(MeterRegistry registry) {
        this.fetchTimer = registry.timer("book.fetch.time");
        this.notFoundCounter = registry.counter("book.notfound.count");
    }

    @GetMapping("/{id}")
    public ResponseEntity<String> getBook(@PathVariable String id) {
        log.info("Request received for book ID: {}", id);

        return fetchTimer.record(() -> {
            if ("404".equals(id)) {
                notFoundCounter.increment();
                log.warn("Book not found");
                return ResponseEntity.notFound().build();
            }
            
            // Simulating Async work with Context Propagation
            ContextSnapshot snapshot = ContextSnapshot.captureAll();
            CompletableFuture.runAsync(snapshot.wrap(() -> {
                log.info("Processing async task with preserved Trace ID");
            }));

            return ResponseEntity.ok("Book details for " + id);
        });
    }
}
```