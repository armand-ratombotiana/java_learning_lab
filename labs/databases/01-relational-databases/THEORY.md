# Theory: Relational Databases

## Relational Model (Codd, 1970)
- **Relation** = table with rows (tuples) and columns (attributes)
- **Domain** = set of allowable values for an attribute
- **Tuple** = ordered set of attribute values
- **Degree** = number of attributes
- **Cardinality** = number of tuples

## Keys
- **Candidate Key**: minimal set of attributes that uniquely identifies a tuple
- **Primary Key**: chosen candidate key
- **Foreign Key**: references PK in another table
- **Composite Key**: key with multiple attributes
- **Surrogate Key**: artificial key (e.g., auto-increment ID)

## Normalization

| Normal Form | Rule | Violation Example |
|---|---|---|
| 1NF | Atomic columns, no repeating groups | `phone_numbers VARCHAR(255)` storing multiple numbers |
| 2NF | 1NF + no partial dependencies on composite key | OrderItem with (order_id, product_id) PK storing customer_name |
| 3NF | 2NF + no transitive dependencies | Employee storing department_name instead of department_id |
| BCNF | Every determinant is a candidate key | Complex overlapping keys |

## ACID

| Property | Description | DB Mechanism |
|---|---|---|
| Atomicity | All or nothing | Write-ahead log (WAL) |
| Consistency | Data obeys constraints | Constraints, triggers |
| Isolation | Concurrent txns appear serial | MVCC, locking |
| Durability | Committed data survives | WAL, checkpointing |

## SQL Data Integrity
- `PRIMARY KEY` – unique + not null
- `FOREIGN KEY` – referential integrity
- `UNIQUE` – no duplicates
- `CHECK` – domain constraints
- `NOT NULL` – mandatory values

## JDBC Metadata Example
```java
DatabaseMetaData meta = conn.getMetaData();
ResultSet tables = meta.getTables(null, null, "%", new String[]{"TABLE"});
while (tables.next()) {
    System.out.println(tables.getString("TABLE_NAME"));
}
```

## JPA Entity Relationships
```java
@Entity
public class Order {
    @Id @GeneratedValue
    private Long id;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> items = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;
}
```
