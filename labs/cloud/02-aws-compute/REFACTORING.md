# Refactoring — AWS Compute

## 1. From EC2 Tomcat to Lambda + API Gateway

### Before
```
EC2 ── Tomcat ── WAR ── Spring MVC
├── Manual scaling
├── Pay per hour (always on)
└── Full OS management
```

### After
```
Lambda ── Spring Cloud Function ── JAR
├── Auto scaling (per request)
├── Pay per invocation (idle = free)
└── No OS management
```

```java
// Before: Spring MVC controller
@RestController
public class UserController {
    @GetMapping("/users/{id}")
    public User getUser(@PathVariable String id) {
        return userService.findById(id);
    }
}

// After: Spring Cloud Function
@Bean
public Function<String, User> getUser() {
    return id -> userService.findById(id);
}
```

## 2. From EC2 to ECS Fargate

### Before (Manual EC2)
- SSH into EC2
- Install Docker, pull image, run container
- Configure restart, monitoring, log rotation
- Manual health checks

### After (Fargate)
- Define task definition (JSON)
- Set desired count = 2
- Fargate handles: placement, networking, health, restart
- CloudWatch logs automatically

```json
{
  "family": "spring-app",
  "containerDefinitions": [{
    "name": "app",
    "image": "123456789.dkr.ecr.us-east-1.amazonaws.com/app:latest",
    "memory": 1024,
    "cpu": 512,
    "portMappings": [{"containerPort": 8080}]
  }],
  "requiresCompatibilities": ["FARGATE"],
  "cpu": "512",
  "memory": "1024"
}
```

## 3. From Lambda Monolith to Function Per Use Case

### Before: One handler with routing switch
```java
public class MonolithHandler implements RequestHandler<Map, String> {
    public String handleRequest(Map event, Context ctx) {
        String path = (String) event.get("path");
        switch(path) {
            case "/users": return handleUsers(event);
            case "/orders": return handleOrders(event);
            // ... 20 more cases
        }
    }
}
```

### After: One function per endpoint
```
┌── user-service    ── /users/*
├── order-service   ── /orders/*
├── payment-service ── /payments/*
└── inventory-svc   ── /inventory/*
```
Each function: independent codebase, scaling, deployment, team ownership.
