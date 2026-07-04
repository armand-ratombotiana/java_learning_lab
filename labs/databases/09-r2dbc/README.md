# R2DBC – Reactive Database Connectivity

## Overview
R2DBC (Reactive Relational Database Connectivity) is a reactive API for relational database access in Java. Unlike JDBC which blocks on database calls, R2DBC provides non-blocking, backpressure-aware database access using Reactive Streams. It is the reactive counterpart to JDBC.

## Key Concepts
- **Reactive Streams**: Publisher-Subscriber pattern with backpressure
- **Connection Factory**: Reactive equivalent of DataSource
- **DatabaseClient**: Fluent, non-blocking query execution
- **Spring Data R2DBC**: Repository support for reactive data access
- **Transactional**: `@Transactional` works with reactive transactions

## Java Example
```java
// R2DBC DatabaseClient
DatabaseClient client = DatabaseClient.create(connectionFactory);

client.sql("SELECT id, name FROM users WHERE id = :id")
    .bind("id", 1L)
    .fetch()
    .first()
    .map(row -> new User((Long) row.get("id"), (String) row.get("name")))
    .subscribe(System.out::println);
```
