package com.learning.flyway;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {
    record Migration(String version, String description, String script, String checksum, Instant appliedOn, int executionTimeMs) {
        boolean isPending() { return appliedOn == null; }
    }

    record SchemaHistory(List<Migration> migrations) {
        Migration latest() { return migrations.stream().filter(m -> !m.isPending()).reduce((a, b) -> b).orElse(null); }
        Migration pending() { return migrations.stream().filter(Migration::isPending).findFirst().orElse(null); }
        long count() { return migrations.size(); }
        long appliedCount() { return migrations.stream().filter(m -> !m.isPending()).count(); }
    }

    static class Flyway {
        String[] locations;
        String table = "flyway_schema_history";
        List<Migration> migrations = new ArrayList<>();
        boolean validateOnMigrate = true;

        Flyway(String... locations) { this.locations = locations; }

        void addMigration(String version, String description, String script, String checksum) {
            migrations.add(new Migration(version, description, script, checksum, null, 0));
        }

        void migrate() {
            System.out.println("   Migrating schema with " + migrations.size() + " migrations...");
            for (var m : migrations) {
                if (m.isPending()) {
                    var now = Instant.now();
                    int duration = simulateExecution(m);
                    int idx = migrations.indexOf(m);
                    migrations.set(idx, new Migration(m.version(), m.description(), m.script(),
                        m.checksum(), now, duration));
                    System.out.println("   Applied V" + m.version() + ": " + m.description() + " (" + duration + "ms)");
                }
            }
        }

        int simulateExecution(Migration m) {
            try {
                Thread.sleep(ThreadLocalRandom.current().nextInt(10, 50));
            } catch (InterruptedException e) {}
            return ThreadLocalRandom.current().nextInt(15, 100);
        }

        SchemaHistory history() { return new SchemaHistory(List.copyOf(migrations)); }

        boolean validate() {
            for (var m : migrations) {
                if (m.appliedOn() != null && !m.checksum().startsWith("chk_")) {
                    System.out.println("   Validation FAILED: " + m.script() + " checksum mismatch");
                    return false;
                }
            }
            System.out.println("   All migrations validated successfully");
            return true;
        }

        String info() {
            var sb = new StringBuilder();
            var history = history();
            sb.append("   Schema version: ").append(history.latest() != null ? history.latest().version() : "<< Empty Schema >>").append("\n");
            sb.append("   Applied: ").append(history.appliedCount()).append("/").append(history.count()).append("\n");
            for (var m : migrations) {
                String status = m.isPending() ? "PENDING" : "SUCCESS";
                sb.append("   ").append(status).append(" V").append(m.version()).append(": ").append(m.description());
                if (!m.isPending()) sb.append(" (").append(m.executionTimeMs()).append("ms)");
                sb.append("\n");
            }
            return sb.toString();
        }

        void undo(String version) {
            for (int i = 0; i < migrations.size(); i++) {
                var m = migrations.get(i);
                if (m.version().equals(version) && !m.isPending()) {
                    migrations.set(i, new Migration(m.version(), m.description(), m.script(), m.checksum(), null, 0));
                    System.out.println("   Undone V" + version + ": " + m.description());
                    return;
                }
            }
        }

        void repair() {
            migrations.removeIf(m -> !m.isPending() && m.checksum() == null);
            System.out.println("   Removed failed migrations from history");
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Flyway DB Migration Concepts Lab ===\n");

        Flyway flyway = new Flyway("classpath:db/migration");
        flyway.table = "flyway_schema_history";

        System.out.println("1. Migration scripts (conventional naming):");
        System.out.println("   V1__create_users_table.sql");
        System.out.println("   V2__add_email_column.sql");
        System.out.println("   V3__create_orders_table.sql");
        System.out.println("   V4__add_order_status_index.sql");
        System.out.println("   V5__seed_data.sql\n");

        flyway.addMigration("1", "Create users table", "V1__create_users_table.sql", "chk_a1b2c3");
        flyway.addMigration("2", "Add email column", "V2__add_email_column.sql", "chk_d4e5f6");
        flyway.addMigration("3", "Create orders table", "V3__create_orders_table.sql", "chk_g7h8i9");
        flyway.addMigration("4", "Add order status index", "V4__add_order_status_index.sql", "chk_j0k1l2");
        flyway.addMigration("5", "Seed initial data", "V5__seed_data.sql", "chk_m3n4o5");

        System.out.println("2. Running flyway migrate...");
        flyway.migrate();

        System.out.println("\n3. Flyway info:");
        System.out.println(flyway.info());

        System.out.println("4. Validation:");
        flyway.validate();

        System.out.println("\n5. Adding a new migration:");
        flyway.addMigration("6", "Add product ratings", "V6__add_product_ratings.sql", "chk_p6q7r8");
        System.out.println(flyway.info());

        System.out.println("6. Undo (undo V5):");
        flyway.undo("5");
        System.out.println(flyway.info());

        System.out.println("7. Repair (clean failed migrations):");
        flyway.repair();

        System.out.println("\n8. Migration naming conventions:");
        System.out.println("   - Versioned: V<version>__<description>.sql (applied once, in order)");
        System.out.println("   - Repeatable: R__<description>.sql (re-applied on checksum change)");
        System.out.println("   - Undo: U<version>__<description>.sql (reverses a versioned migration)");

        System.out.println("\n9. Flyway configuration:");
        System.out.println("   flyway.url=jdbc:postgresql://localhost:5432/mydb");
        System.out.println("   flyway.user=db_user");
        System.out.println("   flyway.password=***");
        System.out.println("   flyway.locations=classpath:db/migration");
        System.out.println("   flyway.baseline-on-migrate=true");

        System.out.println("\n10. Flyway with Java callbacks:");
        System.out.println("    Implement MigrationCallback interface");
        System.out.println("    beforeMigrate(), afterEachMigrate(), beforeEachMigrate()");

        System.out.println("\n=== Lab Complete ===");
    }
}
