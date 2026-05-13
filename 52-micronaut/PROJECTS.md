# Micronaut - Projects

This document contains two complete projects demonstrating Micronaut framework: a mini-project for learning AOT compilation and a real-world project implementing production-grade microservices.

## Mini-Projects by Concept

### 1. AOT Compilation (2 hours)
Understand Micronaut's ahead-of-time compilation. Analyze compiled bean definitions and optimization.

### 2. Dependency Injection (2 hours)
Use Micronaut's compile-time DI. Configure bean scopes, qualifiers, and factory methods.

### 3. HTTP Server (2 hours)
Create Micronaut HTTP server with routing. Handle request/response with filters and interceptors.

### 4. Data Access (2 hours)
Implement data access with Micronaut Data. Use JDBC, JPA, and reactive database clients.

### Real-world: Micronaut Microservices
Build production-grade microservices with Micronaut, AOT compilation, and GraalVM native images.

---

## Project 1: Micronaut Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Micronaut concepts including dependency injection, AOT compilation, and GraalVM native image support.

### Project Structure

```
micronaut-basics/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── micronaut/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>micronaut-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <micronaut.version>4.2.1</micronaut.version>
    </properties>
</project>
```

### HelloController.java

```java
package com.learning.micronaut;

import io.micronaut.http.annotation.*;

@Controller("/hello")
public class HelloController {
    
    @Get
    public String hello() {
        return "Hello Micronaut!";
    }
}
```

## Project 2: Production Micronaut Microservices

### Project Structure

```
micronaut-production/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── micronaut/
```

### BookController.java

```java
package com.learning.micronaut;

import io.micronaut.http.annotation.*;
import io.micronaut.serde.annotation.Serdeable;

@Controller("/api/books")
public class BookController {
    
    @Get
    public java.util.List<Book> list() {
        return java.util.List.of(new Book("Test", "Author"));
    }
}

@Serdeable
class Book {
    public String title;
    public String author;
    public Book(String title, String author) {
        this.title = title;
        this.author = author;
    }
}
```

### BookService.java

```java
package com.learning.micronaut;

import jakarta.inject.Singleton;
import java.util.List;

@Singleton
public class BookService {
    
    public List<Book> findAll() {
        return List.of(new Book("Learning Micronaut", "Author"));
    }
}
```

### Build and Run

```bash
cd micronaut-production

# Build native image
mvn package -Dpackaging=native-image

# Or run in development
mvn mn:run
```