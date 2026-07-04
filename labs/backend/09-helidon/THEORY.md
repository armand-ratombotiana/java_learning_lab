# Theory: Helidon

## Two Flavors
Helidon offers two programming models:

### Helidon SE
- Functional, reactive programming style
- Immutable routing configuration
- Built on Java Flow (reactive streams)
- Lighter weight, fewer dependencies
- Programmatic configuration

### Helidon MP
- MicroProfile 6.0 compatible
- Annotations-based (JAX-RS, CDI, JPA)
- Familiar to Jakarta EE developers
- Better for migrating existing applications

## Reactive Routing (SE)
```java
Routing.builder()
    .get("/", (req, res) -> res.send("Hello"))
    .get("/users/{id}", this::getUser)
    .build();
```

## Health Checks
Helidon provides built-in health check support with readiness/liveness probes.
