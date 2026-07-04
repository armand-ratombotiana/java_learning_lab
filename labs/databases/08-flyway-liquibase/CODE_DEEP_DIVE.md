# Code Deep Dive: Database Migrations

## Flyway SQL Migration
File: `db/migration/V1__create_users.sql`
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);
```

File: `db/migration/V2__add_email_index.sql`
```sql
CREATE INDEX idx_users_email ON users(email);
```

## Flyway Java Migration
```java
public class V3__SeedAdminUser extends BaseJavaMigration {
    @Override
    public void migrate(Context context) throws Exception {
        String password = BCrypt.hashpw("admin123", BCrypt.gensalt());
        try (PreparedStatement stmt = context.getConnection()
                .prepareStatement("INSERT INTO users (username, email, password, role) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, "admin");
            stmt.setString(2, "admin@example.com");
            stmt.setString(3, password);
            stmt.setString(4, "ADMIN");
            stmt.executeUpdate();
        }
    }
}
```

## Liquibase XML Changelog
File: `db/changelog/db.changelog-master.xml`
```xml
<databaseChangeLog xmlns="http://www.liquibase.org/xml/ns/dbchangelog">
    <changeSet id="1" author="jratombo">
        <createTable tableName="products">
            <column name="id" type="BIGINT" autoIncrement="true">
                <constraints primaryKey="true"/>
            </column>
            <column name="name" type="VARCHAR(255)">
                <constraints nullable="false"/>
            </column>
            <column name="price" type="DECIMAL(10,2)"/>
        </createTable>
    </changeSet>
    <changeSet id="2" author="jratombo">
        <addColumn tableName="products">
            <column name="description" type="TEXT"/>
        </addColumn>
    </changeSet>
</databaseChangeLog>
```
