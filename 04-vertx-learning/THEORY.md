# Vert.x Learning Theory

## Table of Contents
1. [Vert.x Basics](#vertx-basics)
2. [Event Loop](#event-loop)
3. [Reactive Patterns](#reactive-patterns)

---

## 1. Vert.x Basics

### 1.1 What is Vert.x?

Vert.x is a reactive, event-driven framework for building modern applications. Key characteristics:

- **Polyglot** - Write in Java, Kotlin, JavaScript, Scala, or Groovy
- **Event-Driven** - Non-blocking I/O for high concurrency
- **Scalable** - Small footprint, scales on multi-core systems
- **Simple** - Lightweight and flexible

### 1.2 Vert.x Core Components

**Verticle:**
```java
public class MyVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        System.out.println("Verticle started");
        startPromise.complete();
    }
    
    @Override
    public void stop(Promise<Void> stopPromise) {
        System.out.println("Verticle stopped");
        stopPromise.complete();
    }
}
```

**Deploying Verticles:**
```java
public class Launcher {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(new MyVerticle())
            .onSuccess(id -> System.out.println("Deployed: " + id))
            .onFailure(err -> System.out.println("Failed: " + err.getMessage()));
    }
}
```

### 1.3 Vert.x Configuration

```java
Vertx vertx = Vertx.vertx(new VertxOptions()
    .setWorkerPoolSize(20)
    .setEventLoopPoolSize(10)
    .setBlockedThreadCheckInterval(5, TimeUnit.SECONDS));
```

### 1.4 Verticle Types

**Standard Verticle:** Runs on event loop
```java
public class HttpServerVerticle extends AbstractVerticle {
    @Override
    public void start() {
        vertx.createHttpServer()
            .requestHandler(req -> req.response().end("Hello"))
            .listen(8080);
    }
}
```

**Worker Verticle:** Runs on worker pool
```java
@VertxWorker
public class WorkerVerticle extends AbstractVerticle {
    // Runs on worker thread, can use blocking code
}
```

### 1.5 Vert.x CLI

```bash
# Run Vert.x application
vertx run com.example.MyVerticle

# Run with configuration
vertx run MyVerticle -conf config.json

# Run worker
vertx run WorkerVerticle -worker
```

---

## 2. Event Loop

### 2.1 The Event Loop Model

Vert.x uses a variant of the reactor pattern:
- Multiple event loops (one per CPU core by default)
- Non-blocking handlers
- Tasks scheduled on event loop execute sequentially

```
┌─────────────────────┐
│   Event Loop       │
│  ┌───────────────┐  │
│  │ Handler Queue │  │
│  └───────────────┘  │
│         ↓           │
│  ┌───────────────┐  │
│  │ Process Event │  │
│  └───────────────┘  │
│         ↓           │
│  ┌───────────────┐  │
│  │  Async I/O    │  │
│  └───────────────┘  │
└─────────────────────┘
```

### 2.2 Handling Events

```java
vertx.setTimer(1000, id -> {
    System.out.println("Timer fired");
});

vertx.setPeriodic(2000, id -> {
    System.out.println("Periodic event");
});

vertx.executeBlocking(promise -> {
    // Blocking code here
    String result = blockingOperation();
    promise.complete(result);
}, result -> {
    // Result handler
    System.out.println("Result: " + result);
});
```

### 2.3 Async Patterns

**Callback Pattern:**
```java
vertx.createHttpClient()
    .get(8080, "localhost", "/api")
    .putHeader("Accept", "application/json")
    .putHeader("Content-Type", "application/json")
    .putHeader("Content-Length", "25")
    .write("{\"name\":\"John\",\"age\":30}")
    .send()
    .onSuccess(response -> {
        System.out.println("Status: " + response.statusCode());
        System.out.println("Body: " + response.bodyAsString());
    })
    .onFailure(err -> {
        System.err.println("Error: " + err.getMessage());
    });
```

**Future/Promise Pattern:**
```java
Promise<String> promise = Promise.promise();

database.query("SELECT * FROM users", result -> {
    if (result.succeeded()) {
        promise.complete(result.result());
    } else {
        promise.fail(result.cause());
    }
});

promise.future()
    .map(rows -> rows.size())
    .onSuccess(count -> System.out.println("Rows: " + count));
```

### 2.4 Event Bus

Vert.x uses an internal event bus for communication between verticles:

```java
// Send message
vertx.eventBus().send("address", "message", reply -> {
    if (reply.succeeded()) {
        System.out.println("Reply: " + reply.result().body());
    }
});

// Publish message
vertx.eventBus().publish("address", "message");

// Register consumer
vertx.eventBus().consumer("address", message -> {
    System.out.println("Received: " + message.body());
    message.reply("processed");
});
```

### 2.5 Event Bus Messages

**JSON Messages:**
```java
// Send JSON
JsonObject json = new JsonObject()
    .put("name", "John")
    .put("age", 30);
vertx.eventBus().send("address", json);

// Receive JSON
vertx.eventBus().consumer("address", message -> {
    JsonObject data = (JsonObject) message.body();
    String name = data.getString("name");
});
```

**Codecs:**
```java
// Custom codec
public class UserCodec implements MessageCodec<User, User> {
    @Override
    public void encodeToWire(Buffer buffer, User user) {
        buffer.appendString(user.getName());
    }
    
    @Override
    public User decodeFromWire(int pos, Buffer buffer) {
        return new User(buffer.getString(pos, buffer.length()));
    }
    
    @Override
    public User transform(User user) {
        return new User(user.getName()); // Copy for consumer
    }
    
    @Override
    public String name() { return "user-codec"; }
}
```

---

## 3. Reactive Patterns

### 3.1 Vert.x Web

```java
Router router = Router.router(vertx);

// Route with path
router.get("/api/users").handler(this::getUsers);
router.post("/api/users").handler(this::createUser);

// Route with path parameters
router.get("/api/users/:id").handler(this::getUserById);

// Route with query parameters
router.get("/api/search").handler(this::search);

// Body handling
router.route().handler(BodyHandler.create());

// Mount sub-router
router.mountSubRouter("/api", apiRouter);
```

**Request Handling:**
```java
private void getUsers(RoutingContext ctx) {
    List<User> users = userService.findAll();
    ctx.json(users);
}

private void getUserById(RoutingContext ctx) {
    String id = ctx.pathParam("id");
    userService.findById(id)
        .onSuccess(user -> ctx.json(user))
        .onFailure(err -> ctx.fail(404, err));
}

private void search(RoutingContext ctx) {
    String query = ctx.queryParam("q").getOrDefault("");
    int limit = Integer.parseInt(ctx.queryParam("limit").getOrDefault("10"));
    
    userService.search(query, limit)
        .onSuccess(results -> ctx.json(results))
        .onFailure(err -> ctx.fail(500, err));
}
```

### 3.2 Reactive Streams with Vert.x

```java
// Using ReadStream
vertx.fileSystem().open("data.txt", new OpenOptions(), result -> {
    AsyncFileAsyncFile = result.result();
    
    Pump pump = Pumps.pump(file, new WriteStream<Buffer>() {
        @Override
        public void write(Buffer data) {
            System.out.println(data.toString());
        }
        
        @Override
        public void end() {
            System.out.println("Done");
        }
    });
    
    pump.start();
});
```

### 3.3 Vert.x Web Client

```java
WebClient client = WebClient.create(vertx);

// GET request
client.get(8080, "localhost", "/api/users")
    .addQueryParam("page", "1")
    .addQueryParam("size", "20")
    .putHeader("Accept", "application/json")
    .as(BodyCodec.jsonObject())
    .send()
    .onSuccess(resp -> {
        JsonArray users = resp.body().getJsonArray("users");
        System.out.println(users.encode());
    });

// POST request
client.post(8080, "localhost", "/api/users")
    .putHeader("Content-Type", "application/json")
    .as(BodyCodec.jsonObject())
    .sendJsonObject(new JsonObject()
        .put("name", "John")
        .put("email", "john@example.com"))
    .onSuccess(resp -> {
        JsonObject created = resp.body();
        System.out.println("Created: " + created.getString("id"));
    });
```

### 3.4 Circuit Breaker Pattern

```java
CircuitBreakerOptions options = new CircuitBreakerOptions()
    .setMaxFailures(5)              // Failures before opening
    .setTimeout(2000)               // Timeout for operations
    .setResetTimeout(10000)         // Time before trying again
    .setFallbackOnFailure(true);   // Call fallback on failure

CircuitBreaker breaker = CircuitBreaker.create("my-circuit", vertx, options);

breaker.executeWithFallback(promise -> {
    // Operation that might fail
    httpClient.getNow("/api/service", response -> {
        if (response.statusCode() == 200) {
            promise.complete(response.bodyAsString());
        } else {
            promise.fail(new Exception("Failed"));
        }
    });
}, fallback -> {
    // Fallback response
    return "Fallback response";
}).onSuccess(result -> {
    System.out.println("Result: " + result);
});
```

### 3.5 RxJava Integration

```java
import io.reactivex.rxjava3.core.Single;
import io.vertx.rxjava3.core.Vertx;

// RxJavaified Vert.x
Single<String> result = Vertx.rx(vertx)
    .eventBus()
    .<String>request("test", "message")
    .map(msg -> msg.body());

result.subscribe(
    response -> System.out.println(response),
    error -> System.err.println(error)
);

// From callback to Single
Single.just("value")
    .subscribeOn(Vertx.scheduler(vertx))
    .observeOn(AndroidSchedulers.mainThread());

// HTTP with RxJava
WebClient client = WebClient.create(vertx);
Single<HttpResponse<Buffer>> request = client.get(8080, "localhost", "/api")
    .rxSend();

request.subscribe(
    response -> System.out.println(response.bodyAsString()),
    error -> System.err.println(error)
);
```

### 3.6 Vert.x Service Proxy

**Service Interface:**
```java
@ProxyGen
@VertxGen
public interface UserService {
    
    @Fluent
    UserService findById(Long id, Handler<AsyncResult<User>> resultHandler);
    
    @Fluent
    UserService findAll(Handler<AsyncResult<List<User>>> resultHandler);
}
```

**Service Implementation:**
```java
@VertxGen
public class UserServiceImpl implements UserService {
    
    private final UserRepository repository;
    
    public UserServiceImpl(UserRepository repository) {
        this.repository = repository;
    }
    
    @Override
    public UserService findById(Long id, Handler<AsyncResult<User>> resultHandler) {
        User user = repository.findById(id);
        resultHandler.handle(Future.succeededFuture(user));
        return this;
    }
}
```

**Service Usage:**
```java
UserService service = UserServiceProxy.createProxy(vertx, "user-service");

service.findById(1L, result -> {
    if (result.succeeded()) {
        User user = result.result();
    }
});
```

### 3.7 Vert.x Clustering

**Using Hazelcast:**
```xml
<dependency>
    <groupId>io.vertx</groupId>
    <artifactId>vertx-hazelcast</artifactId>
</dependency>
```

```java
Vertx.clusteredVertx(new VertxOptions(), ar -> {
    if (ar.succeeded()) {
        Vertx clusteredVertx = ar.result();
        clusteredVertx.deployVerticle(new MyVerticle());
    }
});
```

**Event Bus in Cluster:**
```java
// In clustered vertx
vertx.eventBus().publish("clustered-address", "message");
```

---

## Key Concepts Summary

| Concept | Description |
|---------|-------------|
| Verticle | Unit of deployment in Vert.x |
| Event Loop | Processes events non-blocking |
| Event Bus | Inter-verticle communication |
| Vert.x Web | HTTP routing and handling |
| Future/Promise | Async result handling |
| Circuit Breaker | Fault tolerance pattern |
| Reactive Streams | Backpressure handling |
| Clustering | Distributed Vert.x |