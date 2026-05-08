# Spring WebFlux - Exercises

## Lab Exercises

### Exercise 1: Run Application
```bash
mvn spring-boot:run
```
- Note: Uses Netty (not Tomcat)
- Reactive stack replaces blocking I/O

### Exercise 2: Understand Reactive Types
- Identify `Mono` and `Flux` in code
- Understand Lazy execution
- Explore backpressure

### Exercise 3: Reactive Controller
- Review functional endpoints
- Note `ServerRequest` / `ServerResponse`
- Understand route definitions

### Exercise 4: WebClient
- Create reactive WebClient
- Test external API calls
- Handle async responses

### Exercise 5: Database Operations
- Use R2DBC (Reactive DB access)
- Note non-blocking DB calls
- Compare with blocking JPA

### Exercise 6: Testing Reactive
- Use `StepVerifier` for testing
- Test Mono/Flux streams
- Verify error handling

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

## Testing
```bash
curl http://localhost:8080/webflux/data
```