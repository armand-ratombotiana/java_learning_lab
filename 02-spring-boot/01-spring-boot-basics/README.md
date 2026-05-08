# Spring Boot Basics

A foundational module introducing Spring Boot core concepts.

## Overview

This module covers the essentials of Spring Boot:
- Application setup with `@SpringBootApplication`
- Auto-configuration原理
- Dependency injection with Spring beans
- REST endpoints with `@RestController`
- Embedded server (Tomcat/Jetty)
- Spring Boot starters

## Key Files

- `src/main/java/com/learning/boot/Lab.java` - Main application
- `src/main/java/com/learning/boot/GreetingService.java` - Service bean
- `src/main/java/com/learning/boot/DemoController.java` - REST controller

## Running

```bash
mvn clean install
mvn spring-boot:run
```

## Endpoints

| Endpoint | Description |
|----------|-------------|
| `GET /api/hello/{name}` | Greeting endpoint |
| `GET /api/lessons` | List of lessons |
| `GET /api/status` | Application status |

## Dependencies

- spring-boot-starter
- spring-boot-starter-web

## Version

Spring Boot 3.3.0