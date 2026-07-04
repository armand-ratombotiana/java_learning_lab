# Code Deep Dive: Helidon

## Helidon SE Reactive Application
```java
public class Main {
    public static void main(String[] args) {
        ServerConfig config = ServerConfig.builder()
            .port(8080)
            .build();

        Health health = Health.builder()
            .add(HealthChecks.healthChecks())
            .build();

        Routing routing = Routing.builder()
            .register(health)
            .register(MetricsFeature.create())
            .get("/api/users", (req, res) -> {
                res.send(Json.createArrayBuilder()
                    .add(Json.createObjectBuilder()
                        .add("id", 1).add("name", "John"))
                    .build());
            })
            .get("/api/users/{id}", (req, res) -> {
                String id = req.path().param("id");
                // fetch user
                res.send("User: " + id);
            })
            .build();

        WebServer.builder(routing)
            .config(config)
            .build()
            .start();
    }
}
```
