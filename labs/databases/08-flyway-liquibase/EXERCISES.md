# Exercises: Database Migrations

## Exercise 1 – Flyway Setup
- Add Flyway to a Spring Boot project with H2
- Create V1 migration to create `departments` table
- Create V2 migration to create `employees` table with FK to departments
- Verify `flyway_schema_history` table contents

## Exercise 2 – Liquibase Setup
- Configure Liquibase with XML changelog
- Create changesets to build a `customers` and `orders` schema
- Add rollback commands to each changeset
- Test rollback with `mvn liquibase:rollback`

## Exercise 3 – Data Migration
- Create V3 migration that populates a lookup table (e.g., statuses)
- Use Flyway Java migration to migrate existing data
- Implement batch processing for large tables

## Exercise 4 – Rollback Strategy
- Add a NOT NULL column with default value
- Create a "rollback" migration (V4) that reverts the change
- Test the full migrate → rollback → migrate cycle

## Exercise 5 – CI/CD Pipeline
- Write a GitHub Actions workflow that runs Flyway migrations
- Add validation step before applying
- Add notification on migration failure
