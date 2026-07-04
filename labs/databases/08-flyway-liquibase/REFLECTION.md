# Reflection: Database Migrations

## Key Takeaways
- Migrations solve the database drift problem by treating schema as versioned code
- Flyway's simplicity (SQL files in order) is often preferable to complex XML configuration
- Rollback strategy is as important as the migration itself
- CI/CD integration is essential; manual migrations introduce risk
- The schema history table is invaluable for debugging and auditing

## When to Use Flyway
- SQL-first teams who want simplicity
- Projects with a single database vendor
- Mature databases where full control over SQL is needed

## When to Use Liquibase
- Multi-vendor database support required
- Need free built-in rollback support
- Complex preconditions and context-based filtering
- Teams preferring declarative XML/YAML over SQL
