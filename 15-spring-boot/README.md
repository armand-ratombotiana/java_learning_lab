# Spring Boot - Framework & REST APIs

## Overview
Spring Boot simplifies Spring application development by providing auto-configuration, embedded servers, and production-ready features. This module covers Spring Boot fundamentals, REST API development, HATEOAS, and production-ready patterns.

## Key Concepts
- **Auto-Configuration**: Automatic bean discovery and configuration
- **Spring Boot Starters**: Pre-configured dependency sets
- **Spring Boot Actuator**: Production monitoring and management
- **REST APIs**: Resource-based API design
- **HATEOAS**: Hypermedia-driven APIs
- **Embedded Servers**: Tomcat, Jetty, Undertow
- **Properties & Configuration**: YAML, properties files, profiles

## Project Structure
```
15-spring-boot/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Application.java
│   ├── controller/
│   ├── service/
│   ├── repository/
│   └── config/
```

## Running
```bash
cd 15-spring-boot
mvn clean compile
mvn spring-boot:run
```

## Concepts Covered
- Spring Boot Auto-Configuration
- Spring Boot Properties & YAML
- Embedded Web Servers
- REST Controller Development
- Request/Response Handling
- Exception Handling
- Validation & Error Messages
- HATEOAS Links & Resources
- Spring Boot Actuator Endpoints
- Custom Starters & Auto-Configuration
- Caching & Async Processing
- Profile-based Configuration

## Dependencies
- Spring Boot Starter Web
- Spring Boot Starter Data JPA
- Spring Boot Starter Validation
- Spring Boot Starter Actuator
- H2 Database (development)