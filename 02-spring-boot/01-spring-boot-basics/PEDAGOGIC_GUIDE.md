# Spring Boot Basics - Pedagogic Guide

## Learning Path

### Phase 1: Core Concepts (Day 1)
1. **Spring Boot Application** - `@SpringBootApplication` as the entry point
2. **Auto-Configuration** - How Spring Boot auto-configures based on classpath
3. **Spring Boot Starters** - Pre-configured dependency bundles
4. **Embedded Server** - No need for external Tomcat/Jetty

### Phase 2: Dependency Injection (Day 2)
1. **@Bean** - Defining beans in configuration classes
2. **@Service** - Business logic layer
3. **@Autowired** - Automatic bean injection
4. **Constructor Injection** - Preferred DI pattern

### Phase 3: REST Controllers (Day 3)
1. **@RestController** - REST endpoint annotation
2. **@RequestMapping** - URL path mapping
3. **@GetMapping, @PostMapping** - HTTP method mapping
4. **@PathVariable** - URL parameter extraction

## Key Concepts

### Auto-Configuration
- Spring Boot checks classpath for available libraries
- Creates appropriate beans automatically
- Can override with explicit configuration

### Spring Boot Starters
- `spring-boot-starter` - Core auto-config
- `spring-boot-starter-web` - Web + REST
- `spring-boot-starter-data-jpa` - Database access
- `spring-boot-starter-security` - Security

### Configuration
- `application.properties` or `application.yml`
- Override defaults with custom values
- Profile-specific configuration (`application-dev.properties`)

## Code Structure Patterns
```
com.learning.boot/
├── Lab.java              # Main application class
├── GreetingService.java  # @Service bean
└── DemoController.java   # @RestController
```

## Common Patterns
1. **Constructor Injection** - Recommended over field injection
2. **Records** - For simple DTOs (Java 16+)
3. **Lombok** - Reduce boilerplate (optional)

## Best Practices
- Keep main class in root package
- Use constructor injection
- Follow REST naming conventions
- Externalize configuration