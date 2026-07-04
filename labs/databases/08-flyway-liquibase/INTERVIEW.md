# Interview: Database Migrations

## Common Questions

**Q:** Compare Flyway vs Liquibase.
**A:** Flyway is SQL-first, simpler, opinionated. Liquibase is XML/JSON/YAML-based, more flexible (rollback built-in, preconditions, contexts). Flyway's rollback is paid; Liquibase includes it free.

**Q:** How do you handle a failed migration in production?
**A:** 1) Don't panic. 2) Assess: can the SQL be fixed and re-run? 3) Use `flyway repair` to clear the failed entry. 4) Fix the migration SQL. 5) Re-run. 6) If data corruption occurred, restore from backup.

**Q:** How do you manage migrations across multiple database vendors?
**A:** Liquibase abstracts DDL (vendor-agnostic changelogs). Flyway uses vendor-specific SQL but supports different migration directories per database type via location configuration.

**Q:** Describe your ideal migration strategy.
**A:** Small, atomic, well-tested migrations. SQL-first for DDL. Java for complex data migrations. CI/CD-gated. All changes reviewed. Rollback plan documented. Out-of-order migrations disabled in production.

**Q:** How do you prevent locking issues during migrations?
**A:** Use `pt-online-schema-change` or `gh-ost` for large ALTER TABLE. Run migrations during low traffic. Batch large data changes. Set appropriate lock wait timeouts.
