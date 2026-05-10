# JPA & Hibernate - Persistence & ORM

## Overview
JPA (Java Persistence API) is the standard ORM specification, and Hibernate is its most popular implementation. This module covers entity mapping, relationships, JPQL, Criteria API, and advanced persistence patterns.

## Key Concepts
- **Entity Mapping**: Table, column, column definitions
- **Relationships**: One-to-One, One-to-Many, Many-to-Many
- **JPQL**: Java Persistence Query Language
- **Criteria API**: Type-safe query building
- **Entity Lifecycle**: Transient, Persistent, Detached, Removed
- **Transactions**: Transaction demarcation, isolation levels
- **Caching**: First-level, Second-level cache
- **Batch Operations**: Bulk inserts, updates, deletes

## Project Structure
```
17-jpa-hibernate/
├── pom.xml
├── src/main/java/com/learning/
│   ├── Main.java
│   ├── entity/
│   ├── repository/
│   ├── service/
│   └── util/
```

## Running
```bash
cd 17-jpa-hibernate
mvn clean compile
mvn exec:java -Dexec.mainClass="com.learning.Main"
```

## Concepts Covered
- Entity mapping with annotations
- Relationship management
- JPQL and native queries
- Criteria API queries
- Entity graphs and fetch joins
- Transaction management
- Caching strategies
- Batch processing
- Entity listeners and callbacks
- Custom types and converters

## Dependencies
- Hibernate Core (6.2.x)
- Jakarta Persistence API
- H2 Database
- Lombok (optional)