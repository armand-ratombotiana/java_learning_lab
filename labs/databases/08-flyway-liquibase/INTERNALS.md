# Internals: Database Migrations

## Flyway Internal Classes

| Class | Role |
|---|---|
| `Flyway` | Entry point, builder pattern |
| `MigrationResolver` | Scans locations for migration files |
| `MigrationExecutor` | Executes a single migration |
| `SchemaHistory` | Manages flyway_schema_history table |
| `MigrationInfoService` | Provides migration status info |
| `SqlScript` | Parses and splits SQL statements |

## Flyway Migration Naming
```
[Prefix][Version][Separator][Description][Suffix]
 V        2_1_1     __         add_index   .sql
```
- Prefix: `V` (versioned), `R` (repeatable), `U` (undo, Pro)
- Separator: `__` (double underscore)
- Version: dots or underscores (converted to numeric)

## Liquibase Internal Classes

| Class | Role |
|---|---|
| `Liquibase` | Main API class |
| `DatabaseChangelog` | Represents parsed changelog |
| `ChangeSet` | Single atomic change |
| `ChangeFactory` | Creates change instances from tags |
| `Database` | Abstraction over specific DB |

## Liquibase Locking
- `databasechangeloglock` table with `LOCKED` boolean column
- 15-minute wait timeout before giving up
- Sticky: same lock row reused across migrations
