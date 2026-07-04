# Step by Step: Database Migrations with Flyway

## Step 1: Add Dependency
```xml
<dependency>
    <groupId>org.flywaydb</groupId>
    <artifactId>flyway-core</artifactId>
</dependency>
```
For Spring Boot: `spring-boot-starter-data-jpa` auto-configures Flyway.

## Step 2: Configure
```properties
spring.flyway.enabled=true
spring.flyway.locations=classpath:db/migration
spring.flyway.baseline-on-migrate=true
```

## Step 3: Create Migration Directory
```
src/main/resources/db/migration/
```

## Step 4: Create First Migration
File: `db/migration/V1__create_users.sql`
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL
);
```

## Step 5: Run Application
Flyway auto-migrates on startup. Verify with `flyway_schema_history` table.

## Step 6: Create Second Migration
File: `db/migration/V2__add_email.sql`
```sql
ALTER TABLE users ADD COLUMN email VARCHAR(255);
```

## Step 7: Check Migration Status
```sql
SELECT version, description, installed_on, success FROM flyway_schema_history;
```
