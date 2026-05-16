# Vert.x Learning Interview Questions

## Section 1: Vert.x Basics

### Q1: What is Vert.x and what are its main characteristics?
**Answer:** Vert.x is a reactive, event-driven framework for building modern applications. Main characteristics:
- Polyglot (Java, Kotlin, JS, Scala, Groovy)
- Non-blocking I/O for high concurrency
- Lightweight and scalable
- Simple API for building distributed systems

---

### Q2: Explain the Vert.x event loop model
**Answer:** Vert.x uses an event loop to process events asynchronously. Each event loop handles events sequentially. Key points:
- One event loop per CPU core by default
- All handlers must be non-blocking
- Blocking operations should use worker threads
- Vert.x logs warnings if event loop is blocked

---

### Q3: What is a Verticle? How do you create one?
**Answer:** A Verticle is the basic unit of deployment in Vert.x. To create:
```java
public class MyVerticle extends AbstractVerticle {
    @Override
    public void start(Promise<Void> startPromise) {
        // Your code here
        startPromise.complete();
    }
}
```

---

### Q4: How do you deploy multiple verticles?
**Answer:** Using Vertx.deployVerticle():
```java
Vertx vertx = Vertx.vertx();
vertx.deployVerticle(new MyVerticle())
    .onSuccess(id -> System.out.println("Deployed: " + id));
```

---

### Q5: What is the difference between standard and worker verticles?
**Answer:**
- Standard verticle: Runs on event loop, for async non-blocking code
- Worker verticle: Runs on worker thread pool, for blocking operations
- Create worker with @VertxWorker or deploy with -worker flag

---

## Section 2: Event Loop and Async

### Q6: How do you handle blocking code in Vert.x?
**Answer:** Using executeBlocking:
```java
vertx.executeBlocking(promise -> {
    // Blocking code here
    String result = blockingOperation();
    promise.complete(result);
}, result -> {
    // Result handler
});
```

---

### Q7: Explain the Promise and Future pattern in Vert.x
**Answer:**
- Promise: Producer side - can be succeeded or failed
- Future: Consumer side - represents the eventual result
- Methods: onSuccess, onFailure, map, compose, etc.

---

### Q8: What is the event bus? How does it work?
**Answer:** The event bus enables communication between verticles:
- send(): Point-to-point with reply expected
- publish(): Pub-sub, all consumers receive
- consumer(): Register message handler
- Messages can be Strings, JSON, or custom types with codecs

---

### Q9: How do you send and receive messages on the event bus?
**Answer:**
```java
// Send and expect reply
vertx.eventBus().send("address", "message", reply -> {
    if (reply.succeeded()) {
        System.out.println(reply.result().body());
    }
});

// Publish (fire and forget)
vertx.eventBus().publish("address", "message");

// Consumer
vertx.eventBus().consumer("address", message -> {
    System.out.println("Received: " + message.body());
});
```

---

### Q10: How do you prevent blocking the event loop?
**Answer:**
- Never use Thread.sleep() or blocking I/O in handlers
- Use executeBlocking for any blocking code
- Use non-blocking APIs (WebClient, SQL client, etc.)
- Monitor with blockedThreadCheckInterval

---

## Section 3: Vert.x Web

### Q11: How does the Vert.x Web Router work?
**Answer:** Router maps HTTP requests to handlers:
```java
Router router = Router.router(vertx);
router.get("/api/users").handler(ctx -> {...});
router.post("/api/users").handler(ctx -> {...});
router.route().handler(BodyHandler.create());
```

---

### Q12: How do you handle path parameters in Vert.x Web?
**Answer:** Using pathParam():
```java
router.get("/api/users/:id").handler(ctx -> {
    String id = ctx.pathParam("id");
    // use id
});
```

---

### Q13: How do you handle request body in Vert.x Web?
**Answer:**
```java
router.route().handler(BodyHandler.create());

// In handler
JsonObject body = ctx.body().asJsonObject();
String text = ctx.body().asString();
```

---

### Q14: What is WebClient and how do you use it?
**Answer:** Non-blocking HTTP client:
```java
WebClient client = WebClient.create(vertx);
client.get(8080, "localhost", "/api")
    .as(BodyCodec.jsonObject())
    .send()
    .onSuccess(response -> {...})
    .onFailure(err -> {...});
```

---

### Q15: How do you implement exception handling in Vert.x Web?
**Answer:**
```java
router.route().failureHandler(ctx -> {
    // Handle failures
    ctx.response().setStatusCode(500).end("Error");
});
// Or use @ControllerAdvice equivalent via exception handlers
```

---

## Section 4: Advanced Patterns

### Q16: What is the Circuit Breaker pattern in Vert.x?
**Answer:** Provides fault tolerance:
```java
CircuitBreaker breaker = CircuitBreaker.create("name", vertx, 
    new CircuitBreakerOptions()
        .setMaxFailures(3)
        .setTimeout(2000)
        .setResetTimeout(10000));

breaker.executeWithFallback(promise -> {
    // operation
}, fallback -> "fallback response");
```

---

### Q17: Explain Vert.x service proxies
**Answer:** Generate async proxies for inter-verticle calls:
```java
@ProxyGen
@VertxGen
public interface MyService {
    void getData(Handler<AsyncResult<String>> result);
}

MyService service = MyServiceProxy.createProxy(vertx, "address");
service.getData(result -> {...});
```

---

### Q18: How do you handle timeouts in Vert.x?
**Answer:**
```java
vertx.setTimer(5000, id -> {
    // Timeout handler
});

client.getNow(request).timeout(5000).onComplete(ar -> {...});
```

---

### Q19: How do you implement WebSocket in Vert.x?
**Answer:**
```java
server.webSocketHandler(ws -> {
    ws.textMessageHandler(msg -> {...});
    ws.writeTextMessage("Hello");
    ws.close();
});
```

---

### Q20: What is the difference between Vert.x and traditional servlet containers?
**Answer:**
- Servlet containers: Thread-per-request (blocking)
- Vert.x: Event loop per core (non-blocking)
- Vert.x scales better with many concurrent connections
- Vert.x is more resource-efficient

---

## Section 5: Clustering and Performance

### Q21: How does Vert.x clustering work?
**Answer:** Using a cluster manager (Hazelcast, Infinispan):
```java
Vertx.clusteredVertx(options, ar -> {
    Vertx clustered = ar.result();
    clustered.deployVerticle(new MyVerticle());
});
```

---

### Q22: How do you configure Vert.x for performance?
**Answer:**
- Adjust eventLoopPoolSize based on workload
- Use worker pool for blocking operations
- Configure appropriate pool sizes
- Enable metrics for monitoring
- Use connection pooling for external services

---

### Q23: What is Vert.x RxJava integration?
**Answer:** Provides RxJava bindings:
```java
import io.vertx.rxjava3.core.Vertx;
Single<String> result = Vertx.rx(vertx)
    .eventBus()
    .<String>request("addr", "msg")
    .map(msg -> msg.body());
```

---

### Q24: How do you implement health checks in Vert.x?
**Answer:**
```java
router.get("/health").handler(ctx -> {
    ctx.json(JsonObject.of(
        "status", "UP",
        "timestamp", System.currentTimeMillis()
    ));
});
```

---

### Q25: What are best practices for Vert.x application design?
**Answer:**
- Keep handlers non-blocking
- Use appropriate verticle types
- Implement proper error handling
- Use event bus for loose coupling
- Leverage circuit breakers
- Monitor metrics
- Handle backpressure properly