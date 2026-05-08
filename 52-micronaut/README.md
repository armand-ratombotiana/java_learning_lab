# Micronaut - Modern Microservices

## Overview
Micronaut is a JVM-based framework for building microservices with compile-time dependency injection and native image support.

## Key Features
- Compile-time DI (no reflection at runtime)
- GraalVM native image support
- Reactive programming (RxJava, Project Reactor)
- HTTP client/server annotations
- Data access with Micronaut Data

## Project Structure
```
52-micronaut/
  micronaut-data/
    src/main/java/com/learning/micronaut/data/MicronautDataLab.java
```

## Running
```bash
cd 52-micronaut/micronaut-data
mvn compile exec:java
```

## Concepts Covered
- Controller and routing
- Dependency injection
- GraalVM compatibility
- Reactive data access

## Dependencies
- Micronaut HTTP Server
- Micronaut Data