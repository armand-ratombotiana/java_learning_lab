# Database Migrations: Flyway & Liquibase

## Overview
Database migrations manage version-controlled, incremental changes to database schemas. Flyway and Liquibase are the two dominant Java migration tools. They enable teams to evolve database schemas alongside application code in a repeatable, auditable manner.

## Key Concepts
- **Migration**: A single, versioned change to the schema (DDL, DML)
- **Versioning**: Sequential migration numbering (V1, V2, V3...)
- **Checksums**: Integrity verification to detect tampering
- **Rollback**: Reverting a migration to a previous state
- **Idempotent**: Run repeatedly with same result (Liquibase changesets)

## Java Example
```java
// Flyway programmatic configuration
Flyway flyway = Flyway.configure()
    .dataSource(url, user, password)
    .locations("classpath:db/migration")
    .load();
flyway.migrate();

// Liquibase programmatic
Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.yaml",
    new DatabaseConnection(connection), new FileSystemResourceAccessor());
liquibase.update("");
```
