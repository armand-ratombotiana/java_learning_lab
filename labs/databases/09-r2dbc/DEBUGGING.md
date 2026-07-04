# Debugging: R2DBC

## Enable Logging
```properties
logging.level.io.r2dbc=DEBUG
logging.level.org.springframework.data.r2dbc=DEBUG
logging.level.org.springframework.r2dbc=DEBUG
```

## Common Issues

| Symptom | Cause | Fix |
|---|---|---|
| `NoSuchElementException` | `.one()` on empty result | Use `.first()` (Mono.empty if no results) |
| Connection refused | Database not running | Check `spring.r2dbc.url` and port |
| `Transaction rolled back` | Error in reactive stream | Add `.onErrorResume()` error handling |
| Blocking call detected | JDBC or Thread.sleep in chain | Move to `Schedulers.boundedElastic()` |
| Memory leak | Unbounded fetch + slow consumer | Apply `.limitRate()` |

## Debugging Reactive Chains
```java
// Log elements passing through
repository.findAll()
    .log("user-repository")
    .doOnNext(user -> log.debug("Processing: {}", user))
    .doOnError(err -> log.error("Error: {}", err.getMessage()))
    .doFinally(signal -> log.info("Complete: {}", signal));
```

## Testing
```java
@SpringBootTest
class UserRepositoryTest {
    @Autowired private UserRepository repository;

    @Test
    void testFindAll() {
        StepVerifier.create(repository.findAll())
            .expectNextCount(3)
            .verifyComplete();
    }
}
```
