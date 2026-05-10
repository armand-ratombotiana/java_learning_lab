package com.learning.lab.module47;

import liquibase.Contexts;
import liquibase.LabelExpression;
import liquibase.Liquibase;
import liquibase.change.Change;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;

public class Lab {
    public static void main(String[] args) throws Exception {
        System.out.println("=== Module 47: Liquibase Lab ===\n");

        Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://localhost:5432/mydb", "user", "pass");
        Database database = DatabaseFactory.getInstance()
                .findCorrectDatabaseImplementation(new JdbcConnection(connection));

        System.out.println("1. Liquibase Configuration:");
        System.out.println("   - ChangeLog: db/changelog.xml");
        System.out.println("   - Table: DATABASECHANGELOG");
        System.out.println("   - Contexts: dev, test, prod");
        System.out.println("   - Labels: release-1.0");

        System.out.println("\n2. Change Sets:");
        changeSetsDemo();

        System.out.println("\n3. Database Changes:");
        databaseChangesDemo();

        System.out.println("\n4. Rollbacks:");
        rollbackDemo();

        System.out.println("\n5. ChangeLog Structure:");
        changelogStructureDemo();

        Liquibase liquibase = new Liquibase(
                "db/changelog.xml",
                new ClassLoaderResourceAccessor(),
                database);

        System.out.println("\n6. Commands:");
        System.out.println("   - liquibase update");
        System.out.println("   - liquibase rollback <tag>");
        System.out.println("   - liquibase status");
        System.out.println("   - liquibase history");
        System.out.println("   - liquibase diff");

        liquibase.update(new Contexts(), new LabelExpression());
        connection.close();

        System.out.println("\n=== Liquibase Lab Complete ===");
    }

    static void changeSetsDemo() {
        System.out.println("   ChangeSet Structure:");
        System.out.println("   <changeSet id=\"1\" author=\"developer\">");
        System.out.println("       <createTable tableName=\"users\">");
        System.out.println("           <column name=\"id\" type=\"INT\"/>");
        System.out.println("           <column name=\"name\" type=\"VARCHAR(100)\"/>");
        System.out.println("       </createTable>");
        System.out.println("   </changeSet>");

        System.out.println("\n   ChangeSet Attributes:");
        System.out.println("   - id: Unique identifier");
        System.out.println("   - author: Creator name");
        System.out.println("   - runAlways: Execute every time");
        System.out.println("   - runOnChange: Re-run on changes");
        System.out.println("   - failOnError: Stop on error");
        System.out.println("   - context: Execution context");
    }

    static void databaseChangesDemo() {
        System.out.println("   Table Operations:");
        System.out.println("   - createTable, dropTable, renameTable");
        System.out.println("   - addColumn, dropColumn, renameColumn");
        System.out.println("   - addPrimaryKey, dropPrimaryKey");

        System.out.println("\n   Constraint Operations:");
        System.out.println("   - addNotNullConstraint, dropNotNullConstraint");
        System.out.println("   - addUniqueConstraint, dropUniqueConstraint");
        System.out.println("   - addForeignKeyConstraint, dropForeignKey");

        System.out.println("\n   Data Operations:");
        System.out.println("   - insert, update, delete");
        System.out.println("   - loadData (CSV)");
        System.out.println("   - executeCommand");

        System.out.println("\n   Other Changes:");
        System.out.println("   - createIndex, dropIndex");
        System.out.println("   - createProcedure, alterSequence");
    }

    static void rollbackDemo() {
        System.out.println("   Rollback Mechanisms:");
        System.out.println("   1. Explicit Rollback:");
        System.out.println("      <changeSet id=\"1\">");
        System.out.println("          <createTable tableName=\"users\"/>");
        System.out.println("          <rollback>dropTable users</rollback>");
        System.out.println("      </changeSet>");

        System.out.println("\n   2. Tag-Based Rollback:");
        System.out.println("      liquibase rollback <tag>");
        System.out.println("      Requires: <changeSet> with tag");

        System.out.println("\n   3. Generate Rollback:");
        System.out.println("      liquibase rollbackSQL <tag>");

        System.out.println("\n   Automatic Rollback:");
        System.out.println("   - createTable -> dropTable");
        System.out.println("   - addColumn -> dropColumn");
        System.out.println("   - insert -> delete");
    }

    static void changelogStructureDemo() {
        System.out.println("   Master Changelog:");
        System.out.println("   <databaseChangeLog>");
        System.out.println("       <include file=\"v1-changelog.xml\"/>");
        System.out.println("       <include file=\"v2-changelog.xml\"/>");
        System.out.println("   </databaseChangeLog>");

        System.out.println("\n   Structured Format:");
        System.out.println("   - XML: Structured, verbose");
        System.out.println("   - YAML: Human-readable");
        System.out.println("   - JSON: Machine-friendly");
        System.out.println("   - SQL: Direct SQL scripts");

        System.out.println("\n   Best Practices:");
        System.out.println("   - One changeSet per logical change");
        System.out.println("   - Use meaningful IDs and authors");
        System.out.println("   - Include rollback scripts");
        System.out.println("   - Use contexts for environment control");
    }
}