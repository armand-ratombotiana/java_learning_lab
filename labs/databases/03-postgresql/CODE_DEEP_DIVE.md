# Code Deep Dive: PostgreSQL with Java

## PostgreSQL-Specific Data Types in JPA

```java
@Entity
@Table(name = "products")
@TypeDef(name = "jsonb", typeClass = JsonBinaryType.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE,
                    generator = "product_seq")
    @SequenceGenerator(name = "product_seq", sequenceName = "product_id_seq",
                       allocationSize = 50)
    private Long id;

    @Column(nullable = false)
    private String name;

    // PostgreSQL native array type
    @Column(columnDefinition = "text[]")
    private List<String> tags;

    // JSONB column with hibernate-types
    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private Map<String, Object> attributes;

    // tsvector for full-text search
    @Column(columnDefinition = "tsvector")
    private String searchVector;
}
```

## Full-Text Search with PostgreSQL

```java
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
        SELECT p.*, ts_rank(p.search_vector, to_tsquery('english', :query)) AS rank
        FROM products p
        WHERE p.search_vector @@ to_tsquery('english', :query)
        ORDER BY rank DESC
        LIMIT 20
        """, nativeQuery = true)
    List<Product> searchByText(@Param("query") String query);
}
```

## Trigger Function for tsvector Update

```sql
CREATE OR REPLACE FUNCTION product_search_update()
RETURNS trigger AS $$
BEGIN
    NEW.search_vector := to_tsvector('english',
        coalesce(NEW.name, '') || ' ' || coalesce(NEW.description, '')
    );
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_product_search
    BEFORE INSERT OR UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION product_search_update();
```

## JSONB Querying with JDBC

```java
public List<Map<String, Object>> findUsersByPreference(
        Connection conn, String preferenceKey, Object value)
        throws SQLException {

    String sql = """
        SELECT id, name, data
        FROM user_profiles
        WHERE data -> 'preferences' ->> ? = ?
        """;

    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
        stmt.setString(1, preferenceKey);
        stmt.setString(2, value.toString());
        ResultSet rs = stmt.executeQuery();
        List<Map<String, Object>> results = new ArrayList<>();
        while (rs.next()) {
            Map<String, Object> row = new HashMap<>();
            row.put("id", rs.getLong("id"));
            row.put("name", rs.getString("name"));
            row.put("data", rs.getString("data"));
            results.add(row);
        }
        return results;
    }
}
```

## GIN Index Creation

```sql
CREATE INDEX idx_products_attributes ON products USING GIN (attributes);
CREATE INDEX idx_products_search ON products USING GIN (search_vector);
```

## Partial Index for Active Records

```sql
CREATE INDEX idx_orders_active ON orders (created_at)
    WHERE status NOT IN ('cancelled', 'delivered');
```

```java
@Entity
@Table(name = "orders")
@Where(clause = "status NOT IN ('cancelled', 'delivered')")
public class ActiveOrder {
    // only active orders loaded by this entity
}
```

## Indexing with Spring Data

```java
@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_users_email", columnList = "email", unique = true),
    @Index(name = "idx_users_status_created",
           columnList = "status, created_at DESC"),
    @Index(name = "idx_users_data_gin", columnList = "data",
           unique = false)
})
public class User {
    // ...
}
```
