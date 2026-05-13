# 46 - Flyway Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Version Migration | V1__, V2__ - numbered changes |
| Repeatable Migration | R__ - re-runnable scripts |
| Schema History | Tracks applied migrations |
| Baseline | Mark existing DB as migrated |
| Repair | Fix corrupted history |

## Commands

```bash
# Maven plugin
mvn flyway:migrate
mvn flyway:clean
mvn flyway:info
mvn flyway:validate
mvn flyway:repair

# CLI
flyway migrate
flyway info
flyway validate
flyway repair
flyway undo
```

## Migration Naming

```
V1__Create_users_table.sql
V2__Add_email_column.sql
V3__Create_orders_table.sql

R__Update_indexes.sql    -- Re-runs on every change
```

## Configuration

```properties
# application.properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
spring.flyway.baseline-version=1

# Maven pom.xml
<plugin>
  <groupId>org.flywaydb</groupId>
  <artifactId>flyway-maven-plugin</artifactId>
  <version>9.22.0</version>
  <configuration>
    <url>jdbc:postgresql://localhost:5432/db</url>
    <user>postgres</user>
    <password>secret</password>
    <locations>
      <location>filesystem:src/main/resources/db/migration</location>
    </locations>
  </configuration>
</plugin>
```

## Common SQL Patterns

```sql
-- Version migration
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL
);

-- Repeatable migration
CREATE INDEX idx_users_email ON users(email);

-- With rollback
-- Flyway Teams/Enterprise required
```

## Migration Locations

| Location | Description |
|----------|-------------|
| `classpath:db/migration` | Default classpath |
| `filesystem:/path/to/sql` | Filesystem files |
| `s3:migrations/` | S3 bucket |
| `gcs:migrations/` | Google Cloud Storage |