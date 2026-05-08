# Flyway - Database Migration

## Overview
Flyway is a database migration tool that version-controls database schema changes alongside application code.

## Key Features
- Version-based migrations (V1, V2, V3...)
- Repeatable migrations (R__)
- Support for undo migrations
- Multi-database support
- Command-line and build tool integration

## Project Structure
```
46-flyway/
  flyway-migrations/
    src/main/java/com/learning/flyway/Lab.java
```

## Running
```bash
cd 46-flyway/flyway-migrations
mvn compile exec:java
```

## Concepts Covered
- Migration versioning and naming conventions
- Schema history management
- Validation and checksum
- Undo and repair operations

## Dependencies
- Flyway core library
- H2 database (for testing)