# Quarkus - Projects

This document contains two complete projects demonstrating Quarkus framework: a mini-project for learning Quarkus basics and a real-world project implementing production-grade microservices.

## Project 1: Quarkus Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Quarkus concepts including reactive endpoints, Panache ORM, and hot reload. It serves as a learning starting point for Quarkus development.

### Project Structure

```
quarkus-basics/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── learning/
        │           └── quarkus/
        │               └── GreetingResource.java
        └── resources/
            └── application.properties
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>quarkus-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <maven.compiler.source>21</maven.compiler.source>
        <maven.compiler.target>21</maven.compiler.target>
        <quarkus.platform.version>3.7.1</quarkus.platform.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.quarkus.platform</groupId>
                <artifactId>quarkus-bom</artifactId>
                <version>${quarkus.platform.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-arc</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-resteasy-reactive</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
        </dependency>
    </dependencies>
</project>
```

### GreetingResource.java

```java
package com.learning.quarkus;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import java.util.concurrent.CompletableFuture;

@Path("/hello")
public class GreetingResource {
    
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String hello() {
        return "Hello Quarkus!";
    }
    
    @GET
    @Path("/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public String helloName(@PathParam("name") String name) {
        return "Hello " + name + "!";
    }
    
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public GreetingResponse create(GreetingRequest request) {
        return new GreetingResponse("Hello " + request.getName() + "!");
    }
}

class GreetingRequest {
    private String name;
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
}

class GreetingResponse {
    private String message;
    public GreetingResponse(String message) { this.message = message; }
    public String getMessage() { return message; }
}
```

### application.properties

```properties
quarkus.http.port=8080
quarkus.log.level=INFO
quarkus.banner.enabled=false
```

### Build and Run

```bash
cd quarkus-basics
mvn quarkus:dev

# In another terminal, test the endpoint
curl http://localhost:8080/hello
curl http://localhost:8080/hello/World
```

## Project 2: Production Quarkus Microservices

### Project Structure

```
quarkus-production/
├── pom.xml
└── src/
    └── main/
        ├── java/
        │   └── com/
        │       └── learning/
        │           └── quarkus/
        │               ├── entity/
        │               │   └── Book.java
        │               ├── repository/
        │               │   └── BookRepository.java
        │               ├── resource/
        │               │   └── BookResource.java
        │               └── service/
        │                   └── BookService.java
        └── resources/
            └── application.properties
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>quarkus-production</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <quarkus.platform.version>3.7.1</quarkus.platform.version>
    </properties>
    
    <dependencyManagement>
        <dependencies>
            <dependency>
                <groupId>io.quarkus.platform</groupId>
                <artifactId>quarkus-bom</artifactId>
                <version>${quarkus.platform.version}</version>
                <type>pom</type>
                <scope>import</scope>
            </dependency>
        </dependencies>
    </dependencyManagement>
    
    <dependencies>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-hibernate-orm-panache</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-jdbc-postgresql</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-resteasy-reactive-jackson</artifactId>
        </dependency>
        <dependency>
            <groupId>io.quarkus</groupId>
            <artifactId>quarkus-arc</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>io.quarkus.platform</groupId>
                <artifactId>quarkus-maven-plugin</artifactId>
                <version>${quarkus.platform.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>build</goal>
                            <goal>generate-code</goal>
                            <goal>generate-code-tests</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

### Book.java (Panache Entity)

```java
package com.learning.quarkus.entity;

import io.quarkus.hibernate.orm.panache.PanacheEntity;
import jakarta.persistence.*;

@Entity
public class Book extends PanacheEntity {
    
    public String title;
    public String author;
    public int year;
    public String isbn;
    public double price;
    public int stockQuantity;
    
    public static Book findByISBN(String isbn) {
        return find("isbn", isbn).firstResult();
    }
    
    public static java.util.List<Book> findByAuthor(String author) {
        return list("author", author);
    }
    
    public static java.util.List<Book> findByYearRange(int startYear, int endYear) {
        return list("year >= ?1 and year <= ?2", startYear, endYear);
    }
}
```

### BookRepository.java

```java
package com.learning.quarkus.repository;

import com.learning.quarkus.entity.Book;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class BookRepository implements PanacheRepository<Book> {
    
    public Book findByISBN(String isbn) {
        return find("isbn", isbn).firstResult();
    }
    
    public List<Book> findAllAvailable() {
        return list("stockQuantity > 0");
    }
}
```

### BookService.java

```java
package com.learning.quarkus.service;

import com.learning.quarkus.entity.Book;
import com.learning.quarkus.repository.BookRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.List;

@ApplicationScoped
public class BookService {
    
    @Inject
    BookRepository bookRepository;
    
    public List<Book> getAllBooks() {
        return bookRepository.listAll();
    }
    
    public Book getBook(Long id) {
        return bookRepository.findById(id);
    }
    
    public Book createBook(Book book) {
        bookRepository.persist(book);
        return book;
    }
    
    public void deleteBook(Long id) {
        bookRepository.deleteById(id);
    }
}
```

### BookResource.java

```java
package com.learning.quarkus.resource;

import com.learning.quarkus.entity.Book;
import com.learning.quarkus.service.BookService;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.util.List;

@Path("/api/books")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class BookResource {
    
    @Inject
    BookService bookService;
    
    @GET
    public List<Book> getAll() {
        return bookService.getAllBooks();
    }
    
    @GET
    @Path("/{id}")
    public Book getById(@PathParam("id") Long id) {
        return bookService.getBook(id);
    }
    
    @POST
    public Response create(Book book) {
        Book created = bookService.createBook(book);
        return Response.status(Response.Status.CREATED)
            .entity(created)
            .build();
    }
    
    @DELETE
    @Path("/{id}")
    public Response delete(@PathParam("id") Long id) {
        bookService.deleteBook(id);
        return Response.noContent().build();
    }
}
```

### application.properties

```properties
quarkus.http.port=8080
quarkus.datasource.db-kind=postgresql
quarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5432/books
quarkus.datasource.username=postgres
quarkus.datasource.password=postgres
quarkus.hibernate-orm.database.generation=drop-and-create
quarkus.log.console.enable=true
```

### Native Build

```bash
cd quarkus-production

# Build native executable
mvn package -Dquarkus.native.enabled=true

# Or build container
mvn package -Dquarkus.container-image.build=true
```

### Run Commands

```bash
# Development mode
mvn quarkus:dev

# Run in production
java -jar target/quarkus-production-1.0.0-runner.jar

# Test endpoints
curl http://localhost:8080/api/books
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Test","author":"Author","year":2024,"isbn":"123","price":29.99,"stockQuantity":10}'
```

## Summary

These projects demonstrate:

1. **Mini-Project**: Basic Quarkus REST endpoints with reactive programming
2. **Production Project**: Complete microservices with Panache ORM, PostgreSQL, and native compilation

Quarkus enables ultra-fast boot times and low memory footprint for cloud-native applications.