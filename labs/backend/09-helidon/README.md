# Helidon

Helidon is a set of Java libraries for building microservices, with SE and MP flavors.

## Topics
- Helidon SE (reactive, functional)
- Helidon MP (MicroProfile compatible)
- Reactive routing
- Health checks and metrics
- OpenAPI support
- Config and security
- Database integration with Helidon DB

## Example
```java
// Helidon SE routing
Routing.builder()
    .get("/api/users/{id}", (req, res) -> {
        String id = req.path().param("id");
        res.send(userService.findById(id));
    })
    .build();
```
