# 19 - Javalin

Javalin lightweight web framework for Java. Covers route handling (GET, POST, PUT, DELETE), path parameters (`:param` syntax), query parameters, middleware (before/after handlers), CRUD operations with in-memory data store, and static file serving.

## Prerequisites

- Java 11+
- Maven 3.x

## Key Concepts

- Route registration: `app.get()`, `app.post()`, `app.put()`, `app.delete()`
- Path parameters: `/hello/:name` with `ctx.pathParam("name")`
- Query parameters and request body handling
- Middleware: `before()` and `after()` handlers for cross-cutting concerns
- CRUD operations with RESTful endpoints
- Static file configuration and CORS
- Handler context with path params, query params, and body

## Module Structure

- `01-javalin-basics/` - Javalin routing and middleware simulation

## Learning Objectives

- Create RESTful APIs with Javalin
- Implement route handlers with path and query parameters
- Add middleware for request/response processing

## Estimated Time

- 1-2 hours

## How to Build

```bash
cd 19-javalin
mvn clean package
```

Run the lab:

```bash
cd 01-javalin-basics
mvn compile exec:java -Dexec.mainClass="com.learning.javalin.Lab"
```
