# Spring Data JPA

Database persistence module using Spring Data JPA with Hibernate.

## Overview

- JPA entity mapping with Hibernate
- Spring Data repositories
- CRUD operations
- Query derivation
- Relationship mappings

## Key Concepts

- **Entity** - JPA-managed object
- **Repository** - Data access layer
- **EntityManager** - Persistence operations
- **Persistence Context** - First-level cache

## Running

```bash
mvn spring-boot:run
```

## Dependencies

- spring-boot-starter-data-jpa
- h2 (in-memory database)

## Key Entities

- Review source code for entity definitions
- Note relationship mappings

## Repository Methods

Spring Data auto-generates:
- `save(S)` - Insert/update
- `findById(ID)` - Find by PK
- `findAll()` - List all
- `delete(T)` - Remove entity

## Configuration

```properties
spring.datasource.url=jdbc:h2:mem:testdb
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Version

Spring Boot 3.3.0