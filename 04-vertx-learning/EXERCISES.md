# Vert.x Learning Exercises

## Exercise 1: Basic Verticle and HTTP Server

**Problem:** Create a Vert.x application that serves a REST API for a simple user service.

**Requirements:**
1. Create a verticle that starts an HTTP server
2. Handle GET, POST, PUT, DELETE endpoints
3. Return proper JSON responses
4. Use async patterns properly

**Solution:**

```java
// User.java
public class User {
    private Long id;
    private String name;
    private String email;
    
    public User() {}
    
    public User(Long id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }
    
    // Getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}

// UserVerticle.java
public class UserVerticle extends AbstractVerticle {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1;
    
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        
        router.get("/api/users").handler(this::getAllUsers);
        router.get("/api/users/:id").handler(this::getUser);
        router.post("/api/users").handler(this::createUser);
        router.put("/api/users/:id").handler(this::updateUser);
        router.delete("/api/users/:id").handler(this::deleteUser);
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080, ar -> {
                if (ar.succeeded()) {
                    System.out.println("Server started on port 8080");
                    startPromise.complete();
                } else {
                    startPromise.fail(ar.cause());
                }
            });
    }
    
    private void getAllUsers(RoutingContext ctx) {
        ctx.json(users.values().stream().toList());
    }
    
    private void getUser(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        Long id = Long.parseLong(idParam);
        
        User user = users.get(id);
        if (user != null) {
            ctx.json(user);
        } else {
            ctx.response()
                .setStatusCode(404)
                .end(Json.encode(new JsonObject().put("error", "User not found")));
        }
    }
    
    private void createUser(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        
        User user = new User(nextId++, 
            body.getString("name"), 
            body.getString("email"));
        
        users.put(user.getId(), user);
        
        ctx.response()
            .setStatusCode(201)
            .json(user);
    }
    
    private void updateUser(RoutingContext ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        
        if (!users.containsKey(id)) {
            ctx.response().setStatusCode(404).end("User not found");
            return;
        }
        
        JsonObject body = ctx.body().asJsonObject();
        User user = users.get(id);
        user.setName(body.getString("name"));
        user.setEmail(body.getString("email"));
        
        ctx.json(user);
    }
    
    private void deleteUser(RoutingContext ctx) {
        Long id = Long.parseLong(ctx.pathParam("id"));
        
        if (users.remove(id) != null) {
            ctx.response().setStatusCode(204).end();
        } else {
            ctx.response().setStatusCode(404).end("User not found");
        }
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        vertx.deployVerticle(new UserVerticle());
    }
}
```

---

## Exercise 2: Event Bus Communication

**Problem:** Implement a distributed system using Vert.x event bus for communication.

**Requirements:**
1. Create separate verticles for API, user service, and notification
2. Use event bus for inter-verticle communication
3. Handle request-response pattern
4. Implement publish-subscribe for notifications

**Solution:**

```java
// UserServiceVerticle.java
public class UserServiceVerticle extends AbstractVerticle {
    
    private final Map<Long, User> users = new ConcurrentHashMap<>();
    private long nextId = 1;
    
    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().consumer("user.create", this::createUser);
        vertx.eventBus().consumer("user.get", this::getUser);
        vertx.eventBus().consumer("user.getAll", this::getAllUsers);
        vertx.eventBus().consumer("user.update", this::updateUser);
        vertx.eventBus().consumer("user.delete", this::deleteUser);
        
        System.out.println("User service ready");
        startPromise.complete();
    }
    
    private void createUser(Message<JsonObject> msg) {
        JsonObject body = msg.body();
        User user = new User(nextId++, 
            body.getString("name"), 
            body.getString("email"));
        
        users.put(user.getId(), user);
        
        msg.reply(JsonObject.of(
            "id", user.getId(),
            "name", user.getName(),
            "email", user.getEmail()
        ));
        
        // Publish user created event
        vertx.eventBus().publish("user.events", 
            JsonObject.of("type", "created", "userId", user.getId()));
    }
    
    private void getUser(Message<JsonObject> msg) {
        Long id = msg.body().getLong("id");
        User user = users.get(id);
        
        if (user != null) {
            msg.reply(JsonObject.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail()
            ));
        } else {
            msg.fail(404, "User not found");
        }
    }
    
    private void getAllUsers(Message<JsonObject> msg) {
        List<JsonObject> result = users.values().stream()
            .map(u -> JsonObject.of("id", u.getId(), "name", u.getName(), "email", u.getEmail()))
            .toList();
        
        msg.reply(new JsonArray(result));
    }
    
    private void updateUser(Message<JsonObject> msg) {
        JsonObject body = msg.body();
        Long id = body.getLong("id");
        
        User user = users.get(id);
        if (user != null) {
            user.setName(body.getString("name"));
            user.setEmail(body.getString("email"));
            msg.reply(JsonObject.of("success", true));
        } else {
            msg.fail(404, "User not found");
        }
    }
    
    private void deleteUser(Message<JsonObject> msg) {
        Long id = msg.body().getLong("id");
        
        if (users.remove(id) != null) {
            msg.reply(JsonObject.of("success", true));
            vertx.eventBus().publish("user.events", 
                JsonObject.of("type", "deleted", "userId", id));
        } else {
            msg.fail(404, "User not found");
        }
    }
}

// NotificationVerticle.java
public class NotificationVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        vertx.eventBus().<JsonObject>consumer("user.events", msg -> {
            JsonObject event = msg.body();
            String type = event.getString("type");
            System.out.println("Notification: User " + type);
            
            if ("created".equals(type)) {
                sendEmailNotification(event.getLong("userId"));
            }
        });
        
        System.out.println("Notification service ready");
        startPromise.complete();
    }
    
    private void sendEmailNotification(Long userId) {
        System.out.println("Sending email for user: " + userId);
    }
}

// ApiVerticle.java
public class ApiVerticle extends AbstractVerticle {
    
    @Override
    public void start(Promise<Void> startPromise) {
        Router router = Router.router(vertx);
        
        router.get("/api/users").handler(ctx -> {
            vertx.eventBus().request("user.getAll", new JsonObject())
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        ctx.json(ar.result().body());
                    } else {
                        ctx.fail(500);
                    }
                });
        });
        
        router.get("/api/users/:id").handler(ctx -> {
            String idParam = ctx.pathParam("id");
            JsonObject request = new JsonObject().put("id", Long.parseLong(idParam));
            
            vertx.eventBus().<JsonObject>request("user.get", request)
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        ctx.json(ar.result().body());
                    } else {
                        ctx.fail(404);
                    }
                });
        });
        
        router.post("/api/users").handler(ctx -> {
            JsonObject body = ctx.body().asJsonObject();
            vertx.eventBus().request("user.create", body)
                .onComplete(ar -> {
                    if (ar.succeeded()) {
                        ctx.json(ar.result().body());
                    } else {
                        ctx.fail(500);
                    }
                });
        });
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(s -> {
                System.out.println("API server ready on port 8080");
                startPromise.complete();
            });
    }
}

// Main.java
public class Main {
    public static void main(String[] args) {
        Vertx vertx = Vertx.vertx();
        
        vertx.deployVerticle(new UserServiceVerticle());
        vertx.deployVerticle(new NotificationVerticle());
        vertx.deployVerticle(new ApiVerticle());
    }
}
```

---

## Exercise 3: Async Database Operations

**Problem:** Implement async database operations using Vert.x SQL client.

**Requirements:**
1. Connect to PostgreSQL
2. Execute queries asynchronously
3. Handle transactions
4. Use prepared statements

**Solution:**

```java
// DatabaseClientExample.java
public class DatabaseVerticle extends AbstractVerticle {
    
    private SQLClient client;
    
    @Override
    public void start(Promise<Void> startPromise) {
        JsonObject config = new JsonObject()
            .put("host", "localhost")
            .put("port", 5432)
            .put("database", "mydb")
            .put("username", "user")
            .put("password", "password")
            .put("max_pool_size", 10);
        
        client = PostgreSQLClient.createShared(vertx, config);
        
        initializeSchema()
            .compose(v -> startHttpServer())
            .onSuccess(v -> {
                System.out.println("Database verticle started");
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }
    
    private Future<Void> initializeSchema() {
        Promise<Void> promise = Promise.promise();
        
        client.query("CREATE TABLE IF NOT EXISTS users (" +
            "id SERIAL PRIMARY KEY, " +
            "name VARCHAR(255) NOT NULL, " +
            "email VARCHAR(255) UNIQUE NOT NULL, " +
            "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP)")
            .execute()
            .onSuccess(v -> {
                System.out.println("Schema initialized");
                promise.complete();
            })
            .onFailure(promise::fail);
        
        return promise.future();
    }
    
    private Future<Void> startHttpServer() {
        Promise<Void> promise = Promise.promise();
        
        Router router = Router.router(vertx);
        
        router.get("/api/db/users").handler(this::getAllUsers);
        router.get("/api/db/users/:id").handler(this::getUserById);
        router.post("/api/db/users").handler(this::createUser);
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(s -> promise.complete())
            .onFailure(promise::fail);
        
        return promise.future();
    }
    
    private void getAllUsers(RoutingContext ctx) {
        client.query("SELECT * FROM users ORDER BY id")
            .execute()
            .onSuccess(rows -> {
                List<JsonObject> users = new ArrayList<>();
                for (Row row : rows) {
                    users.add(JsonObject.of(
                        "id", row.getLong("id"),
                        "name", row.getString("name"),
                        "email", row.getString("email")
                    ));
                }
                ctx.json(users);
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    private void getUserById(RoutingContext ctx) {
        String idParam = ctx.pathParam("id");
        
        client.preparedQuery("SELECT * FROM users WHERE id = $1")
            .execute(Tuple.of(Integer.parseInt(idParam)))
            .onSuccess(rows -> {
                if (rows.rowCount() > 0) {
                    Row row = rows.iterator().next();
                    ctx.json(JsonObject.of(
                        "id", row.getLong("id"),
                        "name", row.getString("name"),
                        "email", row.getString("email")
                    ));
                } else {
                    ctx.response().setStatusCode(404).end();
                }
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    private void createUser(RoutingContext ctx) {
        JsonObject body = ctx.body().asJsonObject();
        
        client.preparedQuery(
            "INSERT INTO users (name, email) VALUES ($1, $2) RETURNING id, name, email")
            .execute(Tuple.of(body.getString("name"), body.getString("email")))
            .onSuccess(rows -> {
                Row row = rows.iterator().next();
                ctx.response().setStatusCode(201)
                    .json(JsonObject.of(
                        "id", row.getLong("id"),
                        "name", row.getString("name"),
                        "email", row.getString("email")
                    ));
            })
            .onFailure(err -> ctx.fail(500, err));
    }
    
    @Override
    public void stop(Promise<Void> stopPromise) {
        if (client != null) {
            client.close();
        }
        stopPromise.complete();
    }
}
```

---

## Exercise 4: Circuit Breaker Implementation

**Problem:** Implement fault tolerance using circuit breaker pattern.

**Requirements:**
1. Configure circuit breaker with proper settings
2. Implement fallback logic
3. Handle different failure scenarios
4. Monitor circuit state

**Solution:**

```java
// CircuitBreakerExample.java
public class FaultTolerantVerticle extends AbstractVerticle {
    
    private CircuitBreaker breaker;
    private WebClient webClient;
    
    @Override
    public void start(Promise<Void> startPromise) {
        webClient = WebClient.create(vertx);
        
        breaker = CircuitBreaker.create("backend-service", vertx,
            new CircuitBreakerOptions()
                .setMaxFailures(3)
                .setTimeout(2000)
                .setResetTimeout(10000)
                .setFallbackOnFailure(true));
        
        breaker.openHandler(v -> 
            System.out.println("Circuit opened - service unavailable"));
        
        breaker.closeHandler(v -> 
            System.out.println("Circuit closed - service recovered"));
        
        breaker.halfOpenHandler(v -> 
            System.out.println("Circuit half-open - testing service"));
        
        Router router = Router.router(vertx);
        
        router.get("/api/external").handler(this::callExternalService);
        router.get("/api/status").handler(this::getCircuitStatus);
        
        vertx.createHttpServer()
            .requestHandler(router)
            .listen(8080)
            .onSuccess(s -> startPromise.complete())
            .onFailure(startPromise::fail);
    }
    
    private void callExternalService(RoutingContext ctx) {
        breaker.executeWithFallback(promise -> {
            // Primary operation - call external service
            webClient.get(9999, "localhost", "/health")
                .send()
                .onSuccess(response -> {
                    if (response.statusCode() == 200) {
                        promise.complete(response.bodyAsString());
                    } else {
                        promise.fail(new Exception("Service returned: " + response.statusCode()));
                    }
                })
                .onFailure(promise::fail);
        }, fallback -> {
            // Fallback response when circuit is open or operation fails
            return JsonObject.of(
                "status", "fallback",
                "message", "Service temporarily unavailable",
                "data", getCachedData()
            ).encode();
        }).onSuccess(result -> {
            ctx.json(JsonObject.of(
                "status", "success",
                "data", result
            ));
        }).onFailure(err -> {
            ctx.json(JsonObject.of(
                "status", "error",
                "message", "Operation failed"
            ));
        });
    }
    
    private String getCachedData() {
        // Return cached response from last successful call
        return "{\"cached\": true, \"data\": \"sample\"}";
    }
    
    private void getCircuitStatus(RoutingContext ctx) {
        String state = breaker.state().name();
        int failures = breaker.failureCount();
        
        ctx.json(JsonObject.of(
            "state", state,
            "failureCount", failures,
            "name", "backend-service"
        ));
    }
    
    @Override
    public void stop(Promise<Void> stopPromise) {
        webClient.close();
        breaker.close();
        stopPromise.complete();
    }
}
```

---

## Exercise 5: Real-time WebSocket Server

**Problem:** Implement a WebSocket server for real-time communication.

**Requirements:**
1. Handle WebSocket connections
2. Broadcast messages to all clients
3. Handle connection lifecycle events
4. Support JSON messages

**Solution:**

```java
// WebSocketChatVerticle.java
public class WebSocketChatVerticle extends AbstractVerticle {
    
    private final Set<ServerWebSocket> clients = ConcurrentHashMap.newKeySet();
    
    @Override
    public void start(Promise<Void> startPromise) {
        HttpServer server = vertx.createHttpServer();
        
        server.webSocketHandler(ws -> {
            System.out.println("New WebSocket connection: " + ws.textHandlerID());
            clients.add(ws);
            
            // Handle text messages
            ws.textMessageHandler(message -> {
                JsonObject json = new JsonObject(message);
                handleMessage(ws, json);
            });
            
            // Handle close
            ws.closeHandler(v -> {
                System.out.println("WebSocket closed: " + ws.textHandlerID());
                clients.remove(ws);
            });
            
            // Handle errors
            ws.exceptionHandler(err -> {
                System.err.println("WebSocket error: " + err.getMessage());
                clients.remove(ws);
            });
            
            // Send welcome message
            ws.writeTextMessage(JsonObject.of(
                "type", "welcome",
                "message", "Connected to chat server"
            ).encode());
        });
        
        server.listen(8080)
            .onSuccess(s -> {
                System.out.println("WebSocket server on port 8080");
                startPromise.complete();
            })
            .onFailure(startPromise::fail);
    }
    
    private void handleMessage(ServerWebSocket ws, JsonObject message) {
        String type = message.getString("type");
        
        switch (type) {
            case "chat" -> {
                String text = message.getString("text");
                String sender = message.getString("sender", "Anonymous");
                
                JsonObject broadcast = JsonObject.of(
                    "type", "chat",
                    "sender", sender,
                    "text", text,
                    "timestamp", System.currentTimeMillis()
                );
                
                broadcastToAll(broadcast, ws);
            }
            
            case "broadcast" -> {
                String text = message.getString("text");
                
                JsonObject broadcast = JsonObject.of(
                    "type", "broadcast",
                    "text", text,
                    "sender", "System"
                );
                
                broadcastToAll(broadcast, null);
            }
            
            case "ping" -> {
                ws.writeTextMessage(JsonObject.of(
                    "type", "pong",
                    "timestamp", System.currentTimeMillis()
                ).encode());
            }
        }
    }
    
    private void broadcastToAll(JsonObject message, ServerWebSocket exclude) {
        String encoded = message.encode();
        
        clients.forEach(client -> {
            if (client != exclude && client.isClosed()) {
                client.writeTextMessage(encoded);
            }
        });
        
        // Remove closed connections
        clients.removeIf(WebSocketBase::isClosed);
    }
    
    @Override
    public void stop(Promise<Void> stopPromise) {
        clients.forEach(ws -> ws.close());
        clients.clear();
        stopPromise.complete();
    }
}

// Client example (HTML/JavaScript)
/*
const ws = new WebSocket('ws://localhost:8080');

ws.onmessage = (event) => {
    const data = JSON.parse(event.data);
    console.log('Received:', data);
};

ws.send(JSON.stringify({
    type: 'chat',
    text: 'Hello world',
    sender: 'User1'
}));
*/
```

---

## Summary

| Exercise | Key Concepts |
|----------|-------------|
| 1 | Verticle lifecycle, HTTP server, routing, JSON |
| 2 | Event bus, message patterns, service communication |
| 3 | SQL client, async queries, transactions |
| 4 | Circuit breaker, fault tolerance, fallbacks |
| 5 | WebSocket, real-time, broadcast |