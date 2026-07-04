# Refactoring: Database Migrations

## Manual Scripts → Versioned Migrations

### Before
```sql
-- run_this_on_prod.sql (emailed around, no versioning)
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

### After
```sql
-- V4__add_phone_to_users.sql (version-controlled, in repo)
ALTER TABLE users ADD COLUMN phone VARCHAR(20);
```

## Large Single Migration → Smaller Atomic Migrations

### Before
```sql
-- V1 has 30 unrelated changes
CREATE TABLE a (...); CREATE TABLE b (...); ALTER TABLE c ADD COLUMN ...;
```

### After
```sql
-- V1__create_table_a.sql
-- V2__create_table_b.sql
-- V3__add_column_to_c.sql
```

## Migration Reorganization
- Use Flyway `cherryPick` to selectively apply migrations
- Liquibase: use `includeAll` with ordered directories
- Separate baseline migrations from regular ones via `baseline-version`
