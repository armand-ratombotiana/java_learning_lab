# Common Mistakes: Database Migrations

## Modifying Applied Migrations
```sql
-- Applied to production. Then someone edits V1 to add a column.
-- Flyway's checksum validation will FAIL on next deploy.
```
**Fix**: Never edit applied migrations. Create V3 with the change.

## Running DDL Inside Transactions (PostgreSQL)
Postgres auto-commits DDL. A migration with CREATE TABLE + INSERT may partially apply if the INSERT fails.
**Fix**: Split into multiple migrations or use Flyway's `group` mode.

## Missing Base Migrations
Starting Flyway on an existing database without `baseline-on-migrate=true` causes false migration detection.
**Fix**: `flyway baseline -baselineVersion=1` to mark current state.

## Environment-Specific SQL
```sql
-- Works in MySQL but fails in PostgreSQL
-- Fix: Use placeholders or database-agnostic SQL
```

## Ignoring Rollback Strategy
**Fix**: Test rollbacks before production. Have a documented rollback plan (new migration to revert, or database restore).
