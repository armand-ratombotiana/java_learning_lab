# Performance: Database Migrations

## Migration Execution Speed

| Factor | Impact | Mitigation |
|---|---|---|
| Large data migrations | Slow, locks rows | Batch in chunks, run off-peak |
| DDL on large tables | Exclusive locks | Use pg_repack, online schema change tools |
| Many small migrations | Startup time | Combine related changes |
| Checksum calculation | Minimal | Ignore (microseconds per file) |

## Batch Data Migrations
```sql
-- Instead of single UPDATE of 10M rows
DO $$
BEGIN
  FOR i IN 1..100 LOOP
    UPDATE users SET status = 'migrated'
    WHERE status IS NULL
    LIMIT 100000;
    COMMIT;
  END LOOP;
END $$;
```

## Flyway Performance Tuning
- `flyway.batch=true` – group SQL statements
- Use `outOfOrder=true` sparingly (allows newer versions with older timestamps)
- Skip validation on fast startup: `flyway.validateOnMigrate=false`
