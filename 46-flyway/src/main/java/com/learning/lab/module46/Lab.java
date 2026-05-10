package com.learning.lab.module46;

import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.configuration.FlywayConfiguration;
import org.flywaydb.core.api.migration.JavaMigration;
import org.flywaydb.core.internal.configuration.ConfigurationValidator;

import java.sql.*;
import java.util.List;

public class Lab {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 46: Flyway Lab ===\n");

        System.out.println("1. Flyway Configuration:");
        System.out.println("   - Location: db/migration");
        System.out.println("   - Table: flyway_schema_history");
        System.out.println("   - Baseline: baselineOnMigrate");
        System.out.println("   - Validate: validateOnMigrate");

        Flyway flyway = Flyway.configure()
                .dataSource("jdbc:postgresql://localhost:5432/mydb", "user", "pass")
                .locations("db/migration")
                .baselineOnMigrate(true)
                .validateOnMigrate(true)
                .load();

        System.out.println("\n2. Database Migrations:");
        migrationsDemo(flyway);

        System.out.println("\n3. Migration Versioning:");
        versioningDemo();

        System.out.println("\n4. SQL Migration Structure:");
        sqlMigrationDemo();

        System.out.println("\n5. Java Migrations:");
        javaMigrationDemo();

        System.out.println("\n6. Commands:");
        commandsDemo();

        flyway.close();
        System.out.println("\n=== Flyway Lab Complete ===");
    }

    static void migrationsDemo(Flyway flyway) {
        System.out.println("   Migration Types:");
        System.out.println("   - Versioned: V1__Initial_schema.sql, V2__Add_users.sql");
        System.out.println("   - Undo: U1__Initial_schema.sql (requires Team edition)");
        System.out.println("   - Repeatable: R__populate_data.sql");

        System.out.println("   Migration Execution:");
        flyway.migrate();
        System.out.println("   Applied migrations executed");
    }

    static void versioningDemo() {
        System.out.println("   Version Format:");
        System.out.println("   - Prefix: V (versioned), U (undo), R (repeatable)");
        System.out.println("   - Version: Numeric (1, 2, 3...) or Semantic (1_0_0)");
        System.out.println("   - Separator: __ (double underscore)");
        System.out.println("   - Description: snake_case preferred");

        System.out.println("\n   Example Versions:");
        System.out.println("   V1__Create_table.sql");
        System.out.println("   V2_0_0__Add_users_table.sql");
        System.out.println("   V20230101000000__Initial_schema.sql");
    }

    static void sqlMigrationDemo() {
        System.out.println("   SQL Migration Structure:");
        System.out.println("   -- Flyway: My SQL migration");
        System.out.println("   -- Author: developer");
        System.out.println("   CREATE TABLE users (");
        System.out.println("       id INT PRIMARY KEY,");
        System.out.println("       name VARCHAR(100)");
        System.out.println("   );");
        System.out.println("   INSERT INTO users (id, name) VALUES (1, 'admin');");

        System.out.println("\n   Supported Operations:");
        System.out.println("   - CREATE, ALTER, DROP tables");
        System.out.println("   - INSERT, UPDATE, DELETE data");
        System.out.println("   - CREATE INDEX, ALTER constraints");
        System.out.println("   - Stored procedures, functions");
    }

    static void javaMigrationDemo() {
        System.out.println("   Java Migration Interface:");
        System.out.println("   public class V1__Initial implements JavaMigration {");
        System.out.println("       public void migrate(Context context) throws Exception {");
        System.out.println("           context.sql(\"CREATE TABLE users (id INT)\");");
        System.out.println("       }");
        System.out.println("   }");

        System.out.println("\n   Java Migration Benefits:");
        System.out.println("   - Type-safe database operations");
        System.out.println("   - Complex migration logic");
        System.out.println("   - Integration with ORM");
        System.out.println("   - Testability");
    }

    static void commandsDemo() {
        System.out.println("   Flyway Commands:");
        System.out.println("   - migrate: Apply pending migrations");
        System.out.println("   - clean: Drop all objects");
        System.out.println("   - info: Show migration status");
        System.out.println("   - validate: Check migration state");
        System.out.println("   - baseline: Mark baseline version");
        System.out.println("   - repair: Fix checksum issues");

        System.out.println("\n   CLI Examples:");
        System.out.println("   flyway migrate");
        System.out.println("   flyway clean");
        System.out.println("   flyway info");
        System.out.println("   flyway validate");
    }
}