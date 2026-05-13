# 47 - Liquibase Quick Reference

## Key Concepts

| Concept | Description |
|---------|-------------|
| Changelog | Master file tracking all changesets |
| Changeset | Atomic unit of change |
| Change Types | SQL or XML-defined database operations |
| Contexts | Conditional changeset execution |
| Labels | Group changesets for selective execution |
| Preconditions | Validation before changeset execution |

## Change Types

```xml
<!-- Create table -->
<createTable tableName="users">
    <column name="id" type="int" autoIncrement="true">
        <constraints primaryKey="true"/>
    </column>
    <column name="username" type="varchar(255)"/>
    <column name="created_at" type="timestamp"/>
</createTable>

<!-- Add column -->
<addColumn tableName="users">
    <column name="email" type="varchar(255)"/>
</addColumn>

<!-- Create index -->
<createIndex tableName="users" indexName="idx_username">
    <column name="username"/>
</createIndex>

<!-- Add foreign key -->
<addForeignKeyConstraint baseTableName="orders"
    referencedTableName="users"
    baseColumnName="user_id"
    referencedColumnName="id"/>

<!-- Rollback support -->
<rollback>
    DROP TABLE users;
</rollback>
```

## Database-Specific Syntax

```xml
<!-- PostgreSQL -->
<createSequence sequenceName="user_seq"/>

<!-- MySQL -->
<createTable tableName="users">
    <column name="id" type="INT" autoIncrement="true"/>
</createTable>

<!-- Oracle -->
<createTable tableName="users" tablespace="USERS">
    <column name="id" type="NUMBER"/>
</createTable>
```

## Configuration

```properties
# application.properties
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog.xml
spring.liquibase.default-schema=public
spring.liquibase.drop-first=false
spring.liquibase.contexts=dev,test

# Maven pom.xml
<plugin>
    <groupId>org.liquibase</groupId>
    <artifactId>liquibase-maven-plugin</artifactId>
    <version>4.25.0</version>
    <configuration>
        <changeLogFile>src/main/resources/db/changelog.xml</changeLogFile>
        <url>jdbc:postgresql://localhost:5432/db</url>
        <username>postgres</username>
        <password>secret</password>
    </configuration>
</plugin>
```

## Commands

```bash
# Maven
mvn liquibase:update
mvn liquibase:rollback -Dliquibase.rollbackCount=1
mvn liquibase:status
mvn liquibase:diff
mvn liquibase:generateChangeLog

# CLI
liquibase update
liquibase rollback --count 1
liquibase history
liquibase validate
```

## Changelog Structure

```xml
<!-- Master changelog -->
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    <include file="changelog-001.xml"/>
    <include file="changelog-002.xml"/>
</databaseChangeLog>

<!-- Individual changelog -->
<databaseChangeLog>
    <changeSet id="001" author="developer">
        <createTable tableName="users">
            <column name="id" type="BIGINT"/>
        </createTable>
        <rollback dropTable tableName="users"/>
    </changeSet>
</databaseChangeLog>
```

## Advanced Features

| Feature | Use Case |
|---------|----------|
| storedProcedures | Database logic migration |
| modifyDataType | Schema evolution |
| loadUpdateData | Reference data management |
| customChange | Custom Java-based changes |
| conflicts | Merge conflict resolution |

## Best Practices

Use meaningful changeset IDs and author names. Keep changesets atomic and small. Always provide rollback instructions. Use contexts for environment-specific changes. Store changelogs in version control alongside code.