# 53 - Helidon Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| MicroProfile | Jakarta EE baseline |
| WebServer | Built-in reactive web server |
| Config | Flexible configuration system |
| SE | Standalone (no container) |
| MP | MicroProfile (Jakarta EE) |
| Health | Built-in health checks |

## Helidon SE

```java
// Simple web server setup
public class Main {
    public static void main(String[] args) {
        WebServer.builder()
            .routing(routing -> routing
                .get("/health", (req, res) -> res.send("OK"))
                .get("/api/hello", (req, res) -> 
                    res.send("Hello, " + req.queryParams().first("name").orElse("World")))
            )
            .port(8080)
            .build()
            .start();
    }
}
```

## Helidon MP

```java
// JAX-RS endpoint
@ApplicationScoped
@Path("/api")
public class UserResource {
    
    @Inject
    private UserService userService;
    
    @GET
    @Path("/users/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public User getUser(@PathParam("id") Long id) {
        return userService.findById(id);
    }
    
    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response createUser(User user) {
        User created = userService.create(user);
        return Response.status(Status.CREATED)
            .entity(created)
            .build();
    }
}
```

## Configuration

```yaml
# application.yaml
server:
  port: 8080
  host: 0.0.0.0

database:
  url: jdbc:postgresql://localhost:5432/mydb
  username: admin
  password: ${DB_PASSWORD}
  pool:
    size: 10

tracing:
  service: myapp

metrics:
  enabled: true
```

```java
// Programmatic config
Config config = ConfigSources.from(
    ConfigSource.file("config.yaml"),
    ConfigSource.classpath("application.yaml")
).build();

String value = config.get("key").as(String.class);
```

## Database Access

```java
// Helidon DB Client (SE)
public class UserService {
    private final DbClient dbClient;
    
    public UserService(DbClient dbClient) {
        this.dbClient = dbClient;
    }
    
    public Mono<User> findById(Long id) {
        return dbClient.execute(exec -> 
            exec.createQuery("SELECT * FROM users WHERE id = ?", id)
                .map(row -> new User(
                    row.read("id", Long.class),
                    row.read("name", String.class)
                ))
                .first()
        );
    }
}
```

```java
// Helidon MP with Panache
@ApplicationScoped
public class UserService {
    
    @Inject
    private EntityManager em;
    
    public User findById(Long id) {
        return em.find(User.class, id);
    }
}
```

## WebSocket

```java
@WebSocketEndpoint("/ws")
public class HelloWebSocket {
    
    @OnOpen
    public void onOpen(Session session) {
        session.send("Welcome!");
    }
    
    @OnMessage
    public String onMessage(String message) {
        return "Got: " + message;
    }
    
    @OnError
    public void onError(Session session, Throwable error) {
        // Handle error
    }
    
    @OnClose
    public void onClose(Session session, 
            String reason, boolean closed) {
        // Cleanup
    }
}
```

## Health Checks

```java
// Built-in health check
@Health
@ApplicationScoped
public class CustomHealthCheck implements HealthCheck {
    
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.builder()
            .up()
            .withData("custom", "healthy")
            .build();
    }
}

// MP health
@Readiness
public class ReadinessCheck implements HealthCheck {
    @Override
    public HealthCheckResponse call() {
        return HealthCheckResponse.up();
    }
}
```

## Metrics

```java
@ApplicationScoped
public class MetricsService {
    
    @Inject
    private MetricRegistry registry;
    
    @Timed(name = "request.time")
    public void processRequest() {
        // Method timing tracked
    }
}

// Custom counter
Counter counter = registry.counter("my.counter");
counter.inc();
```

## Testing

```java
// Helidon SE test
public class MainTest {
    
    @Test
    public void testHealth() {
        WebServer webServer = WebServer.builder()
            .routing(routing -> 
                routing.get("/health", 
                    (req, res) -> res.send("OK")))
            .build()
            .start();
        
        try {
            WebClient client = WebClient.create();
            String result = client.get(
                "http://localhost:" + webServer.port() + "/health"
            ).await();
            assertEquals("OK", result);
        } finally {
            webServer.shutdown();
        }
    }
}
```

## Best Practices

Use Helidon SE for lightweight microservices. Use Helidon MP for Jakarta EE compatibility. Leverage built-in web server without external containers. Use DB Client for reactive database operations. Take advantage of health checks for container orchestration.