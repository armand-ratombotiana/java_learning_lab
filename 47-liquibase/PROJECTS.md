# Liquibase - Projects

This document contains two complete projects demonstrating Liquibase: a mini-project for learning database refactoring and a real-world project implementing production-grade schema management.

## Project 1: Liquibase Basics Mini-Project

### Overview

This mini-project demonstrates fundamental Liquibase concepts including changeset definitions, change types, rollback support, and changelog formats. It serves as a learning starting point for database refactoring.

### Project Structure

```
liquibase-basics/
├── pom.xml
└── src/
    └── main/
        └── resources/
            └── db/
                └── changelog/
```

### pom.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
    http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    
    <groupId>com.learning</groupId>
    <artifactId>liquibase-basics</artifactId>
    <version>1.0.0</version>
    
    <properties>
        <java.version>21</java.version>
        <liquibase.version>4.25.1</liquibase.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.postgresql</groupId>
            <artifactId>postgresql</artifactId>
        </dependency>
    </dependencies>
    
    <build>
        <plugins>
            <plugin>
                <groupId>org.liquibase</groupId>
                <artifactId>liquibase-maven-plugin</artifactId>
                <version>${liquibase.version}</version>
            </plugin>
        </plugins>
    </build>
</project>
```

### db.changelog-master.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.liquibase.org/xml/ns/dbchangelog
    http://www.liquibase.org/xml/ns/dbchangelog.xsd">
    
    <include file="db/changelog/v1-initial-schema.xml"/>
    <include file="db/changelog/v2-add-columns.xml"/>
    <include file="db/changelog/v3-add-indexes.xml"/>
</databaseChangeLog>
```

### v1-initial-schema.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    
    <changeSet id="1" author="admin">
        <createTable tableName="users">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true" nullable="false"/>
            </column>
            <column name="username" type="VARCHAR(50)">
                <constraints nullable="false" unique="true"/>
            </column>
            <column name="password_hash" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="first_name" type="VARCHAR(50)"/>
            <column name="last_name" type="VARCHAR(50)"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
            <column name="is_active" type="BOOLEAN" defaultValue="true"/>
        </createTable>
    </changeSet>
    
    <changeSet id="2" author="admin">
        <createTable tableName="products">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="name" type="VARCHAR(100)">
                <constraints nullable="false"/>
            </column>
            <column name="description" type="TEXT"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>
    
    <changeSet id="3" author="admin">
        <createTable tableName="orders">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="user_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="users"/>
            </column>
            <column name="order_number" type="VARCHAR(20)" unique="true"/>
            <column name="total_amount" type="DECIMAL(10,2)"/>
            <column name="status" type="VARCHAR(20)" defaultValue="PENDING"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

### v2-add-columns.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    
    <changeSet id="4" author="alice">
        <addColumn tableName="users">
            <column name="email" type="VARCHAR(100)">
                <constraints nullable="false" unique="true"/>
            </column>
        </addColumn>
    </changeSet>
    
    <changeSet id="5" author="alice">
        <addColumn tableName="users">
            <column name="phone" type="VARCHAR(20)"/>
            <column name="avatar_url" type="VARCHAR(255)"/>
            <column name="bio" type="TEXT"/>
        </addColumn>
    </changeSet>
    
    <changeSet id="6" author="alice">
        <addColumn tableName="products">
            <column name="price" type="DECIMAL(10,2)">
                <constraints nullable="false"/>
            </column>
            <column name="stock_quantity" type="INT" defaultValue="0"/>
            <column name="category" type="VARCHAR(50)"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```

### v3-add-indexes.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    
    <changeSet id="7" author="bob">
        <createIndex tableName="users" indexName="idx_users_email">
            <column name="email"/>
        </createIndex>
    </changeSet>
    
    <changeSet id="8" author="bob">
        <createIndex tableName="users" indexName="idx_users_active">
            <column name="is_active"/>
        </createIndex>
    </changeSet>
    
    <changeSet id="9" author="bob">
        <createIndex tableName="products" indexName="idx_products_category">
            <column name="category"/>
        </createIndex>
    </changeSet>
    
    <changeSet id="10" author="bob">
        <createIndex tableName="orders" indexName="idx_orders_user">
            <column name="user_id"/>
        </createIndex>
        <createIndex tableName="orders" indexName="idx_orders_status">
            <column name="status"/>
        </createIndex>
    </changeSet>
</databaseChangeLog>
```

### LiquibaseLab.java

```java
package com.learning.liquibase;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.changelog.ChangeSet;
import liquibase.changelog.ChangeLog;
import liquibase.global.Preconditions;

import java.sql.Connection;

public class LiquibaseLab {
    
    public static void main(String[] args) throws Exception {
        System.out.println("=== Liquibase Basics Lab ===\n");
        
        System.out.println("1. Changelog Formats:");
        System.out.println("   - XML: Standard Liquibase format");
        System.out.println("   - YAML: Human-readable");
        System.out.println("   - JSON: Machine-readable");
        System.out.println("   - SQL: Raw SQL with comments");
        
        System.out.println("\n2. Common Change Types:");
        String[] changes = {
            "createTable", "dropTable", "renameTable",
            "addColumn", "dropColumn", "modifyDataType",
            "createIndex", "dropIndex",
            "addForeignKeyConstraint", "dropForeignKeyConstraint",
            "insert", "update", "delete",
            "sql", "sqlFile"
        };
        for (String c : changes) System.out.println("   - " + c);
        
        System.out.println("\n3. ChangeSet Attributes:");
        System.out.println("   id: unique identifier");
        System.out.println("   author: changeset creator");
        System.out.println("   failOnError: stop on error");
        System.out.println("   runOnChange: re-run on content change");
        System.out.println("   context: environment filter");
        System.out.println("   dbms: database type filter");
        
        System.out.println("\n4. Rollback:");
        System.out.println("   - Automatic: liquibase rollback <tag>");
        System.out.println("   - Manual: <rollback> SQL block");
        
        showChangeSetStatus();
        
        System.out.println("\n=== Lab Complete ===");
    }
    
    private static void showChangeSetStatus() {
        String[][] sets = {
            {"1", "admin", "v1", "Create users table", "EXECUTED"},
            {"2", "admin", "v1", "Create products table", "EXECUTED"},
            {"3", "admin", "v1", "Create orders table", "EXECUTED"},
            {"4", "alice", "v2", "Add email column", "EXECUTED"},
            {"5", "alice", "v2", "Add phone column", "EXECUTED"},
            {"6", "bob", "v3", "Create idx_users_email", "EXECUTED"},
            {"7", "bob", "v3", "Create idx_users_active", "NOT EXECUTED"}
        };
        
        System.out.println("\n5. ChangeSet Status:");
        System.out.println("   ID    | Author | File       | Description         | Status");
        System.out.println("   ------|--------|------------|-------------------|--------");
        for (String[] s : sets) {
            System.out.printf("   %-5s | %-6s | %-10s | %-18s | %s%n",
                s[0], s[1], s[2], s[3], s[4]);
        }
    }
}
```

### Build and Run Instructions

```bash
cd liquibase-basics

# Run migrations
mvn liquibase:update -Dliquibase.url=jdbc:postgresql://localhost:5432/mydb

# Check status
mvn liquibase:status

# Generate SQL without running
mvn liquibase:updateSQL
```

## Project 2: Production Schema Refactoring

### Overview

This real-world project implements comprehensive production database refactoring with complex change types, contexts, rollback strategies, and diff-based changelog generation.

### Project Structure

```
liquibase-production/
├── pom.xml
├── src/
│   └── main/
│       └── resources/
│           └── db/
│               └── changelog/
└── config/
    └── liquibase.properties
```

### liquibase.properties

```properties
url=jdbc:postgresql://localhost:5432/orders
username=postgres
password=postgres
changeLogFile=db/changelog/db.changelog-master.xml
defaultSchemaName=public
```

### db.changelog-master.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    
    <includeAll path="db/changelog/v1/"/>
    <includeAll path="db/changelog/v2/"/>
    <includeAll path="db/changelog/v3/"/>
    
    <preConditions onFail="CONTINUE">
        <sqlCheck expectedResult="1">SELECT COUNT(*) FROM users</sqlCheck>
    </preConditions>
</databaseChangeLog>
```

### v1/initial-schema.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    logicalFilePath="db/changelog/v1/initial-schema.xml">
    
    <changeSet id="101" author="admin" context="!prod">
        <createTable tableName="categories">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="name" type="VARCHAR(50)" unique="true"/>
            <column name="parent_id" type="BIGINT"/>
        </createTable>
    </changeSet>
    
    <changeSet id="102" author="admin">
        <createTable tableName="product_images">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="product_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="products"/>
            </column>
            <column name="url" type="VARCHAR(255)"/>
            <column name="is_primary" type="BOOLEAN" defaultValue="false"/>
            <column name="display_order" type="INT" defaultValue="0"/>
        </createTable>
    </changeSet>
    
    <changeSet id="103" author="admin">
        <createTable tableName="reviews">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="product_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="products"/>
            </column>
            <column name="user_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="users"/>
            </column>
            <column name="rating" type="INT">
                <constraints checkConstraint="rating BETWEEN 1 AND 5"/>
            </column>
            <column name="comment" type="TEXT"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>
</databaseChangeLog>
```

### v2/payment-schema.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog"
    logicalFilePath="db/changelog/v2/payment-schema.xml">
    
    <changeSet id="201" author="admin">
        <createTable tableName="payment_methods">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="user_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="users"/>
            </column>
            <column name="type" type="VARCHAR(20)">
                <constraints nullable="false"/>
            </column>
            <column name="provider" type="VARCHAR(20)"/>
            <column name="token" type="VARCHAR(255)"/>
            <column name="last_four" type="VARCHAR(4)"/>
            <column name="is_default" type="BOOLEAN" defaultValue="false"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
        </createTable>
    </changeSet>
    
    <changeSet id="202" author="admin">
        <createTable tableName="payments">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="order_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="orders"/>
            </column>
            <column name="payment_method_id" type="BIGINT">
                <constraints foreignKeyReference="id" foreignKeyTableName="payment_methods"/>
            </column>
            <column name="amount" type="DECIMAL(10,2)">
                <constraints nullable="false"/>
            </column>
            <column name="currency" type="VARCHAR(3)" defaultValue="USD"/>
            <column name="status" type="VARCHAR(20)" defaultValue="PENDING"/>
            <column name="transaction_id" type="VARCHAR(100)"/>
            <column name="created_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
            <column name="completed_at" type="TIMESTAMP"/>
        </createTable>
    </changeSet>
    
    <changeSet id="203" author="admin" failOnError="false">
        <dropColumn tableName="orders" columnName="shipping_address"/>
        <dropColumn tableName="orders" columnName="billing_address"/>
    </changeSet>
</databaseChangeLog>
```

### v3/complex-changes.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    
    <changeSet id="301" author="admin">
        <modifyDataType tableName="products" columnName="description" newDataType="TEXT"/>
    </changeSet>
    
    <changeSet id="302" author="admin">
        <renameColumn tableName="products" oldColumnName="category" newColumnName="category_id"/>
    </changeSet>
    
    <changeSet id="303" author="admin">
        <dropIndex tableName="orders" indexName="idx_orders_status"/>
        <createIndex tableName="orders" indexName="idx_orders_status_created">
            <column name="status"/>
            <column name="created_at"/>
        </createIndex>
    </changeSet>
    
    <changeSet id="304" author="alice" dbms="postgresql">
        <sql>
            CREATE OR REPLACE FUNCTION update_updated_at_column()
            RETURNS TRIGGER AS $$
            BEGIN
                NEW.updated_at = CURRENT_TIMESTAMP;
                RETURN NEW;
            END;
            $$ language 'plpgsql';
        </sql>
    </changeSet>
    
    <changeSet id="305" author="alice" dbms="postgresql">
        <sqlFile path="db/changelog/triggers.sql" relativeToChangelogFile="true"/>
    </changeSet>
    
    <changeSet id="306" author="bob" runAlways="true">
        <insert tableName="categories">
            <column name="name" value="Uncategorized"/>
        </insert>
    </changeSet>
    
    <changeSet id="307" author="bob" runOnChange="true">
        <update tableName="categories">
            <where>name = 'Uncategorized'</where>
            <columns>
                <column name="updated_at" valueComputed="CURRENT_TIMESTAMP"/>
            </columns>
        </update>
    </changeSet>
</databaseChangeLog>
```

### v4-rollback.xml (With Manual Rollback)

```xml
<?xml version="1.0" encoding="UTF-8"?>
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    
    <changeSet id="401" author="admin">
        <createTable tableName="audit_logs">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="table_name" type="VARCHAR(50)"/>
            <column name="record_id" type="BIGINT"/>
            <column name="action" type="VARCHAR(10)"/>
            <column name="changed_at" type="TIMESTAMP" defaultValueComputed="CURRENT_TIMESTAMP"/>
            <column name="changed_by" type="BIGINT"/>
        </createTable>
        
        <rollback>
            DROP TABLE IF EXISTS audit_logs;
        </rollback>
    </changeSet>
    
    <changeSet id="402" author="admin">
        <addColumn tableName="orders">
            <column name="notes" type="TEXT"/>
        </addColumn>
        
        <rollback>
            <dropColumn tableName="orders" columnName="notes"/>
        </rollback>
    </changeSet>
</databaseChangeLog>
```

### LiquibaseService.java

```java
package com.learning.liquibase;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import liquibase.command.CommandScope;
import liquibase.command.core.UpdateCommandStep;
import liquibase.command.core.RollbackCommandStep;

import java.sql.Connection;
import java.util.Properties;

public class LiquibaseService {
    
    private Database database;
    private Liquibase liquibase;
    
    public void initialize(String url, String username, String password) throws Exception {
        Connection jdbcConnection = DriverManager.getConnection(url, username, password);
        database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
            new JdbcConnection(jdbcConnection));
        
        liquibase = new Liquibase(
            "db/changelog/db.changelog-master.xml",
            new ClassLoaderResourceAccessor(),
            database
        );
    }
    
    public void update() throws Exception {
        liquibase.update();
    }
    
    public void update(String context) throws Exception {
        liquibase.update(context);
    }
    
    public void rollback(int count, String rollbackScript) throws Exception {
        liquibase.rollback(count, rollbackScript);
    }
    
    public void rollbackToDate(String tag, java.util.Date date) throws Exception {
        liquibase.rollback(date, tag);
    }
    
    public void tag(String tagName) throws Exception {
        liquibase.tag(tagName);
    }
    
    public void checkIntegrity() throws Exception {
        var changeSets = liquibase.getDatabase().getChangeLog().getChangeSets();
        System.out.println("Total changesets: " + changeSets.size());
        
        boolean hasPending = changeSets.stream()
            .anyMatch(cs -> cs.getOperateAction().getAction().equals("EXECUTED"));
        System.out.println("Has pending changes: " + hasPending);
    }
}
```

### Build and Run Instructions

```bash
cd liquibase-production

# Run migrations
mvn liquibase:update

# Rollback last 2 changes
mvn liquibase:rollback -Dliquibase.rollbackCount=2

# Tag current state
mvn liquibase:tag -Dliquibase.tag=release-1.0

# Generate changelog from diff
mvn liquibase:diffChangeLog -Dliquibase.referenceUrl=jdbc:postgresql://localhost:5432/refdb

# Validate
mvn liquibase:validate
```

### Maven Commands Summary

```bash
mvn liquibase:update          # Apply all pending changesets
mvn liquibase:updateSQL       # Show SQL without executing
mvn liquibase:rollback      # Rollback changes
mvn liquibase:tag           # Tag current state
mvn liquibase:status        # Show pending changes
mvn liquibase:validate     # Validate changelog
mvn liquibase:diffChangeLog # Generate from diff
```

## Summary

These two projects demonstrate:

1. **Mini-Project**: Basic Liquibase setup with XML changelogs, common change types, and change attributes
2. **Production Project**: Complete production schema management with contexts, advanced change types, and manual rollbacks

Liquibase provides powerful database refactoring capabilities with multiple format support and automatic rollback functionality.