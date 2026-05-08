# Spring Boot Reactive - Exercises

## Lab Exercises

### Exercise 1: Run Application
```bash
mvn spring-boot:run
```
- Note reactive stack (WebFlux)
- Non-blocking execution

### Exercise 2: Reactive Programming
- Explore Mono and Flux
- Understand event-driven model
- Note "lazy" nature of reactive types

### Exercise 3: Reactive Repositories
- Find reactive database access
- Understand R2DBC
- Compare with blocking JPA

### Exercise 4: WebClient
- Use WebClient for API calls
- Handle async responses
- Test with external service

### Exercise 5: Error Handling
- Handle errors in reactive chain
- Use onErrorResume, onErrorReturn
- Implement retry logic

### Exercise 6: Testing Reactive
- Use StepVerifier
- Test Mono/Flux behavior
- Verify stream operations

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

## Testing
```bash
curl http://localhost:8080/reactive/data
```