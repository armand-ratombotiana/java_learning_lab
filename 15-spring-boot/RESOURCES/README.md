# Spring Boot Resources

Quick reference materials for Spring Boot development.

## Contents

- [Spring Boot Cheat Sheet](./spring-boot-cheat-sheet.md) - Essential annotations, properties, and patterns
- [Official Documentation](#official-documentation)
- [Key Concepts](#key-concepts)

---

## Official Documentation

| Topic | Link |
|-------|------|
| Spring Boot Reference | https://docs.spring.io/spring-boot/docs/current/reference/ |
| Spring Boot API | https://docs.spring.io/spring-boot/docs/current/api/ |
| Spring Framework Docs | https://docs.spring.io/spring-framework/docs/current/reference/ |
| Spring Initializr | https://start.spring.io/ |

---

## Key Concepts

### Core Features
- **Auto-configuration** - Automatic bean configuration based on classpath
- **Starter POMs** - Convenient dependency descriptors
- **Actuator** - Production-ready features
- **Embedded Server** - Run as standalone JAR

### Common Annotations
- `@SpringBootApplication` - Main entry point
- `@RestController` - REST endpoints
- `@Service` - Business logic
- `@Repository` - Data access
- `@Configuration` - Bean definitions

### Configuration
- `application.properties` / `application.yml`
- Profile-specific: `application-{profile}.properties`
- Environment variables and external config