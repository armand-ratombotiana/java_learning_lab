# Helidon - Projects

This document contains two complete projects demonstrating Helidon framework: a mini-project for learning Helidon MP/NP and a real-world project implementing production-grade reactive services.

## Project 1: Helidon Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Helidon concepts including MP (MicroProfile) and NP (Netty-based) programming models.

### Project Structure

```
helidon-basics/
├── pom.xml
└── src/
    └── main/
        └── java/
            └── com/
                └── learning/
                    └── helidon/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project>
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>helidon-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <helidon.version>4.0.1</helidon.version>
    </properties>
</project>
```

### HelloResource.java

```java
package com.learning.helidon;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;

@Path("/hello")
public class HelloResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hello Helidon!";
    }
}
```

## Project 2: Production Helidon Services

### BookService.java

```java
package com.learning.helidon;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.*;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
public class BookService {
    
    @GET
    public List<Book> list() {
        return List.of(new Book("1", "Title", "Author"));
    }
}

class Book {
    public String id;
    public String title;
    public String author;
    
    public Book(String id, String title, String author) {
        this.id = id;
        this.title = title;
        this.author = author;
    }
}
```

### Build and Run

```bash
cd helidon-production

# Run
java -jar target/helidon-production.jar

# Test
curl http://localhost:8080/api/books
```