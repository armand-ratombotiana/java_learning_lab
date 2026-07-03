# 18 - Helidon

Helidon SE (functional) and Helidon MP (MicroProfile) reactive web framework. Covers Helidon SE functional routing with handler composition, Helidon MP annotated controllers with JAX-RS style, health checks, and reactive programming support.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Helidon SE: functional routing, `SERouting.get()/post()`, handler functions, reactive pipeline
- Helidon MP: `@Path`, `@GET`, `@POST` annotations, CDI dependency injection
- Health checks: disk space, database connectivity, cache status
- Reactive request processing with CompletableFuture
- Configuration and metrics support

## Module Structure

- `01-helidon-basics/` - Helidon SE and MP concept exploration

## Learning Objectives

- Build REST APIs with Helidon SE functional style
- Use Helidon MP with MicroProfile annotations
- Implement health checks and reactive endpoints

## Estimated Time

- 2-3 hours

## How to Build

```bash
cd 18-helidon
mvn clean package
```

Run the lab:

```bash
cd 01-helidon-basics
mvn compile exec:java -Dexec.mainClass="com.learning.helidon.Lab"
```
