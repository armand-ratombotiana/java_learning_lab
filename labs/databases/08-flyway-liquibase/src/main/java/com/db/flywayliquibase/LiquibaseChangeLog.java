package com.db.flywayliquibase;

/**
 * Demonstrates Liquibase changelog configuration using XML format.
 *
 * Liquibase uses changelog files with changesets to manage database
 * schema evolution. Changesets are idempotent — each has a unique
 * combination of id + author + file path.
 *
 * This class shows changelog examples in XML, YAML, and JSON formats.
 */
public class LiquibaseChangeLog {

    /**
     * Example changelog in XML format:
     *
     * <?xml version="1.0" encoding="UTF-8"?>
     * <databaseChangeLog
     *     xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
     *     xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
     *     xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
     *         http://www.liquibase.org/xml/ns/dbchangelog/dbchangelog-4.23.xsd">
     *
     *     <changeSet id="1" author="academy">
     *         <createTable tableName="users">
     *             <column name="id" type="bigint" autoIncrement="true">
     *                 <constraints primaryKey="true"/>
     *             </column>
     *             <column name="name" type="varchar(100)">
     *                 <constraints nullable="false"/>
     *             </column>
     *             <column name="email" type="varchar(255)">
     *                 <constraints unique="true" nullable="false"/>
     *             </column>
     *         </createTable>
     *     </changeSet>
     *
     *     <changeSet id="2" author="academy">
     *         <createTable tableName="orders">
     *             <column name="id" type="bigint" autoIncrement="true">
     *                 <constraints primaryKey="true"/>
     *             </column>
     *             <column name="user_id" type="bigint">
     *                 <constraints nullable="false"
     *                     foreignKeyName="fk_orders_users"
     *                     references="users(id)"/>
     *             </column>
     *             <column name="total" type="decimal(10,2)">
     *                 <constraints nullable="false"/>
     *             </column>
     *             <column name="created_at" type="timestamp"
     *                 defaultValueComputed="NOW()"/>
     *         </createTable>
     *     </changeSet>
     *
     *     <changeSet id="3" author="academy">
     *         <sql>
     *             INSERT INTO users (name, email) VALUES
     *             ('Alice', 'alice@example.com'),
     *             ('Bob', 'bob@example.com');
     *         </sql>
     *         <rollback>
     *             DELETE FROM users WHERE email IN
     *             ('alice@example.com', 'bob@example.com');
     *         </rollback>
     *     </changeSet>
     *
     * </databaseChangeLog>
     */

    static String showXmlChangelog() {
        return """
            XML changelog: src/main/resources/db/changelog/db.changelog-master.xml
            Changesets:
              id=1, author=academy  → CREATE TABLE users
              id=2, author=academy  → CREATE TABLE orders (FK to users)
              id=3, author=academy  → INSERT seed data (with rollback)
            """;
    }

    /**
     * Liquibase can also use SQL-style raw changesets for complex logic.
     */
    static String rawSqlChangesetExample() {
        return """
            <changeSet id="4" author="academy">
                <comment>Create product catalog view</comment>
                <sql>
                    CREATE MATERIALIZED VIEW IF NOT EXISTS product_catalog AS
                    SELECT id, name, price, category
                    FROM products
                    WHERE active = true;
                </sql>
                <rollback>
                    DROP MATERIALIZED VIEW IF EXISTS product_catalog;
                </rollback>
            </changeSet>
            """;
    }

    public static void main(String[] args) {
        System.out.println("=== Liquibase Database Migrations ===\n");

        System.out.println(showXmlChangelog());
        System.out.println("Raw SQL changeset example:");
        System.out.println(rawSqlChangesetExample());

        System.out.println("\nLiquibase commands:");
        System.out.println("  update       — apply pending changesets");
        System.out.println("  rollback     — revert by count/tag/date");
        System.out.println("  status       — show pending changesets");
        System.out.println("  diff         — compare DB to expected state");
        System.out.println("  generateChangelog — generate from existing DB");

        System.out.println("\nSupported changelog formats:");
        System.out.println("  - XML  (db.changelog-master.xml)");
        System.out.println("  - YAML (db.changelog-master.yaml)");
        System.out.println("  - JSON (db.changelog-master.json)");
        System.out.println("  - SQL  (raw SQL with Liquibase comments)");

        System.out.println("\nKey differences from Flyway:");
        System.out.println("  - Changesets are idempotent via id+author");
        System.out.println("  - Built-in rollback support");
        System.out.println("  - Multi-format changelogs (XML/YAML/JSON/SQL)");
        System.out.println("  - Contexts and labels for environment targeting");
    }
}
