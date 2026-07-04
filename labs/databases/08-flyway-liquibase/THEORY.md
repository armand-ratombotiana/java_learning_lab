# Theory: Database Migrations

## Migration Models

### State-Based (Declarative)
Define the desired target schema; tool computes diff and applies changes (e.g., Hibernate `ddl-auto=update`). Fragile in production.

### Migration-Based (Imperative)
Define each incremental change as a script. Tool applies pending migrations in order. The source of truth is the migration scripts, not the current schema.

## Flyway Approach
- SQL-based migrations: `V1__create_users.sql`, `V2__add_email.sql`
- Java-based migrations for complex logic
- Schema history table: `flyway_schema_history`
- Checksum validation on each run
- `undo` (paid) for rollbacks

## Liquibase Approach
- XML, YAML, JSON, or SQL changelogs
- Changesets with author + id for uniqueness
- `databasechangelog` and `databasechangeloglock` tables
- Built-in rollback per changeset
- Contexts and labels for environment filtering
- Preconditions for validation

## Versioning Strategies
- Semantic versioning: `V1.0.1__description.sql`
- Timestamp-based: `V20240101_120000__description.sql`
- Sequential: `V1__`, `V2__`, `V3__`
