# Layered Architecture Exercises

## Beginner Exercises

### Exercise 1: Identify Layers
Given a Spring Boot application, identify which files belong to each layer.

### Exercise 2: Create a Layered CRUD
Build a simple Product CRUD with controller, service, and repository layers.

## Intermediate Exercises

### Exercise 3: Add Business Logic
Add validation and business rules to the simple CRUD:
- No duplicate product names
- Price must be positive
- Category must exist before product creation

### Exercise 4: Exception Handling
Add a global exception handler and create custom exceptions for each error case.

### Exercise 5: Add Cross-Cutting
Add logging aspect and performance monitoring to the service layer:
```java
@Aspect
@Component
public class LoggingAspect { }
```

## Advanced Exercises

### Exercise 6: N-Tier Deployment
Create Docker Compose setup with separate containers for:
- Web tier (Spring Boot)
- App tier (Spring Boot, separate)
- Database tier (PostgreSQL)
Configure them to communicate correctly.

### Exercise 7: Multi-Module Project
Organize the application as Maven multi-module project:
- api-module (controllers, DTOs)
- service-module (services)
- data-module (repositories, entities)

### Exercise 8: Detect Layer Violations
Write ArchUnit tests to detect and prevent:
- Controllers accessing repositories directly
- Services accessing database-specific features
- Circular dependencies between layers
