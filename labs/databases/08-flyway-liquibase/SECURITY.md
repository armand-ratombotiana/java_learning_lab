# Security: Database Migrations

## Credential Management
- **Never hardcode DB credentials** in migration configuration
- Use environment variables, vaults (Hashicorp Vault), or Spring Cloud Config
- Flyway/Liquibase support `spring.datasource.*` properties

## Migration File Integrity
- Flyway checksum verification detects tampered migrations
- Store migration files in version control with code review
- Sign migration artifacts in CI/CD pipeline

## Access Control
- Production migration credentials should be read-write but restricted
- Use separate service accounts for migrations vs. application
- Audit `installed_by` column in schema history table

## Sensitive Data in Migrations
```sql
-- NEVER commit plaintext passwords in seed data
-- Use Flyway Java migrations with hashing:
INSERT INTO users (username, password_hash)
VALUES ('admin', '$2a$10$...'); -- pre-hashed value
```

## Secure Deployment
- Run migrations in CI/CD, not manually
- Use Flyway callbacks for pre/post migration security checks
- Liquibase preconditions can verify environment before running
