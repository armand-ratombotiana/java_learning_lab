# 33 - PostgreSQL Learning Module

## Overview
PostgreSQL is a powerful, open-source object-relational database system. This module covers advanced PostgreSQL features with Spring Boot.

## Module Structure
- `postgresql-learning/` - Spring Data JPA implementation

## Technology Stack
- Spring Boot 3.x
- Spring Data JPA
- Hibernate
- PostgreSQL JDBC Driver
- Maven

## Prerequisites
- PostgreSQL server running on `localhost:5432`
- Database: `learning_db`
- User: `postgres` with password `postgres`

## Key Features
- ACID compliance
- Advanced data types (JSONB, ARRAY, UUID, ENUM)
- Full-text search
- Window functions
- CTEs and recursive queries
- Foreign data wrappers

## Build & Run
```bash
cd postgresql-learning
mvn clean install
mvn spring-boot:run
```

## Default Configuration
- Host: `localhost`
- Port: `5432`
- Database: `learning_db`
- Schema: `public`

## Related Modules
- 31-mongodb (NoSQL comparison)
- 42-testcontainers (testing with databases)