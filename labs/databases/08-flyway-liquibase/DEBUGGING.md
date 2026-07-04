# Debugging: Database Migrations

## Flyway Common Issues

| Issue | Symptom | Cause | Fix |
|---|---|---|---|
| Checksum mismatch | `Migration checksum mismatch` | Applied migration edited | Restore original file or `flyway repair` |
| Missing schema table | `flyway_schema_history doesn't exist` | Wrong schema search path | Check `spring.flyway.schemas` |
| Lock timeout | `Unable to acquire migration lock` | Migration crashed mid-run | `UPDATE flyway_schema_history SET locked = false` |
| Failed migration | `Migration V2 failed` | SQL error | Fix SQL, `flyway repair` to remove failed row |
| Baseline mismatch | `Baseline version mismatch` | Existing schema at wrong version | Correct baseline version |

## Liquibase Common Issues

| Issue | Cause | Fix |
|---|---|---|
| Lock file stuck | `databasechangeloglock.LOCKED=1` | `UPDATE databasechangeloglock SET LOCKED=0` |
| Changeset rerun | Changeset modified after apply | Create new changeset |
| MD5Sum check | Same changeset content must match | Never alter applied changesets |

## Debug Commands
```bash
# Flyway info
mvn flyway:info

# Flyway repair
mvn flyway:repair

# Liquibase status
mvn liquibase:status

# Liquibase rollback SQL (dry run)
mvn liquibase:rollbackSQL -Dliquibase.rollbackCount=1
```
