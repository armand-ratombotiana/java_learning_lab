# 54 - Vert.x Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Event Loop | Non-blocking I/O handler |
| Verticle | Deployment unit |
| Handler | Async callback |
| Context | Execution context |
| Future | Async result wrapper |
| Message | Inter-verticle communication |

## Verticle Structure

```java
// Verticle implementation
public class MyVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        // Start async operations
        startPromise.complete();
    }
    
    @Override
    public void stop(Promise<Void> stopPromise) {
        // Cleanup
        stopPromise.complete();
    }
}

// Deploy verticle
vertx.deployVerticle(new MyVerticle());
vertx.deployVerticle("com.example.MyVerticle");
```

## HTTP Server

```java
// Simple HTTP server
vertx.createHttpServer()
    .requestHandler(req -> {
        req.response()
            .putHeader("content-type", "application/json")
            .end("{\"message\":\"Hello\"}");
    })
    .listen(8080, result -> {
        if (result.succeeded()) {
            System.out.println("Server started");
        }
    });

// Router-based routing
Router router = Router.router(vertx);

router.get("/api/users/:id")
    .handler(this::getUser);

router.post("/api/users")
    .consumes("application/json")
    .handler(this::createUser);

vertx.createHttpServer()
    .requestHandler(router)
    .listen(8080);
```

## Event Bus

```java
// Send message (point-to-point)
vertx.eventBus().send("address", "message", result -> {
    if (result.succeeded()) {
        System.out.println("Reply: " + result.result().body());
    }
});

// Publish (fan-out)
vertx.eventBus().publish("news", "Breaking news!");

// Consumer
vertx.eventBus().consumer("address", message -> {
    String body = message.body();
    message.reply("Processed: " + body);
});
```

## Async Patterns

```java
// Future composition
Future<User> userFuture = userService.findById(1);
Future<Order> orderFuture = orderService.getOrder(100);

userFuture.compose(user -> 
    orderFuture.map(order -> new UserOrder(user, order))
).onComplete(ar -> {
    if (ar.succeeded()) {
        UserOrder result = ar.result();
    }
});

// Parallel execution
Future.all(userFuture, orderFuture)
    .onComplete(ar -> {
        if (ar.succeeded()) {
            List<Object> results = ar.result().list();
        }
    });
```

## Database Access

```java
// PostgreSQL client
PostgreSQLClient client = PostgreSQLClient.createShared(
    vertx, 
    new JsonObject()
        .put("host", "localhost")
        .put("port", 5432)
        .put("database", "mydb")
        .put("username", "admin")
        .put("password", "secret")
);

client.query("SELECT * FROM users", result -> {
    ResultSet rs = result.result();
    for (JsonObject row : rs.getRows()) {
        System.out.println(row.getString("name"));
    }
});

// Prepared queries
client.preparedQuery(
    "SELECT * FROM users WHERE id = $1",
    new Tuple().addInteger(1),
    result -> {
        // Process results
    }
);
```

## Web Client

```java
// HTTP client
WebClient client = WebClient.create(vertx);

client.get(8080, "localhost", "/api/users")
    .as(BodyCodec.jsonArray())
    .send(result -> {
        if (result.succeeded()) {
            HttpResponse<JsonArray> response = result.result();
            JsonArray body = response.body();
        }
    });

// POST with JSON
client.post(8080, "localhost", "/api/users")
    .putHeader("Content-Type", "application/json")
    .sendJson(new JsonObject()
        .put("name", "John")
        .put("email", "john@example.com")
    );
```

## Verticles Communication

```java
// Request-response pattern
public class UserServiceVerticle extends AbstractVerticle {
    
    @Override
    public void start() {
        vertx.eventBus().consumer("user.get", message -> {
            Long userId = message.body();
            userRepository.findById(userId)
                .onSuccess(user -> message.reply(user))
                .onFailure(err -> message.fail(500, err.getMessage()));
        });
    }
}

// Calling service
vertx.eventBus().<User>request("user.get", 1L)
    .onSuccess(reply -> {
        User user = reply.body();
    });
```

## Configuration

```yaml
# config.yaml
server:
  port: 8080
  host: "0.0.0.0"

database:
  host: "localhost"
  port: 5432

app:
  name: myapp

# Load config
ConfigStore store = ConfigStoreOptions()
    .setType("file")
    .setConfig(new JsonObject()
        .put("path", "config.yaml"));

ConfigRetriever retriever = ConfigRetriever.create(vertx,
    new ConfigRetrieverOptions().addStore(store));

retriever.getConfig(ar -> {
    JsonObject config = ar.result();
});
```

## Best Practices

Never block the event loop. Use executeBlocking for blocking operations. Leverage Future composition for async workflows. Use event bus for loose coupling between verticles. Keep verticles focused and single-purpose. Use clustered mode for scaling.