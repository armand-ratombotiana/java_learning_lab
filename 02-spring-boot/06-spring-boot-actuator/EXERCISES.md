# Spring Boot Actuator - Exercises

## Lab Exercises

### Exercise 1: Run Application
```bash
mvn spring-boot:run
```
- Actuator endpoints enabled
- Access /actuator/health

### Exercise 2: Explore Actuator Endpoints
- List all available endpoints
- Understand each endpoint purpose
- Note: Some require authentication

### Exercise 3: Configuration
- Enable/disable endpoints in config:
```properties
management.endpoints.web.exposure.include=health,info,metrics
management.endpoint.health.show-details=always
```

### Exercise 4: Custom Health Indicator
- Implement `HealthIndicator`
- Return custom health status
- Add to /actuator/health response

### Exercise 5: Metrics
- Access /actuator/metrics
- View HTTP request metrics
- Use custom metrics

### Exercise 6: Info Endpoint
- Add info from build.gradle:
```properties
management.info.build.enabled=true
info.app.name=My Application
```

## Build & Run
```bash
mvn clean install
mvn spring-boot:run
```

## Endpoints
- GET /actuator/health
- GET /actuator/info
- GET /actuator/metrics
- GET /actuator/env