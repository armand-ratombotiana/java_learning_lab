# Spring Boot Microservices - Exercises

## Lab Exercises

### Exercise 1: Run Services
```bash
mvn spring-boot:run
```
- Multiple service instances
- Service discovery ready

### Exercise 2: Service Architecture
- Identify service components
- Understand service boundaries
- Review communication patterns

### Exercise 3: Service Discovery
- Register with Eureka/Consul (if configured)
- Dynamic service location
- Load balancing

### Exercise 4: Inter-service Communication
- Use RestTemplate or WebClient
- Feign client (if present)
- Circuit breaker

### Exercise 5: Configuration
- Externalized config
- Environment-specific settings
- Config server (if used)

### Exercise 6: Distributed Tracing
- Add tracing correlation IDs
- Log aggregation
- Debug distributed calls

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

## Port Configuration
Set different ports for each service instance.