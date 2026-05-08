# Spring Boot Basics - Exercises

## Lab Exercises

### Exercise 1: Run the Application
```bash
mvn spring-boot:run
```
- Access http://localhost:8080/api/hello/YourName
- Check http://localhost:8080/api/lessons

### Exercise 2: Understand Auto-Configuration
- Examine `Lab.java` - note `@SpringBootApplication` combines:
  - `@SpringBootConfiguration`
  - `@EnableAutoConfiguration`
  - `@ComponentScan`
- Try running WITHOUT `mvn spring-boot:run` to see embedded server

### Exercise 3: Dependency Injection
- Add a new bean in `Lab.java`
- Inject it into `GreetingService`
- Create a new endpoint that uses the injected bean

### Exercise 4: REST Controller
- Add `@PostMapping("/api/hello")` endpoint
- Accept a `name` request body and return greeting

### Exercise 5: Customize Configuration
- Create `application.properties`:
  ```properties
  server.port=9090
  spring.application.name=my-app
  ```
- Add custom property and inject with `@Value`

### Exercise 6: Add Spring Boot Starter
- Add `spring-boot-starter-actuator` dependency
- Access `/actuator/health` endpoint

## Getting Started
1. Ensure Maven is installed
2. Run `mvn clean install`
3. Start with `mvn spring-boot:run`