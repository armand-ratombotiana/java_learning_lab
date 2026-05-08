# Liquibase - Database Migration

## Overview
Liquibase is a database schema versioning tool that tracks, manages, and applies database changes using declarative changelog files.

## Key Features
- Multiple changelog formats (XML, YAML, JSON, SQL)
- Rollback capabilities (by count, date, tag)
- Context-based deployments
- Database-independent approach

## Project Structure
```
47-liquibase/
  liquibase-db/
    src/main/java/com/learning/liquibase/Lab.java
```

## Running
```bash
cd 47-liquibase/liquibase-db
mvn compile exec:java
```

## Concepts Covered
- ChangeSet and changelog management
- Rollback operations (count, date, tag)
- Change types (createTable, addColumn, etc.)
- Diff/changelog generation

## Dependencies
- Liquibase core
- H2 database