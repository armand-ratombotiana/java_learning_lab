# Quarkus - Supersonic Subatomic Java

## Overview
Quarkus is a Kubernetes-native Java framework optimized for GraalVM and cloud-native development with fast startup and low memory.

## Key Features
- Panache ORM (active record pattern)
- Reactive and imperative programming
- GraalVM native image support
- Live coding (dev mode)
- Extension ecosystem

## Project Structure
```
51-quarkus/
  quarkus-panache/
    src/main/java/com/learning/quarkus/panache/QuarkusPanacheLab.java
```

## Running
```bash
cd 51-quarkus/quarkus-panache
mvn compile exec:java
```

## Concepts Covered
- Panache entity and repository
- REST endpoints with JAX-RS
- Application.properties configuration
- Reactive routes

## Dependencies
- Quarkus Panache
- RESTEasy Reactive