package com.learning.liquibase;

import java.time.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

public class Lab {
    record ChangeSet(String id, String author, String filePath, String comment, Instant appliedOn, boolean ran) {
        boolean isPending() { return appliedOn == null; }
    }

    record ChangeLog(String path, List<ChangeSet> changeSets) {
        long total() { return changeSets.size(); }
        long applied() { return changeSets.stream().filter(ChangeSet::ran).count(); }
        long pending() { return changeSets.stream().filter(cs -> !cs.ran).count(); }
    }

    static class Liquibase {
        String changeLogFile;
        List<ChangeLog> changeLogs = new ArrayList<>();
        Map<String, String> parameters = new LinkedHashMap<>();

        Liquibase(String changeLogFile) { this.changeLogFile = changeLogFile; }

        void addChangeLog(ChangeLog cl) { changeLogs.add(cl); }

        void update() {
            System.out.println("   Running Liquibase update...");
            for (var cl : changeLogs) {
                for (var cs : cl.changeSets()) {
                    if (cs.isPending()) {
                        simulateExecution(cs);
                        reportChangeSet(cs, "EXECUTED");
                    }
                }
            }
        }

        void rollback(int count) {
            System.out.println("   Rolling back last " + count + " changesets...");
            var applied = changeLogs.stream()
                .flatMap(cl -> cl.changeSets().stream())
                .filter(ChangeSet::ran)
                .sorted(Comparator.comparing(ChangeSet::appliedOn).reversed())
                .collect(Collectors.toList());

            for (int i = 0; i < Math.min(count, applied.size()); i++) {
                var cs = applied.get(i);
                System.out.println("   Rolled back: " + cs.id() + " (" + cs.comment() + ")");
            }
        }

        void rollbackToDate(Instant date) {
            System.out.println("   Rolling back changes after " + date + "...");
            var toRollback = changeLogs.stream()
                .flatMap(cl -> cl.changeSets().stream())
                .filter(cs -> cs.ran() && cs.appliedOn().isAfter(date))
                .sorted(Comparator.comparing(ChangeSet::appliedOn).reversed())
                .collect(Collectors.toList());
            for (var cs : toRollback)
                System.out.println("   Rolled back: " + cs.id() + " (" + cs.comment() + ")");
        }

        void rollbackToTag(String tag) {
            System.out.println("   Rolling back to tag '" + tag + "'...");
            var found = new java.util.concurrent.atomic.AtomicBoolean(false);
            var toRollback = changeLogs.stream()
                .flatMap(cl -> cl.changeSets().stream())
                .filter(cs -> {
                    if (cs.id().equals(tag)) found.set(true);
                    return found.get() && cs.ran();
                })
                .sorted(Comparator.comparing(ChangeSet::appliedOn).reversed())
                .collect(Collectors.toList());
            for (var cs : toRollback)
                System.out.println("   Rolled back: " + cs.id());
        }

        void tag(String tagName) {
            System.out.println("   Tagged database state as '" + tagName + "'");
        }

        void simulateExecution(ChangeSet cs) {
            try { Thread.sleep(ThreadLocalRandom.current().nextInt(5, 30)); } catch (InterruptedException e) {}
        }

        void reportChangeSet(ChangeSet cs, String status) {
            System.out.println("   " + status + " " + cs.id() + ": " + cs.comment() +
                " (" + cs.author() + ")");
        }

        void status() {
            for (var cl : changeLogs) {
                System.out.println("   ChangeLog: " + cl.path());
                System.out.println("   Applied: " + cl.applied() + "/" + cl.total());
                for (var cs : cl.changeSets()) {
                    String s = cs.ran() ? "RAN" : "NOT RAN";
                    System.out.println("     [" + s + "] " + cs.id() + " - " + cs.comment());
                }
            }
        }

        String generateChangelog() {
            return """
                <?xml version="1.0" encoding="UTF-8"?>
                <databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
                  <changeSet id="1" author="admin">
                    <createTable tableName="users">
                      <column name="id" type="bigint" autoIncrement="true">
                        <constraints primaryKey="true"/>
                      </column>
                      <column name="name" type="varchar(255)"/>
                    </createTable>
                  </changeSet>
                </databaseChangeLog>
                """;
        }
    }

    public static void main(String[] args) {
        System.out.println("=== Liquibase DB Migration Concepts Lab ===\n");

        Liquibase liquibase = new Liquibase("db/changelog/db.changelog-master.xml");

        System.out.println("1. ChangeLog formats:");
        System.out.println("   - XML (db.changelog-master.xml)");
        System.out.println("   - YAML (db.changelog-master.yaml)");
        System.out.println("   - JSON (db.changelog-master.json)");
        System.out.println("   - SQL (raw SQL with Liquibase comments)\n");

        var v1 = new ChangeLog("db/changelog/v1-initial-schema.xml", List.of(
            new ChangeSet("1", "jratombo", "v1-initial-schema.xml", "Create users table", Instant.now().minusSeconds(86400), true),
            new ChangeSet("2", "jratombo", "v1-initial-schema.xml", "Create products table", Instant.now().minusSeconds(86000), true),
            new ChangeSet("3", "jratombo", "v1-initial-schema.xml", "Create orders table", Instant.now().minusSeconds(85600), true)
        ));

        var v2 = new ChangeLog("db/changelog/v2-add-columns.xml", List.of(
            new ChangeSet("4", "alice", "v2-add-columns.xml", "Add email column to users", Instant.now().minusSeconds(43200), true),
            new ChangeSet("5", "alice", "v2-add-columns.xml", "Add price column to products", Instant.now().minusSeconds(42800), true)
        ));

        var v3 = new ChangeLog("db/changelog/v3-indexes.xml", List.of(
            new ChangeSet("6", "bob", "v3-indexes.xml", "Create idx_users_email", null, false),
            new ChangeSet("7", "bob", "v3-indexes.xml", "Create idx_orders_user_id", null, false)
        ));

        liquibase.addChangeLog(v1);
        liquibase.addChangeLog(v2);
        liquibase.addChangeLog(v3);

        System.out.println("2. Initial state:");
        liquibase.status();

        System.out.println("\n3. Running liquibase update (applying pending changesets)...");
        liquibase.update();

        System.out.println("\n4. Status after update:");
        liquibase.status();

        System.out.println("\n5. Tagging:");
        liquibase.tag("release-1.0.0");

        System.out.println("\n6. Rollback operations:");
        System.out.println("   liquibase rollbackCount 2");
        liquibase.rollback(2);
        System.out.println("   liquibase rollbackToDate 2026-01-01");
        liquibase.rollbackToDate(Instant.parse("2026-01-01T00:00:00Z"));
        System.out.println("   liquibase rollbackToTag release-1.0.0");
        liquibase.rollbackToTag("release-1.0.0");

        System.out.println("\n7. Changeset attributes:");
        System.out.println("   id: unique identifier");
        System.out.println("   author: who created the changeset");
        System.out.println("   failOnError: whether to stop on failure");
        System.out.println("   runOnChange: re-run if changeset content changes");
        System.out.println("   context: deployment environment filter");
        System.out.println("   dbms: database type filter (h2, postgresql, mysql)");

        System.out.println("\n8. Common Change Types:");
        System.out.println("   createTable, addColumn, dropColumn, modifyDataType");
        System.out.println("   createIndex, dropIndex, addForeignKeyConstraint");
        System.out.println("   insert, update, delete (data changes)");
        System.out.println("   sql (arbitrary SQL), sqlFile (external SQL file)");

        System.out.println("\n9. Diff / Changelog generation:");
        System.out.println("   Compare two databases and generate changelog");
        System.out.println("   liquibase diff --referenceUrl=jdbc:... --url=jdbc:...");

        System.out.println("\n10. Liquibase vs Flyway comparison:");
        System.out.println("    Liquibase: XML/YAML/JSON/SQL, rollback support, contexts");
        System.out.println("    Flyway: SQL-only, simpler, undo available via pro");
        System.out.println("    Both: version control for database schemas, idempotent");

        System.out.println("\n=== Lab Complete ===");
    }
}
