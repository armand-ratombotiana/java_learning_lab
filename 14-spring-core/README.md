# Spring Core - Dependency Injection & IoC Container

## Overview
Spring Core is the foundation of the Spring Framework, providing the Inversion of Control (IoC) container and Dependency Injection (DI) capabilities. This module covers the core concepts of Spring's DI container, bean lifecycle, AOP fundamentals, and advanced configuration patterns.

## Key Concepts
- **Inversion of Control (IoC)**: Framework controls object creation and lifecycle
- **Dependency Injection**: Dependencies are injected rather than created
- **Bean Scopes**: singleton, prototype, request, session, application
- **Spring Container**: BeanFactory and ApplicationContext
- **AOP (Aspect-Oriented Programming)**: Cross-cutting concerns separation
- **Bean Post-Processors**: Custom initialization and destruction logic

## Project Structure
```
14-spring-core/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── config/
│   ├── service/
│   ├── repository/
│   └── aspect/
```

## Running
```bash
cd 14-spring-core
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

## Concepts Covered
- Spring IoC Container and BeanFactory
- Constructor, Setter, and Field Injection
- Java-based Configuration (@Configuration, @Bean)
- Component Scanning (@Component, @Service, @Repository)
- Bean Scopes and Custom Scopes
- Spring Events and Event Handling
- AOP Proxies and AspectJ
- Bean Post-Processors and Factory Hooks
- Profile-based Configuration
- Conditional Bean Creation

## Dependencies
- Spring Context (6.1.x)
- Spring AOP (6.1.x)
- Jakarta Annotations API
- CGLIB (for proxies)