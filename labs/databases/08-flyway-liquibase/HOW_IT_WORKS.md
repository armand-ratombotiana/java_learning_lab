# How Database Migrations Work

## Flyway Migration Process
1. **Check schema history**: Read `flyway_schema_history` table (create if absent)
2. **Validate**: Compare checksums of applied migrations against files
3. **Resolve**: Determine which migrations need to be applied
4. **Lock**: Acquire exclusive lock (cluster-safe via DB lock)
5. **Migrate**: Apply pending migrations in version order
6. **Record**: Insert rows into `flyway_schema_history`
7. **Release lock**: Free the migration lock

## Liquibase Process
1. **Lock**: Acquire `databasechangeloglock`
2. **Diff**: Compare `databasechangelog` against changelog files
3. **Filter**: Identify unapplied changesets
4. **Validate**: Check preconditions for each changeset
5. **Execute**: Run changesets in order within a transaction
6. **Record**: Insert rows into `databasechangelog`
7. **Release lock**

## Transaction Handling
- Flyway wraps each migration in a transaction by default
- Liquibase runs each changeset as a transaction
- Some DDL (ALTER TABLE, CREATE INDEX) may auto-commit depending on database
