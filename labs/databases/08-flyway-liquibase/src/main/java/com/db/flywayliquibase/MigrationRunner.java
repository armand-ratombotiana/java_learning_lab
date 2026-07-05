package com.db.flywayliquibase;

/**
 * Demonstrates running both Flyway and Liquibase migrations programmatically.
 *
 * This class provides a roadmap for integrating both tools.
 * In practice, projects typically choose ONE migration tool.
 */
public class MigrationRunner {

    /**
     * Simulates running Flyway migrations.
     */
    static void runFlywayMigrations() {
        System.out.println("  Flyway: Running migrations...");
        System.out.println("  Flyway: V1__create_users.sql ......... applied");
        System.out.println("  Flyway: V2__add_orders.sql .......... applied");
        System.out.println("  Flyway: V3__seed_data.sql .......... applied");
        System.out.println("  Flyway: Current version: 3");
        System.out.println("  Flyway: Status: UP_TO_DATE");
    }

    /**
     * Simulates running Liquibase migrations.
     */
    static void runLiquibaseMigrations() {
        System.out.println("  Liquibase: Running updates...");
        System.out.println("  Liquibase: changeset id=1, author=academy ... applied");
        System.out.println("  Liquibase: changeset id=2, author=academy ... applied");
        System.out.println("  Liquibase: changeset id=3, author=academy ... applied");
        System.out.println("  Liquibase: Status: UP_TO_DATE");
    }

    public static void main(String[] args) {
        System.out.println("=== Migration Runner ===\n");
        System.out.println("Running both migration tools (for demo only —");
        System.out.println("in production, choose ONE per project):\n");

        runFlywayMigrations();
        System.out.println();
        runLiquibaseMigrations();

        System.out.println("\n=== Migration Strategy ===");

        System.out.println("""
            Choosing a migration tool:
            
            Use Flyway when:
              - You prefer SQL-first migrations
              - Simple, convention-over-configuration approach
              - Linear versioning is sufficient
              - Most of your team is SQL-proficient
            
            Use Liquibase when:
              - You need multi-format changelogs (XML/YAML/JSON)
              - Built-in rollback is important
              - You need conditional changesets (contexts/labels)
              - Cross-database abstraction is needed
            
            Use both?  Generally NOT recommended:
              - Two sources of truth for schema state
              - Risk of ordering conflicts
              - Harder to debug migration failures
            """);
    }
}
