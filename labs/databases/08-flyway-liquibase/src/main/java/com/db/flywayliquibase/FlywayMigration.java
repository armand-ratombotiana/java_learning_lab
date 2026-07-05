package com.db.flywayliquibase;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.output.MigrateResult;

/**
 * Demonstrates Flyway database migration configuration and execution.
 *
 * Flyway uses versioned SQL migration scripts to manage schema changes.
 * Migration files follow the naming convention:
 *   V{version}__{description}.sql
 *   e.g., V1__create_users.sql, V2__add_orders_table.sql
 *
 * This class shows how to configure and run Flyway programmatically.
 */
public class FlywayMigration {

    // Example migration scripts that Flyway would discover on the classpath:
    //
    // db/migration/V1__create_users.sql:
    //   CREATE TABLE users (
    //       id       BIGSERIAL PRIMARY KEY,
    //       name     VARCHAR(100) NOT NULL,
    //       email    VARCHAR(255) UNIQUE NOT NULL
    //   );
    //
    // db/migration/V2__add_orders.sql:
    //   CREATE TABLE orders (
    //       id          BIGSERIAL PRIMARY KEY,
    //       user_id     BIGINT NOT NULL REFERENCES users(id),
    //       total       DECIMAL(10,2) NOT NULL,
    //       created_at  TIMESTAMP DEFAULT NOW()
    //   );
    //
    // db/migration/V3__seed_data.sql:
    //   INSERT INTO users (name, email) VALUES
    //       ('Alice', 'alice@example.com'),
    //       ('Bob', 'bob@example.com');

    static String printMigrationScripts() {
        return """
            Classpath migration scripts:
              db/migration/V1__create_users.sql
              db/migration/V2__add_orders.sql
              db/migration/V3__seed_data.sql
              db/migration/V4__add_product_table.sql
              db/migration/V5__add_order_items.sql
            """;
    }

    /**
     * Configures and runs Flyway against a database.
     * In production, use Flyway callback classes or Spring Boot auto-configuration.
     */
    static MigrateResult runFlyway(String jdbcUrl, String user, String password) {
        Flyway flyway = Flyway.configure()
            .dataSource(jdbcUrl, user, password)
            .locations("classpath:db/migration")
            .baselineOnMigrate(true)
            .validateOnMigrate(true)
            .outOfOrder(false)
            .load();

        MigrateResult result = flyway.migrate();
        return result;
    }

    public static void main(String[] args) {
        System.out.println("=== Flyway Database Migrations ===\n");

        System.out.println(printMigrationScripts());

        System.out.println("Configuration:");
        System.out.println("  locations:     classpath:db/migration");
        System.out.println("  baselineOnMigrate: true (baselines existing DBs)");
        System.out.println("  validateOnMigrate: true (checks checksums)");
        System.out.println("  outOfOrder:    false (strict version ordering)");

        System.out.println("\nNaming convention:");
        System.out.println("  V{version}__{description}.sql");
        System.out.println("  Example: V4__add_product_table.sql");

        System.out.println("\nFlyway commands:");
        System.out.println("  migrate   — apply pending migrations");
        System.out.println("  clean     — drop all objects");
        System.out.println("  info      — show migration status");
        System.out.println("  validate  — verify applied migrations");
        System.out.println("  undo      — undo last migration (Pro/Teams)");

        System.out.println("\nUsage:");
        System.out.println("  Flyway flyway = Flyway.configure()");
        System.out.println("      .dataSource(url, user, password)");
        System.out.println("      .load();");
        System.out.println("  MigrateResult result = flyway.migrate();");
        System.out.println("  System.out.println(result.migrationsExecuted + \" migrations applied\");");
    }
}
